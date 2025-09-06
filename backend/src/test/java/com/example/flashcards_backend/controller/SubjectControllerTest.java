package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubjectControllerIT extends AbstractIntegrationTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @BeforeEach
    void seedSubjects() {
        // Clear any existing data (optional if @Transactional is used)
        subjectRepository.deleteAll();

        subjectRepository.save(Subject.builder()
                .name("Subject 1")
                .user(testUser)
                .build());

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
}
