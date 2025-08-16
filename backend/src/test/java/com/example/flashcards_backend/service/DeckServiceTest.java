package com.example.flashcards_backend.service;


import com.example.flashcards_backend.dto.DeckResponse;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
class DeckServiceTest {

    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    private Deck deck1;
    private Deck deck2;

    @BeforeEach
    void setUp() {
        deckService = new DeckService(deckRepository);

        // Initialize test data
        deck1 = Deck.builder().id(1L).name("Deck 1").build();
        deck2 = Deck.builder().id(2L).name("Deck 2").build();
    }

    @Test
    void testGetAllDecks() {
        Set<Deck> expectedDecks = Set.of(deck1, deck2);
        when(deckRepository.findAllWithCards()).thenReturn(expectedDecks.stream().toList());

        Set<DeckResponse> actualDecks = deckService.getAll();

        var expected = expectedDecks.stream().map(DeckResponse::fromEntity).collect(Collectors.toSet());

        assertThat(actualDecks).isEqualTo(expected);
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
    void testGetDecksByCardId() {
        when(deckRepository.findDecksByCardId(1L)).thenReturn(Set.of(deck1));
        Set<Deck> actualDecks = deckService.getDecksByCardId(1L);
        assertThat(actualDecks).containsExactly(deck1);
    }

    @Test
    void testGetDecksByCardId_returnsEmptySetIfNoDecksFound() {
        when(deckRepository.findDecksByCardId(99L)).thenReturn(Set.of());
        Set<Deck> actualDecks = deckService.getDecksByCardId(99L);
        assertThat(actualDecks).isEmpty();
    }

    @Test
    void testRenameDeck() {
        String newName = "New Deck Name";
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));
        when(deckRepository.existsByName(newName)).thenReturn(false);

        Deck updatedDeck = deckService.renameDeck(1L, newName);

        assertThat(updatedDeck.getName()).isEqualTo(newName);
    }

    @Test
    void testRenameDeck_throwsExceptionIfNameNotUnique() {
        String newName = "New Deck Name";
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));
        when(deckRepository.existsByName(newName)).thenReturn(true);

        assertThatThrownBy(() ->
            deckService.renameDeck(1L, newName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Deck name must be unique: " + newName);
    }

    @Test
    void testDeleteDeck() {
        when(deckRepository.findById(1L)).thenReturn(Optional.of(deck1));

        deckService.deleteDeck(1L);

        verify(deckRepository).delete(deck1);
    }

    @Test
    void testDeleteDeck_throwsExceptionIfDeckNotFound() {
        when(deckRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            deckService.deleteDeck(99L))
            .isInstanceOf(DeckNotFoundException.class)
            .hasMessageContaining("Deck not found with id: 99");

        verify(deckRepository, never()).delete(any(Deck.class));
    }


}