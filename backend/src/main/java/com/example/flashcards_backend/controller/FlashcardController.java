package com.example.flashcards_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlashcardController {
    @GetMapping("/api/cards")
    public String getCard() {
        return "{\"id\": 1, \"front\": \"Hello\", \"back\": \"Hola\"}";
    }
}