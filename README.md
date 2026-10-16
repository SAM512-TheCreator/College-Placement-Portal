# College Placement Portal — v1

A Java Swing desktop application built as a mid-semester OOP review project.
Demonstrates **Inheritance, Abstraction, Polymorphism, Encapsulation**, and custom **Exception Handling**.

---

## Project Structure

```
Course_Project/
├── src/
│   ├── model/
│   │   ├── User.java                  ← abstract base class
│   │   ├── Student.java               ← extends User
│   │   ├── Admin.java                 ← extends User
│   │   ├── Recruiter.java             ← extends User
│   │   ├── Company.java
│   │   ├── EligibilityCriteria.java
│   │   ├── JobDrive.java
│   │   └── Application.java           ← contains Status enum
│   ├── exception/
│   │   ├── NotEligibleException.java
│   │   ├── DuplicateApplicationException.java
│   │   └── InvalidLoginException.java
│   ├── service/
│   │   └── PlacementManager.java      ← all business logic + hardcoded data
│   └── gui/
│       ├── Main.java                  ← entry point
│       ├── LoginFrame.java
│       ├── StudentDashboard.java
│       └── SimpleDashboard.java
└── out/                               ← compiled .class files (auto-generated, not in git)
```

---

## Compile

```bash
mkdir -p out
javac -d out -sourcepath src $(find src -name "*.java" | tr '\n' ' ')
```

**Windows (Command Prompt):**
```cmd
mkdir out
javac -d out -sourcepath src src\model\*.java src\exception\*.java src\service\*.java src\gui\*.java
```

---

## Run

```bash
java -cp out gui.Main
```

---

## Test Credentials

| Email | Password | Role | Drives visible |
|---|---|---|---|
| `admin@portal.com` | `admin123` | Admin | Welcome screen |
| `recruiter@tcs.com` | `tcs123` | Recruiter | Welcome screen |
| `alice@student.com` | `pass123` | Student — CSE, CGPA 9.1, 0 backlogs | All 4 drives |
| `bob@student.com` | `pass123` | Student — ECE, CGPA 7.2, 1 backlog | 2 drives |
| `charlie@student.com` | `pass123` | Student — MECH, CGPA 6.5, 2 backlogs | 1 drive |

---

## OOP Concepts

| Concept | Where |
|---|---|
| **Abstraction** | `User` is abstract; `showDashboard()` is abstract — subclasses must implement it |
| **Inheritance** | `Student`, `Admin`, `Recruiter` extend `User` and inherit `login()`, id, name, email, password |
| **Polymorphism** | `LoginFrame` calls `user.showDashboard()` on a `User` reference — JVM picks the right window at runtime |
| **Encapsulation** | All fields are `private`; `PlacementManager` lists are never accessed directly by the GUI |
| **Exception Handling** | `NotEligibleException`, `DuplicateApplicationException`, `InvalidLoginException` — all shown as GUI popups |

---

## Not in v1 (future work)

- Registration / sign-up
- File or database persistence
- Admin manage companies / view all applications
- Recruiter shortlist / select candidates
- Notifications, reports
# College-Placement-Portal
# College-Placement-Portal
# College-Placement-Portal
# College-Placement-Portal
# College-Placement-Portal
# College-Placement-Portal
