# Ecommerce-Management-System
# Java Ecommerce Application

## Overview
This is a standalone Java desktop application for ecommerce management built using Java Swing, MySQL, and JDBC. The application provides a complete solution for managing an online store with features including user authentication, product management, shopping cart functionality, order processing, and transaction management.

## Technology Stack
- **Backend**: Java (JDK 17+)
- **Frontend**: Java Swing for GUI
- **Database**: MySQL
- **Database Connection**: JDBC
- **Data Persistence**: DAO (Data Access Object) pattern

## Features

### 1. User Authentication
- User registration with email and password
- User login functionality
- Role-based access control (RBAC) for Customers and Admins
- Secure password storage using hashing techniques

### 2. Product Management
- Admin interface for adding, updating, and deleting products
- Product categorization 
- Each product has attributes: name, price, stock quantity, category, and description
- Search functionality for finding products

### 3. Shopping Cart
- Add products to cart
- Update product quantities
- Remove items from cart
- Persistent cart that saves between sessions

### 4. Order Processing
- Order summary page before checkout
- Stock quantity validation and update
- Order history for users
- Complete order details stored in database

### 5. Transaction Management
- ACID compliance using database transactions
- Transaction rollback on failure to ensure data integrity
- Order state management

### 6. GUI Features
- Login and registration screens
- Product catalog with search functionality
- Shopping cart interface
- Order summary and checkout UI
- Admin dashboard for product management

## Project Structure
```
ecommerce-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── ecommerce/
│   │   │   │   │   ├── Main.java (Application entry point)
│   │   │   │   │   ├── config/
│   │   │   │   │   │   └── DatabaseConnection.java
│   │   │   │   │   ├── dao/ (Data Access Objects)
│   │   │   │   │   │   ├── UserDAO.java
│   │   │   │   │   │   ├── ProductDAO.java
│   │   │   │   │   │   ├── CategoryDAO.java
│   │   │   │   │   │   ├── OrderDAO.java
│   │   │   │   │   │   ├── OrderItemDAO.java
│   │   │   │   │   │   └── CartItemDAO.java
│   │   │   │   │   ├── model/ (Data models/entities)
│   │   │   │   │   │   ├── User.java
│   │   │   │   │   │   ├── Product.java
│   │   │   │   │   │   ├── Category.java
│   │   │   │   │   │   ├── Order.java
│   │   │   │   │   │   ├── OrderItem.java
│   │   │   │   │   │   └── CartItem.java
│   │   │   │   │   ├── service/ (Business logic)
│   │   │   │   │   │   ├── AuthService.java
│   │   │   │   │   │   ├── ProductService.java
│   │   │   │   │   │   ├── CartService.java
│   │   │   │   │   │   └── OrderService.java
│   │   │   │   │   ├── ui/ (Swing UI components)
│   │   │   │   │   │   ├── LoginPanel.java
│   │   │   │   │   │   ├── RegisterPanel.java
│   │   │   │   │   │   ├── MainFrame.java
│   │   │   │   │   │   ├── ProductListPanel.java
│   │   │   │   │   │   ├── CartPanel.java
│   │   │   │   │   │   ├── OrderSummaryPanel.java
│   │   │   │   │   │   ├── AdminDashboardPanel.java
│   │   │   │   │   │   └── ProductManagementPanel.java
│   │   │   │   │   └── util/ (Utility classes)
│   │   │   │   │       ├── PasswordUtil.java
│   │   │   │   │       └── ValidationUtil.java
│   │   │   │   │
│   │   │   │   └── resources/
│   │   │   │       ├── config.properties (Database connection details)
│   │   │   │       └── images/
└── lib/ (External JAR dependencies)
    ├── mysql-connector-java-8.0.27.jar
    └── other-dependencies.jar
```

## Implementation Details

### Database Design

The database schema includes the following tables:

1. **users**
   - id (Primary Key)
   - email
   - password_hash
   - name
   - role (ADMIN or CUSTOMER)
   - created_at

2. **categories**
   - id (Primary Key)
   - name
   - description

3. **products**
   - id (Primary Key)
   - name
   - description
   - price
   - stock
   - category_id (Foreign Key to categories)
   - created_at

4. **cart_items**
   - id (Primary Key)
   - user_id (Foreign Key to users)
   - product_id (Foreign Key to products)
   - quantity
   - added_at

5. **orders**
   - id (Primary Key)
   - user_id (Foreign Key to users)
   - total_amount
   - status (PENDING, COMPLETED, CANCELLED)
   - created_at

6. **order_items**
   - id (Primary Key)
   - order_id (Foreign Key to orders)
   - product_id (Foreign Key to products)
   - quantity
   - price_at_purchase

### Design Patterns

The application implements several design patterns:

1. **DAO (Data Access Object)** - Separates the data access layer from the business logic
2. **Service Layer** - Contains the business logic and coordinates between UI and DAO
3. **MVC (Model-View-Controller)** - Models represent data, Views are UI components, Controllers are service classes
4. **Singleton** - Used for database connection management

### Key Classes

#### Config
- **DatabaseConnection.java** - Manages database connections using a connection pool

#### Models
- **User.java** - Represents user data with authentication information
- **Product.java** - Contains product details including price and inventory
- **Category.java** - Represents product categories
- **Order.java** - Contains order information
- **OrderItem.java** - Represents items within an order
- **CartItem.java** - Represents items in a user's shopping cart

#### Data Access Objects (DAOs)
- **UserDAO.java** - Handles user data persistence
- **ProductDAO.java** - Manages product data
- **CategoryDAO.java** - Handles category data
- **OrderDAO.java** - Manages order records
- **OrderItemDAO.java** - Handles order item records
- **CartItemDAO.java** - Manages shopping cart data

#### Services
- **AuthService.java** - Handles user authentication and registration
- **ProductService.java** - Manages product operations and inventory
- **CartService.java** - Handles shopping cart operations
- **OrderService.java** - Manages order processing and transaction management

#### UI Components
- **MainFrame.java** - Main application window that contains all panels
- **LoginPanel.java** - User login interface
- **RegisterPanel.java** - User registration interface
- **ProductListPanel.java** - Displays product catalog with search
- **CartPanel.java** - Shows the user's shopping cart
- **OrderSummaryPanel.java** - Displays order summary before checkout
- **AdminDashboardPanel.java** - Main navigation hub for admin functions
- **ProductManagementPanel.java** - Interface for managing products

#### Utilities
- **PasswordUtil.java** - Handles password hashing and verification
- **ValidationUtil.java** - Input validation utilities

### ACID Transaction Management

The application ensures ACID (Atomicity, Consistency, Isolation, Durability) compliance for critical operations:

```java
// Example from OrderService.java
public Order createOrder(int userId, List<CartItem> cartItems) throws Exception {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction
        
        // 1. Create order record
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalAmount(calculateTotal(cartItems));
        order = orderDAO.create(conn, order);
        
        // 2. Create order items and update inventory
        for (CartItem item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProduct().getId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(item.getProduct().getPrice());
            orderItemDAO.create(conn, orderItem);
            
            // Update product stock
            productDAO.updateStock(conn, item.getProduct().getId(), 
                    item.getProduct().getStock() - item.getQuantity());
        }
        
        // 3. Update order status
        order.setStatus("COMPLETED");
        orderDAO.update(conn, order);
        
        conn.commit(); // Commit transaction
        return order;
    } catch (Exception e) {
        if (conn != null) {
            try {
                conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                throw new Exception("Failed to rollback transaction", ex);
            }
        }
        throw e;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                // Log error
            }
        }
    }
}
```

### Security Features

1. **Password Security**
   - Passwords are hashed using BCrypt algorithm
   - Salt is automatically generated for each password

2. **Input Validation**
   - All user inputs are validated for format and content
   - Protection against SQL injection using prepared statements

3. **Access Control**
   - Role-based access control (RBAC)
   - Admin functions are protected from regular users

## Setup Instructions

### Prerequisites
- JDK 17 or higher
- MySQL Database Server
- MySQL Connector/J (included in lib folder)

### Database Setup
1. Create a new MySQL database named `ecommerce_db`
2. Execute the SQL script in `database_setup.sql` to create the required tables
3. Update the `config.properties` file with your database credentials

### Running the Application
1. Compile the application:
   ```
   javac -cp "lib/*" -d bin src/main/java/com/ecommerce/Main.java
   ```
2. Run the application:
   ```
   java -cp "bin;lib/*" com.ecommerce.Main
   ```

## Default Admin Account
- Email: admin@example.com
- Password: admin123

## Future Enhancements
- Implement product image upload and display
- Add customer reviews and ratings
- Implement discount/coupon system
- Add payment gateway integration
- Create reports and analytics for admins
- Improve UI with additional styling
- Add internationalization (i18n) support
- Implement inventory notifications for low stock
