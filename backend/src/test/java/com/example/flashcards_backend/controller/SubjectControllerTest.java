package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.SubjectDto;
import com.example.flashcards_backend.exception.SubjectNotFoundException;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
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
class SubjectControllerTest {

    public static final String ENDPOINT = "/subjects";
    static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @MockitoBean
    private SubjectService subjectService;

    @Autowired
    private MockMvc mockMvc;

    private Subject subject1;
    private Subject subject2;

    @BeforeEach
    void setUp() {
        subject1 = Subject.builder().id(1L).name("Subject 1").build();
        subject2 = Subject.builder().id(2L).name("Subject 2").build();
    }

    @Test
    void getAllForUserSubjects() throws Exception {

        when(subjectService.findByUserId(USER_ID)).thenReturn(List.of(subject1, subject2));

        mockMvc.perform(get(ENDPOINT).param("userId", USER_ID.toString()))
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
        mockMvc.perform(post(ENDPOINT)
                        .param("userId", USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Subject 3\"}"))
                .andExpect(status().isOk());

        ArgumentCaptor<SubjectDto> captor = ArgumentCaptor.forClass(SubjectDto.class);
        verify(subjectService).create(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("Subject 3");
    }

    @Test
    void createSubjectInvalidInput() throws Exception {
        doThrow(new IllegalArgumentException("Invalid input")).when(subjectService).create(any(SubjectDto.class));

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

        when(subjectService.update(eq(1L), any(SubjectDto.class))).thenReturn(updatedSubject);

        mockMvc.perform(put(ENDPOINT + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Subject 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Subject 1"));
    }

    @Test
    void updateSubjectNotFound() throws Exception {
        when(subjectService.update(eq(1L), any(SubjectDto.class)))
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