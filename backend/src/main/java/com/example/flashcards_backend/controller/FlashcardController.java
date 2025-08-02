package com.example.flashcards_backend.controller;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlashcardController {

    private final CardRepository cardRepository;

    public FlashcardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @GetMapping("/cards")
    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    @PostMapping("/cards")
    public Card addCard(@RequestBody Card card) {
        return cardRepository.save(card);
    }
}