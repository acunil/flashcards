package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.dto.DeckResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/decks")
@Validated
@AllArgsConstructor
public class DeckController {

    private final DeckService deckService;
    private final CardDeckService cardDeckService;

    @Operation(summary = "Get all decks", description = "Returns all decks.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Set<DeckResponse>> getAll() {
        Set<Deck> decks = deckService.getAll();
        return ResponseEntity.ok(generateResponse(decks));
    }

    @Operation(summary = "Get deck by ID", description = "Returns a deck by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deck not found",
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable Long id) {
        Deck deck = deckService.getDeckById(id);
        return ResponseEntity.ok(generateResponse(deck));
    }

    @Operation(summary = "Create a new deck", description = "Creates a new deck.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Deck created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckResponse.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<DeckResponse> createDeck(@RequestBody CreateDeckRequest request) {
        Deck createdDeck = cardDeckService.createDeck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(generateResponse(createdDeck));
    }

    @Operation(summary = "Update deck name", description = "Renames a deck.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deck updated",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deck not found",
            content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeckName(@PathVariable Long id, @RequestBody UpdateDeckNameRequest request) {
        Deck updatedDeck = deckService.renameDeck(id, request.newName());
        return ResponseEntity.ok(generateResponse(updatedDeck));
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

    @Operation(summary = "Get or create decks by names", description = "Returns or creates decks by their names.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DeckResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Set<DeckResponse>> getDecksByNames(@RequestParam DeckNamesDto deckNamesDto) {
        Set<Deck> decks = cardDeckService.getOrCreateDecksByNames(deckNamesDto);
        return ResponseEntity.ok(generateResponse(decks));
    }

    /* Helpers */

    private static Set<DeckResponse> generateResponse(Set<Deck> decks) {
        return decks.stream()
            .map(DeckResponse::fromEntity)
            .collect(Collectors.toSet());
    }

    private static DeckResponse generateResponse(Deck deck) {
        return DeckResponse.fromEntity(deck);
    }
}