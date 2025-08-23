package com.example.flashcards_backend.exception;

import java.util.List;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }

    public CardNotFoundException(List<Long> ids) {
        super("Cards not found with ids: " + ids);
    }

    public CardNotFoundException(Long id, Throwable cause) {
        super("Card not found with id: " + id, cause);
    }
}