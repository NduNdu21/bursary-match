package com.ndumiso.bursarymatch.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Base class for anyone who can log into the system (Student or BursaryProvider).
 * Matches the "Person" class from the Phase 2 design document.
 */
public abstract class Person {

    private String idNumber;
    private String name;
    private String surname;
    private String username;
    private String password;
    private LocalDate dateOfBirth;

    public Person(String idNumber, String name, String surname, String username,
                  String password, LocalDate dateOfBirth) {
        this.idNumber = idNumber;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        // Basic age calculation from date of birth to today
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "ID: " + idNumber
                + ", Name: " + name
                + ", Surname: " + surname
                + ", Username: " + username
                + ", Date of Birth: " + dateOfBirth;
    }
}
