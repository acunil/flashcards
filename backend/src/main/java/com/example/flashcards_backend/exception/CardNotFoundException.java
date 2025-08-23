package com.example.flashcards_backend.exception;

import java.util.List;

public class CardNotFoundException extends RuntimeException {

    public static final String CARD_NOT_FOUND = "Card not found with id: ";

    public CardNotFoundException(Long id) {
        super(CARD_NOT_FOUND + id);
    }

    public CardNotFoundException(List<Long> ids) {
        super(ids.size() == 1
                ? CARD_NOT_FOUND + ids.getFirst()
                : "Cards not found with ids: " + ids);
    }

    public CardNotFoundException(Long id, Throwable cause) {
        super(CARD_NOT_FOUND + id, cause);
    }
}