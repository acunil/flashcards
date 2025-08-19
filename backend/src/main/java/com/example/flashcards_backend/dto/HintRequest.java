package com.example.flashcards_backend.dto;

import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record HintRequest(
        @Length(max = 100) String hintFront,
        @Length(max = 100) String hintBack
) {
}
