package ro.adrian.exceptions;

public class InvalidFirstNameOrLastNameException extends RuntimeException {
  public InvalidFirstNameOrLastNameException(String message) {
    super(message);
  }
}
