package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
class CardDeckServiceTest {

    private CardDeckService cardDeckService;

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private CardRepository cardRepository;

    private Deck deck1;
    private Deck deck2;

    @BeforeEach
    void setUp() {
        cardDeckService = new CardDeckService(deckRepository, cardRepository);
        deck1 = Deck.builder().id(1L).name("Deck 1").build();
        deck2 = Deck.builder().id(2L).name("Deck 2").build();
    }

    @Test
    void testGetOrCreateDecksByNames_withNoNewNames_doesNotSaveToRepo() {
        var expectedDecks = Set.of(deck1, deck2);
        when(deckRepository.findByNameIn(anySet()))
            .thenReturn(expectedDecks);

        var deckNames = Set.of("Deck 1", "Deck 2");
        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNames(deckNames);

        assertThat(actualDecks)
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(expectedDecks);
        verify(deckRepository).findByNameIn(deckNames);
        verify(deckRepository, never()).saveAll(anySet());
    }

    @Test
    void testGetOrCreateDecksByNames_withNewNames_savesNewDecksToRepo() {
        Set<String> deckNames = Set.of("Deck 1", "Deck 2", "Deck 3");
        Set<Deck> existingDecks = Set.of(deck1, deck2);
        when(deckRepository.findByNameIn(anySet())).thenReturn(existingDecks);

        Deck newDeck = Deck.builder().name("Deck 3").build();
        when(deckRepository.saveAll(anySet())).thenReturn(List.of(newDeck));

        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNames(deckNames);

        assertThat(actualDecks)
            .isNotNull()
            .hasSize(3)
            .extracting("name")
            .containsExactlyInAnyOrder("Deck 1", "Deck 2", "Deck 3");
        verify(deckRepository).findByNameIn(deckNames);

        ArgumentCaptor<List<Deck>> captor = ArgumentCaptor.forClass(List.class);

        verify(deckRepository).saveAll(captor.capture());
        List<Deck> savedDecks = captor.getValue();
        assertThat(savedDecks).hasSize(1);
        assertThat(savedDecks.getFirst().getName()).isEqualTo("Deck 3");
    }

    @Test
    void testCreateDeck() {
        CreateDeckRequest request = new CreateDeckRequest("New Deck", null);
        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);
        assertThat(actualDeck.getName()).isEqualTo("New Deck");
    }

    @Test
    void testCreateDeck_withCards() {
        Set<Card> cards = Set.of(Card.builder().id(1L).build(), Card.builder().id(2L).build());
        CreateDeckRequest request = new CreateDeckRequest("New Deck", Set.of(1L, 2L));

        when(cardRepository.findAllById(anySet())).thenReturn(cards.stream().toList());

        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);

        assertThat(actualDeck.getName()).isEqualTo("New Deck");
        assertThat(actualDeck.getCards())
                .extracting(Card::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        Deck savedDeck = captor.getValue();
        assertThat(savedDeck.getName()).isEqualTo("New Deck");
        assertThat(savedDeck.getCards())
                .extracting(Card::getId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void testCreateDeck_withNoCards() {
        CreateDeckRequest request = new CreateDeckRequest("New Deck", Set.of());

        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);

        assertThat(actualDeck.getName()).isEqualTo("New Deck");
        assertThat(actualDeck.getCards()).isEmpty();

        Deck savedDeck = captor.getValue();
        assertThat(savedDeck.getName()).isEqualTo("New Deck");
        assertThat(savedDeck.getCards()).isEmpty();
    }

    @Test
    void testCreateDeck_withAlreadyExistingDeckName() {
        CreateDeckRequest request = new CreateDeckRequest("Existing Deck", null);

        when(deckRepository.save(any(Deck.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> cardDeckService.createDeck(request))
                .isInstanceOf(DuplicateDeckNameException.class)
                .hasMessageContaining("A deck with the name 'Existing Deck' already exists");
    }

}