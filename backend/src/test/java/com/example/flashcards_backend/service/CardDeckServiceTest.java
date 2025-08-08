package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
class CardDeckServiceTest {

    private CardDeckService cardDeckService;

    @Mock
    private DeckRepository deckRepository;

    private Deck deck1;
    private Deck deck2;

    @BeforeEach
    void setUp() {
        cardDeckService = new CardDeckService(deckRepository);
        deck1 = Deck.builder().id(1L).name("Deck 1").build();
        deck2 = Deck.builder().id(2L).name("Deck 2").build();
    }

    @Test
    void testGetOrCreateDecksByNames_withNoNewNames_doesNotSaveToRepo() {
        var expectedDecks = Set.of(deck1, deck2);
        when(deckRepository.findByNameIn(anySet()))
            .thenReturn(expectedDecks);

        var deckNamesDto = DeckNamesDto.of("Deck 1", "Deck 2");
        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNames(deckNamesDto);

        assertThat(actualDecks)
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(expectedDecks);
        verify(deckRepository).findByNameIn(deckNamesDto.deckNames());
        verify(deckRepository, never()).saveAll(anySet());
    }

    @Test
    void testGetOrCreateDecksByNames_withNewNames_savesNewDecksToRepo() {
        var existingDecks = Set.of(deck1);
        var newDecks = List.of(Deck.builder().name("Deck 2").build(),
            Deck.builder().name("Deck 3").build());
        var deckNamesDto = DeckNamesDto.of("Deck 1", "Deck 2", "Deck 3");
        when(deckRepository.findByNameIn(deckNamesDto.deckNames()))
            .thenReturn(existingDecks);
        when(deckRepository.saveAll(anyList())).thenReturn(newDecks);

        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNames(deckNamesDto);
        Set<Deck> expectedDecks = new HashSet<>(existingDecks);
        expectedDecks.addAll(newDecks);
        assertThat(actualDecks)
            .isNotNull()
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(expectedDecks);
        verify(deckRepository).findByNameIn(deckNamesDto.deckNames());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Deck>> captor = ArgumentCaptor.forClass(List.class);
        verify(deckRepository).saveAll(captor.capture());

        List<Deck> savedDecks = captor.getValue();
        assertThat(savedDecks)
            .extracting(Deck::getName)
            .containsExactlyInAnyOrder("Deck 2", "Deck 3");
    }
}