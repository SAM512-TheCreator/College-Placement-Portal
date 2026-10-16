package model;

// Base class for all users in the system
public abstract class User {

    private int    id;
    private String name;
    private String email;
    private String password;

    public User(int id, String name, String email, String password) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    // Returns true if the given password matches
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    // Each subclass opens its own dashboard window
    public abstract void showDashboard();

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }

    // Package-private so only the service layer can read it
    String getPassword() { return password; }

    @Override
    public String toString() { return name + " (" + email + ")"; }
}
