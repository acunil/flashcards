package com.example.flashcards_backend.repository;

public interface CardDeckRowProjection {
    Long getCardId();
    String getFront();
    String getBack();

    // deck may be null
    Long getDeckId();
    String getDeckName();

    // history fields may be null
    Double getAvgRating();
    Integer getViewCount();
    String getLastViewed();
    Integer getLastRating();
}