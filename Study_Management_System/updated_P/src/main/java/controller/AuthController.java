package main.controller;

import main.model.DBConnection;
import main.utils.PasswordUtil;
import main.utils.Validator;
import main.utils.OTPUtil;
import main.utils.MailSender;
import main.view.DashboardStudent;
import main.view.DashboardTeacher;
import main.view.LoginView;

import javax.swing.*;
import java.sql.*;

public class AuthController {

    // LOGIN METHOD
    public static void login(String email, String password, JFrame frame) {
        if (!Validator.isValidEmail(email) || !Validator.isValidField(password)) {
            JOptionPane.showMessageDialog(frame, "Invalid email or password format.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                String hashedInput = PasswordUtil.hashPassword(password);
                if (hashedInput.equals(storedHash)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");

                    if ("teacher".equals(role)) {
                        new DashboardTeacher(userId);
                    } else {
                        new DashboardStudent(userId);
                    }
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect password.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "User not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database error: " + e.getMessage());
        }
    }

    // REGISTER METHOD (ONLY AFTER OTP VERIFIED)
    public static void register(String name, String email, String password, String role,
                                String studentId, String courseCode, String courseName, JFrame frame) {
        if (!Validator.isValidEmail(email) || !Validator.isValidField(password) || !Validator.isValidField(name)) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please check your fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert course if not exists
            String insertCourse = "INSERT IGNORE INTO courses (course_code, course_name) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCourse)) {
                ps.setString(1, courseCode);
                ps.setString(2, courseName);
                ps.executeUpdate();
            }

            // Get course ID
            int courseId = -1;
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM courses WHERE course_code = ?")) {
                ps.setString(1, courseCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("id");
                }
            }

            if (courseId == -1) throw new SQLException("Course not found.");

            int userId = -1;

            // Check if user already exists
            String checkUserSQL = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkUserSQL)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id"); // Existing user
                }
            }

            // Insert new user if not exists
            if (userId == -1) {
                String insertUser = "INSERT INTO users (name, email, password, role, student_id) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, PasswordUtil.hashPassword(password));
                    ps.setString(4, role);
                    ps.setString(5, role.equals("student") ? studentId : null);
                    ps.executeUpdate();

                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }

            // Enroll into course (ignore if already enrolled)
            String enrollSQL = role.equals("student")
                    ? "INSERT IGNORE INTO student_courses (student_id, course_id) VALUES (?, ?)"
                    : "INSERT IGNORE INTO teacher_courses (teacher_id, course_id) VALUES (?, ?)";

            try (PreparedStatement psEnroll = conn.prepareStatement(enrollSQL)) {
                psEnroll.setInt(1, userId);
                psEnroll.setInt(2, courseId);
                psEnroll.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(frame, "Registration successful or updated!");
            new LoginView();
            frame.dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // SEND OTP TO EMAIL
    public static void sendOtp(String email, JFrame frame) {
        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(frame, "Invalid email format.");
            return;
        }

        String otp = OTPUtil.generateOtp();

        try (Connection conn = DBConnection.getConnection()) {
            // Save OTP to DB with expiry 5 minutes from now
            String insertOtp = "INSERT INTO email_otps (email, otp_code, expires_at) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE)) " +
                    "ON DUPLICATE KEY UPDATE otp_code = VALUES(otp_code), expires_at = VALUES(expires_at)";
            PreparedStatement ps = conn.prepareStatement(insertOtp);
            ps.setString(1, email);
            ps.setString(2, otp);
            ps.executeUpdate();

            // Send OTP email
            MailSender.send(email, "Your OTP Code", "Your OTP is: " + otp + "\nValid for 5 minutes.");

            JOptionPane.showMessageDialog(frame, "OTP sent to your email.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error sending OTP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // VERIFY OTP FROM EMAIL
    public static boolean verifyOtp(String email, String otp, JFrame frame) {
        if (!Validator.isValidEmail(email) || otp == null || otp.trim().length() == 0) {
            JOptionPane.showMessageDialog(frame, "Invalid email or OTP.");
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM email_otps WHERE email = ? AND otp_code = ? AND expires_at > NOW()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, otp);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // OTP valid ➜ Delete it so it can’t be reused
                String deleteSql = "DELETE FROM email_otps WHERE email = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                psDelete.setString(1, email);
                psDelete.executeUpdate();

                return true;
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid or expired OTP.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error verifying OTP: " + e.getMessage());
            return false;
        }
    }
}
