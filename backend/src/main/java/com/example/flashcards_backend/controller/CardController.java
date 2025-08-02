package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.dto.CardDto;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.service.CardService;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<List<CardDto>> getAll() {
        List<CardDto> cardDtos = cardService.getAll()
            .stream()
            .map(CardDto::fromEntity)
            .toList();
        return ResponseEntity.ok(cardDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getById(@PathVariable Long id) {
        Card card = cardService.getById(id); // throws CardNotFoundException if missing
        return ResponseEntity.ok(CardDto.fromEntity(card));
    }

    @PostMapping
    public ResponseEntity<CardDto> create(@Valid @RequestBody CardDto dto) {
        Card created = cardService.create(dto);
        return ResponseEntity
            .created(URI.create("/api/cards/" + created.getId()))
            .body(CardDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable Long id,
        @Valid @RequestBody CardDto dto
    ) {
        cardService.update(id, dto); // throws CardNotFoundException if missing
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(CardNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}