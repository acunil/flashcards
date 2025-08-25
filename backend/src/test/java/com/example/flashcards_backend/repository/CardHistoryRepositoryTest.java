package com.example.flashcards_backend.repository;

import com.example.flashcards_backend.model.Card;
import com.example.flashcards_backend.model.CardHistory;
import com.example.flashcards_backend.model.Subject;
import com.example.flashcards_backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CardHistoryRepositoryTest {

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Autowired
    CardHistoryRepository cardHistoryRepository;

    @Autowired
    TestEntityManager entityManager;

    User user;
    Card card1;
    Card card2;
    Card card3;
    Subject subject;

    @BeforeEach
    void setUp() {
        // Persist a user
        user = User.builder().username("me").id(ID).build();
        entityManager.persist(user);

        subject = Subject.builder().name("Subject 1").user(user).build();
        entityManager.persist(subject);

        // Create and persist cards
        card1 = Card.builder().user(user).subject(subject)
                .front("f1")
                .back("b1")
                .build();
        card2 = Card.builder().user(user).subject(subject)
                .front("f2")
                .back("b2")
                .build();
        card3 = Card.builder().user(user).subject(subject)
                .front("f3")
                .back("b3")
                .build();

        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.persist(card3);

        // Create and persist histories
        CardHistory ch1 = CardHistory.builder().user(user).avgRating(1.0).viewCount(10).build();
        ch1.setCard(card1);
        CardHistory ch2 = CardHistory.builder().user(user).avgRating(2.0).viewCount(20).build();
        ch2.setCard(card2);
        CardHistory ch3 = CardHistory.builder().user(user).avgRating(3.0).viewCount(30).build();
        ch3.setCard(card3);
        cardHistoryRepository.saveAll(List.of(ch1, ch2, ch3));

        // Ensure we hit the DB, not just the persistence context
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void totalViewCountByUserId_shouldReturnCorrectSum() {
        Long result = cardHistoryRepository.totalViewCountByUserId(ID);
        assertThat(result).isEqualTo(60L);
    }

    @Test
    void totalViewCountByUserId_shouldReturnZeroWhenNoHistory() {
        UUID anotherUserId = UUID.randomUUID();
        Long result = cardHistoryRepository.totalViewCountByUserId(anotherUserId);
        assertThat(result).isZero();
    }
}
