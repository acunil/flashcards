package com.example.flashcards_backend.exception;

public class DuplicateDeckNameException extends RuntimeException {
    public DuplicateDeckNameException(String name) {
        super("A deck with the name '" + name + "' already exists");
    }
}