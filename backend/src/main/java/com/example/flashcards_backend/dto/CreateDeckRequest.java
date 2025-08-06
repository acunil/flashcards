package com.example.flashcards_backend.dto;

import java.util.Arrays;
import java.util.Set;

public record CreateDeckRequest(String name, Set<Long> cardIds) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateDeckRequest(String name1, Set<Long> ids))) return false;
        return name.equals(name1) && Arrays.equals(cardIds.toArray(), ids.toArray());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(cardIds.toArray());
        return result;
    }

    @Override
    public String toString() {
        return "CreateDeckRequest{" +
                "name='" + name + '\'' +
                ", cardIds=" + cardIds +
                '}';
    }
}