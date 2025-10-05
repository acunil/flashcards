package com.example.flashcards_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.flashcards_backend.dto.CardSummary;
import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.integration.AbstractIntegrationTest;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

class CsvUploadControllerTest extends AbstractIntegrationTest {

  private static final String PATH = "/csv";

  @Test
  void uploadCsv_noFileProvided_returnsBadRequestAndLogsError() throws Exception {
    MockMultipartFile empty =
        new MockMultipartFile("file", "", MediaType.TEXT_PLAIN_VALUE, new byte[0]);
    mockMvc
        .perform(multipart(PATH + "/" + subject1.getId()).file(empty).with(jwt))
        .andExpect(status().isBadRequest());
  }

  @Test
  void uploadCsv_invalidFileFormat_returnsBadRequest() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "test.csv", "text/csv", "invalid,format,file".getBytes());
    mockMvc
        .perform(
            multipart(PATH + "/" + subject1.getId())
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
                multipart(PATH + "/" + subject1.getId())
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
    Optional<CardSummary> withHintF =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("withHintF"))
            .findFirst();
    assertThat(withHintF).isPresent();
    assertThat(withHintF.get().decks()).hasSize(2);
    assertThat(withHintF.get().hintFront()).isEqualTo("h1");
    assertThat(withHintF.get().hintBack()).isEqualTo("h2");

    Optional<CardSummary> a =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("a"))
            .findFirst();
    assertThat(a).isPresent();
    assertThat(a.get().decks()).isEmpty();
    Optional<CardSummary> c =
        responseDto.saved().stream()
            .filter(cardResponse -> cardResponse.front().equals("c"))
            .findFirst();
    assertThat(c).isPresent();
    assertThat(c.get().decks()).singleElement().extracting("name").isEqualTo("d1");
  }

}
