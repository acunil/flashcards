package com.example.flashcards_backend.service;

import java.io.IOException;

public interface CsvExportService {
    byte[] exportCards(CardSource cardSource, Long id) throws IOException;

    enum CardSource {
        SUBJECT, DECK
    }

}
