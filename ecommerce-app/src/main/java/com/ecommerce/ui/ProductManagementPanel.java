package com.ecommerce.ui;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel for managing products (admin functionality)
 */
public class ProductManagementPanel extends JPanel {
    private MainFrame mainFrame;
    private ProductService productService;
    
    private JTable productsTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField priceField;
    private JTextField stockField;
    private JComboBox<Category> categoryComboBox;
    private JTextArea descriptionArea;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton backButton;
    
    private List<Product> products;
    private List<Category> categories;
    private Product selectedProduct;

    /**
     * Constructor for the ProductManagementPanel
     * @param mainFrame The main application frame
     */
    public ProductManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.productService = new ProductService(); // Initialize service
        
        loadData();
        initializeComponents();
        setupLayout();
        setupListeners();
    }
    
    /**
     * Load products and categories from the database
     */
    private void loadData() {
        try {
            products = productService.getAllProducts();
            categories = productService.getAllCategories();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialize all UI components
     */
    private void initializeComponents() {
        // Create table model with column names
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Price", "Stock", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Create table and set properties
        productsTable = new JTable(tableModel);
        productsTable.setRowHeight(25);
        productsTable.getTableHeader().setReorderingAllowed(false);
        
        // Populate table with products
        populateTable();
        
        // Create form fields
        nameField = new JTextField(20);
        priceField = new JTextField(10);
        stockField = new JTextField(10);
        
        // Create category combo box
        categoryComboBox = new JComboBox<>();
        for (Category category : categories) {
            categoryComboBox.addItem(category);
        }
        
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        
        // Create buttons
        addButton = new JButton("Add Product");
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");
        clearButton = new JButton("Clear Form");
        backButton = new JButton("Back to Dashboard");
        
        // Disable update and delete buttons initially
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    /**
     * Populate the table with products
     */
    private void populateTable() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add rows for each product
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getName()
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
        JLabel headerLabel = new JLabel("Product Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(headerLabel, BorderLayout.NORTH);
        
        // Create split pane for table and form
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // Add table in a scroll pane to the left side
        JScrollPane tableScrollPane = new JScrollPane(productsTable);
        splitPane.setLeftComponent(tableScrollPane);
        
        // Create form panel for right side
        JPanel formPanel = createFormPanel();
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        splitPane.setRightComponent(formScrollPane);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Add back button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the form panel for adding/editing products
     * @return The form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add form title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel formTitle = new JLabel("Product Details", JLabel.LEFT);
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        formPanel.add(formTitle, gbc);
        
        // Add name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Add price field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);
        
        // Add stock field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Stock:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(stockField, gbc);
        
        // Add category field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(categoryComboBox, gbc);
        
        // Add description field
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        // Add buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }

    /**
     * Set up action listeners for buttons and table
     */
    private void setupListeners() {
        // Table selection listener
        productsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadProductDetails(selectedRow);
                    updateButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });
        
        // Add button listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        
        // Update button listener
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });
        
        // Delete button listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });
        
        // Clear button listener
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Back button listener
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showPanel(new AdminDashboardPanel(mainFrame));
            }
        });
    }
    
    /**
     * Load the details of the selected product into the form
     * @param selectedRow The selected row index in the table
     */
    private void loadProductDetails(int selectedRow) {
        int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Find the product with the matching ID
        for (Product product : products) {
            if (product.getId() == productId) {
                selectedProduct = product;
                
                // Populate form fields
                nameField.setText(product.getName());
                priceField.setText(String.valueOf(product.getPrice()));
                stockField.setText(String.valueOf(product.getStock()));
                descriptionArea.setText(product.getDescription());
                
                // Select the correct category in the combo box
                for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                    Category category = categoryComboBox.getItemAt(i);
                    if (category.getId() == product.getCategory().getId()) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
                break;
            }
        }
    }
    
    /**
     * Add a new product to the database
     */
    private void addProduct() {
        try {
            // Validate form fields
            if (!validateForm()) {
                return;
            }
            
            // Create new product object
            Product product = new Product();
            product.setName(nameField.getText().trim());
            product.setPrice(Double.parseDouble(priceField.getText().trim()));
            product.setStock(Integer.parseInt(stockField.getText().trim()));
            product.setCategory((Category) categoryComboBox.getSelectedItem());
            product.setDescription(descriptionArea.getText().trim());
            
            // Add product to database
            Product addedProduct = productService.addProduct(product);
            
            // Refresh data and table
            products.add(addedProduct);
            populateTable();
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Product added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearForm();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid number format. Please check price and stock fields.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error adding product: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update an existing product in the database
     */
    private void updateProduct() {
        try {
            // Check if a product is selected
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a product to update.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate form fields
            if (!validateForm()) {
                return;
            }
            
            // Update product object
            selectedProduct.setName(nameField.getText().trim());
            selectedProduct.setPrice(Double.parseDouble(priceField.getText().trim()));
            selectedProduct.setStock(Integer.parseInt(stockField.getText().trim()));
            selectedProduct.setCategory((Category) categoryComboBox.getSelectedItem());
            selectedProduct.setDescription(descriptionArea.getText().trim());
            
            // Update product in database
            productService.updateProduct(selectedProduct);
            
            // Refresh table
            populateTable();
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Product updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form and selection
            clearForm();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid number format. Please check price and stock fields.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating product: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Delete a product from the database
     */
    private void deleteProduct() {
        try {
            // Check if a product is selected
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a product to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete product: " + selectedProduct.getName() + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Delete product from database
                productService.deleteProduct(selectedProduct.getId());
                
                // Remove from list and refresh table
                products.remove(selectedProduct);
                populateTable();
                
                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Product deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form and selection
                clearForm();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error deleting product: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Clear all form fields and reset selection
     */
    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        descriptionArea.setText("");
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }
        
        selectedProduct = null;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        productsTable.clearSelection();
    }
    
    /**
     * Validate form fields
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        // Check if name is empty
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Product name cannot be empty.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        // Check if price is valid
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Price must be greater than zero.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Price must be a valid number.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        // Check if stock is valid
        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this,
                    "Stock cannot be negative.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                stockField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Stock must be a valid integer.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            stockField.requestFocus();
            return false;
        }
        
        // Check if category is selected
        if (categoryComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a category.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            categoryComboBox.requestFocus();
            return false;
        }
        
        return true;
    }
}