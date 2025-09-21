package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.RateCardResponse;
import com.example.flashcards_backend.exception.CardHistoryNotFoundException;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CardHistoryService {
  private final CardHistoryRepository cardHistoryRepository;

  @Transactional
  public RateCardResponse recordRating(Long cardId, int rating, User user)
      throws DataAccessException, CardNotFoundException {
    try {
      log.info("Recording rating {} for card with id {}", rating, cardId);
      cardHistoryRepository.recordCardRating(cardId, rating);
      CardHistory cardHistory =
          cardHistoryRepository
              .findByCardIdAndUserId(cardId, user.getId())
              .orElseThrow(() -> new CardHistoryNotFoundException(cardId, user.getId()));
      return RateCardResponse.fromHistory(cardHistory);

    } catch (SQLException e) {
      if ("P0001".equals(e.getSQLState()) && e.getMessage().contains("Card with id")) {
        // Error is the one thrown by the stored procedure indicating the card was not found
        log.error("Card with id {} not found", cardId);
        throw new CardNotFoundException(cardId, e);
      }
      log.error("Database error while recording card rating", e);
      throw new DataAccessException("Database error while recording card rating", e) {};
    }
  }

  @Transactional
  public void deleteByCardIds(List<Long> ids) {
    log.info("Deleting card history for cards with ids {}", ids);
    cardHistoryRepository.deleteByCardIds(ids);
  }
}
