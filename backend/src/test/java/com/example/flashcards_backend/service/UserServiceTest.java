package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.UserDto;
import com.example.flashcards_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void testGetAllUsers() {
        UserDto user = new UserDto("Bob", UUID.randomUUID(), true);
        when(userRepository.findAllUsersAsDtos()).thenReturn(List.of(user));

        List<UserDto> all = userService.findAll();

        assertThat(all).singleElement().isEqualTo(user);
    }

}