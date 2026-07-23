package com.ndumiso.bursarymatch.util;

/**
 * Standalone utility to generate a BCrypt hash for a given plain-text
 * password. Useful for manually inserting test accounts into the database
 * before SignUpGUI exists.
 *
 * Run with: mvn compile exec:java -Dexec.mainClass="com.ndumiso.bursarymatch.util.HashGenerator" -Dexec.args="yourPasswordHere"
 */
public class HashGenerator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: pass the password you want hashed as an argument.");
            return;
        }

        String plainPassword = args[0];
        String hashed = PasswordUtil.hash(plainPassword);
        System.out.println("Hashed password:");
        System.out.println(hashed);
    }
}
