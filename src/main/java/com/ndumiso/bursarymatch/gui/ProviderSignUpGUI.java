package com.ndumiso.bursarymatch.gui;

import com.ndumiso.bursarymatch.auth.AuthService;
import com.ndumiso.bursarymatch.auth.RegistrationResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Sign-up screen for bursary providers. Creates a row in tblBursaryProviders.
 * Providers have no date of birth — there is no column for it.
 */
public class ProviderSignUpGUI extends JFrame {

    private JTextField idNumberField;
    private JTextField providerNameField;
    private JTextArea biographyArea;
    private JTextField phoneNumberField;
    private JTextField emailField;
    private JTextField websiteField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private JButton signUpButton;

    private final AuthService authService;

    public ProviderSignUpGUI() {
        authService = new AuthService();
        setTitle("Bursary Match System - Provider Sign Up");
        setSize(440, 560);
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

        JLabel titleLabel = new JLabel("Bursary Provider Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(titleLabel, c);

        int row = 1;

        idNumberField = new JTextField(15);
        row = addField(panel, c, row, "Reference/ID Number:", idNumberField);

        providerNameField = new JTextField(15);
        row = addField(panel, c, row, "Provider Name:", providerNameField);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel("Biography:"), c);

        biographyArea = new JTextArea(4, 15);
        biographyArea.setLineWrap(true);
        biographyArea.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(biographyArea);
        c.gridx = 1;
        panel.add(bioScroll, c);
        row++;

        phoneNumberField = new JTextField(15);
        row = addField(panel, c, row, "Phone Number:", phoneNumberField);

        emailField = new JTextField(15);
        row = addField(panel, c, row, "Email:", emailField);

        websiteField = new JTextField(15);
        row = addField(panel, c, row, "Website:", websiteField);

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
        String providerName = providerNameField.getText().trim();
        String biography = biographyArea.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        String website = websiteField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (idNumber.isEmpty() || providerName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        signUpButton.setEnabled(false);
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Creating account...");

        RegistrationResult result = authService.registerProvider(
                idNumber, providerName,
                biography.isEmpty() ? null : biography,
                phoneNumber.isEmpty() ? null : phoneNumber,
                email.isEmpty() ? null : email,
                website.isEmpty() ? null : website,
                username, password);

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