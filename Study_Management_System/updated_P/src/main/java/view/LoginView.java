package main.view;

import main.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginView() {
        setTitle("Login - Study Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        Container c = getContentPane();
        c.setBackground(new Color(255, 239, 213)); // peach

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);

        // Get screen width and height
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int frameWidth = screenSize.width;
        int frameHeight = screenSize.height;

        // Center form panel size
        int formWidth = 500;
        int formHeight = 300;
        int x = (frameWidth - formWidth) / 2;
        int y = (frameHeight - formHeight) / 2;

        JLabel title = new JLabel("Welcome to Study Management System");
        title.setFont(new Font("Monaco", Font.BOLD, 24));
        title.setBounds(x+20, y-80, formWidth, 30);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setBounds(x+80, y, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setBounds(x + 200, y, 250, 35);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(x+80, y + 60, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(x + 200, y + 60, 250, 35);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Monaco", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(x+80, y + 130, 150, 40);
        add(loginButton);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Monaco", Font.BOLD, 16));
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBounds(x + 300, y + 130, 150, 40);
        add(registerButton);

        // Button Actions
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            AuthController.login(email, password, LoginView.this);
        });

        registerButton.addActionListener(e -> {
            new RegisterView();
            dispose();
        });

        setVisible(true);
    }
}