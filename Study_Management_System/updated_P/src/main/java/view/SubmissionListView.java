package main.view;

import main.model.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionListView extends JFrame {
    private int teacherId;
    private String courseCode;

    private JComboBox<AssignmentItem> assignmentComboBox;
    private JLabel descriptionLabel;
    private DefaultTableModel tableModel;
    private JTable submissionTable;

    private List<AssignmentItem> assignmentsList = new ArrayList<>();

    public SubmissionListView(int teacherId, String courseCode) {
        this.teacherId = teacherId;
        this.courseCode = courseCode;

        setTitle("Submitted Assignments - " + courseCode);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Container c = getContentPane();
        c.setBackground(new Color(255, 239, 213)); // Light peach background
        setLayout(new BorderLayout(20, 20));

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font comboFont = new Font("Arial", Font.PLAIN, 16);
        Font tableFont = new Font("Serif", Font.PLAIN, 16);
        Font btnFont = new Font("Monospaced", Font.BOLD, 18);

        // === NORTH: Assignment selection panel ===
        JPanel comboPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        comboPanel.setBorder(BorderFactory.createTitledBorder(null, "Select Assignment",
                0, 0, labelFont, new Color(70, 70, 70)));
        comboPanel.setBackground(c.getBackground());

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topRow.setBackground(c.getBackground());
        assignmentComboBox = new JComboBox<>();
        assignmentComboBox.setFont(comboFont);
        assignmentComboBox.setPreferredSize(new Dimension(300, 30));
        topRow.add(assignmentComboBox);

        descriptionLabel = new JLabel("Description: ");
        descriptionLabel.setFont(labelFont);
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        bottomRow.setBackground(c.getBackground());
        bottomRow.add(descriptionLabel);

        comboPanel.add(topRow);
        comboPanel.add(bottomRow);
        add(comboPanel, BorderLayout.NORTH);

        // === CENTER: Submission table ===
        String[] columns = {"Student ID", "Name", "Submission Link", "Submitted At"};
        tableModel = new DefaultTableModel(columns, 0);
        submissionTable = new JTable(tableModel);
        submissionTable.setFont(tableFont);
        submissionTable.setRowHeight(28);
        submissionTable.getTableHeader().setFont(labelFont);

        // Hyperlink style for Submission Link column
        submissionTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel("<html><a href=''>" + value + "</a></html>");
                label.setForeground(new Color(0, 102, 204));
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (isSelected) {
                    label.setBackground(new Color(220, 240, 255));
                    label.setOpaque(true);
                } else {
                    label.setOpaque(false);
                }
                return label;
            }
        });

        // Open link on click
        submissionTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = submissionTable.rowAtPoint(e.getPoint());
                int col = submissionTable.columnAtPoint(e.getPoint());
                if (col == 2 && row != -1) {
                    Object link = submissionTable.getValueAt(row, col);
                    if (link != null && !link.toString().isEmpty()) {
                        try {
                            Desktop.getDesktop().browse(new URI(link.toString()));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(SubmissionListView.this, "Failed to open link.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Right-click copy menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copy");
        popupMenu.add(copyItem);

        submissionTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = submissionTable.rowAtPoint(point);
                int col = submissionTable.columnAtPoint(point);
                if (row >= 0 && col >= 0) {
                    submissionTable.setRowSelectionInterval(row, row);
                    submissionTable.setColumnSelectionInterval(col, col);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(submissionTable, e.getX(), e.getY());
                }
            }
        });

        copyItem.addActionListener(e -> {
            int row = submissionTable.getSelectedRow();
            int col = submissionTable.getSelectedColumn();
            if (row >= 0 && col >= 0) {
                Object value = submissionTable.getValueAt(row, col);
                if (value != null) {
                    StringSelection selection = new StringSelection(value.toString());
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(submissionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // === SOUTH: Back button panel ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 15));
        bottomPanel.setBackground(c.getBackground());

        JButton backButton = new JButton("Back");
        backButton.setFont(btnFont);
        backButton.setBackground(new Color(220, 53, 69));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(120, 45));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            // new DashboardTeacher(teacherId);
            new DashboardTeacher(teacherId, courseCode);

            dispose();
        });

        // Load assignments and submissions
        loadAssignments();

        assignmentComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AssignmentItem selected = (AssignmentItem) assignmentComboBox.getSelectedItem();
                if (selected != null) {
                    descriptionLabel.setText("Description: " + selected.description);
                    loadSubmissions(selected.id);
                }
            }
        });

        setVisible(true);
    }

    private void loadAssignments() {
        String sql = "SELECT id, assignment_number, assignment_text FROM assignments " +
                "WHERE course_id = (SELECT id FROM courses WHERE course_code = ?) " +
                "ORDER BY assignment_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            assignmentsList.clear();
            assignmentComboBox.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id");
                int number = rs.getInt("assignment_number");
                String text = rs.getString("assignment_text");

                AssignmentItem item = new AssignmentItem(id, number, text);
                assignmentsList.add(item);
                assignmentComboBox.addItem(item);
            }

            if (!assignmentsList.isEmpty()) {
                assignmentComboBox.setSelectedIndex(0);
                descriptionLabel.setText("Description: " + assignmentsList.get(0).description);
                loadSubmissions(assignmentsList.get(0).id);
            } else {
                JOptionPane.showMessageDialog(this, "No assignments found for this course.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assignments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubmissions(int assignmentId) {
        String sql = "SELECT u.student_id, u.name, " +
                "       COALESCE(s.github_link, s.file_path) AS submission_link, " +
                "       s.submitted_at " +
                "FROM submissions s " +
                "JOIN users u ON s.student_id = u.id " +
                "WHERE s.assignment_id = ? ORDER BY s.submitted_at DESC";

        tableModel.setRowCount(0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String name = rs.getString("name");
                String link = rs.getString("submission_link");
                String submittedAt = rs.getString("submitted_at");

                tableModel.addRow(new Object[]{studentId, name, link, submittedAt});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading submissions", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class AssignmentItem {
        int id;
        int number;
        String description;

        AssignmentItem(int id, int number, String description) {
            this.id = id;
            this.number = number;
            this.description = description;
        }

        @Override
        public String toString() {
            return "Assignment " + number;
        }
    }
}