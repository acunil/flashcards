package com.example.flashcards_backend.repository;

public interface CardExportRowProjection {
    Long getCardId();
    String getFront();
    String getBack();
    String getHintFront();
    String getHintBack();
    String getDeck();
}
