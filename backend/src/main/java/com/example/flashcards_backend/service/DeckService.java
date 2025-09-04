package com.example.flashcards_backend.service;

import com.example.flashcards_backend.annotations.DeckName;
import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.DeckRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeckService {

    private final DeckRepository deckRepository;

    public Set<DeckSummary> getDeckSummariesBySubjectId(Long subjectId) {
        log.info("Getting deck summaries for subject with id {}", subjectId);
        return deckRepository.findBySubjectId(subjectId).stream()
                .map(DeckSummary::fromEntity)
                .collect(Collectors.toSet());
    }

    public Deck getDeckById(Long id) {
        log.info("Getting deck with id {}", id);
        return deckRepository.findById(id)
            .orElseThrow(() -> new DeckNotFoundException(id));
    }

    public Deck getDeckByName(String name) {
        return deckRepository.findByName(name)
            .orElseThrow(() -> new DeckNotFoundException(name));
    }

    @Transactional
    public Deck renameDeck(Long id, @DeckName String name) {
        log.info("Renaming deck with id {} to {}", id, name);
        Deck deck = getDeckById(id);
        if (deckRepository.existsByNameAndSubject(name.trim(), deck.getSubject())) {
            throw new DuplicateDeckNameException("Deck name must be unique: " + name);
        }
        deck.setName(name.trim());
        return deck;
    }

}
