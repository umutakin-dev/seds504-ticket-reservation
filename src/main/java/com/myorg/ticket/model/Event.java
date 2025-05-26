package com.myorg.ticket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event {
    private final int eventId;
    private final UUID uuid;
    private final String name;
    private final LocalDateTime dateTime;
    private final String location;
    private final List<TicketCategory> categories;

    private Event(Builder b) {
        this.eventId = b.eventId;
        this.uuid = (b.uuid != null) ? b.uuid : UUID.randomUUID();
        this.name = b.name;
        this.dateTime = b.dateTime;
        this.location = b.location;
        this.categories = List.copyOf(b.categories);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int eventId = 0;
        private UUID uuid;
        private String name;
        private LocalDateTime dateTime;
        private String location;
        private final List<TicketCategory> categories = new ArrayList<>();

        public Builder eventId(int eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

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

        public Event build() {
            if (name == null || dateTime == null || location == null) {
                throw new IllegalStateException("Event must have name, dateTime, and location");
            }
            return new Event(this);
        }
    }

    // ─── Getters ────────────────────────────────────────────────────────────────

    public int getEventId() {
        return eventId;
    }

    public UUID getUuid() {
        return uuid;
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
