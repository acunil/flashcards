package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import lombok.Builder;

@Builder
public record CardDto(Long id, String front, String back) {
    public static CardDto fromEntity(Card card) {
        return new CardDto(card.getId(), card.getFront(), card.getBack());
    }
}