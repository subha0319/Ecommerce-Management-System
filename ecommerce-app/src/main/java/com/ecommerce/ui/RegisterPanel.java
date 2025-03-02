package com.ecommerce.ui;

import com.ecommerce.service.AuthService;
import com.ecommerce.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel statusLabel;
    private AuthService authService;
    private MainFrame parent;
    
    public RegisterPanel(MainFrame parent) {
        this.parent = parent;
        this.authService = new AuthService();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Create an Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameField = new JTextField(20);
        
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField(20);
        
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(backButton);
        formPanel.add(registerButton);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel);
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showLoginPanel();
            }
        });
    }
    
    private void handleRegistration() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate inputs
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All fields are required");
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            statusLabel.setText("Invalid email format");
            return;
        }
        
        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }
        
        // Register the user
        boolean success = authService.register(email, password, fullName);
        
        if (success) {
            statusLabel.setText("");
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            parent.showLoginPanel();
        } else {
            statusLabel.setText("Registration failed. Email may already be in use.");
        }
    }
    
    public void clearFields() {
        fullNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        statusLabel.setText("");
    }
}