package com.example.fitnessbackend.exceptions;

public class EmailAlreadyExists extends RuntimeException {
  public EmailAlreadyExists(String message) {
    super(message);
  }
}
