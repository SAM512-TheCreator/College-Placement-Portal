package gui;

import javax.swing.*;
import java.awt.*;

public class SimpleDashboard extends JFrame {

    public SimpleDashboard(String userName, String role) {
        setTitle(role + " Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI(userName, role);
    }

    private void buildUI(String userName, String role) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));
        setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.insets = new Insets(10, 20, 10, 20);

        JLabel welcome = new JLabel("Welcome, " + userName + "!", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcome.setForeground(new Color(30, 60, 120));
        gbc.gridy = 0;
        panel.add(welcome, gbc);

        JLabel roleLabel = new JLabel("Role: " + role, SwingConstants.CENTER);
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 1;
        panel.add(roleLabel, gbc);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame(Main.getManager()).setVisible(true);
        });
        gbc.gridy = 2;
        panel.add(logout, gbc);
    }
}
