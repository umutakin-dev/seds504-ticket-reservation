// src/main/java/com/myorg/ticket/model/Event.java
package com.myorg.ticket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event {
    private final UUID id;
    private final String name;
    private final LocalDateTime dateTime;
    private final String location;
    private final List<TicketCategory> categories;

    private Event(Builder b) {
        this.id = (b.id != null) ? b.id : UUID.randomUUID();
        this.name = b.name;
        this.dateTime = b.dateTime;
        this.location = b.location;
        this.categories = List.copyOf(b.categories);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private LocalDateTime dateTime;
        private String location;
        private final List<TicketCategory> categories = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder dateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder addCategory(String catName, double price, int available) {
            this.categories.add(new TicketCategory(catName, price, available));
            return this;
        }

        public Builder id(String id) {
            this.id = UUID.fromString(id);
            return this;
        }

        public Event build() {
            if (name == null || dateTime == null || location == null) {
                throw new IllegalStateException("Event must have name, dateTime, and location");
            }
            return new Event(this);
        }
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public List<TicketCategory> getCategories() {
        return categories;
    }
}
