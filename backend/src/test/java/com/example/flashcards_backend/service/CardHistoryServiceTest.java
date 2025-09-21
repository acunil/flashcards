package com.example.flashcards_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.example.flashcards_backend.dto.RateCardResponse;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CardHistoryServiceTest {

  private static final UUID TEST_USER_ID = UUID.randomUUID();
  public static final long CARD_ID = 42L;

  @Mock private CardHistoryRepository cardHistoryRepo;

  @InjectMocks private CardHistoryService cardHistoryService;

  User user;

  @BeforeEach
  void setUp() {
    user = User.builder().id(TEST_USER_ID).username("testuser").build();
  }

  @Test
  void recordRating_invokesProcedureWithParameters() throws SQLException {
    CardHistory mockHistory =
        CardHistory.builder().id(1L).card(new Card()).user(user).lastRating(5).viewCount(1).avgRating(5).build();
    when(cardHistoryRepo.findByCardIdAndUserId(CARD_ID, TEST_USER_ID))
        .thenReturn(Optional.of(mockHistory));
    RateCardResponse rateCardResponse = cardHistoryService.recordRating(CARD_ID, 5, user);
    assertThat(rateCardResponse).isNotNull();
    assertThat(rateCardResponse.lastRating()).isEqualTo(5);
    assertThat(rateCardResponse.viewCount()).isEqualTo(1);
    assertThat(rateCardResponse.avgRating()).isEqualTo(5);
    verify(cardHistoryRepo).recordCardRating(CARD_ID, 5);
  }

  @Test
  void recordRating_handlesSQLException() throws SQLException {
    SQLException expectedException = new SQLException("Card with id 42 not found", "P0001");
    doThrow(expectedException).when(cardHistoryRepo).recordCardRating(CARD_ID, 5);
    assertThatThrownBy(() -> cardHistoryService.recordRating(CARD_ID, 5, user))
        .isInstanceOf(CardNotFoundException.class)
        .hasMessage("Card not found with id: 42");
  }

  @Test
  void recordRating_handlesOtherSQLException() throws SQLException {
    SQLException expectedException = new SQLException("Some other error", "S0001");
    doThrow(expectedException).when(cardHistoryRepo).recordCardRating(CARD_ID, 5);
    assertThatThrownBy(() -> cardHistoryService.recordRating(CARD_ID, 5, user))
        .isInstanceOf(DataAccessException.class)
        .hasMessage("Database error while recording card rating")
        .cause()
        .isInstanceOf(SQLException.class)
        .hasMessage("Some other error");
  }
}
