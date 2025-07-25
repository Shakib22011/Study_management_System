package main.controller;

import main.model.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseController {

    // Get course name by course code
    public static String getCourseName(String courseCode) {
        String courseName = "Course Not Found";

        String sql = "SELECT course_name FROM courses WHERE course_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                courseName = rs.getString("course_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseName;
    }
}