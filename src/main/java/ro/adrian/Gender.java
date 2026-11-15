package ro.adrian;

import ro.adrian.exceptions.InvalidGenderException;

public enum Gender {
  MALE,
  FEMALE;

  public static Gender parse(String value) {

    if (value == null || value.isBlank()) {
      throw new InvalidGenderException("Gender must be provided");
    }

    return switch (value.toLowerCase()) {
      case "male", "m" -> MALE;
      case "female", "f" -> FEMALE;
      default -> throw new InvalidGenderException("Gender must be male (M) or female (F)");
    };
  }
}
