package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
class CardHistoryServiceTest {

    @Mock
    private CardHistoryRepository cardHistoryRepo;

    @InjectMocks
    private CardHistoryService cardHistoryService;

    @Test
    void recordRating_invokesProcedureWithParameters() throws SQLException {
        cardHistoryService.recordRating(42L, 5);
        verify(cardHistoryRepo).recordCardRating(42L, 5);
    }

    @Test
    void recordRating_handlesSQLException() throws SQLException {
        SQLException expectedException = new SQLException("Card with id 42 not found", "P0001");
        doThrow(expectedException).when(cardHistoryRepo).recordCardRating(42L, 5);
        assertThatThrownBy(() -> cardHistoryService.recordRating(42L, 5))
            .isInstanceOf(CardNotFoundException.class)
            .hasMessage("Card not found with id: 42");
    }

    @Test
    void recordRating_handlesOtherSQLException() throws SQLException {
        SQLException expectedException = new SQLException("Some other error", "S0001");
        doThrow(expectedException).when(cardHistoryRepo).recordCardRating(42L, 5);
        assertThatThrownBy(() -> cardHistoryService.recordRating(42L, 5))
            .isInstanceOf(DataAccessException.class)
            .hasMessage("Database error while recording card rating")
            .cause()
            .isInstanceOf(SQLException.class)
            .hasMessage("Some other error");
    }
}
