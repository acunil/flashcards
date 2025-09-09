package com.example.flashcards_backend.service;

import org.springframework.http.ResponseEntity;

public interface CsvExportService {
    ResponseEntity<byte[]> exportCards(CardSource cardSource, Long id);

    enum CardSource {
        SUBJECT, DECK
    }

}
