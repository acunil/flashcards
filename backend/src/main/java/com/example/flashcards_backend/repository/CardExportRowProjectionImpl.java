package com.example.flashcards_backend.repository;

public class CardExportRowProjectionImpl implements CardExportRowProjection {

  private final Long cardId;
  private final String front;
  private final String back;
  private final String hintFront;
  private final String hintBack;
  private final String deck;

  public CardExportRowProjectionImpl(
      Long cardId,
      String front,
      String back,
      String hintFront,
      String hintBack,
      String deck) {
    this.cardId = cardId;
    this.front = front;
    this.back = back;
    this.hintFront = hintFront;
    this.hintBack = hintBack;
    this.deck = deck;
  }

  @Override public Long getCardId() { return cardId; }
  @Override public String getFront() { return front; }
  @Override public String getBack() { return back; }
  @Override public String getHintFront() { return hintFront; }
  @Override public String getHintBack() { return hintBack; }
  @Override public String getDeck() { return deck; }

}
