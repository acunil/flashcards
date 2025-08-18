package com.example.flashcards_backend.exception;

public class SubjectNotFoundException extends RuntimeException {
    public SubjectNotFoundException(Long id) {
        super("Subject not found with id: " + id);
    }

    public SubjectNotFoundException(Long id, Throwable cause) {
        super("Subject not found with id: " + id, cause);
    }
}