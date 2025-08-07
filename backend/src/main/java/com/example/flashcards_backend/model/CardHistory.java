package com.example.flashcards_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_history")
@Data
public class CardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "avg_rating")
    private double avgRating;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "last_viewed")
    private LocalDateTime lastViewed;

    @Column(name = "last_rating")
    private Integer lastRating;

    public void setCard(Card card) {
        this.card = card;
        if (card != null) {
            card.getCardHistories().add(this);
        }
    }
}