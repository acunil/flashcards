package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubjectControllerTest extends AbstractIntegrationTest {

    @Autowired
    private SubjectRepository subjectRepository;

    Subject subjectOne;

    @BeforeEach
    void seedSubjects() {
        // Clear any existing data (optional if @Transactional is used)
        subjectRepository.deleteAll();

        subjectOne = Subject.builder()
                .name("Subject 1")
                .user(testUser)
                .build();
        subjectRepository.save(subjectOne);

        subjectRepository.save(Subject.builder()
                .name("Subject 2")
                .user(testUser)
                .build());
    }

    @Test
    void getAllForUserSubjects() throws Exception {
        mockMvc.perform(get("/subjects").with(jwtForTestUser()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Subject 1"))
                .andExpect(jsonPath("$[1].name").value("Subject 2"));
    }

    @Test
    void createSubject() throws Exception {
        String requestJson = """
                {
                  "name": "New Subject"
                }
                """;

        mockMvc.perform(post("/subjects")
                        .with(jwtForTestUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/subjects/")))
                .andExpect(jsonPath("$.name").value("New Subject"));
    }

    @Test
    void getSubjectById() throws Exception {
        mockMvc.perform(get("/subjects/" + subjectOne.getId())
                        .with(jwtForTestUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Subject 1"));
    }

    @Test
    void getSubjectById_NotFound() throws Exception {
        mockMvc.perform(get("/subjects/999").with(jwtForTestUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Subject not found with id: 999"));
    }

    @Test
    void updateSubject() throws Exception {
        String content = """
                {
                  "name": "NewName",
                  "backLabel": "NewBack",
                  "frontLabel": "NewFront",
                  "displayDeckNames": true,
                  "defaultSide": "BACK"
                }
                """;

        mockMvc.perform(put("/subjects/" + subjectOne.getId())
                        .with(jwtForTestUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
        assertThat(subjectOne.getName()).isEqualTo("NewName");
        assertThat(subjectOne.getBackLabel()).isEqualTo("NewBack");
        assertThat(subjectOne.getFrontLabel()).isEqualTo("NewFront");
        assertThat(subjectOne.getDisplayDeckNames()).isTrue();
        assertThat(subjectOne.getDefaultSide()).isEqualTo(Subject.Side.BACK);
    }

    @Test
    void deleteSubject() throws Exception {
        mockMvc.perform(delete("/subjects/" + subjectOne.getId())
                        .with(jwtForTestUser()))
                .andExpect(status().isNoContent());
        assertThat(subjectRepository.findById(1L)).isEmpty();
    }
}
