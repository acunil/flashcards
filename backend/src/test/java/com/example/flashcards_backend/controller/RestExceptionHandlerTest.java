package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.DuplicateDeckNameException;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import liquibase.exception.DatabaseException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void handleCardNotFound() {
        CardNotFoundException ex = new CardNotFoundException(1L);
        ResponseEntity<Map<String, String>> response = handler.handleCardNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "Card not found with id: 1");
    }

    @Test
    void handleDeckNotFoundException() {
        DeckNotFoundException ex = new DeckNotFoundException(1L);
        ResponseEntity<Map<String, String>> response = handler.handleDeckNotFoundException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "Deck not found with id: 1");
    }

    @Test
    void handleSubjectNotFound() {
        SubjectNotFoundException ex = new SubjectNotFoundException(1L);
        ResponseEntity<Map<String, String>> response = handler.handleSubjectNotFound(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "Subject not found with id: 1");
    }

    @Test
    void handleDatabaseException() {
        DatabaseException ex = new DatabaseException("DB error");
        ResponseEntity<Map<String, String>> response = handler.handleDatabaseException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "Database error occurred: DB error");
    }

    @Test
    void handleDataAccessException() {
        DataAccessException ex = new org.springframework.dao.EmptyResultDataAccessException(1);
        ResponseEntity<Map<String, String>> response = handler.handleDataAccessException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid arg");
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgumentException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "Invalid arg");
    }

    @Test
    void handleDuplicateDeckNameException() {
        DuplicateDeckNameException ex = new DuplicateDeckNameException("Duplicate deck", "My Subject");
        ResponseEntity<Map<String, String>> response = handler.handleDuplicateDeckNameException(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).containsEntry("error", "A deck with the name 'Duplicate deck' already exists in subject My Subject");
    }
}