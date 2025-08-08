package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CardResponse(
    @JsonProperty("id")         Long    id,
    @JsonProperty("front")      String  front,
    @JsonProperty("back")       String  back,
    @JsonProperty("deckNames")  Set<String> deckNames,
    @JsonProperty("avgRating")  Double  avgRating,
    @JsonProperty("viewCount")  Integer viewCount,
    @JsonProperty("lastViewed") String  lastViewed,
    @JsonProperty("lastRating") Integer lastRating
) {

    @JsonCreator
    public CardResponse {
        // canonical constructor; Jackson will call this
    }

    public static CardResponse fromEntity(Card card) {
        CardHistory ch = card.getCardHistories()
            .stream()
            .findFirst()
            .orElseGet(CardHistory::new);

        return new CardResponse(
            card.getId(),
            card.getFront(),
            card.getBack(),
            card.getDeckNames(),
            ch.getAvgRating(),
            ch.getViewCount(),
            ch.getLastViewed() != null ? ch.getLastViewed().toString() : null,
            ch.getLastRating()
        );
    }
}