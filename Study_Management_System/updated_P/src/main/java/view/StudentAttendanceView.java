package main.view;

import main.controller.AttendanceController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class StudentAttendanceView extends JFrame {
    private int studentId;
    private String courseCode;

    public StudentAttendanceView(int studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;

        setTitle("Attendance Record - " + courseCode);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // === Main Panel with soft background color ===
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 248, 255)); // Soft bluish-white background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // === Header Label ===
        JLabel titleLabel = new JLabel("Attendance Report for Course: " + courseCode);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // === Attendance Data ===
        Map<Date, Boolean> attendanceRecords = AttendanceController.getAttendanceRecords(studentId, courseCode);

        String[] columns = {"Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table read-only
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        int presentCount = 0;
        int totalCount = attendanceRecords.size();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map.Entry<Date, Boolean> entry : attendanceRecords.entrySet()) {
            String dateStr = sdf.format(entry.getKey());
            String status = entry.getValue() ? "✓ Present" : "✗ Absent";
            if (entry.getValue()) presentCount++;
            model.addRow(new Object[]{dateStr, status});
        }

        double attendancePercent = totalCount == 0 ? 0.0 : (presentCount * 100.0 / totalCount);
        String summaryText = String.format("Total: %d | Present: %d | Attendance: %.2f%%", totalCount, presentCount, attendancePercent);

        JLabel summaryLabel = new JLabel(summaryText);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // === Back Button with color ===
        JButton backButton = new JButton("⬅ Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setFocusPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(70, 130, 180)); // Steel blue
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        backButton.addActionListener(e -> {
            // new DashboardStudent(studentId);
            new DashboardStudent(studentId, courseCode);

            dispose();


        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(mainPanel.getBackground());
        southPanel.add(summaryLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainPanel.getBackground());
        buttonPanel.add(backButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === Final Assembly ===
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}