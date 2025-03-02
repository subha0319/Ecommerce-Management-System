package com.ecommerce.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin Dashboard Panel that provides navigation to various administrative functions
 */
public class AdminDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private JButton manageProductsButton;
    private JButton viewOrdersButton;
    private JButton manageCategoriesButton;
    private JButton viewCustomersButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    /**
     * Constructor for the AdminDashboardPanel
     * @param mainFrame The main application frame
     */
    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        welcomeLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        manageProductsButton = new JButton("Manage Products");
        manageProductsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        
        viewOrdersButton = new JButton("View Orders");
        viewOrdersButton.setFont(new Font("Arial", Font.PLAIN, 16));
        
        manageCategoriesButton = new JButton("Manage Categories");
        manageCategoriesButton.setFont(new Font("Arial", Font.PLAIN, 16));
        
        viewCustomersButton = new JButton("View Customers");
        viewCustomersButton.setFont(new Font("Arial", Font.PLAIN, 16));
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    /**
     * Set up the panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add welcome label at the top
        add(welcomeLabel, BorderLayout.NORTH);

        // Create button panel with a grid layout
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        
        buttonPanel.add(manageProductsButton);
        buttonPanel.add(viewOrdersButton);
        buttonPanel.add(manageCategoriesButton);
        buttonPanel.add(viewCustomersButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Set up action listeners for buttons
     */
    private void setupListeners() {
        manageProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showPanel(new ProductManagementPanel(mainFrame));
            }
        });

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Order management functionality will be implemented soon.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        manageCategoriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Category management functionality will be implemented soon.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Customer management functionality will be implemented soon.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    mainFrame.logout();
                }
            }
        });
    }

    /**
     * Update welcome message with admin name
     * @param adminName The name of the admin user
     */
    public void setAdminName(String adminName) {
        welcomeLabel.setText("Welcome, Admin " + adminName + "!");
    }
}