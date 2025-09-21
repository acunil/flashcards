package com.example.flashcards_backend.integration;

import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected UserRepository userRepository;
  @Autowired protected ObjectMapper objectMapper;

  protected User testUser;
  protected RequestPostProcessor jwt;


  @BeforeEach
  void initTestUser() {
    // This is the Auth0 subject that will go into the JWT
    String auth0Jwt = "auth0|test-user-123";

    testUser =
        userRepository.save(
            User.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .username("me")
                .auth0Id(auth0Jwt)
                .isActive(true)
                .build());
    jwt = jwtForTestUser();
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
