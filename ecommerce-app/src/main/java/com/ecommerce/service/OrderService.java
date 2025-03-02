package com.ecommerce.service;

import com.ecommerce.config.DatabaseConnection;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.OrderItemDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final ProductDAO productDAO;
    private final CartService cartService;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.productDAO = new ProductDAO();
        this.cartService = new CartService();
    }

    public List<Order> getUserOrders(int userId) {
        try {
            return orderDAO.getOrdersByUser(userId);
        } catch (SQLException e) {
            System.err.println("Error retrieving user orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Order getOrderDetails(int orderId) {
        try {
            Order order = orderDAO.getOrderById(orderId);
            if (order != null) {
                // Load order items
                List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);
                order.setOrderItems(orderItems);
            }
            return order;
        } catch (SQLException e) {
            System.err.println("Error retrieving order details: " + e.getMessage());
            return null;
        }
    }

    public int createOrder(User user, String shippingAddress) {
        Connection connection = null;
        try {
            // Get user's cart items
            List<CartItem> cartItems = cartService.getUserCart(user.getId());
            if (cartItems.isEmpty()) {
                System.err.println("Cart is empty");
                return -1;
            }

            // Validate stock availability
            if (!cartService.validateCartItemsAvailability(user.getId())) {
                System.err.println("Some items are out of stock");
                return -1;
            }

            // Calculate total amount
            double totalAmount = cartService.calculateCartTotal(user.getId());

            // Start transaction
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending");
            order.setOrderDate(new Date());
            order.setShippingAddress(shippingAddress);

            int orderId = orderDAO.createOrder(order);
            if (orderId == -1) {
                connection.rollback();
                return -1;
            }

            // Create order items and update stock
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                // Create order item
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getProduct().getPrice());
                orderItems.add(orderItem);

                // Update stock
                if (!productDAO.updateStock(cartItem.getProduct().getId(), 
                        cartItem.getProduct().getStockQuantity() - cartItem.getQuantity())) {
                    connection.rollback();
                    return -1;
                }
            }

            // Save order items
            if (!orderItemDAO.addBatchOrderItems(orderItems)) {
                connection.rollback();
                return -1;
            }

            // Clear cart
            if (!cartService.clearCart(user.getId())) {
                connection.rollback();
                return -1;
            }

            // Commit transaction
            connection.commit();
            return orderId;
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return -1;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        try {
            return orderDAO.updateOrderStatus(orderId, newStatus);
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    public List<Order> getAllOrders() {
        try {
            return orderDAO.getAllOrders();
        } catch (SQLException e) {
            System.err.println("Error retrieving all orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}