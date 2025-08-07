package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CardDeckService {
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    @Transactional
    public void setDecks(Long cardId, DeckNamesDto deckNamesDto) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException(cardId));
        card.removeAllDecks();
        Set<Deck> newDecks = getOrCreateDecksByNames(deckNamesDto);
        card.addDecks(newDecks);
    }

    @Transactional
    public void removeDeckFromAllCards(Long deckId) {
        Set<Card> cards = deckRepository.findById(deckId).filter( deck -> !deck.getCards().isEmpty())
            .map(Deck::getCards)
            .orElseThrow(() -> new IllegalArgumentException("Deck with id " + deckId + " not found or has no cards"));
        for (Card card : cards) {
            card.removeDeck(card.getDeckById(deckId));
        }
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

}
