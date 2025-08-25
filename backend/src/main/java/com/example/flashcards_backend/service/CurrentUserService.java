package com.example.flashcards_backend.service;

import com.example.flashcards_backend.exception.UserNotFoundException;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser(Jwt jwt) {
        String auth0Id = jwt.getClaim("sub");
        if (auth0Id == null || auth0Id.isBlank()) {
            throw new IllegalArgumentException("JWT is missing 'sub' claim");
        }

        return userRepository.findByAuth0Id(auth0Id)
                // Throwing exception while we don't want to be able to create new user
                .orElseThrow(() -> new UserNotFoundException(auth0Id));
    }
}
