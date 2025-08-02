package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CardDtoTest {
    @Test
    void testCardDtoCreation() {
        CardDto cardDto = CardDto.builder()
                .id(1L)
                .front("Front Text")
                .back("Back Text")
                .build();
        assertThat(cardDto.id()).isEqualTo(1L);
        assertThat(cardDto.front()).isEqualTo("Front Text");
        assertThat(cardDto.back()).isEqualTo("Back Text");
    }

    @Test
    void testCardDtoFromEntity() {
        Card card = Card.builder()
                .id(2L)
                .front("Card Front")
                .back("Card Back")
                .build();
        CardDto cardDto = CardDto.fromEntity(card);
        assertThat(cardDto.id()).isEqualTo(2L);
        assertThat(cardDto.front()).isEqualTo("Card Front");
        assertThat(cardDto.back()).isEqualTo("Card Back");
    }

}