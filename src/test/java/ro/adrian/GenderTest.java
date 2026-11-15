package ro.adrian;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.adrian.exceptions.InvalidGenderException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class GenderTest {

  @ParameterizedTest
  @MethodSource("invalidGenders")
  public void testGenderParseWithInvalidInput(String input, String expectedErrorMessage) {
    // when + then
    assertThatThrownBy(() -> Gender.parse(input))
        .isInstanceOf(InvalidGenderException.class)
        .hasMessage(expectedErrorMessage);
  }

  private static Stream<Arguments> invalidGenders() {
    return Stream.of(
        Arguments.of(null, "Gender must be provided"),
        Arguments.of("aaa", "Gender must be male (M) or female (F)"));
  }

  @ParameterizedTest
  @MethodSource("validGenders")
  public void testGenderParseHappyPath(String input, Gender expectedGender) {
    // when
    Gender actualGender = Gender.parse(input);

    // then
    assertThat(actualGender).isEqualTo(expectedGender);
  }

  private static Stream<Arguments> validGenders() {
    return Stream.of(
        Arguments.of("male", Gender.MALE),
        Arguments.of("female", Gender.FEMALE),
        Arguments.of("MALE", Gender.MALE),
        Arguments.of("FEMALE", Gender.FEMALE),
        Arguments.of("m", Gender.MALE),
        Arguments.of("f", Gender.FEMALE),
        Arguments.of("M", Gender.MALE),
        Arguments.of("F", Gender.FEMALE),
        Arguments.of("mAle", Gender.MALE),
        Arguments.of("femAle", Gender.FEMALE));
  }
}
