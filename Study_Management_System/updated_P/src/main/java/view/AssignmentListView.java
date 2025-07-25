package main.view;

import main.controller.AssignmentController;
import main.model.Assignment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignmentListView extends JFrame {
    private int userId;
    private String courseCode;
    private boolean isTeacher;

    public AssignmentListView(int userId, String courseCode, boolean isTeacher) {
        this.userId = userId;
        this.courseCode = courseCode;
        this.isTeacher = isTeacher;

        setTitle("Assignment List - " + courseCode);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue

        Font titleFont = new Font("Arial", Font.BOLD, 26);
        Font listFont = new Font("Arial", Font.PLAIN, 18);
        Font btnFont = new Font("Arial", Font.BOLD, 18);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screen.width / 2 - 300;
        int y = screen.height / 2 - 200;
        int width = 600;
        int height = 400;

        JLabel titleLabel = new JLabel("Assignments for Course: " + courseCode);
        titleLabel.setFont(titleFont);
        titleLabel.setBounds(centerX, y - 60, width, 30);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        List<Assignment> assignments = AssignmentController.getAssignmentsByCourse(courseCode);

        if (assignments.isEmpty()) {
            JLabel noData = new JLabel("No assignments found for this course.");
            noData.setFont(new Font("Arial", Font.ITALIC, 20));
            noData.setBounds(centerX, y, width, 30);
            noData.setHorizontalAlignment(SwingConstants.CENTER);
            add(noData);
        } else {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            int serial = 1;
            for (Assignment a : assignments) {
                listModel.addElement(serial++ + ". " + a.getDescription());
            }

            JList<String> assignmentList = new JList<>(listModel);
            assignmentList.setFont(listFont);
            JScrollPane scrollPane = new JScrollPane(assignmentList);
            scrollPane.setBounds(centerX, y, width, height);
            add(scrollPane);
        }

        JButton backButton = new JButton("Back");
        backButton.setFont(btnFont);
        backButton.setBounds(centerX + 200, y + height + 40, 200, 45);
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        add(backButton);

        backButton.addActionListener(e -> {
            if (isTeacher) {
                new DashboardTeacher(userId, courseCode);  // Pass selected course
            } else {
                new DashboardStudent(userId, courseCode);  // Pass selected course
            }
            dispose();
        });

        setVisible(true);
    }
}