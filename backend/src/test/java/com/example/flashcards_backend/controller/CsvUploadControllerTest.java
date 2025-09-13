package com.example.flashcards_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.integration.AbstractIntegrationTest;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class CsvUploadControllerTest extends AbstractIntegrationTest {

  private static final String PATH = "/csv";

  private RequestPostProcessor jwt;

  @Autowired private SubjectRepository subjectRepository;
  @Autowired private ObjectMapper objectMapper;
  Subject subjectOne;

  @BeforeEach
  void setUp() {
    jwt = jwtForTestUser();
    subjectOne = Subject.builder().user(testUser).name("Subject 1").build();
    subjectRepository.saveAndFlush(subjectOne);
  }

  @Test
  void uploadCsv_noFileProvided_returnsBadRequestAndLogsError() throws Exception {
    MockMultipartFile empty =
        new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
    mockMvc
        .perform(multipart(PATH + "/" + subjectOne.getId()).file(empty).with(jwt))
        .andExpect(status().isBadRequest());
  }

  @Test
  void uploadCsv_invalidFileFormat_returnsBadRequest() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "test.csv", "text/csv", "invalid,format,file".getBytes());
    mockMvc
        .perform(
            multipart(PATH + "/" + subjectOne.getId())
                .file(file)
                .with(jwt)
                .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void uploadCsv_validFile_returnsOkWithResponseBody() throws Exception {
    String csvContents =
        """
        front,back,hint_front,hint_back,decks
        a,b,,,
        c,d,,,d1
        withHintF,withHintB,h1,h2,d1;d2
        """;
    MockMultipartFile file =
        new MockMultipartFile("file", "test.csv", "text/csv", csvContents.getBytes());
    ResultActions response =
        mockMvc
            .perform(
                multipart(PATH + "/" + subjectOne.getId())
                    .file(file)
                    .with(jwt)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.saved").isArray())
            .andExpect(jsonPath("$.duplicates").isArray());
    String responseBody = response.andReturn().getResponse().getContentAsString();
    CsvUploadResponseDto responseDto =
        objectMapper.readValue(responseBody, CsvUploadResponseDto.class);
    assertThat(responseDto.saved()).hasSize(3);
    assertThat(responseDto.duplicates()).isEmpty();
    assertThat(responseDto.saved())
        .extracting("front")
        .containsExactlyInAnyOrder("a", "c", "withHintF");
    assertThat(responseDto.saved())
        .extracting("back")
        .containsExactlyInAnyOrder("b", "d", "withHintB");
    Optional<CardResponse> withHintF =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("withHintF"))
            .findFirst();
    assertThat(withHintF).isPresent();
    assertThat(withHintF.get().decks()).hasSize(2);
    assertThat(withHintF.get().hintFront()).isEqualTo("h1");
    assertThat(withHintF.get().hintBack()).isEqualTo("h2");

    Optional<CardResponse> a =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("a"))
            .findFirst();
    assertThat(a).isPresent();
    assertThat(a.get().decks()).isEmpty();
    Optional<CardResponse> c =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("c"))
            .findFirst();
    assertThat(c).isPresent();
    assertThat(c.get().decks()).singleElement().extracting("name").isEqualTo("d1");
  }

  @AfterEach
  void tearDown() {
    subjectRepository.deleteAll();
  }
}
