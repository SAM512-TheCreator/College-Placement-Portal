package model;

public class Application {

    public enum Status { APPLIED, SHORTLISTED, REJECTED, SELECTED }

    private Student  student;
    private JobDrive drive;
    private Status   status;

    public Application(Student student, JobDrive drive) {
        this.student = student;
        this.drive   = drive;
        this.status  = Status.APPLIED;
    }

    public Student  getStudent() { return student; }
    public JobDrive getDrive()   { return drive; }
    public Status   getStatus()  { return status; }
    public void     setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return student.getName() + " → " + drive.getRole()
             + " at " + drive.getCompany().getName() + " [" + status + "]";
    }
}
