package com.example.flashcards_backend.utility;

import com.example.flashcards_backend.model.Card;
import org.junit.jupiter.api.Test;

import static com.example.flashcards_backend.testutils.ShuffleTestUtils.assertEventuallyReorders;

import java.util.List;

class CardUtilsTest {

     @Test
     void testShuffleCards() {
        var original = List.of(
            Card.builder().id(1L).build(),
            Card.builder().id(2L).build(),
            Card.builder().id(3L).build()
        );

         assertEventuallyReorders(
             () -> CardUtils.shuffleCards(original),
             original
         );
     }

}