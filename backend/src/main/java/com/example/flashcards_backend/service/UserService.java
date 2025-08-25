package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.UserDto;
import com.example.flashcards_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAllUsersAsDtos();
    }
}
