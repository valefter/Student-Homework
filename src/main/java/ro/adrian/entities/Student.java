package ro.adrian.entities;

import lombok.*;
import ro.adrian.Gender;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Student {

  private final String firstName;
  private final String lastName;
  private final LocalDate dateOfBirth;
  private final Gender gender;
  private final String cnp;
}
