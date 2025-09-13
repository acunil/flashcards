package com.example.flashcards_backend.exception;

public class DuplicateDeckNameException extends RuntimeException {
    public DuplicateDeckNameException(String name, String subjectName) {
        super("A deck with the name '"
            + name
            + "' already exists in subject "
            + subjectName);
    }
}