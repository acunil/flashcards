package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CardResponseTest {
    @Test
    void testCardResponseCreation() {
        CardResponse cardResponse = CardResponse.builder()
                .id(1L)
                .front("Front Text")
                .back("Back Text")
                .build();
        assertThat(cardResponse.id()).isEqualTo(1L);
        assertThat(cardResponse.front()).isEqualTo("Front Text");
        assertThat(cardResponse.back()).isEqualTo("Back Text");
    }

    @Test
    void testCardResponseFromEntity() {
        Card card = Card.builder()
                .id(2L)
                .front("Card Front")
                .back("Card Back")
                .build();
        CardResponse cardResponse = CardResponse.fromEntity(card);
        assertThat(cardResponse.id()).isEqualTo(2L);
        assertThat(cardResponse.front()).isEqualTo("Card Front");
        assertThat(cardResponse.back()).isEqualTo("Card Back");
    }

}