package com.ecommerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "/config.properties";
    private static String url;
    private static String username;
    private static String password;
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        // Load database configuration from properties file
        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            
            if (input == null) {
                System.out.println("Sorry, unable to find " + CONFIG_FILE);
                // Fallback to default values
                url = "jdbc:mysql://localhost:3306/ecommerce_db";
                username = "root";
                password = "";
                return;
            }
            
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
            
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to default values
            url = "jdbc:mysql://localhost:3306/ecommerce_db";
            username = "root";
            password = "";
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        return DriverManager.getConnection(url, username, password);
    }
    
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}