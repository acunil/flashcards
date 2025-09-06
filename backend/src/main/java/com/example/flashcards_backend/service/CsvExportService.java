package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.exception.SubjectNotFoundException;

import java.io.IOException;

public interface CsvExportService {
    byte[] exportSubjectCards(Long subjectId) throws SubjectNotFoundException, IOException;
    byte[] exportDeckCards(Long deckId) throws DeckNotFoundException, IOException;
}
