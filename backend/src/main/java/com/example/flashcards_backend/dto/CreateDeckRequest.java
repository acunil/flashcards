package com.example.flashcards_backend.dto;

import java.util.Arrays;

public record CreateDeckRequest(String name, Long[] cardIds) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateDeckRequest(String name1, Long[] ids))) return false;
        return name.equals(name1) && Arrays.equals(cardIds, ids);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(cardIds);
        return result;
    }

    @Override
    public String toString() {
        return "CreateDeckRequest{" +
            "name='" + name + '\'' +
            ", cardIds=" + Arrays.toString(cardIds) +
            '}';
    }
}