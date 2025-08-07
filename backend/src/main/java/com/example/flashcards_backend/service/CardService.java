package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardCreationResult;
import com.example.flashcards_backend.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import static com.example.flashcards_backend.utility.CardUtils.shuffleCards;

import java.util.*;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardHistoryService cardHistoryService;
    private final CardDeckService cardDeckService;

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public List<Card> getAllCards(boolean shuffled) {
        var cards = getAllCards();
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
    }

    public Set<Card> getCardsByIds(Set<Long> ids) {
        return new HashSet<>(cardRepository.findAllById(ids));
    }

    public List<Card> getCardsByMinAvgRating(double threshold) {
        return cardRepository.findByMinAvgRating(threshold);
    }

    public List<Card> getCardsByMinAvgRating(double threshold, boolean shuffled) {
        var cards = getCardsByMinAvgRating(threshold);
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }

    public List<Card> getCardsByMaxAvgRating(double threshold) {
        return cardRepository.findByMaxAvgRating(threshold);
    }

    public List<Card> getCardsByMaxAvgRating(double threshold, boolean shuffled) {
        var cards = getCardsByMaxAvgRating(threshold);
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }

    @Transactional
    public CardCreationResult createCard(CardRequest request) {
        Map<String, Object> row = cardRepository.createIfUnique(
            request.front(),
            request.back()
        );

        Card card = Card.builder()
            .id((Long) row.get("id"))
            .front((String) row.get("front"))
            .back((String) row.get("back"))
            .build();
        Boolean alreadyExisted = (Boolean) row.get("alreadyExisted");
        return new CardCreationResult(card, alreadyExisted);
    }

    @Transactional
    public void updateCard(Long id, CardRequest request) {
        // Completely replace the card's front and back text and set its decks based on the request.
        Card card = getCardById(id);
        card.setFront(request.front());
        card.setBack(request.back());
        cardDeckService.setDecks(id, DeckNamesDto.of(getDeckNames(request)));
        rateCard(id, request.rating());
    }

    @Transactional
    public void rateCard(Long cardId, int rating) throws CardNotFoundException, DataAccessException {
        cardHistoryService.recordRating(cardId, rating);
    }

    /* Helpers */
    private static Set<String> getDeckNames(CardRequest request) {
        return request.decks() == null
               ? Set.of()
               : request.decks();
    }

}
