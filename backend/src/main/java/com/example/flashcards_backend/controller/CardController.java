package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.model.User;
import com.example.flashcards_backend.service.CardHistoryService;
import com.example.flashcards_backend.service.CardService;
import com.example.flashcards_backend.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/cards")
@Validated
@AllArgsConstructor
@Slf4j
public class CardController {

    public static final String REQUEST_MAPPING = "/cards/";
    private final CardService cardService;
    private final CardHistoryService cardHistoryService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Get all cards",
            description = "Returns all cards, optionally with subject ID specified.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse[].class)))
    })
    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCardResponses(
            @RequestParam Long subjectId,
            @AuthenticationPrincipal Jwt jwt
            ) {
        log.info("GET /cards: subjectId={}", subjectId);
        User user = currentUserService.getOrCreateCurrentUser(jwt);
        Instant start = Instant.now();
        var cards = cardService.getAllCardResponsesForUserAndSubject(user, subjectId);
        Instant end = Instant.now();
        double duration = (end.toEpochMilli() - start.toEpochMilli()) / 1000.0;
        log.info("GET /cards: returned {} cards in {} seconds", cards.size(), duration);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Get card by ID", description = "Returns a card by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardResponseById(id)); // throws CardNotFoundException if missing
    }

    @Operation(summary = "Create a new card", description = "Creates a new card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateCardResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CreateCardResponse> createCard(@Valid @RequestBody CardRequest request) {
        CreateCardResponse response = cardService.createCard(request);
        return ResponseEntity
                .created(URI.create(REQUEST_MAPPING + response.id()))
                .body(response);
    }

    @Operation(summary = "Create multiple new cards", description = "Creates multiple new cards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cards created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateCardResponse[].class)))
    })
    @PostMapping("/multiple")
    public ResponseEntity<List<CreateCardResponse>> createCards(@Valid @RequestBody List<CardRequest> requests) {
        List<CreateCardResponse> responses = cardService.createCards(requests);
        return ResponseEntity
                .created(URI.create(REQUEST_MAPPING + "multiple"))
                .body(responses);
    }

    @Operation(summary = "Update card",
            description = "Updates a card by its ID. Existing properties are entirely overwritten by those provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card updated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody CardRequest request
    ) {
        cardService.updateCard(id, request); // throws CardNotFoundException if missing
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rate card", description = "Rates a card by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card rated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Database error",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/{id}/rate")
    public ResponseEntity<Void> rate(
            @PathVariable Long id,
            @RequestParam @Min(1) @Max(5) int rating
    ) {
        cardHistoryService.recordRating(id, rating); // throws CardNotFoundException if missing
        // throws DataAccessException if database error occurs
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete cards", description = "Deletes cards by their IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card(s) deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteCards(
            @RequestBody @NotEmpty List<Long> ids
    ) {
        log.info("DELETE /cards: deleting {} cards", ids.size());
        cardService.deleteCards(ids); // throws CardNotFoundException if any are missing
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Update card hints", description = "Updates card hints by card ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hints saved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Card not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping(value = "/{id}/hints")
    public ResponseEntity<CardResponse> updateCardHints(@RequestBody HintRequest request, @PathVariable Long id) {
        CardResponse response = cardService.setHints(request, id);
        return ResponseEntity.ok(response);
    }

}