package com.example.flashcards_backend.dto;

import com.example.flashcards_backend.annotations.CardContent;
import lombok.Builder;

@Builder
public record HintRequest(
        @CardContent String hintFront,
        @CardContent String hintBack
) {
}
