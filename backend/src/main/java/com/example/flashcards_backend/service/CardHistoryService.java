package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.RateCardResponse;
import com.example.flashcards_backend.exception.CardHistoryNotFoundException;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CardHistoryService {
  private final CardHistoryRepository cardHistoryRepository;
  private final CardRepository cardRepository;

  @Transactional
  public RateCardResponse recordRating(Long cardId, int rating, User user)
      throws DataAccessException, CardNotFoundException {
    try {
      log.info("Recording rating {} for card with id {}", rating, cardId);
      cardHistoryRepository.recordCardRating(cardId, rating);
      CardHistory cardHistory = getCardHistory(cardId, user);
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
  public RateCardResponse recordRatingForUser(Long cardId, int rating, User user) {
    log.info("Recording rating {} for card with id {} for user {}", rating, cardId, user.getId());
    Card card = getCard(cardId);
    CardHistory ch = getOrCreateCardHistoryForUser(user, card);
    ch.setLastRating(rating);
    ch.setViewCount(ch.getViewCount() + 1);
    ch.setAvgRating((ch.getAvgRating() * (ch.getViewCount() - 1) + rating) / ch.getViewCount());
    ch.setLastViewed(LocalDateTime.now());
    cardHistoryRepository.save(ch);
    return RateCardResponse.fromHistory(ch);
  }

  private static CardHistory getOrCreateCardHistoryForUser(User user, Card card) {
    log.info(
        "Getting or creating card history for user {} and card {}", user.getId(), card.getId());
    return card.getCardHistories().stream()
        .filter(ch -> ch.getUser().getId() == user.getId())
        .findFirst()
        .orElseGet(
            () -> {
              log.info("No existing history found, creating new one");
              var ch = CardHistory.builder().user(user).viewCount(0).build();
              ch.setCard(card);
              return ch;
            });
  }

  private CardHistory getCardHistory(Long cardId, User user) {
    return cardHistoryRepository
        .findByCardIdAndUserId(cardId, user.getId())
        .orElseThrow(() -> new CardHistoryNotFoundException(cardId, user.getId()));
  }

  private Card getCard(Long cardId) {
    log.info("Fetching card with id {}", cardId);
    return cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException(cardId));
  }

  @Transactional
  public void deleteByCardIds(List<Long> ids) {
    log.info("Deleting card history for cards with ids {}", ids);
    cardHistoryRepository.deleteByCardIds(ids);
  }
}
