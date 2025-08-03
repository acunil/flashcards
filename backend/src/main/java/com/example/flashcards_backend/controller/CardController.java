package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.dto.CardResponse;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.service.CardService;
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

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CardNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}