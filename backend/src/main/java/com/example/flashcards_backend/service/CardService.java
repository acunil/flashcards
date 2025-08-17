package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardCreationResult;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
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

    public List<CardResponse> getAllCardResponses() {
        List<CardDeckRowProjection> rows = cardRepository.findAllCardDeckRows();

        Map<Long, CardResponse> cardMap = new LinkedHashMap<>();

        for (CardDeckRowProjection row : rows) {
            CardResponse existing = cardMap.get(row.getCardId());

            if (existing == null) {
                existing = new CardResponse(
                        row.getCardId(),
                        row.getFront(),
                        row.getBack(),
                        new HashSet<>(),
                        row.getAvgRating(),
                        row.getViewCount(),
                        row.getLastViewed(),
                        row.getLastRating()
                );

                cardMap.put(row.getCardId(), existing);
            }

            if (row.getDeckId() != null) {
                existing.decks().add(new DeckSummary(row.getDeckId(), row.getDeckName()));
            }
        }

        return new ArrayList<>(cardMap.values());
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
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
        // Completely replace the card's front and back text and set its decks to those of the request.
        Card card = getCardById(id);
        card.setFront(request.front());
        card.setBack(request.back());
        boolean decksDiffer = !card.getDeckNames().equals(getDeckNames(request));
        if (decksDiffer) {
            card.removeAllDecks();
            if (request.deckNames() == null || request.deckNames().isEmpty()) {
                return;
            }
            Set<Deck> decks = cardDeckService.getOrCreateDecksByNames(request.deckNames());
            card.addDecks(decks);
        }
    }

    @Transactional
    public void rateCard(Long cardId, int rating) throws CardNotFoundException, DataAccessException {
        cardHistoryService.recordRating(cardId, rating);
    }

    @Transactional
    public void deleteCard(Long id) {
        Card card = getCardById(id);
        cardRepository.delete(card);
    }

    /* Helpers */
    private static Set<String> getDeckNames(CardRequest request) {
        return request.deckNames() == null
            ? Set.of()
            : request.deckNames();
    }

}
