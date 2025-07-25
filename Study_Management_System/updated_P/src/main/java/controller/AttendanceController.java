package main.controller;

import main.model.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceController {

    public static boolean submitAttendance(int courseId, String date, Map<Integer, String> studentStatusMap) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO attendance (student_id, course_id, date, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (Map.Entry<Integer, String> entry : studentStatusMap.entrySet()) {
                ps.setInt(1, entry.getKey()); // student_id
                ps.setInt(2, courseId);
                ps.setString(3, date);
                ps.setString(4, entry.getValue()); // "present" or "absent"
                ps.addBatch();
            }

            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch attendance records for a student in a course.
     * Returns a LinkedHashMap of Date -> Boolean (true = present, false = absent),
     * ordered by date ascending.
     */
    public static Map<Date, Boolean> getAttendanceRecords(int studentId, String courseCode) {
        Map<Date, Boolean> attendanceMap = new LinkedHashMap<>();

        String sql = "SELECT a.date, a.status " +
                "FROM attendance a " +
                "JOIN courses c ON a.course_id = c.id " +
                "WHERE a.student_id = ? AND c.course_code = ? " +
                "ORDER BY a.date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, courseCode);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Date date = rs.getDate("date");
                String status = rs.getString("status");
                attendanceMap.put(date, "present".equalsIgnoreCase(status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceMap;
    }
}