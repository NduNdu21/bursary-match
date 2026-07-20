package com.ndumiso.bursarymatch;

import com.ndumiso.bursarymatch.db.DataBase;

/**
 * Temporary entry point used to confirm the database connection works
 * before the GUI (LogInGUI, MainScreenGUI, etc.) is rebuilt.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Bursary Match System - starting up...");

        try {
            DataBase db = new DataBase();
            System.out.println("Database connection successful.");
            db.close();
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }

        // TODO: replace this with LogInGUI once the Swing screens are rebuilt
    }
}
