package com.ndumiso.bursarymatch.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps BCrypt so the rest of the app never handles plain-text passwords
 * or raw hashing calls directly.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password for storing in the database.
     * Call this once at sign-up time.
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Checks a plain-text password entered at login against the stored hash.
     */
    public static boolean matches(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, storedHash);
    }
}
