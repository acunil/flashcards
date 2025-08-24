package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final CardRepository cardRepository;
    private final CardHistoryRepository cardHistoryRepository;

    @Transactional(readOnly = true)
    public UserStatsResponse getForUserId(UUID userId) {

        Long totalCards = cardRepository.countByUserId(userId);
        Long totalCardViews = cardHistoryRepository.totalViewCountByUserId(userId);
        Optional<Card> mostViewedCard = cardRepository.findMostViewedByUserId(userId);
        Optional<Card> hardestCard = cardRepository.findHardestByUserId(userId);

        CardResponse hardestCardResponse = hardestCard.map(CardResponse::fromEntity).orElse(null);
        CardResponse mostViewedCardResponse = mostViewedCard.map(CardResponse::fromEntity).orElse(null);

        return UserStatsResponse.builder()
                .totalCards(totalCards)
                .hardestCard(hardestCardResponse)
                .mostViewedCard(mostViewedCardResponse)
                .totalCardViews(totalCardViews)
                .build();
    }
}
