package com.example.flashcards_backend.repository;

import java.util.List;

public interface CardExportProjection {
    String getFront();
    String getBack();
    String getHintFront();
    String getHintBack();
    List<String> getDecks(); // or List<String> if you prefer
}
