package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.service.CurrentUserService;
import com.example.flashcards_backend.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubjectControllerTest {

    public static final String ENDPOINT = "/subjects";
    static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Autowired
    private MockMvc mockMvc;

    private Subject subject1;
    private Subject subject2;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(USER_ID).username("me").build();
        subject1 = Subject.builder().id(1L).name("Subject 1").build();
        subject2 = Subject.builder().id(2L).name("Subject 2").build();
        when(currentUserService.getCurrentUser(any())).thenReturn(user);
    }

    @Test
    void getAllForUserSubjects() throws Exception {
        when(subjectService.findByUserId(USER_ID)).thenReturn(List.of(subject1, subject2));

        mockMvc.perform(get(ENDPOINT)
                .header("Authorization", "Bearer jwt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Subject 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Subject 2"));
    }

    @Test
    void getSubjectByIdFound() throws Exception {
        when(subjectService.findById(1L)).thenReturn(subject1);

        mockMvc.perform(get(ENDPOINT + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Subject 1"));
    }

    @Test
    void getSubjectByIdNotFound() throws Exception {
        when(subjectService.findById(1L)).thenThrow(new SubjectNotFoundException(1L));

        mockMvc.perform(get(ENDPOINT + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSubject() throws Exception {
        when(subjectService.create(any(SubjectRequest.class), any(UUID.class))).thenReturn(subject1);
        mockMvc.perform(post(ENDPOINT)
                        .header("Authorization", "Bearer jwt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Subject 1\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<SubjectRequest> captor = ArgumentCaptor.forClass(SubjectRequest.class);
        verify(subjectService).create(captor.capture(), eq(user));
        assertThat(captor.getValue().name()).isEqualTo("Subject 1");
    }

    @Test
    void createSubjectInvalidInput() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input")).when(subjectService)
                .create(any(SubjectRequest.class), any(User.class));

        mockMvc.perform(post(ENDPOINT)
                        .param("userId", USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    @Test
    void updateSubjectFound() throws Exception {
        Subject updatedSubject = Subject.builder().id(1L).name("Updated Subject 1").build();

        when(subjectService.update(eq(1L), any(SubjectRequest.class))).thenReturn(updatedSubject);

        mockMvc.perform(put(ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Subject 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Subject 1"));
    }

    @Test
    void updateSubjectNotFound() throws Exception {
        when(subjectService.update(eq(1L), any(SubjectRequest.class)))
                .thenThrow(new SubjectNotFoundException(1L));

        mockMvc.perform(put(ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Subject 1\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSubjectFound() throws Exception {
        mockMvc.perform(delete(ENDPOINT + "/1"))
                .andExpect(status().isNoContent());

        verify(subjectService).delete(1L);
    }

    @Test
    void deleteSubjectNotFound() throws Exception {
        doThrow(new SubjectNotFoundException(1L)).when(subjectService).delete(1L);

        mockMvc.perform(delete(ENDPOINT + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Subject not found with id: 1"));
    }

}