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

    public byte[] exportCards(CardSource cardSource, Long id) throws IOException, SubjectNotFoundException, DeckNotFoundException {
        try {
            return switch (cardSource) {
                case SUBJECT -> generateCsvForSubject(id);
                case DECK -> generateCsvForDeck(id);
            };
        } catch (IOException e) {
            log.error("Error generating CSV", e);
            throw e;
        } catch (SubjectNotFoundException | DeckNotFoundException e) {
            log.error("Source not found", e);
            throw e;
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
