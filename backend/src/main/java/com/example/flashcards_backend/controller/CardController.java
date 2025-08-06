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

import static com.example.flashcards_backend.utility.CardUtils.shuffleCards;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Validated
@AllArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public List<CardResponse> getAll(
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getAllCards(shuffled);

        return cards.stream()
            .map(CardResponse::fromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getById(@PathVariable Long id) {
        Card card = cardService.getCardById(id); // throws CardNotFoundException if missing
        return ResponseEntity.ok(CardResponse.fromEntity(card));
    }

    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardRequest request) {
        var cardCreationResult = cardService.createCard(request);
        CardResponse response = CardResponse
            .builder()
            .id(cardCreationResult.card().getId())
            .front(cardCreationResult.card().getFront())
            .back(cardCreationResult.card().getBack())
            .alreadyExisted(cardCreationResult.alreadyExisted())
            .build();
        return ResponseEntity
            .created(URI.create("/api/cards/" + response.id()))
            .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
        @PathVariable Long id,
        @Valid @RequestBody CardRequest request
    ) {
        cardService.updateCard(id, request); // throws CardNotFoundException if missing
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<Void> rate(
        @PathVariable Long id,
        @RequestParam @Min(1) @Max(5) int rating
    ) {
        cardService.rateCard(id, rating);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @RequestMapping("/minAvgRating")
    public ResponseEntity<List<CardResponse>> getByMinAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold,
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getCardsByMinAvgRating(threshold, shuffled);

        List<CardResponse> cardResponses = cards
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    @GetMapping
    @RequestMapping("/maxAvgRating")
    public ResponseEntity<List<CardResponse>> getByMaxAvgRating(
        @RequestParam @Min(1) @Max(5) double threshold,
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getCardsByMaxAvgRating(threshold, shuffled);

        List<CardResponse> cardResponses = cards
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    @GetMapping
    @RequestMapping("/deck/{deckId}")
    public ResponseEntity<List<CardResponse>> getCardsByDeckId(
        @PathVariable Long deckId,
        @RequestParam(name = "shuffled", defaultValue = "false") boolean shuffled
    ) {
        var cards = cardService.getCardsByDeckId(deckId);
        if (shuffled) {
            cards = shuffleCards(cards);
        }

        List<CardResponse> cardResponses = cards
            .stream()
            .map(CardResponse::fromEntity)
            .toList();
        return ResponseEntity.ok(cardResponses);
    }

    /*Exception Handlers*/

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CardNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }
}