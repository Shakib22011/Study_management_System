package main.view;

import javax.swing.*;
import java.awt.*;

public class AssignmentCreateView extends JFrame {

    private JTextField txtCourseCode;
    private JTextField txtAssignmentNumber;
    private JTextArea txtAssignment;
    private JButton btnAddAssignment, btnBack;

    private int teacherId;

    public AssignmentCreateView(int teacherId, String courseCode) {
        this.teacherId = teacherId;

        setTitle("Add Assignment");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        Container c = getContentPane();
        c.setBackground(new Color(255, 239, 213)); // Light peach

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Font btnFont = new Font("Monospaced", Font.BOLD, 18);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width;
        int height = screen.height;

        int centerX = width / 2 - 250;
        int y = height / 2 - 200;
        int fieldWidth = 500;
        int fieldHeight = 40;

        JLabel titleLabel = new JLabel("Create New Assignment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setBounds(centerX + 50, y - 80, 400, 30);
        add(titleLabel);

        JLabel lblCourseCode = new JLabel("Course Code:");
        lblCourseCode.setFont(labelFont);
        lblCourseCode.setBounds(centerX, y, 200, 30);
        add(lblCourseCode);

        txtCourseCode = new JTextField(courseCode);
        txtCourseCode.setFont(fieldFont);
        txtCourseCode.setEditable(false); // make it non-editable for safety
        txtCourseCode.setBounds(centerX, y + 30, fieldWidth, fieldHeight);
        add(txtCourseCode);

        JLabel lblAssignmentNumber = new JLabel("Assignment Number (e.g. 1, 2):");
        lblAssignmentNumber.setFont(labelFont);
        lblAssignmentNumber.setBounds(centerX, y + 90, 400, 30);
        add(lblAssignmentNumber);

        txtAssignmentNumber = new JTextField();
        txtAssignmentNumber.setFont(fieldFont);
        txtAssignmentNumber.setBounds(centerX, y + 120, fieldWidth, fieldHeight);
        add(txtAssignmentNumber);

        JLabel lblAssignmentText = new JLabel("Assignment Description:");
        lblAssignmentText.setFont(labelFont);
        lblAssignmentText.setBounds(centerX, y + 180, 300, 30);
        add(lblAssignmentText);

        txtAssignment = new JTextArea();
        txtAssignment.setFont(fieldFont);
        JScrollPane scrollPane = new JScrollPane(txtAssignment);
        scrollPane.setBounds(centerX, y + 210, fieldWidth, 150);
        add(scrollPane);

        btnAddAssignment = new JButton("Add Assignment");
        btnAddAssignment.setFont(btnFont);
        btnAddAssignment.setBounds(centerX, y + 380, fieldWidth / 2 - 10, fieldHeight);
        btnAddAssignment.setBackground(new Color(40, 167, 69));
        btnAddAssignment.setForeground(Color.WHITE);
        add(btnAddAssignment);

        btnBack = new JButton("Back");
        btnBack.setFont(btnFont);
        btnBack.setBounds(centerX + fieldWidth / 2 + 10, y + 380, fieldWidth / 2 - 10, fieldHeight);
        btnBack.setBackground(new Color(220, 53, 69));
        btnBack.setForeground(Color.WHITE);
        add(btnBack);

        btnAddAssignment.addActionListener(e -> addAssignment());
        btnBack.addActionListener(e -> {
            // new DashboardTeacher(teacherId);
            new DashboardTeacher(teacherId, courseCode);

            dispose();
        });

        setVisible(true);
    }

    private void addAssignment() {
        String courseCode = txtCourseCode.getText().trim();
        String assignmentNumberStr = txtAssignmentNumber.getText().trim();
        String assignmentText = txtAssignment.getText().trim();

        if (courseCode.isEmpty() || assignmentNumberStr.isEmpty() || assignmentText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        int assignmentNumber;
        try {
            assignmentNumber = Integer.parseInt(assignmentNumberStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Assignment number must be an integer!");
            return;
        }

        boolean added = main.controller.AssignmentController.addAssignment(
                teacherId, courseCode, assignmentText, assignmentNumber);

        if (added) {
            JOptionPane.showMessageDialog(this, "Assignment added successfully!");
            txtAssignment.setText("");
            txtAssignmentNumber.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add assignment. Please check the course code.");
        }
    }
}