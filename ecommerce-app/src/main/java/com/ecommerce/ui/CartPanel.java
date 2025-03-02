package com.ecommerce.ui;

import com.ecommerce.model.CartItem;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CartPanel extends JPanel {
    private final CartService cartService;
    private final User currentUser;
    private final MainFrame parentFrame;
    
    private JPanel cartItemsPanel;
    private JLabel totalLabel;
    private List<CartItem> cartItems;

    public CartPanel(CartService cartService, User currentUser, MainFrame parentFrame) {
        this.cartService = cartService;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton backButton = new JButton("Continue Shopping");
        backButton.addActionListener(e -> parentFrame.showProductListPanel());
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with cart items
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cartItemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with total and checkout button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Your cart is empty!", 
                        "Cannot Checkout", JOptionPane.WARNING_MESSAGE);
            } else {
                parentFrame.showOrderSummaryPanel();
            }
        });
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);
        
        bottomPanel.add(totalPanel, BorderLayout.CENTER);
        bottomPanel.add(checkoutButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Load cart items
        loadCartItems();
    }

    private void loadCartItems() {
        cartItemsPanel.removeAll();
        cartItems = cartService.getUserCart(currentUser.getId());
        
        if (cartItems.isEmpty()) {
            JLabel emptyCartLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyCartLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            cartItemsPanel.add(emptyCartLabel);
        } else {
            for (CartItem item : cartItems) {
                cartItemsPanel.add(createCartItemPanel(item));
            }
        }
        
        updateTotal();
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JPanel createCartItemPanel(CartItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Item details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(item.getProduct().getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(nameLabel);
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", item.getProduct().getPrice()));
        detailsPanel.add(priceLabel);
        
        double itemTotal = item.getProduct().getPrice() * item.getQuantity();
        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", itemTotal));
        detailsPanel.add(totalLabel);
        
        // Quantity panel
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel quantityLabel = new JLabel("Quantity: ");
        
        JSpinner quantitySpinner = new JSpinner(
                new SpinnerNumberModel(item.getQuantity(), 1, item.getProduct().getStockQuantity(), 1));
        
        quantitySpinner.addChangeListener(e -> {
            int newQuantity = (int) quantitySpinner.getValue();
            if (cartService.updateCartItemQuantity(item.getId(), newQuantity)) {
                itemTotal = item.getProduct().getPrice() * newQuantity;
                totalLabel.setText(String.format("Total: $%.2f", itemTotal));
                updateTotal();
            }
        });
        
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartService.removeFromCart(item.getId())) {
                    loadCartItems();
                    parentFrame.updateCartCount();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to remove item from cart!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        quantityPanel.add(removeButton);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(quantityPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void updateTotal() {
        double total = cartService.calculateCartTotal(currentUser.getId());
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    public void refresh() {
        loadCartItems();
    }
}