package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.SubjectRequest;
import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubjectControllerTest extends AbstractIntegrationTest {

  @Autowired private SubjectRepository subjectRepository;

  @BeforeEach
  void setUp() {
    subjectRepository.save(Subject.builder().name("Subject 2").user(testUser).build());
  }

  @Test
  void getAllForUserSubjects() throws Exception {
    mockMvc
        .perform(get("/subjects").with(jwt))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name").value("Subject 1"))
        .andExpect(jsonPath("$[1].name").value("Subject 2"));
  }

  @Test
  void createSubject() throws Exception {
    SubjectRequest request = SubjectRequest.builder().name("New Subject").build();
    String requestJson = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(
            post("/subjects")
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", containsString("/subjects/")))
        .andExpect(jsonPath("$.name").value("New Subject"));
  }

  @Test
  void getSubjectById() throws Exception {
    mockMvc
        .perform(get("/subjects/" + subject1.getId()).with(jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Subject 1"));
  }

  @Test
  void getSubjectById_NotFound() throws Exception {
    mockMvc
        .perform(get("/subjects/999").with(jwt))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Subject not found with id: 999"));
  }

  @Test
  void updateSubject() throws Exception {
    SubjectRequest request = SubjectRequest.builder().name("New Name")
        .backLabel("New Back")
        .frontLabel("New Front")
        .displayDeckNames(true)
        .defaultSide(Subject.Side.BACK)
        .cardOrder(Subject.CardOrder.OLDEST)
        .build();
    String content = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/subjects/" + subject1.getId())
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isOk());
    assertThat(subject1.getName()).isEqualTo("New Name");
    assertThat(subject1.getBackLabel()).isEqualTo("New Back");
    assertThat(subject1.getFrontLabel()).isEqualTo("New Front");
    assertThat(subject1.getDisplayDeckNames()).isTrue();
    assertThat(subject1.getDefaultSide()).isEqualTo(Subject.Side.BACK);
    assertThat(subject1.getCardOrder()).isEqualTo(Subject.CardOrder.OLDEST);
  }

  @Test
  void deleteSubject() throws Exception {
    mockMvc
        .perform(delete("/subjects/" + subject1.getId()).with(jwt))
        .andExpect(status().isNoContent());
    assertThat(subjectRepository.findById(1L)).isEmpty();
  }
}
