package model;

import java.util.Arrays;
import java.util.List;

public class EligibilityCriteria {

    private double       minCgpa;
    private List<String> allowedBranches;
    private int          maxBacklogs;

    public EligibilityCriteria(double minCgpa, int maxBacklogs, String... allowedBranches) {
        this.minCgpa         = minCgpa;
        this.maxBacklogs     = maxBacklogs;
        this.allowedBranches = Arrays.asList(allowedBranches);
    }

    // Returns true only if the student passes all three checks
    public boolean isEligible(Student s) {
        return s.getCgpa()     >= minCgpa
            && s.getBacklogs() <= maxBacklogs
            && allowedBranches.contains(s.getBranch());
    }

    public double       getMinCgpa()         { return minCgpa; }
    public int          getMaxBacklogs()      { return maxBacklogs; }
    public List<String> getAllowedBranches()  { return allowedBranches; }

    @Override
    public String toString() {
        return "Min CGPA: " + minCgpa
             + " | Max Backlogs: " + maxBacklogs
             + " | Branches: " + allowedBranches;
    }
}
