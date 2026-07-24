package com.ndumiso.bursarymatch.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.ndumiso.bursarymatch.auth.AuthService;
import com.ndumiso.bursarymatch.auth.LoginResult;

/**
 * Login screen: lets the user pick Student or Provider, enter credentials, and
 * either logs them in or reports why it failed.
 *
 * Matches "LogInGUI" from the Phase 2 design document.
 */
public class LogInGUI extends JFrame {

    private JRadioButton studentRadio;
    private JRadioButton providerRadio;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JButton loginButton;
    private JButton signUpButton;

    private final AuthService authService;

    public LogInGUI() {
        authService = new AuthService();
        setupWindow();
        buildComponents();
    }

    private void setupWindow() {
        setTitle("Bursary Match System - Log In");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buildComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Bursary Match System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(titleLabel, c);

        // Account type selection
        studentRadio = new JRadioButton("Student", true);
        providerRadio = new JRadioButton("Bursary Provider");
        ButtonGroup accountTypeGroup = new ButtonGroup();
        accountTypeGroup.add(studentRadio);
        accountTypeGroup.add(providerRadio);

        JPanel radioPanel = new JPanel();
        radioPanel.add(studentRadio);
        radioPanel.add(providerRadio);
        c.gridy = 1;
        panel.add(radioPanel, c);

        // Username
        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;
        panel.add(new JLabel("Username:"), c);

        usernameField = new JTextField(15);
        c.gridx = 1;
        panel.add(usernameField, c);

        // Password
        c.gridy = 3;
        c.gridx = 0;
        panel.add(new JLabel("Password:"), c);

        passwordField = new JPasswordField(15);
        c.gridx = 1;
        panel.add(passwordField, c);

        // Status label (shows errors)
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        panel.add(statusLabel, c);

        // Login button
        loginButton = new JButton("Log In");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        c.gridy = 5;
        panel.add(loginButton, c);

        // Sign up button
        signUpButton = new JButton("Don't have an account? Sign up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpOptionGUI().setVisible(true);
                dispose();
            }
        });
        c.gridy = 6;
        panel.add(signUpButton, c);

        add(panel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String userType = studentRadio.isSelected() ? "STUDENT" : "PROVIDER";

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both a username and password.");
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setForeground(Color.RED);
        statusLabel.setText("Logging in...");

        LoginResult result = authService.login(username, password, userType);

        loginButton.setEnabled(true);

        if (result.isSuccess()) {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Welcome, " + result.getDisplayName() + "!");
            new MainScreenGUI(result.getUserId(), result.getUserType(), result.getDisplayName()).setVisible(true);
            dispose();
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(result.getErrorMessage());
        }
    }
}
