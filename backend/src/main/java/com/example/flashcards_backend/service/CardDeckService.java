package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class CardDeckService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final SubjectService subjectService;

    @Transactional
    public Set<Deck> getOrCreateDecksByNamesAndSubjectId(Set<String> names, Long subjectId) {
        Subject subject = subjectService.findById(subjectId);
        Set<Deck> existingDecks = deckRepository.findByNameInAndSubjectId(names, subjectId);
        Set<String> existingNames = existingDecks.stream()
                .map(Deck::getName)
                .collect(toSet());
        Set<String> newNames = names.stream()
                .filter(name -> !existingNames.contains(name))
                .collect(toSet());
        Set<Deck> allDecks = new HashSet<>(existingDecks);
        if (!newNames.isEmpty()) {
            List<Deck> newDecks = newNames.stream()
                    .map(name -> Deck.builder()
                            .name(name)
                            .subject(subject)
                            .user(subject.getUser())
                            .build())
                    .collect(toList());
            deckRepository.saveAll(newDecks);
            allDecks.addAll(newDecks);
        }
        return allDecks;
    }

    @Transactional
    public Deck createDeck(CreateDeckRequest request) {
        Subject subject = Subject.builder().id(request.subjectId()).build();
        Deck deck = Deck.builder()
                .name(request.name().trim())
                .subject(subject)
                .build();
        try {
            deck = deckRepository.saveAndFlush(deck);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage(), e);
            throw new DuplicateDeckNameException(
                    "A deck with the name '"
                            + request.name()
                            + "' already exists in subject "
                            + subject.getName());
        }

        if (!isNull(request.cardIds()) && !request.cardIds().isEmpty()) {
            Set<Card> cards = getCards(request);
            Deck finalDeck = deck;
            cards.forEach(card -> card.addDeck(finalDeck));
        }

        return deck;
    }

    private Set<Card> getCards(CreateDeckRequest request) {
        return new HashSet<>(cardRepository.findAllById(request.cardIds()));
    }

}
