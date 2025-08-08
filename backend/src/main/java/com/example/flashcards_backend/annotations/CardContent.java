package com.example.flashcards_backend.annotations;

import org.hibernate.validator.constraints.Length;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Length(min = 1, max = 100, message = "Card content must be between 1 and 100 characters")
@Target(ElementType.TYPE_USE)
public @interface CardContent {
}
