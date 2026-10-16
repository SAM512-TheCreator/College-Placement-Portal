package model;

public class Recruiter extends User {

    private String companyName;

    public Recruiter(int id, String name, String email, String password,
                     String companyName) {
        super(id, name, email, password);
        this.companyName = companyName;
    }

    @Override
    public void showDashboard() {
        new gui.SimpleDashboard(getName(), "Recruiter @ " + companyName).setVisible(true);
    }

    public String getCompanyName() { return companyName; }
}
