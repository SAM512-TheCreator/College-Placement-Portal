package model;

public class Application {

    public enum Status { APPLIED, SHORTLISTED, REJECTED, SELECTED }

    private int      id;
    private Student  student;
    private JobDrive drive;
    private Status   status;

    public Application(int id, Student student, JobDrive drive, Status status) {
        this.id      = id;
        this.student = student;
        this.drive   = drive;
        this.status  = status;
    }

    public Application(Student student, JobDrive drive) {
        this(0, student, drive, Status.APPLIED);
    }

    public int      getId()                  { return id; }
    public void     setId(int id)            { this.id = id; }
    public Student  getStudent()             { return student; }
    public JobDrive getDrive()               { return drive; }
    public Status   getStatus()              { return status; }
    public void     setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return student.getName() + " → " + drive.getRole()
             + " at " + drive.getCompany().getName() + " [" + status + "]";
    }
}
