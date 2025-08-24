package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardHistoryRepository;
import com.example.flashcards_backend.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    UserStatsResponse userStatsResponse;
    Card hardestCard;
    Card mostViewedCard;
    User user;
    Subject subject;

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

        userStatsResponse = UserStatsResponse.builder()
                .hardestCard(CardResponse.fromEntity(hardestCard))
                .mostViewedCard(CardResponse.fromEntity(mostViewedCard))
                .totalCards(100L)
                .totalCardViews(250L)
                .build();
    }

    @Test
    void testGetForUserId_returnsUserStatsResponse() {
        when(cardRepository.countByUserId(USER_ID)).thenReturn(100L);
        when(cardRepository.findHardestByUserId(USER_ID)).thenReturn(Optional.of(hardestCard));
        when(cardRepository.findMostViewedByUserId(USER_ID)).thenReturn(Optional.of(mostViewedCard));
        when(cardHistoryRepository.totalViewCountByUserId(USER_ID)).thenReturn(250L);

        UserStatsResponse response = service.getForUserId(USER_ID);
        assertThat(response).isEqualTo(userStatsResponse);
    }

    @Test
    void testGetForUserId_throwsExceptionIfUserNotFound() {
        when(cardRepository.countByUserId(USER_ID)).thenThrow(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getForUserId(USER_ID)).isInstanceOf(IllegalArgumentException.class);
    }
}