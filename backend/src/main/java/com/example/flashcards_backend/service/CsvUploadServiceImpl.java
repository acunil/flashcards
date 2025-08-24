package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CreateCardResponse;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvUploadServiceImpl implements CsvUploadService {

    public static final String FRONT = "front";
    public static final String BACK = "back";
    public static final String DECKS = "decks";
    private final CardRepository cardRepository;
    private final SubjectRepository subjectRepository;
    private final CardDeckService cardDeckService;
    private final CardService cardService;

    @Transactional
    @Override
    public CsvUploadResponseDto uploadCsv(InputStream csvStream, Long subjectId)
            throws IOException, SubjectNotFoundException {

        Subject subject = fetchSubject(subjectId);

        try (Reader reader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {

            List<CSVRecord> all = parseAllRecords(reader);
            List<CSVRecord> valid  = filterValid(all);
            logInvalid(all, valid);

            Map<Boolean, List<CSVRecord>> byDup = partitionByDuplicate(valid, subjectId);

            List<CardResponse> duplicates = new ArrayList<>();

            // Handle duplicates (for reporting only)
            for (CSVRecord r : byDup.get(true)) {
                duplicates.add(CardResponse.builder()
                                .front(r.get(FRONT))
                                .back(r.get(BACK))
                                .build()
                );
            }

            // Handle new cards
            List<CardRequest> toSave = new ArrayList<>();
            for (CSVRecord r : byDup.get(false)) {
                Set<String> deckNames = parseDecks(r.get(DECKS));

                CardRequest request = CardRequest.builder()
                        .front(r.get(FRONT))
                        .back(r.get(BACK))
                        .subjectId(subjectId)
                        .deckNames(deckNames)
                        .build();
                toSave.add(request);
            }
            List<CreateCardResponse> cardServiceCards = cardService.createCards(toSave);
            List<CardResponse> saved = cardServiceCards.stream().map(CardResponse::fromEntity).toList();

            return CsvUploadResponseDto.builder()
                    .saved(saved)
                    .duplicates(duplicates)
                    .build();

        } catch (IOException e) {
            log.error("CSV processing error", e);
            throw e;
        }
    }


    private Subject fetchSubject(Long subjectId) throws SubjectNotFoundException {
        return subjectRepository.findByIdWithUserAndSubjects(subjectId).orElseThrow(() -> new SubjectNotFoundException(subjectId));
    }

    private static List<CardResponse> generateResponses(List<Card> cards) {
        return cards.stream()
            .map(CardResponse::fromEntity)
            .toList();
    }

    private List<CSVRecord> parseAllRecords(Reader reader) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setTrim(true)
            .get();

        try (CSVParser parser = CSVParser.parse(reader, format)) {
            return parser.getRecords();
        }
    }

    private List<CSVRecord> filterValid(List<CSVRecord> rows) {
        return rows.stream()
            .filter(r -> {
                String f = r.get(FRONT);
                String b = r.get(BACK);
                return f != null && !f.isBlank()
                    && b != null && !b.isBlank();
            })
            .toList();
    }

    private void logInvalid(List<CSVRecord> all, List<CSVRecord> valid) {
        var validSet = new HashSet<>(valid);
        all.stream()
            .filter(r -> !validSet.contains(r))
            .forEach(r -> log.warn("Skipping invalid row: {}", r));
    }

    private Map<Boolean, List<CSVRecord>> partitionByDuplicate(List<CSVRecord> rows, Long subjectId) {
        return rows.stream()
            .collect(Collectors.partitioningBy(r ->
                cardRepository.existsByFrontAndBackAndSubjectId(r.get(FRONT), r.get(BACK), subjectId)
            ));
    }

    private List<Card> buildCards(List<CSVRecord> csvRecords, Subject subject, boolean processDecks) {
        return csvRecords.stream()
                .map(r -> {
                    Set<String> deckNames = parseDecks(r.get(DECKS));
                    Set<Deck> decks = processDecks && deckNames != null && !deckNames.isEmpty()
                            ? cardDeckService.getOrCreateDecksByNamesAndSubjectId(deckNames, subject.getId())
                            : Set.of();

                    return Card.builder()
                            .front(r.get(FRONT))
                            .back(r.get(BACK))
                            .subject(subject)
                            .decks(decks)
                            .user(subject.getUser())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Set<String> parseDecks(String raw) {
        if (raw == null || raw.isBlank()) return Set.of();
        return Arrays.stream(raw.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}

