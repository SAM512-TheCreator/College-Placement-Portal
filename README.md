# College Placement Portal — v2 (with JDBC & Full Dashboards)

A Java Swing desktop application demonstrating **Object-Oriented Programming (OOP)**, **Custom Exception Handling**, and **JDBC Database Persistence** with MySQL.

---

## What's New in v2

1. **JDBC Database Persistence (MySQL)**:
   - Connects to MySQL using JDBC (`java.sql.Connection`, `PreparedStatement`, `ResultSet`).
   - Automatically initializes the `placement_portal` database and tables on startup via `DBConnection`.
   - **Smart Fallback**: If MySQL is not running or the JDBC driver is missing, it automatically falls back to in-memory mode so the application never crashes during a live demo.
2. **Student Registration (`RegisterFrame`)**:
   - New students can register directly from the login screen (Name, Email, Password, Branch, CGPA, Backlogs).
3. **Full Admin Dashboard (`AdminDashboard`)**:
   - View all companies, add new companies, toggle approval status.
   - View all applications across the entire college in real time.
4. **Full Recruiter Dashboard (`RecruiterDashboard`)**:
   - Post new job drives with custom eligibility criteria (role, CTC, deadline, min CGPA, allowed branches).
   - Review applicants for their company drives and update status (`SHORTLISTED`, `SELECTED`, `REJECTED`).
5. **Enhanced Student Dashboard (`StudentDashboard`)**:
   - Tab 1: View eligible drives and apply.
   - Tab 2: View "My Applications & Status" to track acceptance/rejection in real time.

---

## Project Structure

```
Course_Project/
├── src/
│   ├── db/
│   │   └── DBConnection.java          ← JDBC connection manager & table initializer
│   ├── model/
│   │   ├── User.java                  ← abstract base class (Abstraction)
│   │   ├── Student.java               ← extends User (Inheritance & Polymorphism)
│   │   ├── Admin.java                 ← extends User
│   │   ├── Recruiter.java             ← extends User
│   │   ├── Company.java
│   │   ├── EligibilityCriteria.java   ← business rules (isEligible)
│   │   ├── JobDrive.java
│   │   └── Application.java           ← contains Status enum
│   ├── exception/
│   │   ├── NotEligibleException.java
│   │   ├── DuplicateApplicationException.java
│   │   └── InvalidLoginException.java
│   ├── service/
│   │   └── PlacementManager.java      ← JDBC queries + business logic + fallback
│   └── gui/
│       ├── Main.java                  ← entry point
│       ├── LoginFrame.java            ← login & registration trigger
│       ├── RegisterFrame.java         ← student registration window
│       ├── StudentDashboard.java      ← 2-tab student portal
│       ├── AdminDashboard.java        ← company & application management
│       └── RecruiterDashboard.java    ← job posting & candidate review
├── lib/                               ← place mysql-connector-j.jar here
├── schema.sql                         ← MySQL database & table creation script
├── README.md
└── .gitignore
```

---

## How to Set Up MySQL (XAMPP / MySQL Workbench)

### Option A: Automatic Setup (Easiest)
1. Start MySQL in **XAMPP** (or start your local MySQL service on port 3306).
2. Default user is `root` with no password (`""`). If your MySQL has a password, update line 14 of `src/db/DBConnection.java`:
   ```java
   private static final String PASSWORD = "your_password";
   ```
3. Run the app with the MySQL Connector JAR (see instructions below). `DBConnection.initializeDatabase()` will automatically create the database, tables, and sample data!

### Option B: Manual Setup
Open **phpMyAdmin** or **MySQL Workbench**, open `schema.sql`, and execute it.

---

## Compile & Run

### 1. Download MySQL Connector JAR (One-time)
Download `mysql-connector-j-8.x.x.jar` (from MySQL official site or Maven) and place it inside the `lib/` folder:
```
Course_Project/lib/mysql-connector-j.jar
```

### 2. Compile
```bash
mkdir -p out
javac -d out -sourcepath src $(find src -name "*.java" | tr '\n' ' ')
```

### 3. Run

**With MySQL Connector JAR:**
```bash
# macOS / Linux
java -cp "out:lib/*" gui.Main

# Windows (Command Prompt)
java -cp "out;lib/*" gui.Main
```

**Without MySQL (Runs in in-memory fallback mode):**
```bash
java -cp out gui.Main
```

---

## Test Credentials

| Email | Password | Role | What to Test |
|---|---|---|---|
| `admin@portal.com` | `admin123` | Admin | Add companies, toggle approval, view all applications |
| `recruiter@tcs.com` | `tcs123` | Recruiter | Post new drive, review applicants, change status to Selected/Shortlisted |
| `alice@student.com` | `pass123` | Student | High CGPA (9.1, CSE) — eligible for all drives |
| `bob@student.com` | `pass123` | Student | Mid CGPA (7.2, ECE, 1 backlog) — eligible for mid-tier drives |
| `charlie@student.com` | `pass123` | Student | MECH, 6.5 CGPA, 2 backlogs — eligible for open drives |
| *(Any new student)* | *(custom)* | Student | Click **"New Student? Register Here"** on login screen |
