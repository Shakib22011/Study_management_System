package main.view;

import main.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private JTextField nameField, emailField, studentIdField, courseCodeField, courseNameField, otpField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton sendOtpButton, verifyOtpButton;

    public RegisterView() {
        setTitle("Register - Study Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        Container c = getContentPane();
        c.setBackground(new Color(255, 239, 213));

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = screenSize.width;
        int frameHeight = screenSize.height;

        int formWidth = 600;
        int formHeight = 700;
        int x = (frameWidth - formWidth) / 2;
        int y = (frameHeight - formHeight) / 2;

        int labelWidth = 220;
        int fieldWidth = 300;
        int height = 35;
        int gap = 50;
        int currentY = y;

        JLabel title = new JLabel("Register to Study Management System");
        title.setFont(new Font("Monaco", Font.BOLD, 24));
        title.setBounds(x - 30, currentY - 70, formWidth, 30);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // Name
        addLabel("Name:", x, currentY, labelFont, labelWidth);
        nameField = addField(x + labelWidth + 10, currentY, fieldFont, fieldWidth);
        currentY += gap;

        // Email
        addLabel("Email:", x, currentY, labelFont, labelWidth);
        emailField = addField(x + labelWidth + 10, currentY, fieldFont, fieldWidth);
        currentY += gap;

        // Send OTP Button
        sendOtpButton = new JButton("Send OTP");
        sendOtpButton.setBounds(x + labelWidth + 10 + fieldWidth + 10, currentY - gap + 5, 100, 30);
        sendOtpButton.setFont(new Font("Monaco", Font.BOLD, 14));
        sendOtpButton.setBackground(new Color(0, 123, 255));
        sendOtpButton.setForeground(Color.WHITE);
        add(sendOtpButton);

        // OTP Field
        addLabel("Enter OTP:", x, currentY, labelFont, labelWidth);
        otpField = addField(x + labelWidth + 10, currentY, fieldFont, 150);
        currentY += gap;

        // Password
        addLabel("Password:", x, currentY, labelFont, labelWidth);
        passwordField = new JPasswordField();
        passwordField.setBounds(x + labelWidth + 10, currentY, fieldWidth, height);
        passwordField.setFont(fieldFont);
        add(passwordField);
        currentY += gap;

        // Role
        addLabel("Role:", x, currentY, labelFont, labelWidth);
        roleBox = new JComboBox<>(new String[]{"student", "teacher"});
        roleBox.setBounds(x + labelWidth + 10, currentY, fieldWidth, height);
        roleBox.setFont(fieldFont);
        add(roleBox);
        currentY += gap;

        // Student ID
        addLabel("Student ID (only for student):", x, currentY, labelFont, labelWidth);
        studentIdField = addField(x + labelWidth + 10, currentY, fieldFont, fieldWidth);
        currentY += gap;

        // Course Code
        addLabel("Course Code:", x, currentY, labelFont, labelWidth);
        courseCodeField = addField(x + labelWidth + 10, currentY, fieldFont, fieldWidth);
        currentY += gap;

        // Course Name
        addLabel("Course Name:", x, currentY, labelFont, labelWidth);
        courseNameField = addField(x + labelWidth + 10, currentY, fieldFont, fieldWidth);
        currentY += gap + 20;

        // Verify OTP & Register Button
        verifyOtpButton = new JButton("Verify OTP & Register");
        verifyOtpButton.setBounds(x, currentY, 300, 45);
        verifyOtpButton.setFont(new Font("Monaco", Font.BOLD, 18));
        verifyOtpButton.setBackground(new Color(40, 167, 69));
        verifyOtpButton.setForeground(Color.WHITE);
        add(verifyOtpButton);

        // Back button
        JButton backButton = new JButton("Back to Login");
        backButton.setBounds(x + 320, currentY, 250, 45);
        backButton.setFont(new Font("Monaco", Font.BOLD, 18));
        backButton.setBackground(new Color(0, 123, 255));
        backButton.setForeground(Color.WHITE);
        add(backButton);

        // Enable/disable Student ID field based on role
        roleBox.addActionListener(e -> {
            boolean isStudent = roleBox.getSelectedItem().toString().equals("student");
            studentIdField.setEnabled(isStudent);
            if (!isStudent) studentIdField.setText("");
        });

        // Send OTP button action
        sendOtpButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            AuthController.sendOtp(email, RegisterView.this);
        });

        // Verify OTP and Register button action
        verifyOtpButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String otp = otpField.getText().trim();

            if (AuthController.verifyOtp(email, otp, RegisterView.this)) {
                // OTP valid হলে register করো
                AuthController.register(
                        nameField.getText().trim(),
                        email,
                        new String(passwordField.getPassword()).trim(),
                        roleBox.getSelectedItem().toString(),
                        studentIdField.getText().trim(),
                        courseCodeField.getText().trim(),
                        courseNameField.getText().trim(),
                        RegisterView.this
                );
            }
        });

        backButton.addActionListener(e -> {
            new LoginView();
            dispose();
        });

        setVisible(true);
    }

    private void addLabel(String text, int x, int y, Font font, int width) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setBounds(x, y, width, 30);
        add(label);
    }

    private JTextField addField(int x, int y, Font font, int width) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBounds(x, y, width, 35);
        add(field);
        return field;
    }
}
