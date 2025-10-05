package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.flashcards_backend.repository.CardDeckRowProjection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CardSummary(
        @JsonProperty("id") Long id,
        @JsonProperty("front") String front,
        @JsonProperty("back") String back,
        @JsonProperty("hintFront") String hintFront,
        @JsonProperty("hintBack") String hintBack,
        @JsonProperty("decks") Set<DeckSummary> decks,
        @JsonProperty("avgRating") Double avgRating,
        @JsonProperty("viewCount") Integer viewCount,
        @JsonProperty("lastViewed") String lastViewed,
        @JsonProperty("lastRating") Integer lastRating,
        @JsonProperty("subjectId") Long subjectId
) {

    @JsonCreator
    public CardSummary {
        // canonical constructor; Jackson will call this
    }

    public static CardSummary fromEntity(Card card) {
        Set<DeckSummary> deckSummaries = card.getDecks().stream()
                .map(DeckSummary::fromEntity)
                .collect(Collectors.toSet());

        // TODO fetch real CardHistory from DB
      CardHistory ch = CardHistory.builder()
              .avgRating(420.0)
              .viewCount(420)
              .lastViewed(LocalDateTime.now())
              .lastRating(420)
              .build();

        return new CardSummary(
                card.getId(),
                card.getFront(),
                card.getBack(),
                card.getHintFront(),
                card.getHintBack(),
                deckSummaries,
                ch.getAvgRating(),
                ch.getViewCount(),
                ch.getLastViewed() != null ? ch.getLastViewed().toString() : null,
                ch.getLastRating(),
                card.getSubject().getId()
        );
    }

    public static CardSummary fromEntity(CardDeckRowProjection cd) {
        return new CardSummary(
                cd.getCardId(),
                cd.getFront(),
                cd.getBack(),
                cd.getHintFront(),
                cd.getHintBack(),
                new HashSet<>(),
                cd.getAvgRating(),
                cd.getViewCount(),
                cd.getLastViewed() != null ? cd.getLastViewed().toString() : null,
                cd.getLastRating(),
                cd.getSubjectId()
        );
    }

    public static CardSummary fromEntity(CreateCardResponse ccr) {
        return CardSummary.builder()
                .id(ccr.id())
                .front(ccr.front())
                .back(ccr.back())
                .hintFront(ccr.hintFront())
                .hintBack(ccr.hintBack())
                .decks(new HashSet<>(ccr.decks()))
                .build();
    }
}