package com.example.flashcards_backend.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(UUID id, Throwable cause) {
        super("User not found with id: " + id, cause);
    }
}