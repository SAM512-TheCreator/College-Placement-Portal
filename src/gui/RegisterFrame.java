package gui;

import model.Student;
import service.PlacementManager;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private PlacementManager manager;
    private JTextField        nameField;
    private JTextField        emailField;
    private JPasswordField    passwordField;
    private JComboBox<String> branchBox;
    private JTextField        cgpaField;
    private JTextField        backlogsField;

    public RegisterFrame(PlacementManager manager) {
        this.manager = manager;

        setTitle("Student Registration — Placement Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        root.setBackground(new Color(245, 248, 255));
        setContentPane(root);

        JLabel title = new JLabel("📝 Create Student Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 60, 120));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(emailField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(18);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(passwordField, gbc);

        // Branch
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(new JLabel("Branch:"), gbc);
        branchBox = new JComboBox<>(new String[]{"CSE", "ECE", "MECH", "IT", "CIVIL"});
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(branchBox, gbc);

        // CGPA
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(new JLabel("CGPA (0.0 - 10.0):"), gbc);
        cgpaField = new JTextField("8.0", 18);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cgpaField, gbc);

        // Backlogs
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        form.add(new JLabel("Active Backlogs:"), gbc);
        backlogsField = new JTextField("0", 18);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(backlogsField, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnPanel.setOpaque(false);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(30, 140, 70));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        registerBtn.addActionListener(e -> handleRegister());
        btnPanel.add(registerBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(cancelBtn);

        root.add(btnPanel, BorderLayout.SOUTH);
    }

    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String branch   = (String) branchBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double cgpa;
        int backlogs;
        try {
            cgpa = Double.parseDouble(cgpaField.getText().trim());
            if (cgpa < 0.0 || cgpa > 10.0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "CGPA must be a valid number between 0.0 and 10.0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            backlogs = Integer.parseInt(backlogsField.getText().trim());
            if (backlogs < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Backlogs must be a non-negative integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Student s = manager.registerStudent(name, email, password, cgpa, branch, backlogs);
            JOptionPane.showMessageDialog(this,
                    "🎉 Registration successful!\n\nWelcome, " + s.getName() + "!\nYou can now log in with your email and password.",
                    "Registered", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
