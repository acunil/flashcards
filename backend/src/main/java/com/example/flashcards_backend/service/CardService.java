package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import static com.example.flashcards_backend.utility.CardUtils.shuffleCards;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardHistoryService cardHistoryService;
    private final CardDeckService cardDeckService;
    private final SubjectService subjectService;

    public List<CardResponse> getAllCardResponsesFromSubject(Long subjectId) {
        List<CardDeckRowProjection> rows = subjectId == null
                ? cardRepository.findAllCardDeckRows()
                : cardRepository.findAllCardDeckRowsBySubjectId(subjectId);
        Map<Long, CardResponse> cardMap = new LinkedHashMap<>();
        for (CardDeckRowProjection row : rows) {
            CardResponse existing = cardMap.get(row.getCardId());
            if (existing == null) {
                existing = CardResponse.fromEntity(row);
                cardMap.put(row.getCardId(), existing);
            }
            if (row.getDeckId() != null) {
                existing.decks().add(new DeckSummary(row.getDeckId(), row.getDeckName(), row.getSubjectId()));
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
        Optional<Card> exists = getExistingCard(request);
        if (exists.isPresent()) {
            return new CardCreationResult(exists.get(), true);
        }
        Card cardToCreate = Card.builder()
                .front(request.front())
                .back(request.back())
                .build();
        cardToCreate.setSubject(subjectService.findById(request.subjectId()));
        Card saved = cardRepository.saveAndFlush(cardToCreate);
        addDecksIfPresent(request, saved);
        return new CardCreationResult(saved, false);
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
    public void deleteCards(List<Long> ids) throws CardNotFoundException {
        cardRepository.deleteCardsById(ids);
    }

    /* Helpers */

    private void addDecksIfPresent(CardRequest request, Card cardToCreate) {
        if (request.deckNames() != null && !request.deckNames().isEmpty()) {
            log.info("Adding decks {} to card {}", request.deckNames(), cardToCreate.getId());
            cardToCreate.addDecks(cardDeckService.getOrCreateDecksByNames(getDeckNames(request)));
        }
    }

    private Optional<Card> getExistingCard(CardRequest request) {
        return cardRepository.findBySubjectIdAndFrontAndBack(
                request.subjectId(),
                request.front(),
                request.back()
        );
    }

    private static Set<String> getDeckNames(CardRequest request) {
        return request.deckNames() == null
            ? Set.of()
            : request.deckNames();
    }

}
