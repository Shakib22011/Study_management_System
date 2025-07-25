package main.controller;

import main.model.Assignment;
import main.model.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentController {

    // Get all assignments for a course
    public static List<Assignment> getAssignmentsByCourse(String courseCode) {
        List<Assignment> assignments = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT a.* FROM assignments a " +
                    "JOIN courses c ON a.course_id = c.id " +
                    "WHERE c.course_code = ? ORDER BY a.assignment_number ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Assignment a = new Assignment();
                a.setId(rs.getInt("id"));
                a.setTeacherId(rs.getInt("teacher_id"));
                a.setCourseId(rs.getInt("course_id"));
                a.setAssignmentNumber(rs.getInt("assignment_number")); // new
                a.setDescription(rs.getString("assignment_text"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                assignments.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    // Add new assignment (with assignment number)
    public static boolean addAssignment(int teacherId, String courseCode, String assignmentText, int assignmentNumber) {
        try (Connection conn = DBConnection.getConnection()) {

            // Get course id from course code
            String courseSql = "SELECT id FROM courses WHERE course_code = ?";
            PreparedStatement courseStmt = conn.prepareStatement(courseSql);
            courseStmt.setString(1, courseCode);
            ResultSet courseRs = courseStmt.executeQuery();

            if (!courseRs.next()) {
                return false; // course not found
            }

            int courseId = courseRs.getInt("id");

            // Insert assignment with assignment number
            String insertSql = "INSERT INTO assignments (teacher_id, course_id, assignment_text, assignment_number, created_at) " +
                    "VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, teacherId);
            insertStmt.setInt(2, courseId);
            insertStmt.setString(3, assignmentText);
            insertStmt.setInt(4, assignmentNumber);

            int rowsInserted = insertStmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}