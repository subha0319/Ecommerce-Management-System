package com.ecommerce.ui;

import com.ecommerce.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private ProductListPanel productListPanel;
    private CartPanel cartPanel;
    private OrderSummaryPanel orderSummaryPanel;
    private AdminDashboardPanel adminDashboardPanel;
    
    // Panel identifiers
    private static final String LOGIN_PANEL = "LOGIN";
    private static final String REGISTER_PANEL = "REGISTER";
    private static final String PRODUCT_LIST_PANEL = "PRODUCT_LIST";
    private static final String CART_PANEL = "CART";
    private static final String ORDER_SUMMARY_PANEL = "ORDER_SUMMARY";
    private static final String ADMIN_DASHBOARD_PANEL = "ADMIN_DASHBOARD";
    
    public MainFrame() {
        initializeFrame();
        initializePanels();
    }
    
    private void initializeFrame() {
        setTitle("E-Commerce Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center on screen
        
        // Set up card layout for panel switching
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);
        
        // Add window close event listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up resources if needed
                System.out.println("Application closing");
            }
        });
    }
    
    private void initializePanels() {
        // Initialize panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        productListPanel = new ProductListPanel(this);
        cartPanel = new CartPanel(this);
        orderSummaryPanel = new OrderSummaryPanel(this);
        adminDashboardPanel = new AdminDashboardPanel(this);
        
        // Add panels to the content panel with identifiers
        contentPanel.add(loginPanel, LOGIN_PANEL);
        contentPanel.add(registerPanel, REGISTER_PANEL);
        contentPanel.add(productListPanel, PRODUCT_LIST_PANEL);
        contentPanel.add(cartPanel, CART_PANEL);
        contentPanel.add(orderSummaryPanel, ORDER_SUMMARY_PANEL);
        contentPanel.add(adminDashboardPanel, ADMIN_DASHBOARD_PANEL);
        
        // Show the login panel initially
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }
    
    // Navigation methods
    public void showLoginPanel() {
        loginPanel.clearFields();
        cardLayout.show(contentPanel, LOGIN_PANEL);
    }
    
    public void showRegisterPanel() {
        registerPanel.clearFields();
        cardLayout.show(contentPanel, REGISTER_PANEL);
    }
    
    public void showProductList() {
        productListPanel.refreshProducts();
        cardLayout.show(contentPanel, PRODUCT_LIST_PANEL);
    }
    
    public void showCart() {
        cartPanel.refreshCart();
        cardLayout.show(contentPanel, CART_PANEL);
    }
    
    public void showOrderSummary() {
        orderSummaryPanel.refreshOrderSummary();
        cardLayout.show(contentPanel, ORDER_SUMMARY_PANEL);
    }
    
    public void showAdminDashboard() {
        adminDashboardPanel.refreshDashboard();
        cardLayout.show(contentPanel, ADMIN_DASHBOARD_PANEL);
    }
    
    public void logout() {
        AuthService authService = new AuthService();
        authService.logout();
        showLoginPanel();
    }
}