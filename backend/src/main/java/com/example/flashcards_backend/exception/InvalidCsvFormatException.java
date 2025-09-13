package com.example.flashcards_backend.exception;

public class InvalidCsvFormatException extends RuntimeException {
  public InvalidCsvFormatException(String message) {
    super(message);
  }

  public InvalidCsvFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}