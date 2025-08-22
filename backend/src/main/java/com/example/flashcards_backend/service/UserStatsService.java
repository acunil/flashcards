package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;

import java.util.UUID;

public class UserStatsService {
    public UserStatsResponse getForUserId(UUID userId) {
        Long totalCards = 0L;
        CardResponse mostViewedCard = null;
        CardResponse hardestCard = null;




        return UserStatsResponse.builder()
                .totalCards(totalCards)
                .hardestCard(hardestCard)
                .mostViewedCard(mostViewedCard)
                .build();

    }
}
