// src/main/java/com/myorg/ticket/model/Event.java
package com.myorg.ticket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Event {
    private UUID id;
    private String name;
    private LocalDateTime dateTime;
    private String location;
    private final List<TicketCategory> categories = new ArrayList<>();

    public Event(String name, LocalDateTime dateTime, String location) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<TicketCategory> getCategories() {
        return categories;
    }

    public void addCategory(TicketCategory category) {
        categories.add(category);
    }

    public static Event withId(String id, Event e) {
        e.id = UUID.fromString(id); // make id non-final or use reflection/builder
        return e;
    }
}
