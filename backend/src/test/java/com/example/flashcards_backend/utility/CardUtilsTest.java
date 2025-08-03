package com.example.flashcards_backend.utility;

import com.example.flashcards_backend.model.Card;
import org.junit.jupiter.api.Test;

import static com.example.flashcards_backend.testutils.ShuffleTestUtils.assertEventuallyReorders;

import java.util.List;

class CardUtilsTest {

     @Test
     void testShuffleCards() {
        var original = List.of(
            new Card(1L, "Front 1", "Back 1"),
            new Card(2L, "Front 2", "Back 2"),
            new Card(3L, "Front 3", "Back 3")
        );

         assertEventuallyReorders(
             () -> CardUtils.shuffleCards(original),
             original
         );
     }

}