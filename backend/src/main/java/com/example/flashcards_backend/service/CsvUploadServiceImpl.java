package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
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
    private final CardService cardService;

    @Transactional
    @Override
    public CsvUploadResponseDto uploadCsv(InputStream csvStream, Long subjectId)
            throws IOException, SubjectNotFoundException {

        log.info("Starting CSV upload for subject with id: {}", subjectId);

        Subject subject = fetchSubject(subjectId);
        log.info("Subject '{}' found", subject.getName());

        try (Reader reader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            log.info("Parsing CSV with");

            List<CSVRecord> all = parseAllRecords(reader);
            List<CSVRecord> valid  = filterValid(all);
            logInvalid(all, valid);

            if (valid.isEmpty()) {
                log.info("No valid rows found, skipping upload");
                return CsvUploadResponseDto.builder().build();
            }

            Map<Boolean, List<CSVRecord>> recordsGroupedByDuplication = partitionByDuplicate(valid, subjectId);

            List<CardResponse> duplicates = buildDuplicateResponses(recordsGroupedByDuplication);
            List<CardRequest> toSave = buildNewCardRequests(subjectId, recordsGroupedByDuplication);
            log.info("Final report: Valid: {}, Invalid: {}, Duplicates: {}, To save: {}",
                    valid.size(),
                    all.size() - valid.size(),
                    recordsGroupedByDuplication.get(true).size(),
                    toSave.size());

            log.info("Saving {} cards", toSave.size());
            List<CardResponse> saved = cardService.createCards(toSave).stream()
                    .map(CardResponse::fromEntity)
                    .toList();
            log.info("Cards saved");

            log.info("CSV upload complete. Building response.");
            return CsvUploadResponseDto.builder()
                    .saved(saved)
                    .duplicates(duplicates)
                    .build();

        } catch (IOException e) {
            log.error("CSV processing error", e);
            throw e;
        }
    }

    private List<CardRequest> buildNewCardRequests(Long subjectId, Map<Boolean, List<CSVRecord>> recordsGroupedByDuplication) {
        // Handle new cards
        List<CardRequest> toSave = new ArrayList<>();
        for (CSVRecord r : recordsGroupedByDuplication.get(false)) {
            Set<String> deckNames = parseDecks(r.get(DECKS));

            CardRequest request = CardRequest.builder()
                    .front(r.get(FRONT))
                    .back(r.get(BACK))
                    .subjectId(subjectId)
                    .deckNames(deckNames)
                    .build();
            toSave.add(request);
        }
        return toSave;
    }

    private static List<CardResponse> buildDuplicateResponses(Map<Boolean, List<CSVRecord>> recordsGroupedByDuplication) {
        return recordsGroupedByDuplication.get(true).stream()
                .map(r -> CardResponse.builder()
                        .front(r.get(FRONT))
                        .back(r.get(BACK))
                        .build())
                .toList();
    }


    private Subject fetchSubject(Long subjectId) throws SubjectNotFoundException {
        return subjectRepository.findByIdWithUserAndSubjects(subjectId).orElseThrow(() -> new SubjectNotFoundException(subjectId));
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

    private Set<String> parseDecks(String raw) {
        if (Strings.trimToNull(raw) == null) return Set.of();
        return Arrays.stream(raw.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}

