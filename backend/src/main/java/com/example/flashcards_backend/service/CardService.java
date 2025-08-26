package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardDeckRowProjection;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardHistoryService cardHistoryService;
    private final CardDeckService cardDeckService;
    private final SubjectService subjectService;

    /* GET methods */
    protected List<CardResponse> getAllCardResponsesFromSubject(Long subjectId) {
        List<CardDeckRowProjection> rows = cardRepository.findCardDeckRowsBySubjectId(subjectId);
        return mapRowsToResponses(rows);
    }

    @Transactional(readOnly = true)
    public List<CardResponse> getAllCardResponsesForUserAndSubject(User user, Long subjectId) {
        Subject subject = subjectService.findById(subjectId);
        if (!subject.getUser().equals(user)) {
            throw new IllegalArgumentException("User does not own subject");
        }
        return getAllCardResponsesFromSubject(subjectId);
    }

    @Transactional(readOnly = true)
    public CardResponse getCardResponseById(Long id) {
        log.info("Getting card response for id {}", id);
        List<CardDeckRowProjection> rows = cardRepository.findCardDeckRowsByCardId(id);
        if (rows.isEmpty()) {
            throw new CardNotFoundException(id);
        }
        return mapRowsToResponses(rows).getFirst();
    }

    @Transactional
    public CreateCardResponse createCard(CardRequest request) {
        log.info("Creating card with front: '{}'", request.front());
        Optional<Card> exists = getExistingCard(request);
        if (exists.isPresent()) {
            log.info("Card already exists with id: {}", exists.get().getId());
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
        log.info("Saving new card with id");
        Card saved = cardRepository.saveAndFlush(cardToCreate);
        log.info("Saved card with id {}", saved.getId());
        addDecksIfPresent(request, saved);
        return mapCardToCreateCardResponse(saved, false);
    }

    @Transactional
    public List<CreateCardResponse> createCards(@NonNull List<CardRequest> requests) {
        log.info("Creating cards from {} requests", requests.size());
        if (requests.isEmpty()) {
            log.error("No card requests provided");
            throw new IllegalArgumentException("No card requests provided");
        }

        List<CardCreationTask> cardCreationTasks = processCardRequests(requests);

        List<Card> newCards = persistNewCards(cardCreationTasks);

        // Link decks
        boolean decksNeedUpdating = false;
        Map<String, Deck> decksByName = fetchOrCreateDecks(requests);
        for (CardCreationTask p : cardCreationTasks) {
            if (!p.existed && p.req.deckNames() != null && !p.req.deckNames().isEmpty()) {
                Set<Deck> resolved = p.req.deckNames().stream()
                        .map(decksByName::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                p.card.addDecks(resolved);
                decksNeedUpdating = true;
            }
        }
        if (decksNeedUpdating) {
            log.info("Updating decks");
            cardRepository.saveAllAndFlush(newCards);
        }

        // Map at the very end (entities still managed here)
        log.info("createCards complete: {} cards created. Returning results as CreateCardResponses...", cardCreationTasks.size());
        return cardCreationTasks.stream()
                .map(p -> mapCardToCreateCardResponse(p.card, p.existed))
                .toList();
    }



    @Transactional
    public void updateCard(Long id, CardRequest request) {
        // Completely replace the card's front and back text and set its decks to those of the request.
        log.info("Updating card {}", id);
        Card card = fetchCardById(id);
        card.setFront(request.front());
        card.setBack(request.back());
        card.setHintFront(request.hintFront());
        card.setHintBack(request.hintBack());
        boolean decksDiffer = !card.getDeckNames().equals(getDeckNames(request));
        if (decksDiffer) {
            log.info("Decks differ, updating card decks");
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
        log.info("Deleting {} cards", ids.size());
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // Fetch to check existence (future-proof for partial failures)
        List<Card> cards = fetchCardsByIds(ids);
        if (cards.size() != ids.size()) {
            log.info("Found {} cards, expected {}", cards.size(), ids.size());
            Set<Long> foundIds = cards.stream().map(Card::getId).collect(Collectors.toSet());
            List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new CardNotFoundException(missingIds);
        }

        cardHistoryService.deleteByCardIds(ids);

        log.info("Deleting associations between cards and decks");
        cardRepository.deleteDeckAssociationsByCardIds(ids);

        log.info("Deleting cards");
        cardRepository.deleteByIds(ids); // Or cardRepository.deleteAllById(ids) if using Spring Data's built-in
    }

    @Transactional
    public CardResponse setHints(HintRequest request, Long id) {
        log.info("Setting hints for card {}", id);
        Card card = fetchCardById(id);
        card.setHintFront(Strings.trimToNull(request.hintFront()));
        card.setHintBack(Strings.trimToNull(request.hintBack()));
        return CardResponse.fromEntity(card);
    }


    /* Helpers */
    private Card fetchCardById(Long id) {
        log.info("Fetching card with id {}", id);
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    private List<Card> fetchCardsByIds(List<Long> ids) {
        log.info("Fetching {} cards by id", ids.size());
        return cardRepository.findAllById(ids);
    }

    private void addDecksIfPresent(CardRequest request, Card cardToCreate) {
        log.info("Checking for decks to add...");
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
        log.info("Mapping {} projection rows to CardResponses", rows.size());
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


    private List<CardCreationTask> processCardRequests(List<CardRequest> requests) {
        Subject subject = subjectService.findById(enforceSingleSubjectId(requests).iterator().next());
        List<CardCreationTask> cardCreationTasks = new ArrayList<>();
        log.info("Processing requests into existing or new");

        for (CardRequest req : requests) {
            Optional<Card> existing = getExistingCard(req);
            if (existing.isPresent()) {
                log.info("Card '{} : {}'  already exists in subject '{}'", req.front(), req.back(), subject.getName());
                cardCreationTasks.add(new CardCreationTask(req, true, existing.get()));
            } else {
                Card card = Card.builder()
                        .front(req.front())
                        .back(req.back())
                        .hintFront(req.hintFront())
                        .hintBack(req.hintBack())
                        .subject(subject)
                        .user(subject.getUser())
                        .build();
                cardCreationTasks.add(new CardCreationTask(req, false, card));
            }
        }
        return cardCreationTasks;
    }

    private List<Card> persistNewCards(List<CardCreationTask> cardCreationTask) {
        // Persist new cards
        List<Card> newCards = cardCreationTask.stream()
                .filter(p -> !p.existed)
                .map(p -> p.card)
                .toList();
        cardRepository.saveAllAndFlush(newCards);
        log.info("Created {} new cards", newCards.size());
        return newCards;
    }


    private Map<String, Deck> fetchOrCreateDecks(List<CardRequest> requests) {
        Long subjectId = requests.getFirst().subjectId();
        // 1️⃣ Collect all deck names from *new* card requests
        log.info("Collating deck names from {} card requests", requests.size());
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
        log.info("Fetched {} decks from card selection: {}", decksByName.size(), decksByName.keySet());
        return decksByName;
    }

    private static Set<Long> enforceSingleSubjectId(List<CardRequest> requests) {
        log.info("Enforcing single subjectId for {} card requests", requests.size());
        Set<Long> subjectIds = requests.stream()
                .map(CardRequest::subjectId)
                .collect(Collectors.toSet());

        if (subjectIds.size() != 1) {
            log.error("Found {} subjectIds: {}", subjectIds.size(), subjectIds);
            throw new IllegalArgumentException(
                    "All CardRequests must share the same subjectId. Found: " + subjectIds
            );
        }
        log.info("Found single subjectId: {}", subjectIds.iterator().next());
        return subjectIds;
    }

    private record CardCreationTask(CardRequest req, boolean existed, Card card) {
    }


}