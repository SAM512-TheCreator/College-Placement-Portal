package gui;

import exception.DuplicateApplicationException;
import exception.NotEligibleException;
import model.Application;
import model.JobDrive;
import model.Student;
import service.PlacementManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private PlacementManager  manager;
    private Student           student;
    private JTable            driveTable;
    private DefaultTableModel tableModel;
    private List<JobDrive>    eligibleDrives;

    public StudentDashboard(Student student) {
        this.manager = Main.getManager();
        this.student = student;

        setTitle("Dashboard — " + student.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 430);
        setLocationRelativeTo(null);

        buildUI();
        loadDrives();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(new Color(245, 248, 255));
        setContentPane(root);

        JLabel info = new JLabel(
            "Welcome, " + student.getName()
            + "  |  Branch: " + student.getBranch()
            + "  |  CGPA: "   + student.getCgpa()
            + "  |  Backlogs: "+ student.getBacklogs(),
            SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.BOLD, 14));
        info.setForeground(new Color(30, 60, 120));
        root.add(info, BorderLayout.NORTH);

        String[] columns = {"Company", "Role", "Package (LPA)", "Deadline"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        driveTable = new JTable(tableModel);
        driveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driveTable.setRowHeight(24);
        driveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        driveTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(driveTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Eligible Drives"));
        root.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        bottom.setOpaque(false);

        JButton applyBtn = new JButton("✅ Apply to Selected Drive");
        applyBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        applyBtn.setBackground(new Color(30, 150, 80));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.addActionListener(e -> handleApply());
        bottom.add(applyBtn);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> loadDrives());
        bottom.add(refreshBtn);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private void loadDrives() {
        eligibleDrives = manager.getEligibleDrives(student);
        tableModel.setRowCount(0);

        if (eligibleDrives.isEmpty()) {
            tableModel.addRow(new Object[]{"—", "No eligible drives found", "—", "—"});
        } else {
            for (JobDrive d : eligibleDrives) {
                tableModel.addRow(new Object[]{
                    d.getCompany().getName(),
                    d.getRole(),
                    d.getCtcLpa() + " LPA",
                    d.getDeadline().toString()
                });
            }
        }
    }

    private void handleApply() {
        int row = driveTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a drive first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (eligibleDrives == null || eligibleDrives.isEmpty()) return;

        JobDrive selected = eligibleDrives.get(row);
        try {
            Application app = manager.apply(student, selected);
            JOptionPane.showMessageDialog(this,
                "🎉 Applied successfully!\n\nDrive: " + selected.getRole()
                + " at " + selected.getCompany().getName()
                + "\nStatus: " + app.getStatus(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDrives();
        } catch (NotEligibleException ex) {
            JOptionPane.showMessageDialog(this, "❌ " + ex.getMessage(), "Not Eligible", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateApplicationException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ " + ex.getMessage(), "Already Applied", JOptionPane.WARNING_MESSAGE);
        }
    }
}
