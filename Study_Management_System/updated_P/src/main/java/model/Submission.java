package main.model;

public class Submission {
    private int id;
    private int studentId;
    private int assignmentId;  // new field
    private int courseId;
    private String submissionLink;

    public Submission() {}

    public Submission(int id, int studentId, int assignmentId, int courseId, String submissionLink) {
        this.id = id;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.submissionLink = submissionLink;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getSubmissionLink() { return submissionLink; }
    public void setSubmissionLink(String submissionLink) { this.submissionLink = submissionLink; }
}