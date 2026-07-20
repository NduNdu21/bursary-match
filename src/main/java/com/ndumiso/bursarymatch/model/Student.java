package com.ndumiso.bursarymatch.model;

import java.time.LocalDate;

/**
 * A Student is a Person who also belongs to a school and holds a set of marks.
 */
public class Student extends Person {

    private String school;
    private StudentMarks marks; // may be null until the student fills in their academics tab

    public Student(String idNumber, String name, String surname, String username,
                   String password, LocalDate dateOfBirth, String school) {
        super(idNumber, name, surname, username, password, dateOfBirth);
        this.school = school;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public StudentMarks getMarks() {
        return marks;
    }

    public void setMarks(StudentMarks marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return super.toString() + ", School: " + school;
    }
}
