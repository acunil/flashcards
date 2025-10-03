package com.example.flashcards_backend.integration;

import com.example.flashcards_backend.dto.CsvUploadResponseDto;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.CardRepository;
import com.example.flashcards_backend.repository.DeckRepository;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.example.flashcards_backend.repository.UserRepository;
import com.example.flashcards_backend.service.CurrentUserService;
import config.TestSecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class CsvUploadIT {

  public static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  private Long subjectId;

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private SubjectRepository subjectRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private CardRepository cardRepository;

  @Autowired private DeckRepository deckRepository;

  @Autowired private CurrentUserService currentUserService;

  @BeforeEach
  void setUp() {
    clearDatabase();
    // Create a test user
    User testUser =
        User.builder().username("testuser").auth0Id("auth0|test-user-id").id(ID).build();
    userRepository.saveAndFlush(testUser);

    Subject subject = Subject.builder().name("German").user(testUser).build();
    subjectRepository.saveAndFlush(subject);

    Subject persistedSubject =
        subjectRepository
            .findByName(subject.getName())
            .orElseThrow(
                () -> new RuntimeException("Subject not found with name: " + subject.getName()));

    subjectId = persistedSubject.getId();
  }

  @Test
  void uploadCsv_fileOnClasspath_returns200() {
    String uri =
        UriComponentsBuilder.fromUriString("http://localhost")
            .port(port)
            .path("/csv/{id}")
            .buildAndExpand(subjectId)
            .toUriString();
    ClassPathResource resource = new ClassPathResource("csv/vocab_upload_1.csv");

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);

    ResponseEntity<CsvUploadResponseDto> response =
        restTemplate.postForEntity(
            uri, new HttpEntity<>(body, createMultipartHeaders()), CsvUploadResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().saved()).isNotNull();
  }

  @AfterEach
  void tearDown() {
    clearDatabase();
  }

  private HttpHeaders createMultipartHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.setBearerAuth("test-token"); // will be accepted by TestSecurityConfig
    return headers;
  }

  void clearDatabase() {
    cardRepository.deleteAll();
    deckRepository.deleteAll();
    subjectRepository.deleteAll();
    userRepository.deleteAll();
  }
}
