package ro.adrian.exceptions;

public class InvalidGenderException extends RuntimeException {
  public InvalidGenderException(String message) {
    super(message);
  }
}
