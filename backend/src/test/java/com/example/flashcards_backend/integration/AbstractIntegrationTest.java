package com.example.flashcards_backend.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.SubjectRepository;
import com.example.flashcards_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected UserRepository userRepository;
  @Autowired protected ObjectMapper objectMapper;
  @Autowired protected SubjectRepository subjectRepository;

  protected User testUser;
  protected Subject subject1;
  protected RequestPostProcessor jwt;

  @BeforeEach
  void initTestUser_andSubject() {
    objectMapper.registerModule(new JavaTimeModule());
    // This is the Auth0 id that will go into the JWT
    String auth0Jwt = "auth0|test-user-123";
    testUser =
        User.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
            .username("me")
            .auth0Id(auth0Jwt)
            .isActive(true)
            .build();
    userRepository.save(testUser);
    jwt = jwtForTestUser();
    subject1 = Subject.builder().name("Subject 1").user(testUser).build();
    subjectRepository.save(subject1);
  }

  protected RequestPostProcessor jwtForTestUser() {
    return jwt()
        .jwt(
            jwtB ->
                jwtB.claim(
                        "sub", testUser.getAuth0Id()) // match how CurrentUserService resolves user
                    .claim("preferred_username", testUser.getUsername()));
  }
}
