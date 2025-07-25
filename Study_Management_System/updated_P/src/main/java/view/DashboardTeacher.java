package main.view;

import main.controller.CourseController;
import main.model.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardTeacher extends JFrame {
    private int teacherId;
    private JComboBox<String> courseCombo;
    private JLabel courseNameLabel;

    public DashboardTeacher(int teacherId) {
        this(teacherId, null); // Delegate to overloaded constructor
    }

    public DashboardTeacher(int teacherId, String selectedCourseCode) {
        this.teacherId = teacherId;

        setTitle("Teacher Dashboard");
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
        int y = height / 2 - 250;
        int fieldWidth = 400;
        int fieldHeight = 40;

        JLabel title = new JLabel("Welcome to Teacher Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(centerX + 10, y - 80, 600, 30);
        add(title);

        JLabel courseCodeLabel = new JLabel("Select Course Code:");
        courseCodeLabel.setFont(labelFont);
        courseCodeLabel.setBounds(centerX, y, 200, 30);
        add(courseCodeLabel);

        courseCombo = new JComboBox<>();
        courseCombo.setFont(fieldFont);
        courseCombo.setBounds(centerX, y + 40, fieldWidth, fieldHeight);
        add(courseCombo);

        loadAssignedCourses(selectedCourseCode); // set selected course here

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

        JButton addAssignmentButton = new JButton("Add Assignment");
        addAssignmentButton.setFont(btnFont);
        addAssignmentButton.setBounds(centerX, y + 210, fieldWidth, fieldHeight);
        addAssignmentButton.setBackground(new Color(40, 167, 69));
        addAssignmentButton.setForeground(Color.WHITE);
        add(addAssignmentButton);

        JButton viewAssignmentsButton = new JButton("View Assignments");
        viewAssignmentsButton.setFont(btnFont);
        viewAssignmentsButton.setBounds(centerX, y + 270, fieldWidth, fieldHeight);
        viewAssignmentsButton.setBackground(new Color(23, 162, 184));
        viewAssignmentsButton.setForeground(Color.WHITE);
        add(viewAssignmentsButton);

        JButton viewSubmissionsButton = new JButton("Submitted Assignments");
        viewSubmissionsButton.setFont(btnFont);
        viewSubmissionsButton.setBounds(centerX, y + 330, fieldWidth, fieldHeight);
        viewSubmissionsButton.setBackground(new Color(108, 117, 125));
        viewSubmissionsButton.setForeground(Color.WHITE);
        add(viewSubmissionsButton);

        JButton takeAttendanceButton = new JButton("Take Attendance");
        takeAttendanceButton.setFont(btnFont);
        takeAttendanceButton.setBounds(centerX, y + 390, fieldWidth, fieldHeight);
        takeAttendanceButton.setBackground(new Color(255, 193, 7));
        takeAttendanceButton.setForeground(Color.BLACK);
        add(takeAttendanceButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(btnFont);
        logoutButton.setBounds(centerX, y + 450, fieldWidth, fieldHeight);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        add(logoutButton);

        // Load course name
        loadCourseButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode == null || courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a course code!");
                return;
            }

            String courseName = CourseController.getCourseName(courseCode);
            if (courseName != null) {
                courseNameLabel.setText("Course Name: " + courseName);
            } else {
                courseNameLabel.setText("Course not found!");
            }
        });

        // Add Assignment
        addAssignmentButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new AssignmentCreateView(teacherId, courseCode);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course code!");
            }
        });

        // View Assignments
        viewAssignmentsButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new AssignmentListView(teacherId, courseCode, true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course code!");
            }
        });

        // View Submissions
        viewSubmissionsButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new SubmissionListView(teacherId, courseCode);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course code!");
            }
        });

        // Take Attendance
        takeAttendanceButton.addActionListener(e -> {
            String courseCode = (String) courseCombo.getSelectedItem();
            if (courseCode != null && !courseCode.isEmpty()) {
                new TakeAttendanceView(teacherId, courseCode);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a course code!");
            }
        });

        // Logout
        logoutButton.addActionListener(e -> {
            new LoginView();
            dispose();
        });

        setVisible(true);
    }

    private void loadAssignedCourses(String selectedCourseCode) {
        String sql = "SELECT c.course_code FROM courses c " +
                "JOIN teacher_courses tc ON c.id = tc.course_id " +
                "WHERE tc.teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String code = rs.getString("course_code");
                courseCombo.addItem(code);
            }

            if (selectedCourseCode != null) {
                courseCombo.setSelectedItem(selectedCourseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load assigned courses.");
        }
    }
}