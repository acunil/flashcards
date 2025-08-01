package com.example.flashcards_backend.controller;


import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlashcardController {

    private final CardRepository cardRepository;

    public FlashcardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @GetMapping("/api/cards")
    public List<Card> getCards() {
        return cardRepository.findAll();
    }

    @PostMapping("/api/cards")
    public Card addCard(@RequestBody Card card) {
        return cardRepository.save(card);
    }
}