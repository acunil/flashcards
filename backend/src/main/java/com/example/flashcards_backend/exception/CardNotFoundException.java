package com.example.flashcards_backend.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long id) {
        super("Card not found with id: " + id);
    }

    public CardNotFoundException(Long id, Throwable cause) {
        super("Card not found with id: " + id, cause);
    }
}