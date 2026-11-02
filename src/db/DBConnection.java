package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    // Database configuration (change if using a different password in MySQL / XAMPP)
    private static final String HOST     = "localhost";
    private static final int    PORT     = 3306;
    private static final String DB_NAME  = "placement_portal";
    private static final String USER     = "root";
    private static final String PASSWORD = "sambambam";

    private static final String BASE_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_URL   = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Returns a connection directly to the placement_portal database
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath. Ensure mysql-connector-j.jar is in lib/.", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    // Quick check to see if MySQL server is reachable on the root URL
    public static boolean isAvailable() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD)) {
                return conn != null && !conn.isClosed();
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] MySQL Check Notice: " + e.getMessage());
            return false;
        }
    }

    // Automatically creates the database, tables, and seed data if not already present
    public static void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Notice: MySQL Driver not present. Continuing in in-memory mode.");
            return;
        }

        // 1. Create database if it doesn't exist
        try (Connection rootConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = rootConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (Exception e) {
            System.err.println("Notice: Could not connect to MySQL server: " + e.getMessage());
            return;
        }

        // 2. Create tables inside placement_portal
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "email VARCHAR(100) UNIQUE NOT NULL, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(20) NOT NULL, "
                    + "cgpa DOUBLE DEFAULT 0.0, "
                    + "branch VARCHAR(50) DEFAULT '', "
                    + "backlogs INT DEFAULT 0, "
                    + "company_name VARCHAR(100) DEFAULT ''"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS companies ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(100) UNIQUE NOT NULL, "
                    + "approved BOOLEAN DEFAULT TRUE"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS job_drives ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "company_id INT NOT NULL, "
                    + "role VARCHAR(100) NOT NULL, "
                    + "ctc_lpa DOUBLE NOT NULL, "
                    + "deadline DATE NOT NULL, "
                    + "min_cgpa DOUBLE NOT NULL, "
                    + "max_backlogs INT NOT NULL, "
                    + "allowed_branches VARCHAR(255) NOT NULL, "
                    + "FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE"
                    + ")");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS applications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "student_id INT NOT NULL, "
                    + "drive_id INT NOT NULL, "
                    + "status VARCHAR(20) NOT NULL DEFAULT 'APPLIED', "
                    + "UNIQUE KEY unique_student_drive (student_id, drive_id), "
                    + "FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (drive_id) REFERENCES job_drives(id) ON DELETE CASCADE"
                    + ")");

            // Seed default users if users table is empty
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO users (name, email, password, role) VALUES "
                        + "('Admin User', 'admin@portal.com', 'admin123', 'ADMIN')");
                stmt.executeUpdate("INSERT INTO users (name, email, password, role, company_name) VALUES "
                        + "('Ravi Kumar', 'recruiter@tcs.com', 'tcs123', 'RECRUITER', 'TCS')");
                stmt.executeUpdate("INSERT INTO users (name, email, password, role, cgpa, branch, backlogs) VALUES "
                        + "('Alice Sharma', 'alice@student.com', 'pass123', 'STUDENT', 9.1, 'CSE', 0), "
                        + "('Bob Patel', 'bob@student.com', 'pass123', 'STUDENT', 7.2, 'ECE', 1), "
                        + "('Charlie Mehta', 'charlie@student.com', 'pass123', 'STUDENT', 6.5, 'MECH', 2)");

                stmt.executeUpdate("INSERT INTO companies (name, approved) VALUES ('TCS', TRUE), ('Infosys', TRUE)");

                stmt.executeUpdate("INSERT INTO job_drives (company_id, role, ctc_lpa, deadline, min_cgpa, max_backlogs, allowed_branches) VALUES "
                        + "(1, 'SDE (Full-time)', 12.5, '2026-11-30', 8.5, 0, 'CSE,IT'), "
                        + "(1, 'Systems Engineer', 7.0, '2026-12-15', 7.0, 1, 'CSE,ECE'), "
                        + "(1, 'BPS Trainee', 3.5, '2026-12-31', 6.0, 3, 'CSE,ECE,MECH,IT,CIVIL'), "
                        + "(2, 'Tech Analyst', 9.0, '2026-11-20', 7.5, 0, 'CSE,ECE,MECH')");
            }

            System.out.println("✓ MySQL database 'placement_portal' connected and synchronized.");
        } catch (Exception e) {
            System.err.println("Notice during DB setup: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("[Diagnostic] Checking connection to MySQL...");
        if (isAvailable()) {
            System.out.println("[Diagnostic] MySQL Server is REACHABLE!");
            initializeDatabase();
        } else {
            System.out.println("[Diagnostic] MySQL Server is NOT reachable.");
        }
    }
}
