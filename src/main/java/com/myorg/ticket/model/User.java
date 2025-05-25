// // src/main/java/com/myorg/ticket/model/User.java

package com.myorg.ticket.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username; // can be null or empty for "guest"

    private final List<UUID> pastReservations = new ArrayList<>();

    /**
     * 
     * Constructor for creating a brand new user.
     */
    public User(String username) {
        this(UUID.randomUUID(), username);
    }

    /**
     * 
     * Constructor for loading an existing user from the database.
     */
    public User(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UUID> getPastReservations() {
        return pastReservations;
    }

    /**
     * 
     * Populates the list of reservation IDs. Called after loading a user from the
     * DB.
     */
    public void setPastReservations(List<UUID> pastReservations) {
        this.pastReservations.clear();
        this.pastReservations.addAll(pastReservations);
    }

    public void addReservation(UUID reservationId) {
        pastReservations.add(reservationId);
    }

}