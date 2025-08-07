package com.example.flashcards_backend.service;

import com.example.flashcards_backend.repository.CardHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
class CardHistoryServiceTest {

    @Mock
    private CardHistoryRepository historyRepo;

    @InjectMocks
    private CardHistoryService historyService;

    @Test
    void recordRating_invokesProcedureWithParameters() throws SQLException {
        historyService.recordRating(42L, 5);
        verify(historyRepo).recordCardRating(42L, 5);
    }
}
