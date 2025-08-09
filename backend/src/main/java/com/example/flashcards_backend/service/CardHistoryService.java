package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@AllArgsConstructor
public class CardHistoryService {
    private final CardHistoryRepository cardHistoryRepository;

    @Transactional
    public void recordRating(Long cardId, int rating) throws DataAccessException, CardNotFoundException {
        try {
            cardHistoryRepository.recordCardRating(cardId, rating);
        } catch (SQLException e) {
            if ("P0001".equals(e.getSQLState()) && e.getMessage().contains("Card with id")) {
                // Error is the one thrown by the stored procedure indicating the card was not found
                throw new CardNotFoundException(cardId, e);
            }
            throw new DataAccessException("Database error while recording card rating", e) {};
        }
    }
}