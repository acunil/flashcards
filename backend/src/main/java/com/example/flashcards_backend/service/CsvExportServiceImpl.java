package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardExportProjection;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvExportServiceImpl implements CsvExportService {

    private static final char DECK_SEPARATOR = ';';

    private final CardRepository cardRepository;
    private final SubjectRepository subjectRepository;
    private final DeckRepository deckRepository;

    public ResponseEntity<byte[]> exportCards(CardSource cardSource, Long id) {
        try {
            return switch (cardSource) {
                case SUBJECT -> createCsvResponse(CardSource.SUBJECT, id, generateCsvForSubject(id));
                case DECK -> createCsvResponse(CardSource.DECK, id, generateCsvForDeck(id));
            };
        } catch (IOException e) {
            log.error("Error generating CSV", e);
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (SubjectNotFoundException | DeckNotFoundException e) {
            log.error("Entity not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    private byte[] generateCsvForSubject(Long subjectId) throws SubjectNotFoundException, IOException {
        Subject subject = getSubject(subjectId);

        log.info("Exporting all cards for subject '{}'", subject.getName());
        List<CardExportProjection> cards = cardRepository.findExportDataBySubjectId(subjectId);

        log.info("{} cards found", cards.size());
        return generateCsvFromCardProjections(cards);
    }

    private byte[] generateCsvForDeck(Long deckId) throws DeckNotFoundException, IOException {
        Deck deck = getDeck(deckId);

        log.info("Exporting all cards for deck '{}'", deck.getName());
        List<CardExportProjection> cards = cardRepository.findExportDataByDeckId(deckId);

        log.info("{} cards found", cards.size());
        return generateCsvFromCardProjections(cards);
    }

    private ResponseEntity<byte[]> createCsvResponse(CardSource source, Long id, byte[] csv) {
        String sourceName = switch (source) {
            case SUBJECT -> getSubject(id).getName();
            case DECK -> getDeck(id).getName();
        };
        String filename = source.name().toLowerCase() + "_" + sourceName + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    private byte[] generateCsvFromCardProjections(List<CardExportProjection> cards) throws IOException {
        StringWriter out = new StringWriter();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("front", "back", "hint_front", "hint_back", "decks")
                .get();

        try (CSVPrinter printer = new CSVPrinter(out, format)) {
            for (CardExportProjection card : cards) {
                String decksJoined = String.join(String.valueOf(DECK_SEPARATOR), card.getDecks());
                printer.printRecord(
                        card.getFront(),
                        card.getBack(),
                        card.getHintFront(),
                        card.getHintBack(),
                        decksJoined
                );
            }
        }

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Subject getSubject(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));
    }

    private Deck getDeck(Long deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));
    }

}
