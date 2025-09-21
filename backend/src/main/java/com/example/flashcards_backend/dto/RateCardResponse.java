package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.CardHistory;

public record RateCardResponse(
    Long cardId, Integer lastRating, Double avgRating, Integer viewCount, String lastViewed) {
  public static RateCardResponse fromHistory(CardHistory history) {
    return new RateCardResponse(
        history.getCard().getId(),
        history.getLastRating(),
        history.getAvgRating(),
        history.getViewCount(),
        history.getLastViewed() != null ? history.getLastViewed().toString() : null);
  }
}
