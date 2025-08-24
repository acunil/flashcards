package com.example.flashcards_backend.dto;

import lombok.Builder;

@Builder
public record UserStatsResponse(
        Long totalCards,
        CardResponse hardestCard,
        CardResponse mostViewedCard,
        Long totalCardViews,
        Long totalLastRating1,
        Long totalLastRating2,
        Long totalLastRating3,
        Long totalLastRating4,
        Long totalLastRating5,
        Long totalUnviewedCards
) {

}
