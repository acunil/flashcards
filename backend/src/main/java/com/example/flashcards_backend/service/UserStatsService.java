package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Map<Integer, Long> countsForEachLastViewedRating = getCountsForEachLastViewedRating(userId);
        Long totalUnviewedCards = cardRepository.countUnviewedByUserId(userId);

        return UserStatsResponse.builder()
                .totalCards(totalCards)
                .hardestCard(hardestCard.map(CardResponse::fromEntity).orElse(null))
                .mostViewedCard(mostViewedCard.map(CardResponse::fromEntity).orElse(null))
                .totalCardViews(totalCardViews)
                .totalLastRating1(countsForEachLastViewedRating.getOrDefault(1, 0L))
                .totalLastRating2(countsForEachLastViewedRating.getOrDefault(2, 0L))
                .totalLastRating3(countsForEachLastViewedRating.getOrDefault(3, 0L))
                .totalLastRating4(countsForEachLastViewedRating.getOrDefault(4, 0L))
                .totalLastRating5(countsForEachLastViewedRating.getOrDefault(5, 0L))
                .totalUnviewedCards(totalUnviewedCards)
                .build();
    }

    private Map<Integer, Long> getCountsForEachLastViewedRating(UUID userId) {
        return cardHistoryRepository.countByLastRatingForUser(userId).stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));
    }
}
