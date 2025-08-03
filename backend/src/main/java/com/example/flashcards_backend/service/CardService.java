package com.example.flashcards_backend.service;

import com.example.flashcards_backend.dto.CardRequest;
import com.example.flashcards_backend.exception.CardNotFoundException;
import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.flashcards_backend.utility.CardUtils.shuffleCards;

import java.util.List;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardHistoryService cardHistoryService;

    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    public List<Card> getAll(boolean shuffled) {
        var cards = getAll();
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }

    public Card getById(Long id) {
        return cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Transactional
    public Card create(CardRequest request) {
        Card toSave = Card.builder()
            .front(request.front())
            .back(request.back())
            .build();
        return cardRepository.save(toSave);
    }

    @Transactional
    public void update(Long id, CardRequest request) {
        Card existing = cardRepository.findById(id)
            .orElseThrow(() -> new CardNotFoundException(id));
        existing.setFront(request.front());
        existing.setBack(request.back());
    }

    @Transactional
    public void rate(Long cardId, int rating) {
        getById(cardId); // validate card exists
        cardHistoryService.recordRating(cardId, rating);
    }

    public List<Card> getByMinAvgRating(double threshold) {
        return cardRepository.findByMinAvgRating(threshold);
    }

    public List<Card> getByMinAvgRating(double threshold, boolean shuffled) {
        var cards = getByMinAvgRating(threshold);
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }

    public List<Card> getByMaxAvgRating(double threshold) {
        return cardRepository.findByMaxAvgRating(threshold);
    }

    public List<Card> getByMaxAvgRating(double threshold, boolean shuffled) {
        var cards = getByMaxAvgRating(threshold);
        return shuffled
               ? shuffleCards(cards)
               : cards;
    }
}
