package gui;

import model.Admin;
import model.Application;
import model.Company;
import service.PlacementManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private PlacementManager  manager;
    private Admin             admin;

    private DefaultTableModel compModel;
    private JTable            compTable;
    private JTextField        newCompField;

    private DefaultTableModel appModel;
    private JTable            appTable;

    public AdminDashboard(Admin admin) {
        this.manager = Main.getManager();
        this.admin   = admin;

        setTitle("Admin Portal — " + admin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 520);
        setLocationRelativeTo(null);

        buildUI();
        loadCompanies();
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
        JLabel title = new JLabel("🛡️ Administrator Dashboard — " + admin.getName(), SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 60, 120));
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(manager).setVisible(true);
        });
        header.add(logoutBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();

        // ── Tab 1: Companies ─────────────────────────────────────────────────
        JPanel compPanel = new JPanel(new BorderLayout(10, 10));
        compPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        compModel = new DefaultTableModel(new String[]{"ID", "Company Name", "Approval Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        compTable = new JTable(compModel);
        compTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        compTable.setRowHeight(24);
        compPanel.add(new JScrollPane(compTable), BorderLayout.CENTER);

        // Company controls
        JPanel compControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        compControls.add(new JLabel("New Company:"));
        newCompField = new JTextField(15);
        compControls.add(newCompField);

        JButton addCompBtn = new JButton("➕ Add Company");
        addCompBtn.addActionListener(e -> handleAddCompany());
        compControls.add(addCompBtn);

        JButton toggleBtn = new JButton("⚡ Toggle Approval Status");
        toggleBtn.addActionListener(e -> handleToggleApproval());
        compControls.add(toggleBtn);

        JButton refreshCompBtn = new JButton("🔄 Refresh");
        refreshCompBtn.addActionListener(e -> loadCompanies());
        compControls.add(refreshCompBtn);

        compPanel.add(compControls, BorderLayout.SOUTH);
        tabs.addTab("🏢 Manage Companies", compPanel);

        // ── Tab 2: Applications ──────────────────────────────────────────────
        JPanel appPanel = new JPanel(new BorderLayout(10, 10));
        appPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        appModel = new DefaultTableModel(new String[]{"App ID", "Student Name", "Branch", "CGPA", "Company", "Role", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        appTable = new JTable(appModel);
        appTable.setRowHeight(24);
        appPanel.add(new JScrollPane(appTable), BorderLayout.CENTER);

        JPanel appControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshAppBtn = new JButton("🔄 Refresh Applications");
        refreshAppBtn.addActionListener(e -> loadApplications());
        appControls.add(refreshAppBtn);
        appPanel.add(appControls, BorderLayout.SOUTH);

        tabs.addTab("📄 View All Applications", appPanel);

        root.add(tabs, BorderLayout.CENTER);
    }

    private void loadCompanies() {
        compModel.setRowCount(0);
        List<Company> list = manager.getAllCompanies();
        for (Company c : list) {
            compModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.isApproved() ? "✅ Approved" : "❌ Pending / Suspended"
            });
        }
    }

    private void handleAddCompany() {
        String name = newCompField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a company name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            manager.addCompany(name);
            newCompField.setText("");
            loadCompanies();
            JOptionPane.showMessageDialog(this, "Company '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleToggleApproval() {
        int row = compTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a company first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<Company> list = manager.getAllCompanies();
        Company comp = list.get(row);
        manager.toggleCompanyApproval(comp);
        loadCompanies();
    }

    private void loadApplications() {
        appModel.setRowCount(0);
        List<Application> list = manager.getAllApplications();
        if (list.isEmpty()) {
            appModel.addRow(new Object[]{"—", "No applications yet", "—", "—", "—", "—", "—"});
        } else {
            for (Application a : list) {
                appModel.addRow(new Object[]{
                        a.getId(),
                        a.getStudent().getName(),
                        a.getStudent().getBranch(),
                        a.getStudent().getCgpa(),
                        a.getDrive().getCompany().getName(),
                        a.getDrive().getRole(),
                        a.getStatus()
                });
            }
        }
    }
}
