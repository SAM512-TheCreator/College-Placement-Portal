-- College Placement Portal Database Schema (MySQL)
-- You can run this in MySQL Workbench or phpMyAdmin

CREATE DATABASE IF NOT EXISTS placement_portal;
USE placement_portal;

-- 1. Users table (Stores Students, Admins, and Recruiters)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,          -- 'STUDENT', 'ADMIN', 'RECRUITER'
    cgpa DOUBLE DEFAULT 0.0,            -- for students
    branch VARCHAR(50) DEFAULT '',      -- for students
    backlogs INT DEFAULT 0,             -- for students
    company_name VARCHAR(100) DEFAULT '' -- for recruiters
);

-- 2. Companies table
CREATE TABLE IF NOT EXISTS companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    approved BOOLEAN DEFAULT TRUE
);

-- 3. Job Drives table
CREATE TABLE IF NOT EXISTS job_drives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role VARCHAR(100) NOT NULL,
    ctc_lpa DOUBLE NOT NULL,
    deadline DATE NOT NULL,
    min_cgpa DOUBLE NOT NULL,
    max_backlogs INT NOT NULL,
    allowed_branches VARCHAR(255) NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- 4. Applications table
CREATE TABLE IF NOT EXISTS applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    drive_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED', -- 'APPLIED', 'SHORTLISTED', 'REJECTED', 'SELECTED'
    UNIQUE KEY unique_student_drive (student_id, drive_id),
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (drive_id) REFERENCES job_drives(id) ON DELETE CASCADE
);

-- =============================================================================
-- SAMPLE DATA
-- =============================================================================

INSERT INTO users (name, email, password, role) VALUES 
('Admin User', 'admin@portal.com', 'admin123', 'ADMIN');

INSERT INTO users (name, email, password, role, company_name) VALUES 
('Ravi Kumar', 'recruiter@tcs.com', 'tcs123', 'RECRUITER', 'TCS');

INSERT INTO users (name, email, password, role, cgpa, branch, backlogs) VALUES 
('Alice Sharma', 'alice@student.com', 'pass123', 'STUDENT', 9.1, 'CSE', 0),
('Bob Patel', 'bob@student.com', 'pass123', 'STUDENT', 7.2, 'ECE', 1),
('Charlie Mehta', 'charlie@student.com', 'pass123', 'STUDENT', 6.5, 'MECH', 2);

INSERT INTO companies (name, approved) VALUES 
('TCS', TRUE),
('Infosys', TRUE);

INSERT INTO job_drives (company_id, role, ctc_lpa, deadline, min_cgpa, max_backlogs, allowed_branches) VALUES 
(1, 'SDE (Full-time)', 12.5, '2026-11-30', 8.5, 0, 'CSE,IT'),
(1, 'Systems Engineer', 7.0, '2026-12-15', 7.0, 1, 'CSE,ECE'),
(1, 'BPS Trainee', 3.5, '2026-12-31', 6.0, 3, 'CSE,ECE,MECH,IT,CIVIL'),
(2, 'Tech Analyst', 9.0, '2026-11-20', 7.5, 0, 'CSE,ECE,MECH');
