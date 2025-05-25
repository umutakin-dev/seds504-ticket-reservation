// src/main/java/com/myorg/ticket/model/User.java
package com.myorg.ticket.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username; // can be null or empty for "guest"
    private final List<UUID> pastReservations = new ArrayList<>();

    public User(String username) {
        this.id = UUID.randomUUID();
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

    public void addReservation(UUID reservationId) {
        pastReservations.add(reservationId);
    }
}