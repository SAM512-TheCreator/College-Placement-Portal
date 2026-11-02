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
        setSize(440, 340);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel outer = new JPanel(new BorderLayout(10, 10));
        outer.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        outer.setBackground(new Color(245, 248, 255));
        setContentPane(outer);

        // Header with title and database mode badge
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("🎓 Placement Portal", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(30, 60, 120));
        header.add(title);

        String dbStatusText = manager.isUsingDatabase() 
                ? "● Database: MySQL Connected (JDBC Active)" 
                : "○ Database: In-Memory Mock (Start MySQL to persist)";
        JLabel dbBadge = new JLabel(dbStatusText, SwingConstants.CENTER);
        dbBadge.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dbBadge.setForeground(manager.isUsingDatabase() ? new Color(20, 120, 50) : new Color(150, 100, 20));
        header.add(dbBadge);

        outer.add(header, BorderLayout.NORTH);

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

        // Buttons section: Login + Register
        JPanel bottom = new JPanel(new GridLayout(2, 1, 8, 8));
        bottom.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 90, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton registerBtn = new JButton("📝 New Student? Register Here");
        registerBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        registerBtn.addActionListener(e -> new RegisterFrame(manager).setVisible(true));

        passwordField.addActionListener(e -> handleLogin());
        loginBtn.addActionListener(e -> handleLogin());

        bottom.add(loginBtn);
        bottom.add(registerBtn);

        outer.add(bottom, BorderLayout.SOUTH);
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
