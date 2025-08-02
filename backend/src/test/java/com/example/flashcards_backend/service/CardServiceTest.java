package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardDto;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class CardServiceTest {
    private CardService cardService;
    private Card card1;
    private Card card2;
    @Mock
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardRepository);

        card1 = new Card(1L, "Front 1", "Back 1");
        card2 = new Card(2L, "Front 2", "Back 2");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card1));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));
    }

    @Test
    void testGetAllCards() {
         when(cardRepository.findAll()).thenReturn(List.of(card1, card2));
         List<Card> cards = cardService.getAll();
         assertThat(cards).containsExactly(card1, card2);
    }

    @Test
    void testGetCardById() {
        Card foundCard = cardService.getById(1L);
        assertThat(foundCard).isEqualTo(card1);
    }

    @Test
    void testCreateCard() {
        Card newCard = new Card(null, "New Front", "New Back");
        when(cardRepository.save(newCard)).thenReturn(new Card(3L, "New Front", "New Back"));

        Card createdCard = cardService.create(CardDto.fromEntity(newCard));
        assertThat(createdCard.getId()).isNotNull();
        assertThat(createdCard.getFront()).isEqualTo("New Front");
        assertThat(createdCard.getBack()).isEqualTo("New Back");
    }

    @Test
    void testUpdateCard() {
        CardDto updateDto = CardDto.builder()
            .id(1L)
            .front("Updated Front")
            .back("Updated Back")
            .build();

        cardService.update(1L, updateDto);

        Card updatedCard = cardService.getById(1L);
        assertThat(updatedCard.getFront()).isEqualTo("Updated Front");
        assertThat(updatedCard.getBack()).isEqualTo("Updated Back");
    }

}