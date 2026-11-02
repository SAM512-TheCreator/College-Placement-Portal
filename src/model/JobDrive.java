package model;

import java.time.LocalDate;

public class JobDrive {

    private int                 id;
    private Company             company;
    private String              role;
    private double              ctcLpa;
    private LocalDate           deadline;
    private EligibilityCriteria criteria;

    public JobDrive(int id, Company company, String role, double ctcLpa,
                    LocalDate deadline, EligibilityCriteria criteria) {
        this.id       = id;
        this.company  = company;
        this.role     = role;
        this.ctcLpa   = ctcLpa;
        this.deadline = deadline;
        this.criteria = criteria;
    }

    public JobDrive(Company company, String role, double ctcLpa,
                    LocalDate deadline, EligibilityCriteria criteria) {
        this(0, company, role, ctcLpa, deadline, criteria);
    }

    public int                 getId()       { return id; }
    public void                setId(int id) { this.id = id; }
    public Company             getCompany()  { return company; }
    public String              getRole()     { return role; }
    public double              getCtcLpa()   { return ctcLpa; }
    public LocalDate           getDeadline() { return deadline; }
    public EligibilityCriteria getCriteria() { return criteria; }

    @Override
    public String toString() {
        return company.getName() + " — " + role + " | " + ctcLpa + " LPA";
    }
}
