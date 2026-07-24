package com.ndumiso.bursarymatch.gui;

import com.ndumiso.bursarymatch.model.Offer;
import com.ndumiso.bursarymatch.service.EligibilityService;
import com.ndumiso.bursarymatch.service.OfferService;
import com.ndumiso.bursarymatch.service.ProfileService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Main application shell. Tabs shown depend on whether the logged-in user is a
 * Student or a Provider, per the Phase 2 design doc plus the "My Offers"
 * addition for providers (Phase 1 required provider offer management, which the
 * Phase 2 GUI spec never actually gave them a tab for).
 */
public class MainScreenGUI extends JFrame {

    private final String userId;
    private final String userType; // "STUDENT" or "PROVIDER"
    private final String displayName;

    private final ProfileService profileService = new ProfileService();
    private final OfferService offerService = new OfferService();
    private final EligibilityService eligibilityService = new EligibilityService();

    public MainScreenGUI(String userId, String userType, String displayName) {
        this.userId = userId;
        this.userType = userType;
        this.displayName = displayName;

        setTitle("Bursary Match System - " + displayName);
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildTabs();
    }

    private void buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Home", buildHomeTab());

        if ("STUDENT".equals(userType)) {
            tabs.addTab("For You", buildForYouTab());
        }

        if ("PROVIDER".equals(userType)) {
            tabs.addTab("My Offers", buildMyOffersTab());
        }

        tabs.addTab("Search", buildSearchPlaceholderTab());
        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Help", buildHelpTab());

        add(tabs);
    }

    // ---------- Home ----------
    private JPanel buildHomeTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Bursary Match System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea(
                "Welcome, " + displayName + ".\n\n"
                + "Bursary Match System helps South African students find bursaries and "
                + "scholarships that match their academic results, and helps bursary "
                + "providers reach the students who qualify for their offers.\n\n"
                + ("STUDENT".equals(userType)
                ? "Check the For You tab for offers ranked to your results, or "
                + "Search to browse everything available."
                : "Use the My Offers tab to post and manage your bursary listings."));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }

    // ---------- For You (student only) ----------
    private JPanel buildForYouTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Offers you qualify for, ranked by fit", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(headerLabel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Offer", "Faculty", "Deadline", "Margin", "Most Eligible"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        JLabel emptyStateLabel = new JLabel(" ", SwingConstants.CENTER);
        emptyStateLabel.setFont(new Font("Arial", Font.ITALIC, 13));

        try {
            ProfileService.StudentMarksData marks = profileService.getStudentMarks(userId);

            if (marks == null) {
                emptyStateLabel.setText(
                        "You haven't entered your academic results yet - go to Profile > Academics first.");
                panel.add(emptyStateLabel, BorderLayout.CENTER);
            } else {
                List<EligibilityService.RankedOffer> ranked = eligibilityService.getEligibleOffers(marks);

                if (ranked.isEmpty()) {
                    emptyStateLabel.setText("No offers currently match your results.");
                    panel.add(emptyStateLabel, BorderLayout.CENTER);
                } else {
                    for (EligibilityService.RankedOffer r : ranked) {
                        tableModel.addRow(new Object[]{
                            r.offer.getOfferName(),
                            r.offer.getFaculty(),
                            r.offer.getDeadline(),
                            (r.averageMargin >= 0 ? "+" : "") + r.averageMargin,
                            r.mostEligible ? "Yes" : ""
                        });
                    }
                    panel.add(tableScroll, BorderLayout.CENTER);
                }
            }
        } catch (SQLException e) {
            emptyStateLabel.setText("Could not load matches: " + e.getMessage());
            panel.add(emptyStateLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    // ---------- Search (placeholder) ----------
    private JPanel buildSearchPlaceholderTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(
                "Search is coming soon - eligibility ranking is still being built.",
                SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 14));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // ---------- Profile ----------
    private JPanel buildProfileTab() {
        JTabbedPane profileTabs = new JTabbedPane();
        profileTabs.addTab("Personal Info", buildPersonalInfoPanel());

        if ("STUDENT".equals(userType)) {
            profileTabs.addTab("Academics", buildAcademicsPanel());
        }

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(profileTabs, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        if ("STUDENT".equals(userType)) {
            ProfileService.StudentProfile profile;
            try {
                profile = profileService.getStudentProfile(userId);
            } catch (SQLException e) {
                panel.add(new JLabel("Could not load profile: " + e.getMessage()), c);
                return panel;
            }
            if (profile == null) {
                panel.add(new JLabel("No profile found for this account."), c);
                return panel;
            }

            JTextField nameField = new JTextField(profile.firstName, 15);
            JTextField surnameField = new JTextField(profile.surname, 15);
            JTextField schoolField = new JTextField(profile.school, 15);
            JTextField dobField = new JTextField(
                    profile.dateOfBirth != null ? profile.dateOfBirth.toString() : "", 15);
            dobField.setToolTipText("Format: YYYY-MM-DD");

            int row = 0;
            row = addRow(panel, c, row, "Username:", new JLabel(profile.username));
            row = addRow(panel, c, row, "Name:", nameField);
            row = addRow(panel, c, row, "Surname:", surnameField);
            row = addRow(panel, c, row, "School:", schoolField);
            row = addRow(panel, c, row, "Date of Birth (YYYY-MM-DD):", dobField);

            c.gridx = 0;
            c.gridy = row++;
            c.gridwidth = 2;
            panel.add(statusLabel, c);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LocalDate dob;
                    try {
                        dob = LocalDate.parse(dobField.getText().trim());
                    } catch (DateTimeParseException ex) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Date of Birth must be in YYYY-MM-DD format.");
                        return;
                    }
                    try {
                        profileService.updateStudentProfile(userId, nameField.getText().trim(),
                                surnameField.getText().trim(), dob, schoolField.getText().trim());
                        statusLabel.setForeground(new Color(0, 128, 0));
                        statusLabel.setText("Saved.");
                    } catch (SQLException ex) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Could not save: " + ex.getMessage());
                    }
                }
            });
            c.gridy = row;
            panel.add(saveButton, c);

        } else {
            ProfileService.ProviderProfile profile;
            try {
                profile = profileService.getProviderProfile(userId);
            } catch (SQLException e) {
                panel.add(new JLabel("Could not load profile: " + e.getMessage()), c);
                return panel;
            }
            if (profile == null) {
                panel.add(new JLabel("No profile found for this account."), c);
                return panel;
            }

            JTextField nameField = new JTextField(profile.providerName, 15);
            JTextArea bioArea = new JTextArea(profile.biography, 4, 15);
            bioArea.setLineWrap(true);
            bioArea.setWrapStyleWord(true);
            JTextField phoneField = new JTextField(profile.phoneNumber, 15);
            JTextField emailField = new JTextField(profile.email, 15);
            JTextField websiteField = new JTextField(profile.website, 15);

            int row = 0;
            row = addRow(panel, c, row, "Username:", new JLabel(profile.username));
            row = addRow(panel, c, row, "Provider Name:", nameField);
            row = addRow(panel, c, row, "Biography:", new JScrollPane(bioArea));
            row = addRow(panel, c, row, "Phone Number:", phoneField);
            row = addRow(panel, c, row, "Email:", emailField);
            row = addRow(panel, c, row, "Website:", websiteField);

            c.gridx = 0;
            c.gridy = row++;
            c.gridwidth = 2;
            panel.add(statusLabel, c);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        profileService.updateProviderProfile(userId, nameField.getText().trim(),
                                bioArea.getText().trim(), phoneField.getText().trim(),
                                emailField.getText().trim(), websiteField.getText().trim());
                        statusLabel.setForeground(new Color(0, 128, 0));
                        statusLabel.setText("Saved.");
                    } catch (SQLException ex) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Could not save: " + ex.getMessage());
                    }
                }
            });
            c.gridy = row;
            panel.add(saveButton, c);
        }

        return panel;
    }

    private JPanel buildAcademicsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        ProfileService.StudentMarksData existing;
        try {
            existing = profileService.getStudentMarks(userId);
        } catch (SQLException e) {
            panel.add(new JLabel("Could not load marks: " + e.getMessage()), c);
            return panel;
        }
        if (existing == null) {
            existing = new ProfileService.StudentMarksData();
        }

        JTextField homeLangField = new JTextField(str(existing.homeLang), 12);
        JTextField hlMarkField = new JTextField(str(existing.hlMark), 5);
        JTextField falField = new JTextField(str(existing.firstAddLang), 12);
        JTextField falMarkField = new JTextField(str(existing.falMark), 5);
        JTextField mathSubjectField = new JTextField(str(existing.mathSubject), 12);
        JTextField mathMarkField = new JTextField(str(existing.mathMark), 5);
        JTextField sub4Field = new JTextField(str(existing.sub4), 12);
        JTextField sub4MarkField = new JTextField(str(existing.sub4Mark), 5);
        JTextField sub5Field = new JTextField(str(existing.sub5), 12);
        JTextField sub5MarkField = new JTextField(str(existing.sub5Mark), 5);
        JTextField sub6Field = new JTextField(str(existing.sub6), 12);
        JTextField sub6MarkField = new JTextField(str(existing.sub6Mark), 5);
        JTextField loMarkField = new JTextField(str(existing.loMark), 5);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        int row = 0;
        row = addSubjectRow(panel, c, row, "Home Language:", homeLangField, hlMarkField);
        row = addSubjectRow(panel, c, row, "First Additional Language:", falField, falMarkField);
        row = addSubjectRow(panel, c, row, "Maths / Maths Lit:", mathSubjectField, mathMarkField);
        row = addSubjectRow(panel, c, row, "Elective 4:", sub4Field, sub4MarkField);
        row = addSubjectRow(panel, c, row, "Elective 5:", sub5Field, sub5MarkField);
        row = addSubjectRow(panel, c, row, "Elective 6:", sub6Field, sub6MarkField);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel("Life Orientation Mark:"), c);
        c.gridx = 1;
        panel.add(loMarkField, c);
        row++;

        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        panel.add(statusLabel, c);

        JButton saveButton = new JButton("Save");
        ProfileService.StudentMarksData finalExisting = existing;
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfileService.StudentMarksData m = new ProfileService.StudentMarksData();
                try {
                    m.homeLang = homeLangField.getText().trim();
                    m.hlMark = parseIntOrNull(hlMarkField.getText());
                    m.firstAddLang = falField.getText().trim();
                    m.falMark = parseIntOrNull(falMarkField.getText());
                    m.mathSubject = mathSubjectField.getText().trim();
                    m.mathMark = parseIntOrNull(mathMarkField.getText());
                    m.sub4 = sub4Field.getText().trim();
                    m.sub4Mark = parseIntOrNull(sub4MarkField.getText());
                    m.sub5 = sub5Field.getText().trim();
                    m.sub5Mark = parseIntOrNull(sub5MarkField.getText());
                    m.sub6 = sub6Field.getText().trim();
                    m.sub6Mark = parseIntOrNull(sub6MarkField.getText());
                    m.loMark = parseIntOrNull(loMarkField.getText());
                } catch (NumberFormatException nfe) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Marks must be whole numbers.");
                    return;
                }

                try {
                    profileService.upsertStudentMarks(userId, m);
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Saved.");
                } catch (SQLException ex) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Could not save: " + ex.getMessage());
                }
            }
        });
        c.gridy = row;
        panel.add(saveButton, c);

        return panel;
    }

    private int addSubjectRow(JPanel panel, GridBagConstraints c, int row, String label,
            JTextField subjectField, JTextField markField) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel(label), c);

        JPanel pair = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pair.add(subjectField);
        pair.add(new JLabel("Mark:"));
        pair.add(markField);
        c.gridx = 1;
        panel.add(pair, c);

        return row + 1;
    }

    private Integer parseIntOrNull(String text) {
        text = text == null ? "" : text.trim();
        if (text.isEmpty()) {
            return null;
        }
        return Integer.parseInt(text);
    }

    private String str(Object value) {
        return value == null ? "" : value.toString();
    }

    // ---------- My Offers (provider only) ----------
    private JPanel buildMyOffersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Name", "Faculty", "APS Req", "Avg Req", "Deadline", "Slots Left"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable offersTable = new JTable(tableModel);
        panel.add(new JScrollPane(offersTable), BorderLayout.CENTER);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        Runnable refresh = () -> {
            tableModel.setRowCount(0);
            try {
                List<Offer> offers = offerService.getOffersByProvider(userId);
                for (Offer o : offers) {
                    tableModel.addRow(new Object[]{
                        o.getOfferName(), o.getFaculty(), o.getApsRequired(),
                        o.getAvgRequired(), o.getDeadline(), o.getNumBursariesLeft()
                    });
                }
            } catch (SQLException e) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Could not load offers: " + e.getMessage());
            }
        };
        refresh.run();

        JButton addOfferButton = new JButton("Add New Offer");
        addOfferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddOfferDialog(refresh, statusLabel);
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(addOfferButton, BorderLayout.WEST);
        bottom.add(statusLabel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddOfferDialog(Runnable onSaved, JLabel parentStatusLabel) {
        JDialog dialog = new JDialog(this, "Add New Offer", true);
        dialog.setSize(420, 560);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(15);
        JTextArea infoArea = new JTextArea(3, 15);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        JTextField apsField = new JTextField(5);
        JTextField avgField = new JTextField(5);
        JTextField hlField = new JTextField(5);
        JTextField falField = new JTextField(5);
        JTextField mathField = new JTextField(5);
        JTextField sub4SubjectField = new JTextField(10);
        JTextField sub4Field = new JTextField(5);
        JTextField sub5SubjectField = new JTextField(10);
        JTextField sub5Field = new JTextField(5);
        JTextField sub6SubjectField = new JTextField(10);
        JTextField sub6Field = new JTextField(5);
        JTextField facultyField = new JTextField(15);
        JTextField deadlineField = new JTextField(15);
        deadlineField.setToolTipText("Format: YYYY-MM-DD");
        JTextField slotsField = new JTextField(5);

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        int row = 0;
        row = addRow(panel, c, row, "Offer Name:", nameField);
        row = addRow(panel, c, row, "Additional Info:", new JScrollPane(infoArea));
        row = addRow(panel, c, row, "APS Required:", apsField);
        row = addRow(panel, c, row, "Average Required:", avgField);
        row = addRow(panel, c, row, "Home Lang Required:", hlField);
        row = addRow(panel, c, row, "FAL Required:", falField);
        row = addRow(panel, c, row, "Maths Required:", mathField);
        row = addElectiveRow(panel, c, row, "Elective 4:", sub4SubjectField, sub4Field);
        row = addElectiveRow(panel, c, row, "Elective 5:", sub5SubjectField, sub5Field);
        row = addElectiveRow(panel, c, row, "Elective 6:", sub6SubjectField, sub6Field);
        row = addRow(panel, c, row, "Faculty:", facultyField);
        row = addRow(panel, c, row, "Deadline (YYYY-MM-DD):", deadlineField);
        row = addRow(panel, c, row, "Number of Bursaries:", slotsField);

        c.gridx = 0;
        c.gridy = row++;
        c.gridwidth = 2;
        panel.add(statusLabel, c);

        JButton saveButton = new JButton("Save Offer");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        statusLabel.setText("Offer name is required.");
                        return;
                    }
                    LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());

                    offerService.addOffer(
                            name,
                            infoArea.getText().trim(),
                            parseIntOrZero(apsField.getText()),
                            parseIntOrZero(avgField.getText()),
                            parseIntOrZero(hlField.getText()),
                            parseIntOrZero(falField.getText()),
                            parseIntOrZero(mathField.getText()),
                            parseIntOrZero(sub4Field.getText()), sub4SubjectField.getText().trim(),
                            parseIntOrZero(sub5Field.getText()), sub5SubjectField.getText().trim(),
                            parseIntOrZero(sub6Field.getText()), sub6SubjectField.getText().trim(),
                            facultyField.getText().trim(),
                            deadline,
                            parseIntOrZero(slotsField.getText()),
                            userId);

                    dialog.dispose();
                    onSaved.run();

                } catch (DateTimeParseException dtpe) {
                    statusLabel.setText("Deadline must be in YYYY-MM-DD format.");
                } catch (NumberFormatException nfe) {
                    statusLabel.setText("Requirement fields must be whole numbers.");
                } catch (SQLException ex) {
                    statusLabel.setText("Could not save offer: " + ex.getMessage());
                }
            }
        });
        c.gridy = row;
        panel.add(saveButton, c);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private int parseIntOrZero(String text) {
        text = text == null ? "" : text.trim();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    // ---------- Help ----------
    private JPanel buildHelpTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.add(makeHelpButton("Search", "help/Search.txt", displayArea));
        buttonPanel.add(makeHelpButton("Filter", "help/Filter.txt", displayArea));
        buttonPanel.add(makeHelpButton("Create Account", "help/CreateAcc.txt", displayArea));
        buttonPanel.add(makeHelpButton("Enter Marks", "help/Marks.txt", displayArea));
        buttonPanel.add(makeHelpButton("Change Information", "help/ChangeInfo.txt", displayArea));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        return panel;
    }

    private JButton makeHelpButton(String label, String filePath, JTextArea displayArea) {
        JButton button = new JButton(label);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayArea.setText(readHelpFile(filePath));
            }
        });
        return button;
    }

    private String readHelpFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Help file not found: " + filePath
                    + "\n\n(Help text files haven't been written yet.)";
        }
        return sb.toString();
    }

    // ---------- shared helpers ----------
    private int addRow(JPanel panel, GridBagConstraints c, int row, String labelText, JComponent field) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel(labelText), c);

        c.gridx = 1;
        panel.add(field, c);

        return row + 1;
    }

    private int addElectiveRow(JPanel panel, GridBagConstraints c, int row, String label,
            JTextField subjectField, JTextField markField) {
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        panel.add(new JLabel(label), c);

        JPanel pair = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        pair.add(new JLabel("Subject:"));
        pair.add(subjectField);
        pair.add(new JLabel("Min mark:"));
        pair.add(markField);
        c.gridx = 1;
        panel.add(pair, c);

        return row + 1;
    }
}
