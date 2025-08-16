package com.example.flashcards_backend.service;

import com.example.flashcards_backend.annotations.DeckName;
import com.example.flashcards_backend.dto.DeckResponse;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;

    public Set<DeckResponse> getAll() {
        return deckRepository.findAllWithCards()
                .stream()
                .map(DeckResponse::fromEntity)
                .collect(Collectors.toSet());
    }

    public Deck getDeckById(Long id) {
        return deckRepository.findById(id)
            .orElseThrow(() -> new DeckNotFoundException(id));
    }

    public Deck getDeckByName(String name) {
        return deckRepository.findByName(name)
            .orElseThrow(() -> new DeckNotFoundException(name));
    }

    public Set<Deck> getDecksByCardId(Long cardId) {
        return deckRepository.findDecksByCardId(cardId);
    }

    @Transactional
    public Deck renameDeck(Long id, @DeckName String name) {
        Deck deck = getDeckById(id);
        // check that name is unique in database
        if (deckRepository.existsByName(name.trim())) {
            throw new IllegalArgumentException("Deck name must be unique: " + name);
        }
        deck.setName(name.trim());
        return deck;
    }

    @Transactional
    public void deleteDeck(Long id) {
        Deck deck = getDeckById(id);
        deckRepository.delete(deck);
    }

}
