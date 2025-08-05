package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
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

    public Deck createDeck(String name) {
        Deck deck = Deck.builder().name(name.trim()).build();
        if (deck.getName() == null || deck.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Deck name cannot be null or blank");
        }
        return deckRepository.save(deck);
    }

    public Deck createDeck(CreateDeckRequest request) {
        Deck deck = Deck.builder()
            .name(request.name().trim())
            .build();

        Set<Long> uniqueCardIds = Arrays.stream(request.cardIds())
            .collect(Collectors.toSet());

        List<Card> cards = cardRepository.findAllById(uniqueCardIds);

        Set<Long> fetchedCardIds = cards.stream()
            .map(Card::getId)
            .collect(Collectors.toSet());

        Set<Long> invalidCardIds = uniqueCardIds.stream()
            .filter(id -> !fetchedCardIds.contains(id))
            .collect(Collectors.toSet());

        if (!invalidCardIds.isEmpty()) {
            String invalidIdsMessage = invalidCardIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("The following card IDs are invalid: " + invalidIdsMessage);
        }
        deck.setCards(new HashSet<>(cards));
        return deckRepository.save(deck);
    }

    public Deck renameDeck(Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Deck name cannot be null or blank");
        }
        Deck existingDeck = deckRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Deck not found with id: " + id));
        existingDeck.setName(name.trim());
        return deckRepository.save(existingDeck);
    }

    public void deleteDeck(Long id) {
        if (!deckRepository.existsById(id)) {
            throw new IllegalArgumentException("Deck not found with id: " + id);
        }
        deckRepository.deleteById(id);
    }

    public Set<Deck> getDecksByCardId(Long cardId) {
        return deckRepository.findDecksByCardId(cardId);
    }

    public Deck addCardsToDeck(Long deckId, Set<Long> cardIds) {
        Deck deck = getDeckById(deckId);
        Set<Long> existingCardIds = deck.getCards().stream()
            .map(Card::getId)
            .collect(Collectors.toSet());

        for (Long cardId : cardIds) {
            if (!existingCardIds.contains(cardId)) {
                deck.addCard(cardRepository.findById(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + cardId)));
            }
        }

        // use stream to filter out already existing cards, throwing an exception if any card is not found, and adding them to the deck

        deck.setCards(deck.getCards().stream()
            .filter(card -> cardIds.contains(card.getId()))
            .collect(Collectors.toSet()));

        return deckRepository.save(deck);
    }
}
