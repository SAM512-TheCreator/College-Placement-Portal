package gui;

import model.Application;
import model.JobDrive;
import model.Recruiter;
import service.PlacementManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class RecruiterDashboard extends JFrame {

    private PlacementManager  manager;
    private Recruiter         recruiter;

    private DefaultTableModel driveModel;
    private JTable            driveTable;

    private DefaultTableModel appModel;
    private JTable            appTable;
    private List<Application> companyApps;

    // Post drive form fields
    private JTextField roleField;
    private JTextField ctcField;
    private JTextField deadlineField;
    private JTextField minCgpaField;
    private JTextField maxBacklogsField;
    private JTextField branchesField;

    public RecruiterDashboard(Recruiter recruiter) {
        this.manager   = Main.getManager();
        this.recruiter = recruiter;

        setTitle("Recruiter Portal — " + recruiter.getName() + " (" + recruiter.getCompanyName() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        buildUI();
        loadDrives();
        loadApplications();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.setBackground(new Color(245, 248, 255));
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("💼 Recruiter Dashboard — " + recruiter.getName() + " [" + recruiter.getCompanyName() + "]", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(new Color(30, 60, 120));
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(manager).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // ── Tab 1: Drives Management ─────────────────────────────────────────
        JPanel driveTab = new JPanel(new BorderLayout(10, 10));

        // Form to post a drive
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Post a New Job Drive"));

        form.add(new JLabel("Role / Title:"));
        roleField = new JTextField("Software Engineer");
        form.add(roleField);

        form.add(new JLabel("Package (CTC in LPA):"));
        ctcField = new JTextField("10.0");
        form.add(ctcField);

        form.add(new JLabel("Deadline (YYYY-MM-DD):"));
        deadlineField = new JTextField(LocalDate.now().plusMonths(1).toString());
        form.add(deadlineField);

        form.add(new JLabel("Minimum CGPA:"));
        minCgpaField = new JTextField("7.5");
        form.add(minCgpaField);

        form.add(new JLabel("Max Backlogs Allowed:"));
        maxBacklogsField = new JTextField("0");
        form.add(maxBacklogsField);

        form.add(new JLabel("Branches (comma-separated):"));
        branchesField = new JTextField("CSE,IT,ECE");
        form.add(branchesField);

        JPanel postWrap = new JPanel(new BorderLayout(5, 5));
        postWrap.add(form, BorderLayout.CENTER);
        JButton postBtn = new JButton("🚀 Publish Job Drive");
        postBtn.setBackground(new Color(30, 130, 70));
        postBtn.setForeground(Color.WHITE);
        postBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        postBtn.addActionListener(e -> handlePostDrive());
        postWrap.add(postBtn, BorderLayout.SOUTH);
        driveTab.add(postWrap, BorderLayout.NORTH);

        // Drives table
        driveModel = new DefaultTableModel(new String[]{"ID", "Role", "Package", "Deadline", "Eligibility"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        driveTable = new JTable(driveModel);
        driveTable.setRowHeight(22);
        JScrollPane driveScroll = new JScrollPane(driveTable);
        driveScroll.setBorder(BorderFactory.createTitledBorder("Active Drives for " + recruiter.getCompanyName()));
        driveTab.add(driveScroll, BorderLayout.CENTER);

        tabs.addTab("📢 Job Drives", driveTab);

        // ── Tab 2: Applicants Review ─────────────────────────────────────────
        JPanel appTab = new JPanel(new BorderLayout(10, 10));

        appModel = new DefaultTableModel(new String[]{"Student Name", "Branch", "CGPA", "Backlogs", "Applied Role", "Current Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        appTable = new JTable(appModel);
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appTable.setRowHeight(24);
        appTab.add(new JScrollPane(appTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        JButton shortlistBtn = new JButton("⭐ Shortlist Candidate");
        shortlistBtn.addActionListener(e -> updateStatus(Application.Status.SHORTLISTED));
        actionPanel.add(shortlistBtn);

        JButton selectBtn = new JButton("🎉 Select (Job Offer)");
        selectBtn.setBackground(new Color(30, 150, 80));
        selectBtn.setForeground(Color.WHITE);
        selectBtn.addActionListener(e -> updateStatus(Application.Status.SELECTED));
        actionPanel.add(selectBtn);

        JButton rejectBtn = new JButton("❌ Reject");
        rejectBtn.addActionListener(e -> updateStatus(Application.Status.REJECTED));
        actionPanel.add(rejectBtn);

        JButton refreshAppBtn = new JButton("🔄 Refresh");
        refreshAppBtn.addActionListener(e -> loadApplications());
        actionPanel.add(refreshAppBtn);

        appTab.add(actionPanel, BorderLayout.SOUTH);

        tabs.addTab("👥 Review Candidates", appTab);

        root.add(tabs, BorderLayout.CENTER);
    }

    private void loadDrives() {
        driveModel.setRowCount(0);
        List<JobDrive> list = manager.getDrivesForCompany(recruiter.getCompanyName());
        for (JobDrive d : list) {
            driveModel.addRow(new Object[]{
                    d.getId(),
                    d.getRole(),
                    d.getCtcLpa() + " LPA",
                    d.getDeadline().toString(),
                    d.getCriteria().toString()
            });
        }
    }

    private void loadApplications() {
        appModel.setRowCount(0);
        companyApps = manager.getApplicationsForCompany(recruiter.getCompanyName());
        if (companyApps.isEmpty()) {
            appModel.addRow(new Object[]{"—", "No applicants yet", "—", "—", "—", "—"});
        } else {
            for (Application a : companyApps) {
                appModel.addRow(new Object[]{
                        a.getStudent().getName(),
                        a.getStudent().getBranch(),
                        a.getStudent().getCgpa(),
                        a.getStudent().getBacklogs(),
                        a.getDrive().getRole(),
                        a.getStatus()
                });
            }
        }
    }

    private void handlePostDrive() {
        String role = roleField.getText().trim();
        String ctcStr = ctcField.getText().trim();
        String deadlineStr = deadlineField.getText().trim();
        String minCgpaStr = minCgpaField.getText().trim();
        String maxBacklogsStr = maxBacklogsField.getText().trim();
        String branches = branchesField.getText().trim();

        if (role.isEmpty() || ctcStr.isEmpty() || deadlineStr.isEmpty() || branches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all drive details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double ctc = Double.parseDouble(ctcStr);
            LocalDate deadline = LocalDate.parse(deadlineStr);
            double minCgpa = Double.parseDouble(minCgpaStr);
            int maxBacklogs = Integer.parseInt(maxBacklogsStr);

            manager.postJobDrive(recruiter.getCompanyName(), role, ctc, deadline, minCgpa, maxBacklogs, branches);
            JOptionPane.showMessageDialog(this, "Job Drive for '" + role + "' posted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDrives();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to post drive: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(Application.Status status) {
        int row = appTable.getSelectedRow();
        if (row < 0 || companyApps == null || companyApps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an applicant from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Application app = companyApps.get(row);
        manager.updateApplicationStatus(app, status);
        loadApplications();
        JOptionPane.showMessageDialog(this,
                "Status for " + app.getStudent().getName() + " updated to: " + status,
                "Status Updated", JOptionPane.INFORMATION_MESSAGE);
    }
}
