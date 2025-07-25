package main.model;

public class SubmissionDetail {
    private int studentId;
    private String studentName;
    private String submissionLink;

    public SubmissionDetail(int studentId, String studentName, String submissionLink) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionLink = submissionLink;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubmissionLink() {
        return submissionLink;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setSubmissionLink(String submissionLink) {
        this.submissionLink = submissionLink;
    }
}