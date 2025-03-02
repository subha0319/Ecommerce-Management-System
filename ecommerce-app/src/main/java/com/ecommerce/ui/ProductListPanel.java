package com.ecommerce.ui;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductListPanel extends JPanel {
    private final ProductService productService;
    private final CartService cartService;
    private final User currentUser;
    private final MainFrame parentFrame;
    
    private JPanel productsPanel;
    private JComboBox<Category> categoryComboBox;
    private JTextField searchField;
    private JLabel cartCountLabel;

    public ProductListPanel(ProductService productService, CartService cartService, User currentUser, MainFrame parentFrame) {
        this.productService = productService;
        this.cartService = cartService;
        this.currentUser = currentUser;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with search, filter, and cart
        initializeTopPanel();
        
        // Center panel with product list
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load products
        loadProducts();
        updateCartCount();
    }

    private void initializeTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                loadProductsBySearch(keyword);
            } else {
                loadProducts();
            }
        });
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category: "));
        categoryComboBox = new JComboBox<>();
        // Add "All Categories" option
        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName("All Categories");
        categoryComboBox.addItem(allCategory);
        
        // Load categories
        List<Category> categories = productService.getAllCategories();
        for (Category category : categories) {
            categoryComboBox.addItem(category);
        }
        
        categoryComboBox.addActionListener(e -> {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory.getId() == 0) {
                loadProducts();
            } else {
                loadProductsByCategory(selectedCategory.getId());
            }
        });
        filterPanel.add(categoryComboBox);
        
        // Cart panel
        JPanel cartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cartCountLabel = new JLabel("Cart: 0 items");
        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(e -> parentFrame.showCartPanel());
        cartPanel.add(cartCountLabel);
        cartPanel.add(viewCartButton);
        
        // Add panels to top panel
        JPanel searchFilterPanel = new JPanel(new GridLayout(1, 2));
        searchFilterPanel.add(searchPanel);
        searchFilterPanel.add(filterPanel);
        
        topPanel.add(searchFilterPanel, BorderLayout.CENTER);
        topPanel.add(cartPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }

    private void loadProducts() {
        productsPanel.removeAll();
        List<Product> products = productService.getAllProducts();
        displayProducts(products);
    }

    private void loadProductsByCategory(int categoryId) {
        productsPanel.removeAll();
        List<Product> products = productService.getProductsByCategory(categoryId);
        displayProducts(products);
    }

    private void loadProductsBySearch(String keyword) {
        productsPanel.removeAll();
        List<Product> products = productService.searchProducts(keyword);
        displayProducts(products);
    }

    private void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No products found", SwingConstants.CENTER);
            noProductsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            productsPanel.add(noProductsLabel);
        } else {
            for (Product product : products) {
                productsPanel.add(createProductPanel(product));
            }
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductPanel(Product product) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Product details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(nameLabel);
        
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", product.getPrice()));
        detailsPanel.add(priceLabel);
        
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
        JLabel categoryLabel = new JLabel("Category: " + categoryName);
        detailsPanel.add(categoryLabel);
        
        JLabel stockLabel = new JLabel("In Stock: " + product.getStockQuantity());
        detailsPanel.add(stockLabel);
        
        // Add to cart button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getStockQuantity(), 1));
        JButton addToCartButton = new JButton("Add to Cart");
        
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = (int) quantitySpinner.getValue();
                if (cartService.addToCart(currentUser.getId(), product.getId(), quantity)) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            quantity + " " + product.getName() + " added to cart!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateCartCount();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to add product to cart!", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        actionPanel.add(new JLabel("Qty: "));
        actionPanel.add(quantitySpinner);
        actionPanel.add(addToCartButton);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    public void updateCartCount() {
        int count = cartService.getCartItemCount(currentUser.getId());
        cartCountLabel.setText("Cart: " + count + " items");
    }

    public void refresh() {
        loadProducts();
        updateCartCount();
    }
}