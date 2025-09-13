package com.example.flashcards_backend.service;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CardDeckService {
  private final DeckRepository deckRepository;
  private final CardRepository cardRepository;
  private final SubjectService subjectService;

  @Transactional
  public Set<Deck> getOrCreateDecksByNamesAndSubjectId(Set<String> names, Long subjectId) {
    log.info("Creating or getting decks for subject with id {}", subjectId);
    Subject subject = subjectService.findById(subjectId);
    Set<Deck> existingDecks = deckRepository.findByNameInAndSubjectId(names, subjectId);
    Set<String> existingNames = existingDecks.stream().map(Deck::getName).collect(toSet());
    Set<String> newNames =
        names.stream().filter(name -> !existingNames.contains(name)).collect(toSet());
    Set<Deck> allDecks = new HashSet<>(existingDecks);
    if (!newNames.isEmpty()) {
      List<Deck> newDecks =
          newNames.stream()
              .map(
                  name ->
                      Deck.builder().name(name).subject(subject).user(subject.getUser()).build())
              .collect(toList());
      deckRepository.saveAll(newDecks);
      allDecks.addAll(newDecks);
    }
    return allDecks;
  }

  @Transactional
  public Deck createDeck(CreateDeckRequest request) {
    log.info("Creating deck '{}'", request.name());
    Subject subject = subjectService.findById(request.subjectId());
    if (deckRepository.existsByNameAndSubject(request.name(), subject)) {
      throw new DuplicateDeckNameException(request.name(), subject.getName());
    }
    Deck deck =
        Deck.builder().name(request.name().trim()).subject(subject).user(subject.getUser()).build();
    deck = deckRepository.saveAndFlush(deck);

    if (!isNull(request.cardIds()) && !request.cardIds().isEmpty()) {
      Set<Card> cards = getCards(request);
      Deck finalDeck = deck;
      cards.forEach(card -> card.addDeck(finalDeck));
    }

    return deck;
  }

  @Transactional
  public void addDeckToCards(Long id, Set<Long> cardIds) {
    log.info("Adding deck with id {} to {} cards", id, cardIds.size());
    Deck deck = findDeckById(id);
    List<Card> cards = cardRepository.findAllById(cardIds);
    if (cards.stream()
        .anyMatch(card -> !Objects.equals(card.getSubject().getId(), deck.getSubject().getId()))) {
      throw new IllegalArgumentException("Card ids must belong to the same subject as the deck");
    }
    cards.forEach(card -> card.addDeck(deck));
  }

  @Transactional
  public void removeDeckFromCards(Long id, Set<Long> cardIds) {
    log.info("Removing deck with id {} from {} cards", id, cardIds.size());
    Deck deck = findDeckById(id);
    List<Card> cards = cardRepository.findAllById(cardIds);
    HashSet<Long> cardIdsNotWithDeck = new HashSet<>();
    cards.forEach(
        card -> {
          if (!card.getDecks().contains(deck)) {
            cardIdsNotWithDeck.add(card.getId());
          } else {
            card.removeDeck(deck);
          }
        });
    if (!cardIdsNotWithDeck.isEmpty()) {
      log.info("These cards did not have deck {} and were ignored: {}", id, cardIdsNotWithDeck);
    }
  }

  @Transactional
  public void deleteDeck(Long id) {
    log.info("Deleting deck with id {}", id);

    Deck deck = findDeckById(id);
    List<Card> cards = cardRepository.findByDeckId(id);
    cards.forEach(card -> card.removeDeck(deck));

    deckRepository.delete(deck);
  }

  /* Helpers */
  private Deck findDeckById(Long id) {
    return deckRepository.findById(id).orElseThrow(() -> new DeckNotFoundException(id));
  }

  private Set<Card> getCards(CreateDeckRequest request) {
    return new HashSet<>(cardRepository.findAllById(request.cardIds()));
  }
}
