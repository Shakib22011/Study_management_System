package main.model;

import java.util.Date;

public class Attendance {
    private int id;
    private int studentId;
    private int courseId;
    private Date attendanceDate;
    private boolean status; // true for present, false for absent

    public Attendance() {}

    public Attendance(int id, int studentId, int courseId, Date attendanceDate, boolean status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}