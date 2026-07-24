package com.ndumiso.bursarymatch.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ndumiso.bursarymatch.db.DataBase;

/**
 * Reads and writes profile data for students and providers.
 * Bypasses the Student/BursaryProvider model classes (same approach as
 * AuthService.registerStudent/registerProvider) since profile edits only
 * need a handful of fields, not a full domain object.
 */
public class ProfileService {

    public static class StudentProfile {
        public String studentId;
        public String username;
        public String firstName;
        public String surname;
        public LocalDate dateOfBirth;
        public String school;
    }

    public static class ProviderProfile {
        public String providerId;
        public String username;
        public String providerName;
        public String biography;
        public String phoneNumber;
        public String email;
        public String website;
    }

    public static class StudentMarksData {
        public String homeLang;
        public Integer hlMark;
        public String firstAddLang;
        public Integer falMark;
        public String mathSubject;
        public Integer mathMark;
        public String sub4;
        public Integer sub4Mark;
        public String sub5;
        public Integer sub5Mark;
        public String sub6;
        public Integer sub6Mark;
        public Integer loMark;
    }

    public StudentProfile getStudentProfile(String studentId) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "SELECT student_id, username, first_name, surname, date_of_birth, school "
                    + "FROM tblStudents WHERE student_id = ?";
            try (ResultSet rs = db.query(sql, studentId)) {
                if (!rs.next()) {
                    return null;
                }
                StudentProfile p = new StudentProfile();
                p.studentId = rs.getString("student_id");
                p.username = rs.getString("username");
                p.firstName = rs.getString("first_name");
                p.surname = rs.getString("surname");
                Date dob = rs.getDate("date_of_birth");
                p.dateOfBirth = dob != null ? dob.toLocalDate() : null;
                p.school = rs.getString("school");
                return p;
            }
        } finally {
            db.close();
        }
    }

    public void updateStudentProfile(String studentId, String firstName, String surname,
            LocalDate dateOfBirth, String school) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "UPDATE tblStudents SET first_name = ?, surname = ?, date_of_birth = ?, school = ? "
                    + "WHERE student_id = ?";
            db.update(sql, firstName, surname, Date.valueOf(dateOfBirth), school, studentId);
        } finally {
            db.close();
        }
    }

    public ProviderProfile getProviderProfile(String providerId) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "SELECT provider_id, username, provider_name, biography, phone_number, email, website "
                    + "FROM tblBursaryProviders WHERE provider_id = ?";
            try (ResultSet rs = db.query(sql, providerId)) {
                if (!rs.next()) {
                    return null;
                }
                ProviderProfile p = new ProviderProfile();
                p.providerId = rs.getString("provider_id");
                p.username = rs.getString("username");
                p.providerName = rs.getString("provider_name");
                p.biography = rs.getString("biography");
                p.phoneNumber = rs.getString("phone_number");
                p.email = rs.getString("email");
                p.website = rs.getString("website");
                return p;
            }
        } finally {
            db.close();
        }
    }

    public void updateProviderProfile(String providerId, String providerName, String biography,
            String phoneNumber, String email, String website) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "UPDATE tblBursaryProviders SET provider_name = ?, biography = ?, phone_number = ?, "
                    + "email = ?, website = ? WHERE provider_id = ?";
            db.update(sql, providerName, biography, phoneNumber, email, website, providerId);
        } finally {
            db.close();
        }
    }

    public StudentMarksData getStudentMarks(String studentId) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "SELECT home_lang, hl_mark, first_add_lang, fal_mark, math_subject, math_mark, "
                    + "sub4, sub4_mark, sub5, sub5_mark, sub6, sub6_mark, lo_mark "
                    + "FROM tblStudentMarks WHERE student_id = ?";
            try (ResultSet rs = db.query(sql, studentId)) {
                if (!rs.next()) {
                    return null;
                }
                StudentMarksData m = new StudentMarksData();
                m.homeLang = rs.getString("home_lang");
                m.hlMark = (Integer) rs.getObject("hl_mark");
                m.firstAddLang = rs.getString("first_add_lang");
                m.falMark = (Integer) rs.getObject("fal_mark");
                m.mathSubject = rs.getString("math_subject");
                m.mathMark = (Integer) rs.getObject("math_mark");
                m.sub4 = rs.getString("sub4");
                m.sub4Mark = (Integer) rs.getObject("sub4_mark");
                m.sub5 = rs.getString("sub5");
                m.sub5Mark = (Integer) rs.getObject("sub5_mark");
                m.sub6 = rs.getString("sub6");
                m.sub6Mark = (Integer) rs.getObject("sub6_mark");
                m.loMark = (Integer) rs.getObject("lo_mark");
                return m;
            }
        } finally {
            db.close();
        }
    }

    public void upsertStudentMarks(String studentId, StudentMarksData m) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "INSERT INTO tblStudentMarks "
                    + "(student_id, home_lang, hl_mark, first_add_lang, fal_mark, math_subject, math_mark, "
                    + "sub4, sub4_mark, sub5, sub5_mark, sub6, sub6_mark, lo_mark) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (student_id) DO UPDATE SET "
                    + "home_lang = EXCLUDED.home_lang, hl_mark = EXCLUDED.hl_mark, "
                    + "first_add_lang = EXCLUDED.first_add_lang, fal_mark = EXCLUDED.fal_mark, "
                    + "math_subject = EXCLUDED.math_subject, math_mark = EXCLUDED.math_mark, "
                    + "sub4 = EXCLUDED.sub4, sub4_mark = EXCLUDED.sub4_mark, "
                    + "sub5 = EXCLUDED.sub5, sub5_mark = EXCLUDED.sub5_mark, "
                    + "sub6 = EXCLUDED.sub6, sub6_mark = EXCLUDED.sub6_mark, "
                    + "lo_mark = EXCLUDED.lo_mark";

            db.update(sql, studentId, m.homeLang, m.hlMark, m.firstAddLang, m.falMark,
                    m.mathSubject, m.mathMark, m.sub4, m.sub4Mark, m.sub5, m.sub5Mark,
                    m.sub6, m.sub6Mark, m.loMark);
        } finally {
            db.close();
        }
    }
}