package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.UserNotFoundException;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getOrCreateCurrentUser(Jwt jwt) {
        String auth0Id = jwt.getClaim("sub");
        if (auth0Id == null || auth0Id.isBlank()) {
            throw new IllegalArgumentException("JWT is missing 'sub' claim");
        }

        log.info("Looking up user with auth0Id '{}'", auth0Id);
        User user = userRepository.findByAuth0Id(auth0Id)
                // Throwing exception while we don't want to be able to create new user
                .orElseThrow(() -> new UserNotFoundException(auth0Id));
        log.info("Found user with userId '{}'", user.getId());
        return user;
    }
}
