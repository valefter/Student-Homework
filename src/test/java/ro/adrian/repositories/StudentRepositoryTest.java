package ro.adrian.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.adrian.Gender;
import ro.adrian.SortCriteria;
import ro.adrian.entities.Student;
import ro.adrian.exceptions.*;
import ro.adrian.validators.StudentValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

  private final StudentValidator studentValidator = new StudentValidator();

  @Test
  public void testAddStudentHappyPath() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");

    // when
    studentRepository.addStudent(student);

    // then
    assertThat(studentRepository.getStudents()).hasSize(1).containsEntry(student.getCnp(), student);
  }

  @ParameterizedTest
  @MethodSource("studentsWithExistingCnp")
  public void testAddStudentWithAnExistingCnp(Student student) {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student registeredStudent =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    studentRepository.addStudent(registeredStudent);

    // when + then
    assertThatThrownBy(() -> studentRepository.addStudent(student))
        .isInstanceOf(StudentAlreadyExistsException.class)
        .hasMessage("A student with this CNP already exists");
  }

  private static Stream<Arguments> studentsWithExistingCnp() {
    return Stream.of(
        Arguments.of(
            new Student(
                "Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456")),
        Arguments.of(
            new Student(
                "Alex", "Ionescu", LocalDate.of(2001, 4, 13), Gender.MALE, "5000515123456")));
  }

  @Test
  public void testDeletingANonExistentStudent() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);

    // when + then
    assertThatThrownBy(() -> studentRepository.deleteStudent("1234567891234"))
        .isInstanceOf(StudentNotFoundException.class)
        .hasMessage("No student was found with the provided CNP");
  }

  @Test
  public void testDeleteStudentHappyPath() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    studentRepository.addStudent(student);

    // when
    boolean deletedStudent = studentRepository.deleteStudent("5000515123456");

    // then
    assertTrue(deletedStudent);
    assertThat(studentRepository.getStudents()).isEmpty();
  }

  @Test
  public void testFindStudentsByNegativeAge() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);

    // when + then
    assertThatThrownBy(() -> studentRepository.findStudentsByAge(-1))
        .isInstanceOf(NegativeAgeException.class)
        .hasMessage("The age cannot be negative");
  }

  @Test
  public void testFindStudentsByAgeHappyPath() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student1 =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    Student student2 =
        new Student("Maria", "Ionescu", LocalDate.of(2000, 8, 20), Gender.FEMALE, "6000820123456");
    Student student3 =
        new Student("Alex", "Dumitru", LocalDate.of(2000, 11, 10), Gender.MALE, "5001110123456");
    Student student4 =
        new Student("Elena", "Marin", LocalDate.of(1995, 3, 12), Gender.FEMALE, "2950312123456");
    Student student5 =
        new Student("Andrei", "Stan", LocalDate.of(2005, 7, 25), Gender.MALE, "5050725123456");

    studentRepository.addStudent(student1);
    studentRepository.addStudent(student2);
    studentRepository.addStudent(student3);
    studentRepository.addStudent(student4);
    studentRepository.addStudent(student5);

    // when
    List<Student> studentsByAgeFound = studentRepository.findStudentsByAge(26);

    // then
    assertThat(studentsByAgeFound).contains(student1, student2, student3).hasSize(3);
  }

  @Test
  public void testFindStudentsByAgeWhenNoStudentsMatch() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    studentRepository.addStudent(student);

    // when
    List<Student> studentsByAgeFound = studentRepository.findStudentsByAge(23);

    // then
    assertThat(studentsByAgeFound).isEmpty();
  }

  @Test
  public void testGetAllStudentsSortedByNoCriteriaProvided() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    studentRepository.addStudent(student);

    // when + then
    assertThatThrownBy(() -> studentRepository.getAllStudentsSortedBy(null))
        .isInstanceOf(InvalidSortingCriteriaException.class)
        .hasMessage("A sorting criteria must be provided");
  }

  @Test
  public void testGetAllStudentsSortedByLastName() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student1 =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    Student student2 =
        new Student("Maria", "Ionescu", LocalDate.of(2000, 8, 20), Gender.FEMALE, "6000820123456");
    Student student3 =
        new Student("Alex", "Dumitru", LocalDate.of(2000, 11, 10), Gender.MALE, "5001110123456");
    Student student4 =
        new Student("Elena", "Stan", LocalDate.of(1995, 3, 12), Gender.FEMALE, "2950312123456");
    Student student5 =
        new Student("Andrei", "Stan", LocalDate.of(2005, 7, 25), Gender.MALE, "5050725123456");

    studentRepository.addStudent(student1);
    studentRepository.addStudent(student2);
    studentRepository.addStudent(student3);
    studentRepository.addStudent(student4);
    studentRepository.addStudent(student5);

    List<Student> sortedStudents = List.of(student3, student2, student1, student5, student4);

    // when
    List<Student> studentsSortedByLastName =
        studentRepository.getAllStudentsSortedBy(SortCriteria.LAST_NAME);

    // then
    assertThat(studentsSortedByLastName).containsExactlyElementsOf(sortedStudents);
  }

  @Test
  public void testGetAllStudentsSortedByBirthDate() {
    // given
    StudentRepository studentRepository = new StudentRepository(studentValidator);
    Student student1 =
        new Student("Adrian", "Popescu", LocalDate.of(2000, 5, 15), Gender.MALE, "5000515123456");
    Student student2 =
        new Student("Maria", "Ionescu", LocalDate.of(2000, 5, 15), Gender.FEMALE, "6000820123456");
    Student student3 =
        new Student("Alex", "Dumitru", LocalDate.of(2003, 11, 10), Gender.MALE, "5001110123456");
    Student student4 =
        new Student("Elena", "Stan", LocalDate.of(1995, 3, 12), Gender.FEMALE, "2950312123456");
    Student student5 =
        new Student("Andrei", "Stan", LocalDate.of(2005, 7, 25), Gender.MALE, "5050725123456");

    studentRepository.addStudent(student1);
    studentRepository.addStudent(student2);
    studentRepository.addStudent(student3);
    studentRepository.addStudent(student4);
    studentRepository.addStudent(student5);

    List<Student> sortedStudents = List.of(student4, student2, student1, student3, student5);

    // when
    List<Student> studentsSortedByBirthDate =
        studentRepository.getAllStudentsSortedBy(SortCriteria.BIRTH_DATE);

    // then
    assertThat(studentsSortedByBirthDate).containsExactlyElementsOf(sortedStudents);
  }
}
