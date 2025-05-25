// src/main/java/com/myorg/ticket/service/UserService.java
package com.myorg.ticket.service;

import com.myorg.ticket.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final PersistenceService db = PersistenceService.getInstance();

    /**
     * Attempts to log in a user and load their reservations.
     * 
     * @param username The username to look for.
     * @return An Optional containing the fully loaded User if found, otherwise an
     *         empty Optional.
     */
    public Optional<User> login(String username) {

        try {
            User user = db.findUserByUsername(username);

            if (user != null) {
                List<UUID> reservationIds = db.loadReservationIdsForUser(user.getId().toString());
                user.setPastReservations(reservationIds);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            // In a real app, use a logger throw new RuntimeException("Database error during
            // login", e);
        }
        return Optional.empty();
    }

    /**
     * 
     * Creates and persists a new user.
     * 
     * @param username The desired username.
     * 
     * @return The newly created User object.
     * 
     * @throws IllegalStateException if the username is already taken.
     */
    public User signUp(String username) {
        // 1. Check if user already exists
        if (login(username).isPresent()) {
            throw new IllegalStateException("Username '" + username + "' is already taken.");
        }

        // 2. Create and save the new user
        try {
            User newUser = new User(username);
            db.saveUser(newUser);
            return newUser;
        } catch (SQLException e) {
            // In a real app, use a logger
            throw new RuntimeException("Database error during sign up", e);
        }
    }

}