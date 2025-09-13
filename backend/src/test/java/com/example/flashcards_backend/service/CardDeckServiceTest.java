package com.example.flashcards_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CardDeckServiceTest {

    private static final Long SUBJECT_ID = 1L;

    private CardDeckService cardDeckService;

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private CardRepository cardRepository;

    @Mock
    private SubjectService subjectService;

    private Deck deck1;
    private Deck deck2;
    private Subject subject1;

    @BeforeEach
    void setUp() {
        cardDeckService = new CardDeckService(deckRepository, cardRepository, subjectService);
        deck1 = Deck.builder().id(1L).name("Deck 1").build();
        deck2 = Deck.builder().id(2L).name("Deck 2").build();

        User user = User.builder().id(UUID.randomUUID()).username("me").build();
        subject1 = Subject.builder().id(SUBJECT_ID).name("Subject 1").user(user).build();
        deck1.setSubject(subject1);
        deck2.setSubject(subject1);

        when(subjectService.findById(SUBJECT_ID)).thenReturn(subject1);
    }

    @Test
    void testGetOrCreateDecksByNames_withNoNewNames_AndSubject_doesNotSaveToRepo() {
        var expectedDecks = Set.of(deck1, deck2);
        when(deckRepository.findByNameInAndSubjectId(anySet(), anyLong()))
                .thenReturn(expectedDecks);

        var deckNames = Set.of("Deck 1", "Deck 2");
        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNamesAndSubjectId(deckNames, SUBJECT_ID);

        assertThat(actualDecks)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedDecks);
        verify(deckRepository).findByNameInAndSubjectId(deckNames, SUBJECT_ID);
        verify(deckRepository, never()).saveAll(anySet());
    }

    @Test
    void testGetOrCreateDecksByNames_withNewNames_savesNewDecksToRepoAndSubject() {
        Set<String> deckNames = Set.of("Deck 1", "Deck 2", "Deck 3");
        Set<Deck> existingDecks = Set.of(deck1, deck2);
        when(deckRepository.findByNameInAndSubjectId(anySet(), anyLong())).thenReturn(existingDecks);

        Deck newDeck = Deck.builder().name("Deck 3").build();
        when(deckRepository.saveAll(anySet())).thenReturn(List.of(newDeck));

        Set<Deck> actualDecks = cardDeckService.getOrCreateDecksByNamesAndSubjectId(deckNames, SUBJECT_ID);

        assertThat(actualDecks)
                .isNotNull()
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder("Deck 1", "Deck 2", "Deck 3");
        verify(deckRepository).findByNameInAndSubjectId(deckNames, SUBJECT_ID);

        //noinspection unchecked
        ArgumentCaptor<List<Deck>> captor = ArgumentCaptor.forClass(List.class);

        verify(deckRepository).saveAll(captor.capture());
        List<Deck> savedDecks = captor.getValue();
        assertThat(savedDecks).hasSize(1);
        assertThat(savedDecks.getFirst().getName()).isEqualTo("Deck 3");
    }

    @Test
    void testCreateDeck() {
        CreateDeckRequest request = new CreateDeckRequest(SUBJECT_ID, "New Deck", null);
        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);
        assertThat(actualDeck.getName()).isEqualTo("New Deck");
    }

    @Test
    void testCreateDeck_withCards() {
        Subject subject = Subject.builder().id(SUBJECT_ID).build();
        Set<Card> cards = Set.of(
                Card.builder().id(1L).subject(subject).build(),
                Card.builder().id(2L).subject(subject).build()
        );
        CreateDeckRequest request = new CreateDeckRequest(SUBJECT_ID, "New Deck", Set.of(1L, 2L));

        when(cardRepository.findAllById(anySet())).thenReturn(cards.stream().toList());

        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);

        assertThat(actualDeck.getName()).isEqualTo("New Deck");

        Deck savedDeck = captor.getValue();
        assertThat(savedDeck.getName()).isEqualTo("New Deck");
    }

    @Test
    void testCreateDeck_withNoCards() {
        CreateDeckRequest request = new CreateDeckRequest(SUBJECT_ID, "New Deck", Set.of());

        ArgumentCaptor<Deck> captor = ArgumentCaptor.forClass(Deck.class);
        when(deckRepository.saveAndFlush(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Deck actualDeck = cardDeckService.createDeck(request);

        assertThat(actualDeck.getName()).isEqualTo("New Deck");

        Deck savedDeck = captor.getValue();
        assertThat(savedDeck.getName()).isEqualTo("New Deck");
    }

    @Test
    void testCreateDeck_withAlreadyExistingDeckName() {
        CreateDeckRequest request = new CreateDeckRequest(SUBJECT_ID, "Existing Deck", null);

        when(deckRepository.existsByNameAndSubject("Existing Deck", subject1))
                .thenReturn(true);

        assertThatThrownBy(() -> cardDeckService.createDeck(request))
                .isInstanceOf(DuplicateDeckNameException.class)
                .hasMessageContaining("A deck with the name 'Existing Deck' already exists");
    }

    @Test
    void testAddDeckToCards() {
        Card card1 = Card.builder().id(1L).subject(subject1).build();
        Card card2 = Card.builder().id(2L).subject(subject1).build();
        Set<Card> cards = Set.of(card1, card2);
        when(cardRepository.findAllById(anySet())).thenReturn(cards.stream().toList());
        when(deckRepository.findById(deck1.getId())).thenReturn(Optional.of(deck1));

        cardDeckService.addDeckToCards(deck1.getId(), Set.of(1L, 2L));

        verify(deckRepository).findById(deck1.getId());
        verify(cardRepository).findAllById(anySet());

        assertThat(card1.getDecks()).singleElement().isEqualTo(deck1);
        assertThat(card2.getDecks()).singleElement().isEqualTo(deck1);
    }

    @Test
    void testAddDeckToCards_DeckNotFound() {
        Long deck1Id = deck1.getId();
        when(deckRepository.findById(deck1Id)).thenReturn(Optional.empty());
        Set<Long> cardIds = Set.of(1L, 2L);
        assertThatThrownBy(() -> cardDeckService.addDeckToCards(deck1Id, cardIds))
                .isInstanceOf(DeckNotFoundException.class)
                .hasMessageContaining("Deck not found with id: 1");
        verify(deckRepository).findById(deck1Id);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void testAddDeckToCards_DifferentSubject() {
        Card card1 = Card.builder().id(1L).subject(subject1).build();
        Card card2 = Card.builder().id(2L).subject(subject1).build();
        Set<Card> cards = Set.of(card1, card2);
        when(cardRepository.findAllById(anySet())).thenReturn(cards.stream().toList());
        when(deckRepository.findById(deck1.getId())).thenReturn(Optional.of(deck1));
        Deck deck3 = Deck.builder().id(2L).name("Deck 2").subject(Subject.builder().id(2L).build()).build();
        Long deck3Id = deck3.getId();
        when(deckRepository.findById(deck3Id)).thenReturn(Optional.of(deck3));
        Set<Long> cardIds = Set.of(1L, 2L);
        assertThatThrownBy(() -> cardDeckService.addDeckToCards(deck3Id, cardIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Card ids must belong to the same subject as the deck");
        verify(deckRepository).findById(deck3Id);
        verify(cardRepository).findAllById(cardIds);
    }

    @Test
    void testRemoveDeckFromCards() {
        Card card1 = Card.builder().id(1L).subject(subject1).build();
        Card card2 = Card.builder().id(2L).subject(subject1).build();
        Set<Card> cards = Set.of(card1, card2);
        when(cardRepository.findAllById(anySet())).thenReturn(cards.stream().toList());
        when(deckRepository.findById(deck1.getId())).thenReturn(Optional.of(deck1));

        cardDeckService.removeDeckFromCards(deck1.getId(), Set.of(1L, 2L));

        verify(deckRepository).findById(deck1.getId());
        verify(cardRepository).findAllById(anySet());

        assertThat(card1.getDecks()).isEmpty();
        assertThat(card2.getDecks()).isEmpty();
    }

    @Test
    void testRemoveDeckFromCards_DeckNotFound() {
        Long deckId = deck1.getId();
        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());
        Set<Long> cardIds = Set.of(1L, 2L);
        assertThatThrownBy(() -> cardDeckService.removeDeckFromCards(deckId, cardIds))
                .isInstanceOf(DeckNotFoundException.class)
                .hasMessageContaining("Deck not found with id: 1");
        verify(deckRepository).findById(deckId);
        verifyNoInteractions(cardRepository);
    }

    @Test
    void testDeleteDeck_withCards() {
        Card card1 = Card.builder().id(1L).subject(subject1).build();
        Card card2 = Card.builder().id(2L).subject(subject1).build();
        List<Card> cards = List.of(card1, card2);
        card1.addDeck(deck1);
        card2.addDecks(Set.of(deck1, deck2));
        Long deck1Id = deck1.getId();
        when(deckRepository.findById(deck1Id)).thenReturn(Optional.of(deck1));
        when(cardRepository.findByDeckId(deck1Id)).thenReturn(cards);

        cardDeckService.deleteDeck(deck1Id);

        verify(deckRepository).findById(deck1Id);
        verify(cardRepository).findByDeckId(deck1Id);
        verify(deckRepository).delete(deck1);
        assertThat(card1.getDecks()).isEmpty();
        assertThat(card2.getDecks()).containsExactly(deck2);
    }

    @Test
    void testDeleteDeck_withNoCards() {
        Long deck1Id = deck1.getId();
        when(deckRepository.findById(deck1Id)).thenReturn(Optional.of(deck1));
        when(cardRepository.findByDeckId(deck1Id)).thenReturn(Collections.emptyList());

        cardDeckService.deleteDeck(deck1Id);

        verify(deckRepository).findById(deck1Id);
        verify(cardRepository).findByDeckId(deck1Id);
        verify(deckRepository).delete(deck1);
        verifyNoMoreInteractions(deckRepository);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void testDeleteDeck_DeckNotFound() {
        Long deckId = deck1.getId();
        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cardDeckService.deleteDeck(deckId))
                .isInstanceOf(DeckNotFoundException.class)
                .hasMessageContaining("Deck not found with id: 1");
        verify(deckRepository).findById(deckId);
        verify(cardRepository, never()).findByDeckId(deckId);
    }

}