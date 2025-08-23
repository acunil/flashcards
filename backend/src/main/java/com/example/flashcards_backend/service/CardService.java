package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.flashcards_backend.utility.CardUtils.shuffleCards;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardHistoryService cardHistoryService;
    private final CardDeckService cardDeckService;
    private final SubjectService subjectService;

    @Transactional(readOnly = true)
    public List<CardResponse> getAllCardResponsesFromSubject(Long subjectId) {
        List<CardDeckRowProjection> rows = cardRepository.findCardDeckRowsBySubjectId(subjectId);
        return mapRowsToResponses(rows);
    }

    @Transactional(readOnly = true)
    public CardResponse getCardResponseById(Long id) {
        List<CardDeckRowProjection> rows = cardRepository.findCardDeckRowsByCardId(id);
        if (rows.isEmpty()) {
            throw new CardNotFoundException(id);
        }
        return mapRowsToResponses(rows).getFirst();
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByMinAvgRating(double threshold) {
        return cardRepository.findByMinAvgRating(threshold);
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByMinAvgRating(double threshold, boolean shuffled) {
        var cards = getCardsByMinAvgRating(threshold);
        return shuffled
                ? shuffleCards(cards)
                : cards;
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByMaxAvgRating(double threshold) {
        return cardRepository.findByMaxAvgRating(threshold);
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByMaxAvgRating(double threshold, boolean shuffled) {
        var cards = getCardsByMaxAvgRating(threshold);
        return shuffled
                ? shuffleCards(cards)
                : cards;
    }

    @Transactional
    public CreateCardResponse createCard(CardRequest request) {
        Optional<Card> exists = getExistingCard(request);
        if (exists.isPresent()) {
            return mapCardToCreateCardResponse(exists.get(), true);
        }
        Card cardToCreate = Card.builder()
                .front(request.front())
                .back(request.back())
                .build();
        cardToCreate.setSubject(subjectService.findById(request.subjectId()));
        Card saved = cardRepository.saveAndFlush(cardToCreate);
        addDecksIfPresent(request, saved);
        return mapCardToCreateCardResponse(saved, false);
    }


    @Transactional
    public void updateCard(Long id, CardRequest request) {
        // Completely replace the card's front and back text and set its decks to those of the request.
        log.info("Updating card {} with request {}", id, request);
        Card card = fetchCardById(id);
        card.setFront(request.front());
        card.setBack(request.back());
        card.setHintFront(request.hintFront());
        card.setHintBack(request.hintBack());
        boolean decksDiffer = !card.getDeckNames().equals(getDeckNames(request));
        if (decksDiffer) {
            card.removeAllDecks();
            if (request.deckNames() != null && !request.deckNames().isEmpty()) {
                Set<Deck> decks = cardDeckService.getOrCreateDecksByNamesAndSubjectId(
                        request.deckNames(), request.subjectId());
                card.addDecks(decks);
            }
        }
        cardRepository.saveAndFlush(card);
        log.info("Card {} successfully updated", id);
    }

    @Transactional
    public void rateCard(Long cardId, int rating) throws CardNotFoundException, DataAccessException {
        cardHistoryService.recordRating(cardId, rating);
    }

    @Transactional
    public void deleteCards(List<Long> ids) throws CardNotFoundException {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // Fetch to check existence (future-proof for partial failures)
        List<Card> cards = fetchCardsByIds(ids);
        if (cards.size() != ids.size()) {
            Set<Long> foundIds = cards.stream().map(Card::getId).collect(Collectors.toSet());
            List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new CardNotFoundException(missingIds);
        }

        cardHistoryService.deleteByCardIds(ids);

        cardRepository.deleteDeckAssociationsByCardIds(ids);

        cardRepository.deleteByIds(ids); // Or cardRepository.deleteAllById(ids) if using Spring Data's built-in
    }

    @Transactional
    public CardResponse setHints(HintRequest request, Long id) {
        Card card = fetchCardById(id);
        card.setHintFront(Strings.trimToNull(request.hintFront()));
        card.setHintBack(Strings.trimToNull(request.hintBack()));
        return CardResponse.fromEntity(card);
    }


    /* Helpers */
    private Card fetchCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    private List<Card> fetchCardsByIds(List<Long> ids) {
        return cardRepository.findAllById(ids);
    }

    private void addDecksIfPresent(CardRequest request, Card cardToCreate) {
        if (request.deckNames() != null && !request.deckNames().isEmpty()) {
            log.info("Adding decks {} to card {}", request.deckNames(), cardToCreate.getId());
            cardToCreate.addDecks(cardDeckService.getOrCreateDecksByNamesAndSubjectId(
                    getDeckNames(request), request.subjectId()));
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

    private List<CardResponse> mapRowsToResponses(List<CardDeckRowProjection> rows) {
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

    private CreateCardResponse mapCardToCreateCardResponse(Card card, boolean alreadyExisted) {
        return CreateCardResponse.builder()
                .id(card.getId())
                .front(card.getFront())
                .back(card.getBack())
                .decks(card.getDecks().stream().map(DeckSummary::fromEntity).toList())
                .alreadyExisted(alreadyExisted)
                .build();
    }
}
