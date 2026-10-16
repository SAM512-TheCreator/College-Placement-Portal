package gui;

import exception.InvalidLoginException;
import model.User;
import service.PlacementManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private PlacementManager manager;
    private JTextField        emailField;
    private JPasswordField    passwordField;

    public LoginFrame(PlacementManager manager) {
        this.manager = manager;
        setTitle("Placement Portal — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel outer = new JPanel(new BorderLayout(10, 10));
        outer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        outer.setBackground(new Color(245, 248, 255));
        setContentPane(outer);

        JLabel title = new JLabel("🎓 Placement Portal", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(30, 60, 120));
        outer.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(passwordField, gbc);

        outer.add(form, BorderLayout.CENTER);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 90, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        passwordField.addActionListener(e -> handleLogin());
        loginBtn.addActionListener(e -> handleLogin());

        outer.add(loginBtn, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        try {
            User user = manager.login(email, password);
            user.showDashboard(); // polymorphic — opens the right window automatically
            dispose();
        } catch (InvalidLoginException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
