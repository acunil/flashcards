package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.annotations.DeckName;
import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckSummary;
import com.example.flashcards_backend.dto.UpdateDeckNameRequest;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.service.CardDeckService;
import com.example.flashcards_backend.service.DeckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/decks")
@Validated
@AllArgsConstructor
public class DeckController {

    private final DeckService deckService;
    private final CardDeckService cardDeckService;

    @Operation(summary = "Get all decks", description = "Returns all decks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckSummary.class)))
    })
    @GetMapping
    public ResponseEntity<Set<DeckSummary>> getAllForSubject(@RequestParam Long subjectId) {
        log.info("GET /decks from subjectId={}", subjectId);
        Set<DeckSummary> responses = deckService.getDeckSummariesBySubjectId(subjectId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get deck by ID", description = "Returns a deck by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckSummary.class))),
        @ApiResponse(responseCode = "404", description = "Deck not found",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeckSummary> getDeckById(@PathVariable Long id) {
        Deck deck = deckService.getDeckById(id);
        return ResponseEntity.ok(DeckSummary.fromEntity(deck));
    }

    @Operation(summary = "Create a new deck with cards",
            description = "Creates a new deck, optionally adding existing cards by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Deck created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckSummary.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Deck with the same name already exists",
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public ResponseEntity<DeckSummary> createDeck(@RequestBody CreateDeckRequest request) {
        Deck createdDeck = cardDeckService.createDeck(request); // Throws DuplicateDeckNameException if a deck with the same name already exists
        return ResponseEntity.status(HttpStatus.CREATED).body(DeckSummary.fromEntity(createdDeck));
    }

    @Operation(summary = "Update deck name", description = "Renames a deck by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deck updated",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckSummary.class))),
        @ApiResponse(responseCode = "404", description = "Deck not found",
            content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeckSummary> updateDeckName(@PathVariable Long id, @RequestBody UpdateDeckNameRequest request) {
        Deck updatedDeck = deckService.renameDeck(id, request.newName());
        return ResponseEntity.ok(DeckSummary.fromEntity(updatedDeck));
    }

    @Operation(summary = "Delete deck", description = "Deletes a deck by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deck deleted",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Deck not found",
            content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get or create decks by names",
            description = "Returns or creates decks by their names, optionally by subject ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckSummary.class)))
    })
    @PostMapping
    public ResponseEntity<Set<DeckSummary>> upsertDecksByNamesAndSubjectId(
            @RequestParam Set<@DeckName String> names,
            @RequestParam(value = "subjectId", required = false) Long subjectId
    ) {
        Set<Deck> decks = cardDeckService.getOrCreateDecksByNamesAndSubjectId(names, subjectId);
        return ResponseEntity.ok(
            decks.stream()
                .map(DeckSummary::fromEntity)
                .collect(Collectors.toSet())
        );
    }

    /* Helpers */
}