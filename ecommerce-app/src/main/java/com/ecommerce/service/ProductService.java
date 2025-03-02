package com.ecommerce.service;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Product> getProductsByCategory(int categoryId) {
        try {
            return productDAO.getProductsByCategory(categoryId);
        } catch (SQLException e) {
            System.err.println("Error retrieving products by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Product> searchProducts(String keyword) {
        try {
            return productDAO.searchProducts(keyword);
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Product getProductById(int productId) {
        try {
            return productDAO.getProductById(productId);
        } catch (SQLException e) {
            System.err.println("Error retrieving product: " + e.getMessage());
            return null;
        }
    }

    public boolean addProduct(Product product) {
        try {
            // Ensure the category exists
            if (product.getCategory() != null && product.getCategory().getId() > 0) {
                Category category = categoryDAO.getCategoryById(product.getCategory().getId());
                if (category == null) {
                    System.err.println("Invalid category ID");
                    return false;
                }
            } else {
                System.err.println("Category is required");
                return false;
            }
            
            return productDAO.addProduct(product);
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            return productDAO.updateProduct(product);
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        try {
            return productDAO.deleteProduct(productId);
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStock(int productId, int quantityChange) {
        try {
            // Get current product
            Product product = productDAO.getProductById(productId);
            if (product == null) {
                return false;
            }
            
            // Calculate new quantity
            int newQuantity = product.getStockQuantity() + quantityChange;
            if (newQuantity < 0) {
                System.err.println("Insufficient stock");
                return false;
            }
            
            return productDAO.updateStock(productId, newQuantity);
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }

    public List<Category> getAllCategories() {
        try {
            return categoryDAO.getAllCategories();
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Category getCategoryById(int categoryId) {
        try {
            return categoryDAO.getCategoryById(categoryId);
        } catch (SQLException e) {
            System.err.println("Error retrieving category: " + e.getMessage());
            return null;
        }
    }

    public boolean addCategory(Category category) {
        try {
            return categoryDAO.addCategory(category);
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(Category category) {
        try {
            return categoryDAO.updateCategory(category);
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        try {
            return categoryDAO.deleteCategory(categoryId);
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
}