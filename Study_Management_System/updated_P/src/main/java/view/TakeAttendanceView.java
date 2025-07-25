package main.view;

import main.model.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TakeAttendanceView extends JFrame {
    private int teacherId;
    private String courseCode;
    private LocalDate selectedDate;
    private HashMap<Integer, String> studentIdMap = new HashMap<>();
    private HashMap<Integer, JCheckBox> presentCheckMap = new HashMap<>();
    private HashMap<Integer, JCheckBox> absentCheckMap = new HashMap<>();

    public TakeAttendanceView(int teacherId, String courseCode) {
        this.teacherId = teacherId;
        this.courseCode = courseCode;
        this.selectedDate = LocalDate.now();

        setTitle("Take Attendance - " + courseCode);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // === Main Panel ===
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // === Date Label ===
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel dateLabel = new JLabel("Date: " + selectedDate.format(formatter));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(dateLabel, BorderLayout.NORTH);

        // === Table Panel ===
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(mainPanel.getBackground());

        Font headerFont = new Font("Arial", Font.BOLD, 15);
        Color headerBg = new Color(200, 220, 240);

        // === Header Row ===
        JPanel headerRow = new JPanel(new GridLayout(1, 6));
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerRow.setBackground(mainPanel.getBackground());
        headerRow.add(createHeaderLabel("Student ID", headerFont, headerBg));
        headerRow.add(createHeaderLabel("Name", headerFont, headerBg));
        headerRow.add(createHeaderLabel("✓ Present", headerFont, headerBg));
        headerRow.add(createHeaderLabel("✗ Absent", headerFont, headerBg));
        headerRow.add(createHeaderLabel("Present/Total", headerFont, headerBg));
        headerRow.add(createHeaderLabel("%", headerFont, headerBg));
        tablePanel.add(headerRow);

        // === Load Students ===
        String studentQuery = "SELECT u.id, u.name, u.student_id FROM users u " +
                "JOIN student_courses sc ON u.id = sc.student_id " +
                "JOIN courses c ON sc.course_id = c.id " +
                "WHERE c.course_code = ? AND u.role = 'student'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(studentQuery)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                String studentId = rs.getString("student_id");

                studentIdMap.put(userId, studentId);

                JCheckBox presentCheck = new JCheckBox();
                JCheckBox absentCheck = new JCheckBox();

                presentCheck.setBackground(mainPanel.getBackground());
                absentCheck.setBackground(mainPanel.getBackground());

                // Toggle logic
                presentCheck.addActionListener(e -> {
                    if (presentCheck.isSelected()) absentCheck.setSelected(false);
                });
                absentCheck.addActionListener(e -> {
                    if (absentCheck.isSelected()) presentCheck.setSelected(false);
                });

                presentCheckMap.put(userId, presentCheck);
                absentCheckMap.put(userId, absentCheck);

                int totalDays = 0, presentDays = 0;
                String countSql = "SELECT COUNT(*) AS total, " +
                        "SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) AS present " +
                        "FROM attendance WHERE student_id = ? AND course_id = (SELECT id FROM courses WHERE course_code = ?)";

                try (PreparedStatement countPs = conn.prepareStatement(countSql)) {
                    countPs.setInt(1, userId);
                    countPs.setString(2, courseCode);
                    ResultSet countRs = countPs.executeQuery();
                    if (countRs.next()) {
                        totalDays = countRs.getInt("total");
                        presentDays = countRs.getInt("present");
                    }
                }

                float percentage = totalDays > 0 ? (presentDays * 100f / totalDays) : 0;
                DecimalFormat df = new DecimalFormat("0.00");

                // === Row Panel ===
                JPanel rowPanel = new JPanel(new GridLayout(1, 6));
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                rowPanel.setBackground(mainPanel.getBackground());
                rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

                rowPanel.add(createDataLabel(studentId));
                rowPanel.add(createDataLabel(name));

                JPanel presentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                presentPanel.setBackground(mainPanel.getBackground());
                presentPanel.add(presentCheck);
                rowPanel.add(presentPanel);

                JPanel absentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                absentPanel.setBackground(mainPanel.getBackground());
                absentPanel.add(absentCheck);
                rowPanel.add(absentPanel);

                rowPanel.add(createDataLabel(presentDays + "/" + totalDays));
                rowPanel.add(createDataLabel(df.format(percentage) + "%"));

                tablePanel.add(rowPanel);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // === Scrollable Table ===
        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // === Bottom Panel ===
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(mainPanel.getBackground());

        JButton backButton = new JButton("⬅ Back");
        backButton.setBackground(new Color(220, 53, 69));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.addActionListener(e -> {
            //  new DashboardTeacher(teacherId);
            new DashboardTeacher(teacherId, courseCode);

            dispose();
        });

        JButton submitButton = new JButton("Submit Attendance");
        submitButton.setBackground(new Color(0, 153, 76));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.setPreferredSize(new Dimension(180, 40));
        submitButton.addActionListener(e -> submitAttendance());

        buttonPanel.add(backButton, BorderLayout.EAST);
        buttonPanel.add(submitButton, BorderLayout.WEST);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JLabel createHeaderLabel(String text, Font font, Color bg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return label;
    }

    private JLabel createDataLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        return label;
    }

    private void submitAttendance() {
        String courseIdQuery = "SELECT id FROM courses WHERE course_code = ?";
        String checkDuplicate = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND course_id = ? AND date = ?";
        String insertSql = "INSERT INTO attendance (student_id, course_id, date, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement courseStmt = conn.prepareStatement(courseIdQuery)) {

            courseStmt.setString(1, courseCode);
            ResultSet rs = courseStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Course not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int courseId = rs.getInt("id");
            PreparedStatement checkStmt = conn.prepareStatement(checkDuplicate);
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);

            for (int userId : studentIdMap.keySet()) {
                JCheckBox presentBox = presentCheckMap.get(userId);
                JCheckBox absentBox = absentCheckMap.get(userId);

                if (!presentBox.isSelected() && !absentBox.isSelected()) continue;

                String status = presentBox.isSelected() ? "present" : "absent";

                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, courseId);
                checkStmt.setDate(3, Date.valueOf(selectedDate));

                ResultSet checkRs = checkStmt.executeQuery();
                checkRs.next();
                if (checkRs.getInt(1) > 0) continue;

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, courseId);
                insertStmt.setDate(3, Date.valueOf(selectedDate));
                insertStmt.setString(4, status);
                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            JOptionPane.showMessageDialog(this, "Attendance submitted successfully.");
            dispose();
            // new DashboardTeacher(teacherId);
            new DashboardTeacher(teacherId, courseCode);


        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting attendance.", "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}