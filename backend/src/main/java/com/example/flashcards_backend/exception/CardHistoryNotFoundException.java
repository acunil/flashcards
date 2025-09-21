package com.example.flashcards_backend.exception;

import java.util.UUID;

public class CardHistoryNotFoundException extends RuntimeException {
    public static final String CARD_HISTORY_NOT_FOUND = "Card history not found for user with id '%s' on card with id '%s'";

    public CardHistoryNotFoundException(Long cardId, UUID userId) {
        super(String.format(CARD_HISTORY_NOT_FOUND, userId, cardId));
    }

  public CardHistoryNotFoundException(Long cardId, UUID userId, Throwable cause) {
    super(String.format(CARD_HISTORY_NOT_FOUND, userId, cardId), cause);
  }

}
