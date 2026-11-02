package service;

import db.DBConnection;
import exception.DuplicateApplicationException;
import exception.InvalidLoginException;
import exception.NotEligibleException;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlacementManager {

    private List<User>        users;
    private List<Company>     companies;
    private List<JobDrive>    drives;
    private List<Application> applications;
    private boolean           usingDatabase;

    public PlacementManager() {
        users        = new ArrayList<>();
        companies    = new ArrayList<>();
        drives       = new ArrayList<>();
        applications = new ArrayList<>();

        // Check if MySQL is available and initialize
        if (DBConnection.isAvailable()) {
            System.out.println("[PlacementManager] Connecting to MySQL Database via JDBC...");
            DBConnection.initializeDatabase();
            usingDatabase = true;
            loadFromDatabase();
        } else {
            System.out.println("[PlacementManager] MySQL not reachable. Falling back to in-memory mock data.");
            usingDatabase = false;
            loadSampleData();
        }
    }

    public boolean isUsingDatabase() {
        return usingDatabase;
    }

    // -------------------------------------------------------------------------
    // Load data from MySQL Database via JDBC
    // -------------------------------------------------------------------------
    public void loadFromDatabase() {
        users.clear();
        companies.clear();
        drives.clear();
        applications.clear();

        try (Connection conn = DBConnection.getConnection()) {
            // 1. Load Companies
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM companies")) {
                while (rs.next()) {
                    companies.add(new Company(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getBoolean("approved")
                    ));
                }
            }

            // 2. Load Users
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
                while (rs.next()) {
                    int id          = rs.getInt("id");
                    String name     = rs.getString("name");
                    String email    = rs.getString("email");
                    String password = rs.getString("password");
                    String role     = rs.getString("role");

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        users.add(new Admin(id, name, email, password));
                    } else if ("RECRUITER".equalsIgnoreCase(role)) {
                        users.add(new Recruiter(id, name, email, password, rs.getString("company_name")));
                    } else {
                        Student s = new Student(id, name, email, password,
                                rs.getDouble("cgpa"),
                                rs.getString("branch"),
                                rs.getInt("backlogs"));
                        users.add(s);
                    }
                }
            }

            // 3. Load Job Drives
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM job_drives")) {
                while (rs.next()) {
                    int id          = rs.getInt("id");
                    int companyId   = rs.getInt("company_id");
                    String role     = rs.getString("role");
                    double ctc      = rs.getDouble("ctc_lpa");
                    LocalDate date  = rs.getDate("deadline").toLocalDate();
                    double minCgpa  = rs.getDouble("min_cgpa");
                    int maxBacklogs = rs.getInt("max_backlogs");
                    String branches = rs.getString("allowed_branches");

                    Company comp = findCompanyById(companyId);
                    if (comp != null) {
                        EligibilityCriteria criteria = new EligibilityCriteria(
                                minCgpa, maxBacklogs, branches.split(","));
                        drives.add(new JobDrive(id, comp, role, ctc, date, criteria));
                    }
                }
            }

            // 4. Load Applications
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM applications")) {
                while (rs.next()) {
                    int id         = rs.getInt("id");
                    int studentId  = rs.getInt("student_id");
                    int driveId    = rs.getInt("drive_id");
                    String status  = rs.getString("status");

                    Student student = findStudentById(studentId);
                    JobDrive drive  = findDriveById(driveId);

                    if (student != null && drive != null) {
                        Application app = new Application(id, student, drive, Application.Status.valueOf(status));
                        student.addApplication(app);
                        applications.add(app);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error loading from database: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // In-memory fallback sample data
    // -------------------------------------------------------------------------
    private void loadSampleData() {
        users.add(new Admin(1, "Admin User", "admin@portal.com", "admin123"));
        users.add(new Recruiter(2, "Ravi Kumar", "recruiter@tcs.com", "tcs123", "TCS"));

        Student alice   = new Student(3, "Alice Sharma",  "alice@student.com",   "pass123", 9.1, "CSE",  0);
        Student bob     = new Student(4, "Bob Patel",     "bob@student.com",     "pass123", 7.2, "ECE",  1);
        Student charlie = new Student(5, "Charlie Mehta", "charlie@student.com", "pass123", 6.5, "MECH", 2);
        users.add(alice);
        users.add(bob);
        users.add(charlie);

        Company tcs     = new Company(1, "TCS",     true);
        Company infosys = new Company(2, "Infosys", true);
        companies.add(tcs);
        companies.add(infosys);

        EligibilityCriteria topTier  = new EligibilityCriteria(8.5, 0, "CSE", "IT");
        EligibilityCriteria midTier  = new EligibilityCriteria(7.0, 1, "CSE", "ECE");
        EligibilityCriteria openTier = new EligibilityCriteria(6.0, 3, "CSE", "ECE", "MECH", "IT", "CIVIL");
        EligibilityCriteria infoTier = new EligibilityCriteria(7.5, 0, "CSE", "ECE", "MECH");

        drives.add(new JobDrive(1, tcs,     "SDE (Full-time)",   12.5, LocalDate.of(2026, 11, 30), topTier));
        drives.add(new JobDrive(2, tcs,     "Systems Engineer",   7.0, LocalDate.of(2026, 12, 15), midTier));
        drives.add(new JobDrive(3, tcs,     "BPS Trainee",        3.5, LocalDate.of(2026, 12, 31), openTier));
        drives.add(new JobDrive(4, infosys, "Tech Analyst",       9.0, LocalDate.of(2026, 11, 20), infoTier));
    }

    // -------------------------------------------------------------------------
    // Authentication & Registration
    // -------------------------------------------------------------------------
    public User login(String email, String password) throws InvalidLoginException {
        if (usingDatabase) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id       = rs.getInt("id");
                        String name  = rs.getString("name");
                        String role  = rs.getString("role");

                        if ("ADMIN".equalsIgnoreCase(role)) {
                            return new Admin(id, name, email, password);
                        } else if ("RECRUITER".equalsIgnoreCase(role)) {
                            return new Recruiter(id, name, email, password, rs.getString("company_name"));
                        } else {
                            Student s = findStudentById(id);
                            if (s != null) return s;
                            return new Student(id, name, email, password,
                                    rs.getDouble("cgpa"),
                                    rs.getString("branch"),
                                    rs.getInt("backlogs"));
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Database login error: " + e.getMessage());
            }
            throw new InvalidLoginException("Invalid email or password.");
        }

        // In-memory fallback
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (user.login(password)) return user;
                throw new InvalidLoginException("Invalid email or password.");
            }
        }
        throw new InvalidLoginException("Invalid email or password.");
    }

    public Student registerStudent(String name, String email, String password,
                                   double cgpa, String branch, int backlogs) throws Exception {
        // Validate email uniqueness
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                throw new Exception("Email is already registered.");
            }
        }

        int newId = users.size() + 1;
        if (usingDatabase) {
            String sql = "INSERT INTO users (name, email, password, role, cgpa, branch, backlogs) VALUES (?, ?, ?, 'STUDENT', ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setDouble(4, cgpa);
                pstmt.setString(5, branch);
                pstmt.setInt(6, backlogs);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newId = generatedKeys.getInt(1);
                    }
                }
            } catch (SQLException e) {
                throw new Exception("Failed to save to database: " + e.getMessage());
            }
        }

        Student newStudent = new Student(newId, name, email, password, cgpa, branch, backlogs);
        users.add(newStudent);
        return newStudent;
    }

    // -------------------------------------------------------------------------
    // Student Drive & Application Operations
    // -------------------------------------------------------------------------
    public List<JobDrive> getEligibleDrives(Student student) {
        List<JobDrive> eligible = new ArrayList<>();
        for (JobDrive drive : drives) {
            if (drive.getCompany().isApproved() && drive.getCriteria().isEligible(student)) {
                eligible.add(drive);
            }
        }
        return eligible;
    }

    public Application apply(Student student, JobDrive drive)
            throws NotEligibleException, DuplicateApplicationException {

        if (!drive.getCriteria().isEligible(student)) {
            throw new NotEligibleException(
                student.getName() + " is not eligible for " + drive.getRole()
                + " at " + drive.getCompany().getName()
                + ".\nRequires: " + drive.getCriteria());
        }

        if (student.hasApplied(drive)) {
            throw new DuplicateApplicationException(
                "You already applied to " + drive.getRole()
                + " at " + drive.getCompany().getName() + ".");
        }

        int appId = applications.size() + 1;
        if (usingDatabase) {
            String sql = "INSERT INTO applications (student_id, drive_id, status) VALUES (?, ?, 'APPLIED')";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, student.getId());
                pstmt.setInt(2, drive.getId());
                pstmt.executeUpdate();

                try (ResultSet gk = pstmt.getGeneratedKeys()) {
                    if (gk.next()) appId = gk.getInt(1);
                }
            } catch (SQLException e) {
                System.err.println("JDBC apply error: " + e.getMessage());
            }
        }

        Application app = new Application(appId, student, drive, Application.Status.APPLIED);
        student.addApplication(app);
        applications.add(app);
        return app;
    }

    public List<Application> getApplicationsForStudent(Student student) {
        return student.getApplications();
    }

    // -------------------------------------------------------------------------
    // Admin Operations
    // -------------------------------------------------------------------------
    public List<Company> getAllCompanies() {
        return new ArrayList<>(companies);
    }

    public void addCompany(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Company name cannot be empty.");
        }
        for (Company c : companies) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                throw new Exception("Company already exists.");
            }
        }

        int compId = companies.size() + 1;
        if (usingDatabase) {
            String sql = "INSERT INTO companies (name, approved) VALUES (?, TRUE)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name.trim());
                pstmt.executeUpdate();
                try (ResultSet gk = pstmt.getGeneratedKeys()) {
                    if (gk.next()) compId = gk.getInt(1);
                }
            } catch (SQLException e) {
                throw new Exception("Database error: " + e.getMessage());
            }
        }

        companies.add(new Company(compId, name.trim(), true));
    }

    public void toggleCompanyApproval(Company comp) {
        boolean newStatus = !comp.isApproved();
        comp.setApproved(newStatus);

        if (usingDatabase) {
            String sql = "UPDATE companies SET approved = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, newStatus);
                pstmt.setInt(2, comp.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Database toggle error: " + e.getMessage());
            }
        }
    }

    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }

    // -------------------------------------------------------------------------
    // Recruiter Operations
    // -------------------------------------------------------------------------
    public List<JobDrive> getDrivesForCompany(String companyName) {
        List<JobDrive> list = new ArrayList<>();
        for (JobDrive d : drives) {
            if (d.getCompany().getName().equalsIgnoreCase(companyName)) {
                list.add(d);
            }
        }
        return list;
    }

    public List<Application> getApplicationsForCompany(String companyName) {
        List<Application> list = new ArrayList<>();
        for (Application a : applications) {
            if (a.getDrive().getCompany().getName().equalsIgnoreCase(companyName)) {
                list.add(a);
            }
        }
        return list;
    }

    public JobDrive postJobDrive(String companyName, String role, double ctcLpa,
                                LocalDate deadline, double minCgpa, int maxBacklogs,
                                String branchesCsv) throws Exception {
        Company comp = null;
        for (Company c : companies) {
            if (c.getName().equalsIgnoreCase(companyName)) {
                comp = c;
                break;
            }
        }
        if (comp == null) {
            // Auto-register company if missing
            addCompany(companyName);
            for (Company c : companies) {
                if (c.getName().equalsIgnoreCase(companyName)) comp = c;
            }
        }

        String[] branchArray = branchesCsv.split(",");
        for (int i = 0; i < branchArray.length; i++) {
            branchArray[i] = branchArray[i].trim().toUpperCase();
        }
        EligibilityCriteria criteria = new EligibilityCriteria(minCgpa, maxBacklogs, branchArray);

        int driveId = drives.size() + 1;
        if (usingDatabase && comp != null) {
            String sql = "INSERT INTO job_drives (company_id, role, ctc_lpa, deadline, min_cgpa, max_backlogs, allowed_branches) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, comp.getId());
                pstmt.setString(2, role);
                pstmt.setDouble(3, ctcLpa);
                pstmt.setDate(4, Date.valueOf(deadline));
                pstmt.setDouble(5, minCgpa);
                pstmt.setInt(6, maxBacklogs);
                pstmt.setString(7, String.join(",", branchArray));
                pstmt.executeUpdate();
                try (ResultSet gk = pstmt.getGeneratedKeys()) {
                    if (gk.next()) driveId = gk.getInt(1);
                }
            } catch (SQLException e) {
                throw new Exception("Database error: " + e.getMessage());
            }
        }

        JobDrive drive = new JobDrive(driveId, comp, role, ctcLpa, deadline, criteria);
        drives.add(drive);
        return drive;
    }

    public void updateApplicationStatus(Application app, Application.Status newStatus) {
        app.setStatus(newStatus);
        if (usingDatabase) {
            String sql = "UPDATE applications SET status = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newStatus.name());
                pstmt.setInt(2, app.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Database status update error: " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper Lookups
    // -------------------------------------------------------------------------
    private Company findCompanyById(int id) {
        for (Company c : companies) if (c.getId() == id) return c;
        return null;
    }

    private Student findStudentById(int id) {
        for (User u : users) {
            if (u.getId() == id && u instanceof Student) return (Student) u;
        }
        return null;
    }

    private JobDrive findDriveById(int id) {
        for (JobDrive d : drives) if (d.getId() == id) return d;
        return null;
    }

    public List<JobDrive> getAllDrives() { return new ArrayList<>(drives); }
}
