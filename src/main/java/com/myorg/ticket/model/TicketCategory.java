// src/main/java/com/myorg/ticket/model/TicketCategory.java
package com.myorg.ticket.model;

public class TicketCategory {
    private final String name;
    private final double price;
    private int available;

    public TicketCategory(String name, double price, int available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailable() {
        return available;
    }

    /**
     * Attempt to reserve `quantity` tickets.
     * 
     * @return true if enough tickets were available (and decremented), false
     *         otherwise.
     */
    public boolean reserve(int quantity) {
        if (quantity <= available) {
            available -= quantity;
            return true;
        }
        return false;
    }
}
