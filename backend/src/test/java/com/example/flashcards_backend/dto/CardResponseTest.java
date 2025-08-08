package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;

class CardResponseTest {

    @BeforeEach
    void setUp() {
        // This method can be used to set up any common test data or state if needed.
        // Currently, it is empty as the tests do not require any specific setup.

    }

    @Test
    void testCardResponseCreation() {
        CardResponse cardResponse = new CardResponse( 1L, "Front Text", "Back Text", null, null, null, null, null);

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
        LocalDateTime now = now();
        CardHistory cardHistory = CardHistory.builder()
                .avgRating(3.5)
                .viewCount(10)
                .lastViewed(now)
                .lastRating(4)
                .build();
        cardHistory.setCard(card);

        CardResponse cardResponse = CardResponse.fromEntity(card);
        assertThat(cardResponse.id()).isEqualTo(2L);
        assertThat(cardResponse.front()).isEqualTo("Card Front");
        assertThat(cardResponse.back()).isEqualTo("Card Back");
        assertThat(cardResponse.avgRating()).isEqualTo(3.5);
        assertThat(cardResponse.viewCount()).isEqualTo(10);
        assertThat(cardResponse.lastViewed()).isEqualTo(now.toString());
        assertThat(cardResponse.lastRating()).isEqualTo(4);
    }

}