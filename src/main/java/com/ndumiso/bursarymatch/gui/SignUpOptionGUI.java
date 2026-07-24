package com.ndumiso.bursarymatch.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lets a new user choose whether they're signing up as a Student
 * or a Bursary Provider, then opens the matching sign-up screen.
 */
public class SignUpOptionGUI extends JFrame {

    public SignUpOptionGUI() {
        setTitle("Bursary Match System - Sign Up");
        setSize(380, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildComponents();
    }

    private void buildComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Sign up as...", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        c.gridx = 0;
        c.gridy = 0;
        panel.add(titleLabel, c);

        JButton studentButton = new JButton("Student");
        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudentSignUpGUI().setVisible(true);
                dispose();
            }
        });
        c.gridy = 1;
        panel.add(studentButton, c);

        JButton providerButton = new JButton("Bursary Provider");
        providerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProviderSignUpGUI().setVisible(true);
                dispose();
            }
        });
        c.gridy = 2;
        panel.add(providerButton, c);

        JButton backButton = new JButton("Back to Log In");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LogInGUI().setVisible(true);
                dispose();
            }
        });
        c.gridy = 3;
        panel.add(backButton, c);

        add(panel);
    }
}