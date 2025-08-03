package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import lombok.Builder;

@Builder
public record CardResponse(Long id, String front, String back) {
    public static CardResponse fromEntity(Card card) {
        return new CardResponse(card.getId(), card.getFront(), card.getBack());
    }
}