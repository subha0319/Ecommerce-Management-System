package com.ecommerce.service;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.PasswordUtil;

public class AuthService {
    private UserDAO userDAO;
    private static User currentUser;
    
    public AuthService() {
        userDAO = new UserDAO();
    }
    
    public boolean register(String email, String password, String fullName) {
        // Check if user already exists
        if (userDAO.findByEmail(email) != null) {
            return false;
        }
        
        // Create new user with CUSTOMER role
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(PasswordUtil.hashPassword(password));
        newUser.setFullName(fullName);
        newUser.setRole("CUSTOMER");
        
        return userDAO.create(newUser);
    }
    
    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            currentUser = user;
            return user;
        }
        
        return null;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static boolean isAdmin() {
        return isLoggedIn() && currentUser.isAdmin();
    }
}