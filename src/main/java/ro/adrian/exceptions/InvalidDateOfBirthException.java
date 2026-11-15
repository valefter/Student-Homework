package ro.adrian.exceptions;

public class InvalidDateOfBirthException extends RuntimeException {
  public InvalidDateOfBirthException(String message) {
    super(message);
  }
}
