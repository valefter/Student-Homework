package ro.adrian.repositories;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ro.adrian.SortCriteria;
import ro.adrian.entities.Student;
import ro.adrian.exceptions.*;
import ro.adrian.validators.StudentValidator;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class StudentRepository {

  private final Map<String, Student> students;
  private final StudentValidator studentValidator;

  public StudentRepository(StudentValidator studentValidator) {
    this.students = new HashMap<>();
    this.studentValidator = studentValidator;
  }

  public void addStudent(Student student) {
    log.info("Attempting to add a student");
    studentValidator.validate(student);

    if (students.containsKey(student.getCnp())) {
      log.error("Attempted to add a student with an existing CNP");
      throw new StudentAlreadyExistsException("A student with this CNP already exists");
    }

    students.put(student.getCnp(), student);
    log.info("Student has been successfully added to the repository");
  }

  public boolean deleteStudent(String cnp) {
    log.info("Attempting to delete a student");
    studentValidator.validateCnp(cnp);
    Student student = students.get(cnp);

    if (student == null) {
      log.error("Attempted to delete a non-existent student");
      throw new StudentNotFoundException("No student was found with the provided CNP");
    }
    students.remove(cnp);
    log.info("Student has been successfully removed from the repository");
    return true;
  }

  public List<Student> findStudentsByAge(int age) {
    log.info("Attempting to retrieve all students with age {}", age);
    if (age < 0) {
      log.error("A negative age was provided");
      throw new NegativeAgeException("The age cannot be negative");
    }

    List<Student> retrievedStudents =
        students.values().stream()
            .filter(student ->
                Period.between(student.getDateOfBirth(), LocalDate.now()).getYears() == age)
            .toList();

    log.info("Successfully retrieved {} students with age {}", retrievedStudents.size(), age);
    return retrievedStudents;
  }

  public List<Student> getAllStudentsSortedBy(SortCriteria sortCriteria) {
    log.info("Attempting to retrieve all students sorted by {}", sortCriteria);
    if (sortCriteria == null) {
      log.error("Invalid sorting criteria provided");
      throw new InvalidSortingCriteriaException("A sorting criteria must be provided");
    }

    List<Student> sortedStudents =
        switch (sortCriteria) {
          case LAST_NAME ->
              students.values().stream()
                  .sorted(
                      Comparator.comparing(Student::getLastName)
                          .thenComparing(Student::getFirstName))
                  .toList();
          case BIRTH_DATE ->
              students.values().stream()
                  .sorted(
                      Comparator.comparing(Student::getDateOfBirth)
                          .thenComparing(Student::getLastName))
                  .toList();
        };

    log.info(
        "Successfully retrieved {} students sorted by {}", sortedStudents.size(), sortCriteria);
    return sortedStudents;
  }
}
