package com.myorg.ticket.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private final int eventId;
    private final String categoryName;
    private final int quantity;
    private final LocalDateTime reservedAt;

    public Reservation(int eventId, String categoryName, int quantity) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.reservedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public int getEventId() {
        return eventId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public static Reservation withId(String id, LocalDateTime date, Reservation r) {
        r.id = UUID.fromString(id);
        return r;
    }
}
