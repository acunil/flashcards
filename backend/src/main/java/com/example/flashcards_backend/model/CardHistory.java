package com.example.flashcards_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Table(name = "card_history")
public class CardHistory extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "card_id", nullable = false)
  @JsonBackReference
  @ToString.Exclude
  private Card card;

  @Column(name = "avg_rating")
  private double avgRating;

  @Column(name = "view_count")
  private Integer viewCount;

  @Column(name = "last_viewed")
  private LocalDateTime lastViewed;

  @Column(name = "last_rating")
  private Integer lastRating;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
