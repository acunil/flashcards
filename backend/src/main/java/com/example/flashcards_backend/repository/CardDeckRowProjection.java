package com.example.flashcards_backend.repository;

import java.time.LocalDateTime;

public interface CardDeckRowProjection {
    Long getCardId();
    String getFront();
    String getBack();
    String getHintFront();
    String getHintBack();

    // deck may be null
    Long getDeckId();
    String getDeckName();

    // history fields may be null
    Double getAvgRating();
    Integer getViewCount();
    LocalDateTime getLastViewed();
    Integer getLastRating();

    // subject is not null
    String getSubjectName();
    Long getSubjectId();
}