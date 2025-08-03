package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.service.CardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Validated
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    @CrossOrigin("http://localhost:5173")
    public ResponseEntity<List<CardResponse>> getAll() {
        List<CardResponse> cardResponses = cardService.getAll()
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getById(@PathVariable Long id) {
        Card card = cardService.getById(id); // throws CardNotFoundException if missing
        return ResponseEntity.ok(CardResponse.fromEntity(card));
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardRequest request) {
        Card created = cardService.create(request);
        return ResponseEntity
            .created(URI.create("/api/cards/" + created.getId()))
            .body(CardResponse.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable Long id,
        @Valid @RequestBody CardRequest request
    ) {
        cardService.update(id, request); // throws CardNotFoundException if missing
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin("http://localhost:5173")
    @PostMapping("/{id}/rate")
    public ResponseEntity<Void> rate(
        @PathVariable Long id,
        @RequestParam @Min(1) @Max(5) int rating
    ) {
        cardService.rate(id, rating);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @RequestMapping("/minAvgRating")
    public ResponseEntity<List<CardResponse>> getByMinAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold
    ) {
        List<CardResponse> cardResponses = cardService.getByMinAvgRating(threshold)
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    @GetMapping
    @RequestMapping("/maxAvgRating")
    public ResponseEntity<List<CardResponse>> getByMaxAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold
    ) {
        List<CardResponse> cardResponses = cardService.getByMaxAvgRating(threshold)
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CardNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}