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

    @Override
    public byte[] exportSubjectCards(Long subjectId) throws SubjectNotFoundException, IOException {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        log.info("Exporting all cards for subject '{}'", subject.getName());
        List<CardExportProjection> cards = cardRepository.findExportDataBySubjectId(subjectId);

        log.info("{} cards found", cards.size());
        return generateCsv(cards);
    }

    @Override
    public byte[] exportDeckCards(Long deckId) throws DeckNotFoundException, IOException {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));

        log.info("Exporting all cards for deck '{}'", deck.getName());
        List<CardExportProjection> cards = cardRepository.findExportDataByDeckId(deckId);

        log.info("{} cards found", cards.size());
        return generateCsv(cards);
    }

    private byte[] generateCsv(List<CardExportProjection> cards) throws IOException {
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

}
