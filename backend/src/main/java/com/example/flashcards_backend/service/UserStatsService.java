package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final CardRepository cardRepository;

    @Transactional(readOnly = true)
    public UserStatsResponse getForUserId(UUID userId) {

        Long totalCards = cardRepository.countByUserId(userId);
        Card mostViewedCard = cardRepository.findMostViewedByUserId(userId);
        Card hardestCard = cardRepository.findHardestByUserId(userId);

        return UserStatsResponse.builder()
                .totalCards(totalCards)
                .hardestCard(CardResponse.fromEntity(hardestCard))
                .mostViewedCard(CardResponse.fromEntity(mostViewedCard))
                .build();
    }
}
