package com.example.flashcards_backend.service;

import com.example.flashcards_backend.repository.CardHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CardHistoryService {
    private final CardHistoryRepository cardHistoryRepository;

    @Transactional
    public void recordRating(Long cardId, int rating) {
        cardHistoryRepository.recordCardRating(cardId, rating);
    }
}