package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDateTime;

class CardSummaryTest {

    @BeforeEach
    void setUp() {
        // This method can be used to set up any common test data or state if needed.
        // Currently, it is empty as the tests do not require any specific setup.

    }

    @Test
    void testCardResponseCreation() {
        CardSummary cardSummary = new CardSummary( 1L, "Front Text", "Back Text", null, null, null, null, null, null, null, 1L);

        assertThat(cardSummary.id()).isEqualTo(1L);
        assertThat(cardSummary.front()).isEqualTo("Front Text");
        assertThat(cardSummary.back()).isEqualTo("Back Text");
    }

    @Test
    void testCardResponseFromEntity() {
        Card card = Card.builder()
                .id(2L)
                .front("Card Front")
                .back("Card Back")
                .subject(Subject.builder().id(1L).name("Test Subject").build())
                .build();
        LocalDateTime now = now();
        CardHistory cardHistory = CardHistory.builder()
                .avgRating(3.5)
                .viewCount(10)
                .lastViewed(now)
                .lastRating(4)
                .build();
        cardHistory.setCard(card);

        CardSummary cardSummary = CardSummary.fromEntity(card, cardHistory);
        assertThat(cardSummary.id()).isEqualTo(2L);
        assertThat(cardSummary.front()).isEqualTo("Card Front");
        assertThat(cardSummary.back()).isEqualTo("Card Back");
        assertThat(cardSummary.avgRating()).isEqualTo(3.5);
        assertThat(cardSummary.viewCount()).isEqualTo(10);
        assertThat(cardSummary.lastViewed()).isEqualTo(now.toString());
        assertThat(cardSummary.lastRating()).isEqualTo(4);
        assertThat(cardSummary.subjectId()).isEqualTo(1L);
    }

}