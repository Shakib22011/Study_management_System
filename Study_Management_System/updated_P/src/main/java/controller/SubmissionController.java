package main.controller;

import main.model.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubmissionController {

    /**
     * Submit an assignment either as a GitHub link or a local file path.
     * This method detects if the submission is a URL (starts with http/https),
     * and stores it in github_link column; otherwise, stores in file_path column.
     *
     * @param studentId    ID of the student submitting
     * @param assignmentId ID of the assignment being submitted
     * @param submission   GitHub URL or local file path
     * @return true if submission successful, false otherwise
     */
    public static boolean submitAssignment(int studentId, int assignmentId, String submission) {
        try (Connection conn = DBConnection.getConnection()) {

            // Optional: Verify assignment exists (safety check)
            String checkAssignment = "SELECT id FROM assignments WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkAssignment)) {
                checkStmt.setInt(1, assignmentId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    System.err.println("Assignment not found with ID: " + assignmentId);
                    return false;
                }
            }

            // Insert into submissions table
            String insertSQL = "INSERT INTO submissions " +
                    "(student_id, assignment_id, github_link, file_path, submitted_at) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setInt(1, studentId);
                insertStmt.setInt(2, assignmentId);

                if (submission.startsWith("http://") || submission.startsWith("https://")) {
                    insertStmt.setString(3, submission);  // github_link
                    insertStmt.setNull(4, java.sql.Types.VARCHAR); // file_path
                } else {
                    insertStmt.setNull(3, java.sql.Types.VARCHAR); // github_link
                    insertStmt.setString(4, submission);           // file_path
                }

                LocalDateTime now = LocalDateTime.now();
                insertStmt.setString(5, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                int affectedRows = insertStmt.executeUpdate();
                return affectedRows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}