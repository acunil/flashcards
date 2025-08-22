package com.example.flashcards_backend.dto;

import lombok.Builder;

@Builder
public record UserStatsResponse(
        Long totalCards,
        CardResponse hardestCard,
        CardResponse mostViewedCard
) {

}
