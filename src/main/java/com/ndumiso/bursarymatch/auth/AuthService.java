package com.ndumiso.bursarymatch.auth;

import com.ndumiso.bursarymatch.db.DataBase;
import com.ndumiso.bursarymatch.util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Checks a username/password against either tblStudents or tblBursaryProviders,
 * depending on which account type the user selected on LogInGUI.
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
}
