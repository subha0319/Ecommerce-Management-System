package com.ecommerce.ui;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Panel for displaying order summary before checkout
 */
public class OrderSummaryPanel extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private CartService cartService;
    private OrderService orderService;
    
    private JTable orderItemsTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;
    private JButton confirmOrderButton;
    private JButton backToCartButton;
    private JButton cancelButton;
    
    private List<CartItem> cartItems;
    private double totalAmount;
    
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    /**
     * Constructor for the OrderSummaryPanel
     * @param mainFrame The main application frame
     * @param currentUser The currently logged in user
     * @param cartService Service for cart operations
     * @param orderService Service for order operations
     */
    public OrderSummaryPanel(MainFrame mainFrame, User currentUser, 
                            CartService cartService, OrderService orderService) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        this.cartService = cartService;
        this.orderService = orderService;
        
        loadCartItems();
        initializeComponents();
        setupLayout();
        setupListeners();
    }
    
    /**
     * Load cart items from the database
     */
    private void loadCartItems() {
        try {
            cartItems = cartService.getCartItems(currentUser.getId());
            calculateTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading cart items: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Calculate the total amount for all items in cart
     */
    private void calculateTotal() {
        totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Create table model with column names
        tableModel = new DefaultTableModel(
            new Object[]{"Product", "Price", "Quantity", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table and set properties
        orderItemsTable = new JTable(tableModel);
        orderItemsTable.setRowHeight(25);
        orderItemsTable.getTableHeader().setReorderingAllowed(false);
        
        // Populate table with cart items
        populateTable();
        
        // Create labels and buttons
        JLabel headerLabel = new JLabel("Order Summary", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        totalAmountLabel = new JLabel("Total: " + currencyFormat.format(totalAmount), JLabel.RIGHT);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        confirmOrderButton = new JButton("Confirm Order");
        backToCartButton = new JButton("Back to Cart");
        cancelButton = new JButton("Cancel");
    }
    
    /**
     * Populate the table with cart items
     */
    private void populateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add rows for each cart item
        for (CartItem item : cartItems) {
            double subtotal = item.getProduct().getPrice() * item.getQuantity();
            tableModel.addRow(new Object[]{
                item.getProduct().getName(),
                currencyFormat.format(item.getProduct().getPrice()),
                item.getQuantity(),
                currencyFormat.format(subtotal)
            });
        }
    }

    /**
     * Set up the panel layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add header at the top
        JLabel headerLabel = new JLabel("Order Summary", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);
        
        // Add table in a scroll pane in the center
        JScrollPane scrollPane = new JScrollPane(orderItemsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom panel for total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Add total amount label
        bottomPanel.add(totalAmountLabel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backToCartButton);
        buttonPanel.add(confirmOrderButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Set up action listeners for buttons
     */
    private void setupListeners() {
        confirmOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processOrder();
            }
        });
        
        backToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showPanel(new CartPanel(mainFrame, currentUser, cartService));
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to cancel this order?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    mainFrame.showPanel(new ProductListPanel(mainFrame, currentUser));
                }
            }
        });
    }
    
    /**
     * Process the order and create it in the database
     */
    private void processOrder() {
        try {
            // Check if cart is empty
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Your cart is empty. Please add products to your cart.",
                    "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to place this order for " + 
                currencyFormat.format(totalAmount) + "?",
                "Confirm Order", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Create the order using the order service
                Order order = orderService.createOrder(currentUser.getId(), cartItems);
                
                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Order placed successfully. Order ID: " + order.getId(),
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear cart and go back to product list
                cartService.clearCart(currentUser.getId());
                mainFrame.showPanel(new ProductListPanel(mainFrame, currentUser));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error placing order: " + e.getMessage(),
                "Order Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}