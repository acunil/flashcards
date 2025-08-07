package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import lombok.Builder;

import java.util.Set;

@Builder
public record CardResponse(Long id, String front, String back, Set<String> deckNames,
                           Double avgRating, Integer viewCount, String lastViewed, Integer lastRating) {
    public static CardResponse fromEntity(Card card, Long userId) {
        CardHistory cardHistory = card.getCardHistories()
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Card history not found for card ID: " + card.getId()));

        return new CardResponse(
            card.getId(),
            card.getFront(),
            card.getBack(),
            card.getDeckNames(),
            cardHistory.getAvgRating(),
            cardHistory.getViewCount(),
            cardHistory.getLastViewed() != null ? cardHistory.getLastViewed().toString() : null,
            cardHistory.getLastRating()
        );
    }

    public static CardResponse fromEntity(Card card) {
        return fromEntity(card, null);
    }

}