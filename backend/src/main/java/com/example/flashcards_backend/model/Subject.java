package com.example.flashcards_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "subject")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Setter
public class Subject extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "front_label")
  private String frontLabel;

  @Column(name = "back_label")
  private String backLabel;

  @Enumerated(EnumType.STRING)
  @Column(name = "default_side")
  private Side defaultSide;

  @Column(name = "display_deck_names")
  private Boolean displayDeckNames;

  @Column(name = "card_order")
  @Enumerated(EnumType.STRING)
  private CardOrder cardOrder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  @SuppressWarnings("unused")
  public enum Side {
    FRONT,
    BACK,
    ANY
  }

  @SuppressWarnings("unused")
  public enum CardOrder {
    NEWEST,
    OLDEST,
    RANDOM
  }
}
