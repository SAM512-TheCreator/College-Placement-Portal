package model;

public class Company {

    private int     id;
    private String  name;
    private boolean approved;

    public Company(int id, String name, boolean approved) {
        this.id       = id;
        this.name     = name;
        this.approved = approved;
    }

    public Company(String name, boolean approved) {
        this(0, name, approved);
    }

    public int     getId()         { return id; }
    public void    setId(int id)   { this.id = id; }
    public String  getName()       { return name; }
    public boolean isApproved()    { return approved; }
    public void    setApproved(boolean approved) { this.approved = approved; }

    @Override
    public String toString() { return name + (approved ? " [Approved]" : " [Pending]"); }
}
