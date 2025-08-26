package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.UserDto;
import com.example.flashcards_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    public static final String ENDPOINT = "/users";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    void getUsers_returnsUsers() throws Exception {
        UserDto user1 = UserDto.builder()
                .username("user1")
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .isActive(true)
                .build();
        UserDto user2 = UserDto.builder()
                .username("user2")
                .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .isActive(true)
                .build();

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get(ENDPOINT)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

}