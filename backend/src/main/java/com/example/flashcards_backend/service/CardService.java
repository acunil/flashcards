package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardDto;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    public Card getById(Long id) {
        return cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
    }

    public Card create(CardDto dto) {
        Card toSave = Card.builder()
            .front(dto.front())
            .back(dto.back())
            .build();
        return cardRepository.save(toSave);
    }

    public void update(Long id, CardDto dto) {
        Card existing = cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
        existing.setFront(dto.front());
        existing.setBack(dto.back());
    }
}
