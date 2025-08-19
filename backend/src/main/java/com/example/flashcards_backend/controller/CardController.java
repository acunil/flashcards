package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.*;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.service.CardHistoryService;
import com.example.flashcards_backend.service.CardService;
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

    @Operation(summary = "Get all cards",
            description = "Returns all cards, optionally with subject ID specified.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse[].class)))
    })
    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCardResponses(@RequestParam(required = false) Long subjectId) {
        log.info("GET /cards: subjectId={}", subjectId);
        Instant start = Instant.now();
        var cards = cardService.getAllCardResponsesFromSubject(subjectId);
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
    @PutMapping("/{id}/rate")
    public ResponseEntity<Void> rate(
        @PathVariable Long id,
        @RequestParam @Min(1) @Max(5) int rating
    ) {
        cardHistoryService.recordRating(id, rating); // throws CardNotFoundException if missing
                                                    // throws DataAccessException if database error occurs
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get cards by minimum average rating",
            description = "Returns cards with an average rating above a threshold.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CardResponse[].class))),
        @ApiResponse(responseCode = "400", description = "Bad request, invalid rating threshold",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/minAvgRating")
    public ResponseEntity<List<CardResponse>> getByMinAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold,
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getCardsByMinAvgRating(threshold, shuffled);

        return ResponseEntity.ok(generateResponse(cards));
    }

    @Operation(summary = "Get cards by maximum average rating",
            description = "Returns cards with an average rating below a threshold.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CardResponse[].class))),
        @ApiResponse(responseCode = "400", description = "Bad request, invalid rating threshold",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/maxAvgRating")
    public ResponseEntity<List<CardResponse>> getByMaxAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold,
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getCardsByMaxAvgRating(threshold, shuffled);

        return ResponseEntity.ok(generateResponse(cards));
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

    /* Helpers */

    private static List<CardResponse> generateResponse(List<Card> cards) {
        return cards
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
    }

}