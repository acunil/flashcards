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
            description = "Returns all cards.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse[].class)))
    })
    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCardResponses() {
        log.info("GET /cards");
        Instant start = Instant.now();
        var cards = cardService.getAllCardResponses();
        Instant end = Instant.now();
        log.info("GET /cards: returned {} cards in {} seconds", cards.size(), (end.toEpochMilli() - start.toEpochMilli()) / 1000.0);
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
        Card card = cardService.getCardById(id); // throws CardNotFoundException if missing
        return ResponseEntity.ok(CardResponse.fromEntity(card));
    }

    @Operation(summary = "Create a new card", description = "Creates a new card.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Card created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = CreateCardResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CreateCardResponse> createCard(@Valid @RequestBody CardRequest request) {
        var cardCreationResult = cardService.createCard(request);
        CreateCardResponse response = CreateCardResponse
            .builder()
            .id(cardCreationResult.card().getId())
            .front(cardCreationResult.card().getFront())
            .back(cardCreationResult.card().getBack())
            .alreadyExisted(cardCreationResult.alreadyExisted())
            .build();
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

    @Operation(summary = "Delete card", description = "Deletes a card by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Card deleted",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Card not found",
            content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(
        @PathVariable Long id
    ) {
        cardService.deleteCard(id); // throws CardNotFoundException if missing
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