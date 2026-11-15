package ro.adrian.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.adrian.Gender;
import ro.adrian.entities.Student;
import ro.adrian.exceptions.InvalidCnpException;
import ro.adrian.exceptions.InvalidDateOfBirthException;
import ro.adrian.exceptions.InvalidFirstNameOrLastNameException;
import ro.adrian.exceptions.InvalidGenderException;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StudentValidatorTest {

  @ParameterizedTest
  @MethodSource("invalidDateOfBirth")
  public void testValidateStudentWithInvalidDateOfBirth(
      Student student, String expectedErrorMessage) {
    // given
    StudentValidator studentValidator = new StudentValidator();

    // when + then
    assertThatThrownBy(() -> studentValidator.validate(student))
        .isInstanceOf(InvalidDateOfBirthException.class)
        .hasMessage(expectedErrorMessage);
  }

  private static Stream<Arguments> invalidDateOfBirth() {
    int currentYearMinus18 = LocalDate.now().getYear() - 18;
    String expectedMsg = "The date of birth must be between 1900 and " + currentYearMinus18;
    return Stream.of(
        Arguments.of(
            new Student("Adrian", "Popescu", null, Gender.MALE, "5000515123456"), expectedMsg),
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.of(1889, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.now().minusYears(17), Gender.MALE, "5000515123456"),
            expectedMsg));
  }

  @ParameterizedTest
  @MethodSource("invalidNames")
  public void testValidateStudentWithInvalidName(Student student, String expectedErrorMessage) {
    // given
    StudentValidator studentValidator = new StudentValidator();

    // when + then
    assertThatThrownBy(() -> studentValidator.validate(student))
        .isInstanceOf(InvalidFirstNameOrLastNameException.class)
        .hasMessage(expectedErrorMessage);
  }

  private static Stream<Arguments> invalidNames() {
    String expectedMsg = "First name and last name must be provided";
    return Stream.of(
        Arguments.of(
            new Student(null, "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student("Adrian", null, LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student("Adrian", "", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student("Adrian", "   ", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student("", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg),
        Arguments.of(
            new Student("   ", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456"),
            expectedMsg));
  }

  @Test
  public void testValidateStudentWithNullGender() {
    // given
    StudentValidator studentValidator = new StudentValidator();
    Student student =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), null, "5000515123456");

    // when + then
    assertThatThrownBy(() -> studentValidator.validate(student))
        .isInstanceOf(InvalidGenderException.class)
        .hasMessage("Gender must be provided");
  }

  @Test
  public void testValidateNullStudent() {
    // given
    StudentValidator studentValidator = new StudentValidator();

    // when + then
    assertThatThrownBy(() -> studentValidator.validate(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Student cannot be null");
  }

  @ParameterizedTest
  @MethodSource("invalidCnps")
  public void testValidateStudentWithInvalidCnp(Student student, String expectedErrorMessage) {
    // given
    StudentValidator studentValidator = new StudentValidator();

    // when + then
    assertThatThrownBy(() -> studentValidator.validate(student))
        .isInstanceOf(InvalidCnpException.class)
        .hasMessage(expectedErrorMessage);
  }

  private static Stream<Arguments> invalidCnps() {
    String cnpNotProvidedMsg = "A CNP must be provided";
    String invalidCnpMsg = "A valid CNP must be provided";

    return Stream.of(
        Arguments.of(
            new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, null),
            cnpNotProvidedMsg),
        Arguments.of(
            new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, ""),
            cnpNotProvidedMsg),
        Arguments.of(
            new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "   "),
            cnpNotProvidedMsg),
        Arguments.of(
            new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "50005151256"),
            invalidCnpMsg),
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "500051512a456"),
            invalidCnpMsg),
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "500051512345678"),
            invalidCnpMsg),
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "aaaaaaaaaaaaa"),
            invalidCnpMsg));
  }
}
