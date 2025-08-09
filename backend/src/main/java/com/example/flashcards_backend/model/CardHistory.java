package com.example.flashcards_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_history")
public class CardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    @JsonBackReference
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