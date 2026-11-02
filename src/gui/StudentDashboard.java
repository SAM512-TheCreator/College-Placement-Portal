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
    private DefaultTableModel driveModel;
    private List<JobDrive>    eligibleDrives;

    private JTable            myAppsTable;
    private DefaultTableModel myAppsModel;

    public StudentDashboard(Student student) {
        this.manager = Main.getManager();
        this.student = student;

        setTitle("Student Dashboard — " + student.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);

        buildUI();
        loadDrives();
        loadMyApplications();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(new Color(245, 248, 255));
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel info = new JLabel(
            "👤 " + student.getName()
            + "  |  Branch: " + student.getBranch()
            + "  |  CGPA: "   + student.getCgpa()
            + "  |  Backlogs: "+ student.getBacklogs(),
            SwingConstants.LEFT);
        info.setFont(new Font("SansSerif", Font.BOLD, 14));
        info.setForeground(new Color(30, 60, 120));
        header.add(info, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(manager).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // ── Tab 1: Available Drives ──────────────────────────────────────────
        JPanel drivesPanel = new JPanel(new BorderLayout(10, 10));

        driveModel = new DefaultTableModel(new String[]{"Company", "Role", "Package (LPA)", "Deadline"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        driveTable = new JTable(driveModel);
        driveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driveTable.setRowHeight(24);
        driveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        drivesPanel.add(new JScrollPane(driveTable), BorderLayout.CENTER);

        JPanel driveBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        JButton applyBtn = new JButton("✅ Apply to Selected Drive");
        applyBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        applyBtn.setBackground(new Color(30, 150, 80));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> handleApply());
        driveBottom.add(applyBtn);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> {
            loadDrives();
            loadMyApplications();
        });
        driveBottom.add(refreshBtn);
        drivesPanel.add(driveBottom, BorderLayout.SOUTH);

        tabs.addTab("🎯 Eligible Job Drives", drivesPanel);

        // ── Tab 2: My Applications ───────────────────────────────────────────
        JPanel myAppsPanel = new JPanel(new BorderLayout(10, 10));

        myAppsModel = new DefaultTableModel(new String[]{"Company", "Role", "Package", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        myAppsTable = new JTable(myAppsModel);
        myAppsTable.setRowHeight(24);
        myAppsPanel.add(new JScrollPane(myAppsTable), BorderLayout.CENTER);

        JPanel appBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshMyAppsBtn = new JButton("🔄 Refresh Status");
        refreshMyAppsBtn.addActionListener(e -> loadMyApplications());
        appBottom.add(refreshMyAppsBtn);
        myAppsPanel.add(appBottom, BorderLayout.SOUTH);

        tabs.addTab("📋 My Applications & Status", myAppsPanel);

        root.add(tabs, BorderLayout.CENTER);
    }

    private void loadDrives() {
        eligibleDrives = manager.getEligibleDrives(student);
        driveModel.setRowCount(0);

        if (eligibleDrives.isEmpty()) {
            driveModel.addRow(new Object[]{"—", "No eligible drives found", "—", "—"});
        } else {
            for (JobDrive d : eligibleDrives) {
                driveModel.addRow(new Object[]{
                    d.getCompany().getName(),
                    d.getRole(),
                    d.getCtcLpa() + " LPA",
                    d.getDeadline().toString()
                });
            }
        }
    }

    private void loadMyApplications() {
        myAppsModel.setRowCount(0);
        List<Application> apps = manager.getApplicationsForStudent(student);
        if (apps.isEmpty()) {
            myAppsModel.addRow(new Object[]{"—", "No applications submitted yet", "—", "—"});
        } else {
            for (Application a : apps) {
                myAppsModel.addRow(new Object[]{
                        a.getDrive().getCompany().getName(),
                        a.getDrive().getRole(),
                        a.getDrive().getCtcLpa() + " LPA",
                        a.getStatus()
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
            loadMyApplications();
        } catch (NotEligibleException ex) {
            JOptionPane.showMessageDialog(this, "❌ " + ex.getMessage(), "Not Eligible", JOptionPane.ERROR_MESSAGE);
        } catch (DuplicateApplicationException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ " + ex.getMessage(), "Already Applied", JOptionPane.WARNING_MESSAGE);
        }
    }
}
