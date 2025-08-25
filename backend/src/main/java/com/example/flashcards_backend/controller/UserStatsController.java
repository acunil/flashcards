package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.UserStatsResponse;
import com.example.flashcards_backend.service.UserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user-stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService service;

    @Operation(summary = "Get user stats", description = "Returns user stats for a given user.")
    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(schema = @Schema(implementation = UserStatsResponse.class), mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @GetMapping
    public UserStatsResponse getForUser(@RequestParam UUID userId) {
        return service.getForUserId(userId);
    }
}