package com.example.flashcards_backend.service;


import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
class DeckServiceTest {

    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    private Deck deck1;
    private Deck deck2;
    private Subject subject1;

    @BeforeEach
    void setUp() {
        deckService = new DeckService(deckRepository);

        // Initialize test data
        subject1 = Subject.builder().id(1L).name("Subject 1").build();
        deck1 = Deck.builder().id(1L).name("Deck 1").subject(subject1).build();
        deck2 = Deck.builder().id(2L).name("Deck 2").subject(subject1).build();
    }

    @Test
    void testGetDeckById() {
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));

        Deck actualDeck = deckService.getDeckById(1L);

        assertThat(actualDeck).isEqualTo(deck1);
    }

    @Test
    void testGetDeckById_throwsExceptionIfDeckNotFound() {
        when(deckRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            deckService.getDeckById(99L))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 99");
    }

    @Test
    void testGetDeckByName() {
        when(deckRepository.findByName("Deck 1")).thenReturn(Optional.of(deck1));

        Deck actualDeck = deckService.getDeckByName("Deck 1");

        assertThat(actualDeck).isEqualTo(deck1);
    }

    @Test
    void testGetDeckByName_throwsExceptionIfDeckNotFound() {
        when(deckRepository.findByName("Nonexistent Deck")).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
            deckService.getDeckByName("Nonexistent Deck"))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with name: Nonexistent Deck");
    }

    @Test
    void testGetDeckSummariesBySubjectId() {
        when(deckRepository.findBySubjectId(subject1.getId())).thenReturn(List.of(deck1, deck2));
        Set<DeckSummary> deckSummaries = deckService.getDeckSummariesBySubjectId(1L);
        assertThat(deckSummaries)
                .hasSize(2)
                .extracting("name")
                .containsExactly("Deck 1", "Deck 2");
    }

    @Test
    void testRenameDeck() {
        String newName = "New Deck Name";
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));
        when(deckRepository.existsByNameAndSubject(newName, subject1)).thenReturn(false);

        Deck updatedDeck = deckService.renameDeck(1L, newName);

        assertThat(updatedDeck.getName()).isEqualTo(newName);
    }

    @Test
    void testRenameDeck_throwsExceptionIfNameNotUnique() {
        String newName = "Existing Deck Name";
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));
        when(deckRepository.existsByNameAndSubject(newName, subject1)).thenReturn(true);

        assertThatThrownBy(() ->
            deckService.renameDeck(1L, newName))
            .isInstanceOf(DuplicateDeckNameException.class)
            .hasMessageContaining("Deck name must be unique: " + newName);
    }

}