package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
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
                .hintFront(request.hintFront())
                .hintBack(request.hintBack())
                .build();
        Subject subject = subjectService.findById(request.subjectId());
        cardToCreate.setSubject(subject);
        cardToCreate.setUser(subject.getUser());
        Card saved = cardRepository.saveAndFlush(cardToCreate);
        addDecksIfPresent(request, saved);
        return mapCardToCreateCardResponse(saved, false);
    }

    @Transactional
    public List<CreateCardResponse> createCards(List<CardRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("No card requests provided");
        }

        Set<Long> subjectIds = enforceSingleSubjectId(requests);

        Long subjectId = subjectIds.iterator().next();
        Subject subject = subjectService.findById(subjectId);

        List<CreateCardResponse> responses = new ArrayList<>();
        List<Card> toPersist = new ArrayList<>();

        Map<String, Deck> decksByName = fetchOrCreateDecks(requests, subjectId);

        // First pass: build cards or mark as existing
        for (CardRequest req : requests) {
            Optional<Card> existing = getExistingCard(req);
            if (existing.isPresent()) {
                responses.add(mapCardToCreateCardResponse(existing.get(), true));
            } else {
                Card card = Card.builder()
                        .front(req.front())
                        .back(req.back())
                        .hintFront(req.hintFront())
                        .hintBack(req.hintBack())
                        .subject(subject)
                        .user(subject.getUser())
                        .build();
                toPersist.add(card);
                responses.add(mapCardToCreateCardResponse(card, false));
            }
        }

        // Persist all new cards in bulk
        cardRepository.saveAllAndFlush(toPersist);

        // Link decks using the pre‑fetched map
        for (int i = 0; i < requests.size(); i++) {
            CardRequest req = requests.get(i);
            CreateCardResponse resp = responses.get(i);

            if (!resp.alreadyExisted()
                    && req.deckNames() != null
                    && !req.deckNames().isEmpty()) {

                Card card = toPersist.stream()
                        .filter(c -> c.getFront().equals(req.front()) && c.getBack().equals(req.back()))
                        .findFirst()
                        .orElseThrow();

                Set<Deck> resolvedDecks = req.deckNames().stream()
                        .map(decksByName::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                card.addDecks(resolvedDecks);
            }
        }

        // Persist the deck links in bulk
        cardRepository.saveAllAndFlush(toPersist);

        return responses;
    }

    private Map<String, Deck> fetchOrCreateDecks(List<CardRequest> requests, Long subjectId) {
        // 1️⃣ Collect all deck names from *new* card requests
        Set<String> allDeckNames = requests.stream()
                .map(CardRequest::deckNames)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        // 2️⃣ Resolve all required decks in one go
        Map<String, Deck> decksByName = allDeckNames.isEmpty()
                ? Collections.emptyMap()
                : cardDeckService.getOrCreateDecksByNamesAndSubjectId(allDeckNames, subjectId)
                .stream()
                .collect(Collectors.toMap(Deck::getName, d -> d));
        return decksByName;
    }

    private static Set<Long> enforceSingleSubjectId(List<CardRequest> requests) {
        Set<Long> subjectIds = requests.stream()
                .map(CardRequest::subjectId)
                .collect(Collectors.toSet());

        if (subjectIds.size() != 1) {
            throw new IllegalArgumentException(
                    "All CardRequests must share the same subjectId. Found: " + subjectIds
            );
        }
        return subjectIds;
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

    protected Optional<Card> getExistingCard(CardRequest request) {
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
                existing.decks().add(new DeckSummary(row.getDeckId(), row.getDeckName()));
            }
        }
        return new ArrayList<>(cardMap.values());
    }

    protected CreateCardResponse mapCardToCreateCardResponse(Card card, boolean alreadyExisted) {
        return CreateCardResponse.builder()
                .id(card.getId())
                .front(card.getFront())
                .back(card.getBack())
                .decks(card.getDecks().stream().map(DeckSummary::fromEntity).toList())
                .alreadyExisted(alreadyExisted)
                .build();
    }
}
