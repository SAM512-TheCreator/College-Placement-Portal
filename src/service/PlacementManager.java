package service;

import exception.DuplicateApplicationException;
import exception.InvalidLoginException;
import exception.NotEligibleException;
import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlacementManager {

    private List<User>        users;
    private List<Company>     companies;
    private List<JobDrive>    drives;
    private List<Application> applications;

    public PlacementManager() {
        users        = new ArrayList<>();
        companies    = new ArrayList<>();
        drives       = new ArrayList<>();
        applications = new ArrayList<>();
        loadSampleData();
    }

    // -------------------------------------------------------------------------
    // Hardcoded sample data — loaded once at startup
    //
    // Login credentials:
    //   admin@portal.com    / admin123   → Admin
    //   recruiter@tcs.com   / tcs123    → Recruiter
    //   alice@student.com   / pass123   → CSE, 9.1 CGPA, 0 backlogs (sees all drives)
    //   bob@student.com     / pass123   → ECE, 7.2 CGPA, 1 backlog  (sees 2 drives)
    //   charlie@student.com / pass123   → MECH, 6.5 CGPA, 2 backlogs (sees 1 drive)
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

        Company tcs     = new Company("TCS",     true);
        Company infosys = new Company("Infosys", true);
        companies.add(tcs);
        companies.add(infosys);

        EligibilityCriteria topTier  = new EligibilityCriteria(8.5, 0, "CSE", "IT");
        EligibilityCriteria midTier  = new EligibilityCriteria(7.0, 1, "CSE", "ECE");
        EligibilityCriteria openTier = new EligibilityCriteria(6.0, 3, "CSE", "ECE", "MECH", "IT", "CIVIL");
        EligibilityCriteria infoTier = new EligibilityCriteria(7.5, 0, "CSE", "ECE", "MECH");

        drives.add(new JobDrive(tcs,     "SDE (Full-time)",   12.5, LocalDate.of(2026, 11, 30), topTier));
        drives.add(new JobDrive(tcs,     "Systems Engineer",   7.0, LocalDate.of(2026, 12, 15), midTier));
        drives.add(new JobDrive(tcs,     "BPS Trainee",        3.5, LocalDate.of(2026, 12, 31), openTier));
        drives.add(new JobDrive(infosys, "Tech Analyst",       9.0, LocalDate.of(2026, 11, 20), infoTier));
    }

    // Find user by email, then verify password
    public User login(String email, String password) throws InvalidLoginException {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (user.login(password)) return user;
                throw new InvalidLoginException("Invalid email or password.");
            }
        }
        throw new InvalidLoginException("Invalid email or password.");
    }

    // Returns only the drives this student is eligible for
    public List<JobDrive> getEligibleDrives(Student student) {
        List<JobDrive> eligible = new ArrayList<>();
        for (JobDrive drive : drives) {
            if (drive.getCompany().isApproved() && drive.getCriteria().isEligible(student)) {
                eligible.add(drive);
            }
        }
        return eligible;
    }

    // Apply a student to a drive — throws if not eligible or already applied
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

        Application app = new Application(student, drive);
        student.addApplication(app);
        applications.add(app);
        return app;
    }

    public List<JobDrive> getAllDrives() { return new ArrayList<>(drives); }
}
