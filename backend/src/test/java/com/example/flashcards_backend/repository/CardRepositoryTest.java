package com.example.flashcards_backend.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.Map;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)    // or point at your test‐container
class CardRepositoryTest {

    @Autowired
    private CardRepository repo;

    @Test
    void createIfUnique_insertsAndThenReturnsExisting() {
        // First call should insert
        Map<String, Object> first = repo.createIfUnique("A", "B");
        assertThat(first).containsKeys("id", "front", "back");
        Long id1 = ((Number) first.get("id")).longValue();
        assertThat(first.get("front")).isEqualTo("A");
        assertThat(first.get("back")).isEqualTo("B");

        // Second call with the same payload should not create a new row,
        // but return the same id
        Map<String, Object> second = repo.createIfUnique("A", "B");
        Long id2 = ((Number) second.get("id")).longValue();
        assertThat(id2).isEqualTo(id1);
    }
}
