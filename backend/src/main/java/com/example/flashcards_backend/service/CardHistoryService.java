package com.example.flashcards_backend.service;

import com.example.flashcards_backend.repository.CardHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CardHistoryService {
    private final CardHistoryRepository historyRepo;

    @Transactional
    public void recordVote(Long cardId, int rating) {
        historyRepo.recordCardVote(cardId, rating);
    }
}