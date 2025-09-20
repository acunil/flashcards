package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.CardExport;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardExportRowProjection;
import com.example.flashcards_backend.repository.CardRepository;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvExportServiceImpl implements CsvExportService {

  private static final char DECK_SEPARATOR = ';';

  private final CardRepository cardRepository;
  private final SubjectService subjectService;
  private final DeckService deckService;

  public byte[] exportCards(CardSource cardSource, Long id)
      throws IOException, SubjectNotFoundException, DeckNotFoundException {
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

  private byte[] generateCsvForSubject(Long subjectId)
      throws SubjectNotFoundException, IOException {
    Subject subject = getSubject(subjectId);

    log.info("Exporting all cards for subject '{}'", subject.getName());
    List<CardExportRowProjection> rawRows = cardRepository.findExportRowsBySubjectId(subjectId);

    List<CardExport> exports = groupCardExports(rawRows);

    logCardsFound(exports);
    return generateCsvFromCardExports(exports);
  }


  private byte[] generateCsvForDeck(Long deckId) throws DeckNotFoundException, IOException {
    Deck deck = getDeck(deckId);

    log.info("Exporting all cards for deck '{}'", deck.getName());
    List<CardExportRowProjection> cards = cardRepository.findExportRowsByDeckId(deckId);

    List<CardExport> exports = groupCardExports(cards);

    logCardsFound(exports);
    return generateCsvFromCardExports(exports, deck);
  }

  private byte[] generateCsvFromCardExports(List<CardExport> cards) throws IOException {
    return generateCsvFromCardExports(cards, null);
  }

  private byte[] generateCsvFromCardExports(List<CardExport> cards, Deck deck)
      throws IOException {
    StringWriter out = new StringWriter();
    CSVFormat format =
        CSVFormat.DEFAULT
            .builder()
            .setHeader("front", "back", "hint_front", "hint_back", "decks")
            .get();

    try (CSVPrinter printer = new CSVPrinter(out, format)) {
      for (CardExport card : cards) {
        String decksJoined =
            deck != null
                ? deck.getName()
                : String.join(String.valueOf(DECK_SEPARATOR), card.deckNames());
        printer.printRecord(
            card.front(), card.back(), card.hintFront(), card.hintBack(), decksJoined);
      }
    }

    return out.toString().getBytes(StandardCharsets.UTF_8);
  }

  private static List<CardExport> groupCardExports(List<CardExportRowProjection> rawRows) {
    Map<Long, List<CardExportRowProjection>> grouped = rawRows.stream()
        .collect(Collectors.groupingBy(CardExportRowProjection::getCardId));

    return grouped.values().stream()
        .map(group -> new CardExport(
            group.getFirst().getCardId(),
            group.getFirst().getFront(),
            group.getFirst().getBack(),
            group.getFirst().getHintFront(),
            group.getFirst().getHintBack(),
            group.stream()
                .map(CardExportRowProjection::getDeck)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList()
        ))
        .toList();
  }

  private Subject getSubject(Long subjectId) {
    return subjectService.findById(subjectId);
  }

  private Deck getDeck(Long deckId) {
    return deckService.getDeckById(deckId);
  }

  private static void logCardsFound(List<CardExport> cards) {
    log.info("{} cards found", cards.size());
  }
}
