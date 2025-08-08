package com.example.flashcards_backend.annotations;

import org.hibernate.validator.constraints.Length;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Length(min = 1, max = 60, message = "Deck name must be between 1 and 60 characters")
public @interface DeckName {
}
