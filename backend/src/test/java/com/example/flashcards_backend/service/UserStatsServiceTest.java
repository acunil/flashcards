package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardSummary;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class UserStatsServiceTest {

    static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    UserStatsService service;
    @Mock
    CardRepository cardRepository;

    @Mock
    CardHistoryRepository cardHistoryRepository;

    Card hardestCard;
    Card mostViewedCard;
    User user;
    Subject subject;
    CardHistory hardestCardHistory;
    CardHistory mostViewedCardHistory;

    @BeforeEach
    void setUp() {
        service = new UserStatsService(cardRepository, cardHistoryRepository);

        subject = Subject.builder().id(1L).name("Subject 1").build();
        user = User.builder().id(USER_ID).build();
        hardestCard = Card.builder()
                .user(user)
                .id(1L)
                .front("Front 1")
                .back("Back 1")
                .subject(subject)
                .build();
        mostViewedCard = Card.builder()
                .user(user)
                .id(2L)
                .front("Front 2")
                .back("Back 2")
                .subject(subject)
                .build();
        hardestCardHistory = CardHistory.builder()
                .card(hardestCard)
                .avgRating(2.0)
                .viewCount(5)
                .lastViewed(null)
                .lastRating(1)
                .build();
        mostViewedCardHistory = CardHistory.builder()
                .card(mostViewedCard)
                .avgRating(4.5)
                .viewCount(50)
                .lastViewed(null)
                .lastRating(5)
                .build();
    }

    @Test
    void testGetForUserId_returnsUserStatsResponse() {
        when(cardRepository.countByUserId(USER_ID)).thenReturn(100L);
        when(cardRepository.findHardestByUserId(USER_ID)).thenReturn(Optional.of(hardestCard));
        when(cardRepository.findMostViewedByUserId(USER_ID)).thenReturn(Optional.of(mostViewedCard));
        when(cardHistoryRepository.totalViewCountByUserId(USER_ID)).thenReturn(250L);
        when(cardRepository.countUnviewedByUserId(USER_ID)).thenReturn(99L);
        List<Object[]> lastRatingCounts = List.of(
                new Object[] { 1, 50L },
                new Object[] { 2, 20L },
                new Object[] { 3, 10L },
                new Object[] { 4, 5L },
                new Object[] { 5, 1L },
                new Object[] { 0, 30L }
        );
        when(cardHistoryRepository.countByLastRatingForUser(USER_ID)).thenReturn(lastRatingCounts);
        when(cardHistoryRepository.findByCardIdAndUserId(hardestCard.getId(), USER_ID))
                .thenReturn(Optional.of(hardestCardHistory));
        when(cardHistoryRepository.findByCardIdAndUserId(mostViewedCard.getId(), USER_ID))
                .thenReturn(Optional.of(mostViewedCardHistory));

        UserStatsResponse response = service.getForUserId(USER_ID);

        assertThat(response.hardestCard()).isEqualTo(CardSummary.fromEntity(hardestCard, hardestCardHistory));
        assertThat(response.mostViewedCard()).isEqualTo(CardSummary.fromEntity(mostViewedCard, mostViewedCardHistory));
        assertThat(response.totalCards()).isEqualTo(100L);
        assertThat(response.totalCardViews()).isEqualTo(250L);
        assertThat(response.totalLastRating1()).isEqualTo(50L);
        assertThat(response.totalLastRating2()).isEqualTo(20L);
        assertThat(response.totalLastRating3()).isEqualTo(10L);
        assertThat(response.totalLastRating4()).isEqualTo(5L);
        assertThat(response.totalLastRating5()).isEqualTo(1L);
        assertThat(response.totalUnviewedCards()).isEqualTo(99L);
    }

    @Test
    void testGetForUserId_throwsExceptionIfUserNotFound() {
        when(cardRepository.countByUserId(USER_ID)).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getForUserId(USER_ID)).isInstanceOf(IllegalArgumentException.class);
    }
}