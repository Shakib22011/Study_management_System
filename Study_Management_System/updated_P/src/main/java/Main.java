package main;

import main.view.LoginView;

import javax.swing.*;

public class  Main {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater for thread safety on GUI
        SwingUtilities.invokeLater(() -> {
            new LoginView();
        });
    }
}