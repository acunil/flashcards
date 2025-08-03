package com.example.flashcards_backend.utility;

import com.example.flashcards_backend.model.Card;

import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;

public final class CardUtils {

    private CardUtils() {
        // Utility class, prevent instantiation
    }

    public static List<Card> shuffleCards(List<Card> cards) {
        return cards.stream()
            .collect(Collectors.collectingAndThen(
                toList(),
                l -> { shuffle(l); return l; }
            ));
    }
}
