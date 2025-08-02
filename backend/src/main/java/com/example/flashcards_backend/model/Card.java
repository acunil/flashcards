package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "card", uniqueConstraints = @UniqueConstraint(columnNames = {"front", "back"}))
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "German vocab")
    private String front;

    @Column(columnDefinition = "English translation")
    private String back;
}