package com.ndumiso.bursarymatch;

import com.ndumiso.bursarymatch.gui.LogInGUI;

import javax.swing.SwingUtilities;

/**
 * Application entry point. Launches the login screen.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LogInGUI().setVisible(true);
            }
        });
    }
}