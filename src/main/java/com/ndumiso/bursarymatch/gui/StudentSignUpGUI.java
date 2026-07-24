package com.ndumiso.bursarymatch.gui;

import com.ndumiso.bursarymatch.auth.AuthService;
import com.ndumiso.bursarymatch.auth.RegistrationResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Sign-up screen for students. Creates a row in tblStudents.
 * Academic marks are entered later — this screen only creates the account.
 */
public class StudentSignUpGUI extends JFrame {

    private JTextField idNumberField;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField schoolField;
    private JTextField dobField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private JButton signUpButton;

    private final AuthService authService;

    public StudentSignUpGUI() {
        authService = new AuthService();
        setTitle("Bursary Match System - Student Sign Up");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildComponents();
    }

    private void buildComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Student Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(titleLabel, c);

        int row = 1;

        idNumberField = new JTextField(15);
        row = addField(panel, c, row, "ID Number:", idNumberField);

        nameField = new JTextField(15);
        row = addField(panel, c, row, "Name:", nameField);

        surnameField = new JTextField(15);
        row = addField(panel, c, row, "Surname:", surnameField);

        schoolField = new JTextField(15);
        row = addField(panel, c, row, "School:", schoolField);

        dobField = new JTextField(15);
        dobField.setToolTipText("Format: YYYY-MM-DD");
        row = addField(panel, c, row, "Date of Birth (YYYY-MM-DD):", dobField);

        usernameField = new JTextField(15);
        row = addField(panel, c, row, "Username:", usernameField);

        passwordField = new JPasswordField(15);
        row = addField(panel, c, row, "Password:", passwordField);

        confirmPasswordField = new JPasswordField(15);
        row = addField(panel, c, row, "Confirm Password:", confirmPasswordField);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        panel.add(statusLabel, c);

        signUpButton = new JButton("Create Account");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
        c.gridy = row++;
        panel.add(signUpButton, c);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpOptionGUI().setVisible(true);
                dispose();
            }
        });
        c.gridy = row++;
        panel.add(backButton, c);

        add(panel);
    }

    private int addField(JPanel panel, GridBagConstraints c, int row, String labelText, JComponent field) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel(labelText), c);

        c.gridx = 1;
        panel.add(field, c);

        return row + 1;
    }

    private void handleSignUp() {
        String idNumber = idNumberField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String school = schoolField.getText().trim();
        String dobText = dobField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (idNumber.isEmpty() || name.isEmpty() || surname.isEmpty()
                || dobText.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }

        if (!idNumber.matches("\\d+")) {
            statusLabel.setText("ID Number must contain digits only.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(dobText);
        } catch (DateTimeParseException e) {
            statusLabel.setText("Date of Birth must be in YYYY-MM-DD format.");
            return;
        }

        signUpButton.setEnabled(false);
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Creating account...");

        RegistrationResult result = authService.registerStudent(
                idNumber, name, surname, username, password, dateOfBirth,
                school.isEmpty() ? null : school);

        signUpButton.setEnabled(true);

        if (result.isSuccess()) {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Account created! You can now log in.");
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(result.getErrorMessage());
        }
    }
}