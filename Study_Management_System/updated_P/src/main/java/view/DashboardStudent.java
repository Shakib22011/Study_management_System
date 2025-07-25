package main.view;

import main.controller.CourseController;
import main.model.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardStudent extends JFrame {
    private int studentId;
    private JComboBox<String> courseCombo;
    private JLabel courseNameLabel;

    public DashboardStudent(int studentId) {
        this(studentId, null); // Call the overloaded constructor with no selection
    }

    public DashboardStudent(int studentId, String selectedCourseCode) {
        this.studentId = studentId;

        setTitle("Student Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        Container c = getContentPane();
        c.setBackground(new Color(255, 239, 213)); // Light peach

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font btnFont = new Font("Monaco", Font.BOLD, 18);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width;
        int height = screen.height;

        int centerX = width / 2 - 200;
        int y = height / 2 - 200;
        int fieldWidth = 400;
        int fieldHeight = 40;

        JLabel title = new JLabel("Welcome to Student Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(centerX + 10, y - 80, 500, 30);
        add(title);

        JLabel courseCodeLabel = new JLabel("Select Course Code:");
        courseCodeLabel.setFont(labelFont);
        courseCodeLabel.setBounds(centerX, y, 200, 30);
        add(courseCodeLabel);

        courseCombo = new JComboBox<>();
        courseCombo.setFont(fieldFont);
        courseCombo.setBounds(centerX, y + 40, fieldWidth, fieldHeight);
        add(courseCombo);

        loadEnrolledCourses(selectedCourseCode);

        JButton loadCourseButton = new JButton("Load Course");
        loadCourseButton.setFont(btnFont);
        loadCourseButton.setBounds(centerX, y + 100, fieldWidth, fieldHeight);
        loadCourseButton.setBackground(new Color(0, 123, 255));
        loadCourseButton.setForeground(Color.WHITE);
        add(loadCourseButton);

        courseNameLabel = new JLabel("Course Name: ");
        courseNameLabel.setFont(labelFont);
        courseNameLabel.setBounds(centerX, y + 160, fieldWidth, 30);
        add(courseNameLabel);

        JButton viewAssignmentsButton = new JButton("View Assignments");
        viewAssignmentsButton.setFont(btnFont);
        viewAssignmentsButton.setBounds(centerX, y + 210, fieldWidth, fieldHeight);
        viewAssignmentsButton.setBackground(new Color(23, 162, 184));
        viewAssignmentsButton.setForeground(Color.WHITE);
        add(viewAssignmentsButton);

        JButton submitAssignmentButton = new JButton("Submit Assignment");
        submitAssignmentButton.setFont(btnFont);
        submitAssignmentButton.setBounds(centerX, y + 270, fieldWidth, fieldHeight);
        submitAssignmentButton.setBackground(new Color(40, 167, 69));
        submitAssignmentButton.setForeground(Color.WHITE);
        add(submitAssignmentButton);

        JButton viewAttendanceButton = new JButton("View Attendance");
        viewAttendanceButton.setFont(btnFont);
        viewAttendanceButton.setBounds(centerX, y + 330, fieldWidth, fieldHeight);
        viewAttendanceButton.setBackground(new Color(255, 193, 7));
        viewAttendanceButton.setForeground(Color.BLACK);
        add(viewAttendanceButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(btnFont);
        logoutButton.setBounds(centerX, y + 390, fieldWidth, fieldHeight);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        add(logoutButton);

        // Load course name
        loadCourseButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null) {
                String courseName = CourseController.getCourseName(courseCode);
                courseNameLabel.setText("Course Name: " + courseName);
            }
        });

        // View Assignments
        viewAssignmentsButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new AssignmentListView(studentId, courseCode, false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course!");
            }
        });

        // Submit Assignment
        submitAssignmentButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new AssignmentSubmitView(studentId, courseCode);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course!");
            }
        });

        // View Attendance
        viewAttendanceButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new StudentAttendanceView(studentId, courseCode);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course!");
            }
        });

        // Logout
        logoutButton.addActionListener(e -> {
            new LoginView();
            dispose();
        });

        setVisible(true);
    }

    private void loadEnrolledCourses(String selectedCourseCode) {
        String sql = "SELECT c.course_code FROM student_courses sc JOIN courses c ON sc.course_id = c.id WHERE sc.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courseCombo.addItem(rs.getString("course_code"));
            }

            if (selectedCourseCode != null) {
                courseCombo.setSelectedItem(selectedCourseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}