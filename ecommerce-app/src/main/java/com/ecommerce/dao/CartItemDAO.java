package com.ecommerce.dao;

import com.ecommerce.config.DatabaseConnection;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO {
    private Connection connection;

    public CartItemDAO() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }
    }

    public List<CartItem> getCartItemsByUser(int userId) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();
        String query = "SELECT ci.*, p.name, p.price, p.image_url, p.stock_quantity " +
                      "FROM cart_items ci " +
                      "JOIN products p ON ci.product_id = p.id " +
                      "WHERE ci.user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem cartItem = new CartItem();
                    cartItem.setId(rs.getInt("id"));
                    cartItem.setUserId(rs.getInt("user_id"));
                    
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    cartItem.setProduct(product);
                    
                    cartItem.setQuantity(rs.getInt("quantity"));
                    cartItems.add(cartItem);
                }
            }
        }
        return cartItems;
    }

    public CartItem getCartItem(int userId, int productId) throws SQLException {
        String query = "SELECT ci.*, p.name, p.price, p.image_url, p.stock_quantity " +
                      "FROM cart_items ci " +
                      "JOIN products p ON ci.product_id = p.id " +
                      "WHERE ci.user_id = ? AND ci.product_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CartItem cartItem = new CartItem();
                    cartItem.setId(rs.getInt("id"));
                    cartItem.setUserId(rs.getInt("user_id"));
                    
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getDouble("price"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    cartItem.setProduct(product);
                    
                    cartItem.setQuantity(rs.getInt("quantity"));
                    return cartItem;
                }
            }
        }
        return null;
    }

    public boolean addToCart(CartItem cartItem) throws SQLException {
        // Check if the item already exists in the cart
        CartItem existingItem = getCartItem(cartItem.getUserId(), cartItem.getProduct().getId());
        
        if (existingItem != null) {
            // Update quantity if item already exists
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            return updateCartItemQuantity(existingItem.getId(), newQuantity);
        } else {
            // Add new item if it doesn't exist
            String query = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
            
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, cartItem.getUserId());
                stmt.setInt(2, cartItem.getProduct().getId());
                stmt.setInt(3, cartItem.getQuantity());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            cartItem.setId(generatedKeys.getInt(1));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean updateCartItemQuantity(int cartItemId, int quantity) throws SQLException {
        String query = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeFromCart(int cartItemId) throws SQLException {
        String query = "DELETE FROM cart_items WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartItemId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean clearCart(int userId) throws SQLException {
        String query = "DELETE FROM cart_items WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public int getCartItemCount(int userId) throws SQLException {
        String query = "SELECT SUM(quantity) FROM cart_items WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}