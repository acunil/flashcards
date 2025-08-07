package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;

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
        LocalDateTime now = now();
        CardHistory.builder()
                .id(3L)
                .avgRating(3.5)
                .card(card)
                .viewCount(10)
                .lastViewed(now)
                .lastRating(4)
                .build();

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