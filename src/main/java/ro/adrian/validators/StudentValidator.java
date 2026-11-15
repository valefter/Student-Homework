package ro.adrian.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ro.adrian.Gender;
import ro.adrian.entities.Student;
import ro.adrian.exceptions.InvalidCnpException;
import ro.adrian.exceptions.InvalidDateOfBirthException;
import ro.adrian.exceptions.InvalidFirstNameOrLastNameException;
import ro.adrian.exceptions.InvalidGenderException;

import java.time.LocalDate;

@Slf4j
public class StudentValidator {

  public void validate(Student student) {
    if (student == null) {
      log.error("Student validation failed: student is null");
      throw new IllegalArgumentException("Student cannot be null");
    }

    validateDateOfBirth(student.getDateOfBirth());
    validateName(student);
    validateGender(student.getGender());
    validateCnp(student.getCnp());
  }

  public void validateCnp(String cnp) {
    if (cnp == null || cnp.isBlank()) {
      log.error("CNP was not provided");
      throw new InvalidCnpException("A CNP must be provided");
    }

    boolean isValidCnp = (cnp.length() == 13) && (StringUtils.isNumeric(cnp));

    if (!isValidCnp) {
      log.error("Invalid CNP format provided");
      throw new InvalidCnpException("A valid CNP must be provided");
    }
  }

  private void validateGender(Gender gender) {
    if (gender == null) {
      log.error("Gender validation failed");
      throw new InvalidGenderException("Gender must be provided");
    }
  }

  private void validateName(Student student) {
    if (student.getFirstName() == null
        || student.getLastName() == null
        || student.getFirstName().isBlank()
        || student.getLastName().isBlank()) {
      log.error("Name validation failed");
      throw new InvalidFirstNameOrLastNameException("First name and last name must be provided");
    }
  }

  private void validateDateOfBirth(LocalDate dateOfBirth) {
    LocalDate minDateOfBirth = LocalDate.of(1900, 1, 1);
    LocalDate maxDateOfBirth = LocalDate.now().minusYears(18);
    int currentYear = LocalDate.now().getYear() - 18;

    if (dateOfBirth == null
        || dateOfBirth.isBefore(minDateOfBirth)
        || dateOfBirth.isAfter(maxDateOfBirth)) {
      log.error("Date of birth validation failed");
      throw new InvalidDateOfBirthException(
          "The date of birth must be between 1900 and " + currentYear);
    }
  }
}
