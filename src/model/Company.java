package model;

public class Company {

    private String  name;
    private boolean approved;

    public Company(String name, boolean approved) {
        this.name     = name;
        this.approved = approved;
    }

    public String  getName()       { return name; }
    public boolean isApproved()    { return approved; }
    public void    setApproved(boolean approved) { this.approved = approved; }

    @Override
    public String toString() { return name + (approved ? " [Approved]" : " [Pending]"); }
}
