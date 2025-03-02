package com.ecommerce.service;

import com.ecommerce.dao.CartItemDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final CartItemDAO cartItemDAO;
    private final ProductDAO productDAO;

    public CartService() {
        this.cartItemDAO = new CartItemDAO();
        this.productDAO = new ProductDAO();
    }

    public List<CartItem> getUserCart(int userId) {
        try {
            return cartItemDAO.getCartItemsByUser(userId);
        } catch (SQLException e) {
            System.err.println("Error retrieving cart items: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean addToCart(int userId, int productId, int quantity) {
        try {
            // Validate product and stock availability
            Product product = productDAO.getProductById(productId);
            if (product == null) {
                System.err.println("Product not found");
                return false;
            }
            
            if (quantity <= 0) {
                System.err.println("Invalid quantity");
                return false;
            }
            
            if (product.getStockQuantity() < quantity) {
                System.err.println("Insufficient stock");
                return false;
            }
            
            // Create cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            
            return cartItemDAO.addToCart(cartItem);
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCartItemQuantity(int cartItemId, int quantity) {
        try {
            // Validate quantity
            if (quantity <= 0) {
                // If quantity is zero or negative, remove the item
                return cartItemDAO.removeFromCart(cartItemId);
            }
            
            return cartItemDAO.updateCartItemQuantity(cartItemId, quantity);
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFromCart(int cartItemId) {
        try {
            return cartItemDAO.removeFromCart(cartItemId);
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }

    public boolean clearCart(int userId) {
        try {
            return cartItemDAO.clearCart(userId);
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }

    public double calculateCartTotal(int userId) {
        double total = 0.0;
        List<CartItem> cartItems = getUserCart(userId);
        
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        
        return total;
    }

    public int getCartItemCount(int userId) {
        try {
            return cartItemDAO.getCartItemCount(userId);
        } catch (SQLException e) {
            System.err.println("Error getting cart count: " + e.getMessage());
            return 0;
        }
    }

    public boolean validateCartItemsAvailability(int userId) {
        List<CartItem> cartItems = getUserCart(userId);
        
        for (CartItem item : cartItems) {
            try {
                // Get the latest product information
                Product product = productDAO.getProductById(item.getProduct().getId());
                if (product == null || product.getStockQuantity() < item.getQuantity()) {
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("Error validating cart items: " + e.getMessage());
                return false;
            }
        }
        
        return true;
    }
}