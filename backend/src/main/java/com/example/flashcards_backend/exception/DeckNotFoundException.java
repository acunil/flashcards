package com.example.flashcards_backend.exception;

public class DeckNotFoundException extends RuntimeException {
    public DeckNotFoundException(Long id) {
        super("Deck not found with id: " + id);
    }

    public DeckNotFoundException(String name) {
        super("Deck not found with name: " + name);
    }
}