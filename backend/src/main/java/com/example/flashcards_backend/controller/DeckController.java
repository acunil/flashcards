package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CreateDeckRequest;
import com.example.flashcards_backend.dto.DeckNamesDto;
import com.example.flashcards_backend.dto.DeckResponse;
import com.example.flashcards_backend.dto.UpdateDeckRequest;
import com.example.flashcards_backend.exception.DeckNotFoundException;
import com.example.flashcards_backend.model.Deck;
import com.example.flashcards_backend.service.DeckService;
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

    @GetMapping
    public ResponseEntity<Set<DeckResponse>> getAll() {
        Set<Deck> decks = deckService.getAll();
        Set<DeckResponse> response = decks.stream()
            .map(DeckResponse::fromEntity)
            .collect(Collectors.toSet());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable Long id) {
        Deck deck = deckService.getDeckById(id);
        DeckResponse response = DeckResponse.fromEntity(deck);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<DeckResponse> createDeck(@RequestBody CreateDeckRequest request) {
        Deck createdDeck = deckService.createDeck(request);
        DeckResponse response = DeckResponse.fromEntity(createdDeck);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeckResponse> updateDeck(@PathVariable Long id, @RequestBody UpdateDeckRequest request) {
        Deck updatedDeck = deckService.renameDeck(id, request.newName());
        DeckResponse response = DeckResponse.fromEntity(updatedDeck);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<Set<DeckResponse>> getDecksByCardId(@PathVariable Long cardId) {
        Set<Deck> decks = deckService.getDecksByCardId(cardId);
        Set<DeckResponse> response = decks.stream()
            .map(DeckResponse::fromEntity)
            .collect(Collectors.toSet());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cards")
    public ResponseEntity<Set<DeckResponse>> getDecksByNames(@RequestParam DeckNamesDto deckNamesDto) {
        Set<Deck> decks = deckService.getOrCreateDecksByNames(deckNamesDto);
        Set<DeckResponse> response = decks.stream()
            .map(DeckResponse::fromEntity)
            .collect(Collectors.toSet());
        return ResponseEntity.ok(response);
    }

    /*Exception Handlers*/

    @ExceptionHandler(DeckNotFoundException.class)
    public ResponseEntity<String> handleDeckNotFoundException(DeckNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}