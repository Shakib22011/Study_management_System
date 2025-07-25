package main.view;

import main.controller.AssignmentController;
import main.controller.SubmissionController;
import main.model.Assignment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignmentSubmitView extends JFrame {
    private int studentId;
    private String courseCode;

    private JComboBox<AssignmentItem> assignmentComboBox;
    private JTextField submissionField;

    public AssignmentSubmitView(int studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;

        setTitle("Submit Assignment - " + courseCode);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 250, 255));

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load assignments for the course
        List<Assignment> assignments = AssignmentController.getAssignmentsByCourse(courseCode);
        if (assignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments available for this course.");
            //new DashboardStudent(studentId); // Redirect back to dashboard
            new DashboardStudent(studentId, courseCode);

            dispose();
            return;
        }

        JLabel selectAssignmentLabel = new JLabel("Select Assignment:");
        selectAssignmentLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        add(selectAssignmentLabel, gbc);

        assignmentComboBox = new JComboBox<>();
        assignmentComboBox.setFont(fieldFont);
        for (Assignment a : assignments) {
            assignmentComboBox.addItem(new AssignmentItem(a.getId(), a.getDescription()));
        }
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        add(assignmentComboBox, gbc);

        JLabel submissionLabel = new JLabel("Enter PDF file path or GitHub link:");
        submissionLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        add(submissionLabel, gbc);

        submissionField = new JTextField();
        submissionField.setFont(fieldFont);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        add(submissionField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        buttonPanel.setBackground(getContentPane().getBackground());

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(buttonFont);
        submitButton.setBackground(new Color(60, 179, 113)); // Green
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(submitButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(buttonFont);
        backButton.setBackground(new Color(70, 130, 180)); // Blue
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        add(buttonPanel, gbc);

        submitButton.addActionListener(e -> {
            AssignmentItem selected = (AssignmentItem) assignmentComboBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an assignment.");
                return;
            }

            String submission = submissionField.getText().trim();
            if (submission.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the PDF path or GitHub link.");
                return;
            }

            boolean success = SubmissionController.submitAssignment(studentId, selected.getId(), submission);
            if (success) {
                JOptionPane.showMessageDialog(this, "Assignment submitted successfully!");
                //new DashboardStudent(studentId);
                new DashboardStudent(studentId, courseCode);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Submission failed. Please try again.");
            }
        });

        backButton.addActionListener(e -> {
            //new DashboardStudent(studentId);
            new DashboardStudent(studentId, courseCode);

            dispose();
        });

        setVisible(true);
    }

    // Helper class for combo box items
    private static class AssignmentItem {
        private final int id;
        private final String description;

        public AssignmentItem(int id, String description) {
            this.id = id;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return description;
        }
    }
}