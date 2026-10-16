package model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private double            cgpa;
    private String            branch;
    private int               backlogs;
    private List<Application> applications;

    public Student(int id, String name, String email, String password,
                   double cgpa, String branch, int backlogs) {
        super(id, name, email, password);
        this.cgpa         = cgpa;
        this.branch       = branch;
        this.backlogs     = backlogs;
        this.applications = new ArrayList<>();
    }

    @Override
    public void showDashboard() {
        new gui.StudentDashboard(this).setVisible(true);
    }

    public void addApplication(Application app) {
        applications.add(app);
    }

    // Check if student already applied to a drive
    public boolean hasApplied(JobDrive drive) {
        for (Application app : applications) {
            if (app.getDrive().equals(drive)) return true;
        }
        return false;
    }

    public double           getCgpa()         { return cgpa; }
    public String           getBranch()       { return branch; }
    public int              getBacklogs()     { return backlogs; }
    public List<Application> getApplications() { return new ArrayList<>(applications); }

    @Override
    public String toString() {
        return getName() + " | " + branch + " | CGPA: " + cgpa;
    }
}
