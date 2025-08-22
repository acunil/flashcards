package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.service.UserStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserStatsController.class)
class UserStatsControllerTest {

    static final String ENDPOINT = "/user-stats";
    static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserStatsService userStatsService;

    UserStatsResponse userStatsResponse;
    CardResponse hardestCard;
    CardResponse mostViewedCard;
    User user;
    Subject subject;

    @BeforeEach
    void setUp() {

        subject = Subject.builder().id(1L).name("Subject 1").build();
        user = User.builder().id(USER_ID).build();
        hardestCard = CardResponse.fromEntity(Card.builder()
                .user(user)
                .id(1L)
                .front("Front 1")
                .back("Back 1")
                .subject(subject)
                .build());
        mostViewedCard = CardResponse.fromEntity(Card.builder()
                .user(user)
                .id(2L)
                .front("Front 2")
                .back("Back 2")
                .subject(subject)
                .build());

        userStatsResponse = UserStatsResponse.builder()
                .hardestCard(hardestCard)
                .mostViewedCard(mostViewedCard)
                .totalCards(100L)
                .build();
    }

    @Test
    void testGetForUser() throws Exception {
        when(userStatsService.getForUserId(USER_ID)).thenReturn(userStatsResponse);

        String contentAsString = mockMvc.perform(get(ENDPOINT)
                        .param("userId", USER_ID.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserStatsResponse returned = new ObjectMapper().readValue(contentAsString, UserStatsResponse.class);

        assertThat(returned.hardestCard()).isEqualTo(hardestCard);
        assertThat(returned.mostViewedCard()).isEqualTo(mostViewedCard);
        assertThat(returned.totalCards()).isEqualTo(100L);
    }

    @Test
    void testGetForUser_throwsExceptionIfUserIdNotValid() throws Exception {
        when(userStatsService.getForUserId(USER_ID)).thenThrow(new IllegalArgumentException("Invalid user id") );

        mockMvc.perform(get(ENDPOINT)
                        .param("userId", "invalid"))
                .andExpect(status().isBadRequest());
    }


}