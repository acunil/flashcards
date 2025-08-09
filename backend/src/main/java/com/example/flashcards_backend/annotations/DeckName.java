package com.example.flashcards_backend.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {}) // no custom validator; composes @Length
@Length(min = 1, max = 60,
    message = "Deck name must be between 1 and 60 characters")
@Target({ ElementType.TYPE_USE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DeckName {
    String message() default "Invalid deck name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}