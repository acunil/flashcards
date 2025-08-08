package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CardDeckService {
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;

    @Transactional
    public Set<Deck> getOrCreateDecksByNames(DeckNamesDto deckNamesDto) {
        Set<Deck> existingDecks = deckRepository.findByNameIn(deckNamesDto.deckNames());
        Set<String> existingNames = existingDecks.stream()
            .map(Deck::getName)
            .collect(toSet());
        Set<String> newNames = deckNamesDto.deckNames().stream()
            .filter(name -> !existingNames.contains(name))
            .collect(toSet());
        Set<Deck> allDecks = new HashSet<>(existingDecks);
        if (!newNames.isEmpty()) {
            List<Deck> newDecks = newNames.stream()
                .map(name -> Deck.builder().name(name).build())
                .toList();
            deckRepository.saveAll(newDecks);
            allDecks.addAll(newDecks);
        }
        return allDecks;
    }

    @Transactional
    public Deck createDeck(CreateDeckRequest request) {
        Deck deck = getOrCreateDecksByNames(DeckNamesDto.of(request.name()))
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Deck creation failed for name: " + request.name()));

        if (!isNull(request.cardIds()) && !request.cardIds().isEmpty()) {
            deck.addCards(getCards(request));
        }

        return deck;
    }

    private Set<Card> getCards(CreateDeckRequest request) {
        return new HashSet<>(cardRepository.findAllById(request.cardIds()));
    }

}
