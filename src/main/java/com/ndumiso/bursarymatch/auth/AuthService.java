package com.ndumiso.bursarymatch.auth;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ndumiso.bursarymatch.db.DataBase;
import com.ndumiso.bursarymatch.util.PasswordUtil;

/**
 * Checks a username/password against either tblStudents or tblBursaryProviders,
 * depending on which account type the user selected on LogInGUI.
 * Also handles new account creation for both account types.
 */
public class AuthService {

    /**
     * Attempts to log a user in.
     *
     * @param username entered username
     * @param password entered plain-text password
     * @param userType either "STUDENT" or "PROVIDER"
     */
    public LoginResult login(String username, String password, String userType) {
        try {
            DataBase db = new DataBase();
            LoginResult result;

            if ("STUDENT".equals(userType)) {
                result = checkStudent(db, username, password);
            } else {
                result = checkProvider(db, username, password);
            }

            db.close();
            return result;

        } catch (SQLException e) {
            return LoginResult.failure("Could not reach the database: " + e.getMessage());
        }
    }

    private LoginResult checkStudent(DataBase db, String username, String password) throws SQLException {
        String sql = "SELECT student_id, password, first_name, surname "
                + "FROM tblStudents WHERE username = ?";

        try (ResultSet rs = db.query(sql, username)) {
            if (!rs.next()) {
                return LoginResult.failure("No account found with that username.");
            }

            String storedHash = rs.getString("password");
            if (!PasswordUtil.matches(password, storedHash)) {
                return LoginResult.failure("Incorrect password.");
            }

            String studentId = rs.getString("student_id");
            String displayName = rs.getString("first_name") + " " + rs.getString("surname");
            return LoginResult.success(studentId, "STUDENT", displayName);
        }
    }

    private LoginResult checkProvider(DataBase db, String username, String password) throws SQLException {
        String sql = "SELECT provider_id, password, provider_name "
                + "FROM tblBursaryProviders WHERE username = ?";

        try (ResultSet rs = db.query(sql, username)) {
            if (!rs.next()) {
                return LoginResult.failure("No account found with that username.");
            }

            String storedHash = rs.getString("password");
            if (!PasswordUtil.matches(password, storedHash)) {
                return LoginResult.failure("Incorrect password.");
            }

            String providerId = rs.getString("provider_id");
            String displayName = rs.getString("provider_name");
            return LoginResult.success(providerId, "PROVIDER", displayName);
        }
    }

    /**
     * Creates a new student account. studentId doubles as the student's ID number.
     */
    public RegistrationResult registerStudent(String studentId, String firstName, String surname,
            String username, String password, LocalDate dateOfBirth, String school) {
        try {
            DataBase db = new DataBase();

            if (usernameExists(db, "tblStudents", username)) {
                db.close();
                return RegistrationResult.failure("That username is already taken.");
            }

            String hashedPassword = PasswordUtil.hash(password);

            String sql = "INSERT INTO tblStudents "
                    + "(student_id, username, password, first_name, surname, date_of_birth, school) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            db.update(sql, studentId, username, hashedPassword, firstName, surname,
                    Date.valueOf(dateOfBirth), school);

            db.close();
            return RegistrationResult.success();

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return RegistrationResult.failure("An account with that ID number or username already exists.");
            }
            return RegistrationResult.failure("Could not create account: " + e.getMessage());
        }
    }

    /**
     * Creates a new bursary provider account. providerId is a reference/registration
     * number you assign — there is no date-of-birth column for providers.
     */
    public RegistrationResult registerProvider(String providerId, String providerName, String biography,
            String phoneNumber, String email, String website, String username, String password) {
        try {
            DataBase db = new DataBase();

            if (usernameExists(db, "tblBursaryProviders", username)) {
                db.close();
                return RegistrationResult.failure("That username is already taken.");
            }

            String hashedPassword = PasswordUtil.hash(password);

            String sql = "INSERT INTO tblBursaryProviders "
                    + "(provider_id, username, password, provider_name, biography, phone_number, email, website) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            db.update(sql, providerId, username, hashedPassword, providerName, biography,
                    phoneNumber, email, website);

            db.close();
            return RegistrationResult.success();

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return RegistrationResult.failure("An account with that ID number or username already exists.");
            }
            return RegistrationResult.failure("Could not create account: " + e.getMessage());
        }
    }

    private boolean usernameExists(DataBase db, String table, String username) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE username = ?";
        try (ResultSet rs = db.query(sql, username)) {
            return rs.next();
        }
    }
}