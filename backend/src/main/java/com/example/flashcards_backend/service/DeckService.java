package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;

import java.util.*;

@Service
@AllArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    public Set<Deck> getAll() {
        return Set.copyOf(deckRepository.findAll());
    }

    public Deck getDeckById(Long id) {
        return deckRepository.findById(id)
            .orElseThrow(() -> new DeckNotFoundException(id));
    }

    public Deck getDeckByName(String name) {
        return deckRepository.findByName(name)
            .orElseThrow(() -> new DeckNotFoundException(name));
    }

    public Set<Deck> getDecksByCardId(Long cardId) {
        return deckRepository.findDecksByCardId(cardId);
    }

    @Transactional
    public Set<Deck> getOrCreateDecksByNames(DeckNamesDto deckNamesDto) {
        Set<Deck> existingDecks = deckRepository.findByNameIn(deckNamesDto.deckNames());
        Set<String> existingNames = existingDecks.stream()
            .map(Deck::getName)
            .collect(toSet());
        Set<String> newNames = deckNamesDto.deckNames().stream()
            .filter(name -> !existingNames.contains(name))
            .collect(toSet());
        List<Deck> newDecks = newNames.stream()
            .map(name -> Deck.builder().name(name).build())
            .toList();
        deckRepository.saveAll(newDecks);
        Set<Deck> allDecks = new HashSet<>(existingDecks);
        allDecks.addAll(newDecks);
        return allDecks;
    }

    @Transactional
    public Deck createDeck(CreateDeckRequest request) {
        Deck deck = Deck.builder()
            .name(request.name().trim())
            .build();
        deck.addCards(getCards(request));
        return deck;
    }

    @Transactional
    public Deck renameDeck(Long id, @NotBlank @NotNull String name) {
        Deck deck = getDeckById(id);
        // check that name is unique in database
        if (deckRepository.existsByName(name.trim())) {
            throw new IllegalArgumentException("Deck name must be unique: " + name);
        }
        deck.setName(name.trim());
        return deck;
    }

    @Transactional
    public void deleteDeck(Long id) {
        Deck deck = getDeckById(id);
        deckRepository.delete(deck);
    }

    @Transactional
    public Deck addCardsToDeck(Long deckId, Set<Long> cardIds) {
        Deck deck = getDeckById(deckId);
        deck.addCards(getCardsInSelectionNotInDeck(cardIds, deck));
        return deck;
    }

    @Transactional
    public Deck removeCardsFromDeck(Long deckId, Set<Long> cardIds) {
        Deck deck = getDeckById(deckId);
        deck.removeCards(getCardsInDeckAndSelection(cardIds, deck));
        return deck;
    }

    @Transactional
    public void removeAllCardsFromDeck(Long deckId) {
        Deck deck = getDeckById(deckId);
        deck.removeAllCards();
    }

    @Transactional
    public Deck updateDeckCards(Long deckId, Set<Long> cardIds) {
        // Overwrite the deck's cards with the new set of cards.
        Deck deck = getDeckById(deckId);
        Set<Card> cardsToAdd = getCardsInSelectionNotInDeck(cardIds, deck);
        Set<Card> cardsToRemove = getCardsInDeckNotInSelection(cardIds, deck);
        deck.addCards(cardsToAdd);
        deck.removeCards(cardsToRemove);
        return deck;
    }

    /* Private Helpers */

    private Set<Card> getCards(CreateDeckRequest request) {
        return getCardsByIds(request.cardIds());
    }

    private static Set<Card> getCardsInDeckAndSelection(Set<Long> cardIds, Deck deck) {
        return deck.getCards().stream()
            .filter(card -> cardIds.contains(card.getId()))
            .collect(toSet());
    }

    private static Set<Card> getCardsInDeckNotInSelection(Set<Long> cardIds, Deck deck) {
        return deck.getCards().stream()
            .filter(card -> !cardIds.contains(card.getId()))
            .collect(toSet());
    }

    private Set<Card> getCardsInSelectionNotInDeck(Set<Long> cardIds, Deck deck) {
        Set<Long> newCardIds = cardIds.stream()
            .filter(deck::hasNotCard)
            .collect(toSet());
        return getCardsByIds(newCardIds);
    }

    private Set<Card> getCardsByIds(Set<Long> ids) {
        return new HashSet<>(cardRepository.findAllById(ids));
    }
}
