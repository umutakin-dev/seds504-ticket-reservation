// src/main/java/com/myorg/ticket/service/ReservationService.java
package com.myorg.ticket.service;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.model.TicketCategory;
import com.myorg.ticket.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ReservationService {
    private final PersistenceService db = PersistenceService.getInstance();
    private final EventService eventSvc = new EventService();

    /**
     * Reserve tickets in a given category for an event.
     * If user ≠ null, associates reservation with that user.
     */
    public Reservation makeReservation(UUID eventId, String categoryName, int qty, User user) {
        try {
            // 1) Load event & find the right category
            Event evt = eventSvc.findById(eventId);
            TicketCategory cat = evt.getCategories().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            // 2) Attempt to reserve
            if (!cat.reserve(qty)) {
                throw new IllegalStateException("Not enough tickets available");
            }
            // 3) Persist updated availability
            db.updateCategory(eventId.toString(), categoryName, cat.getAvailable());

            // 4) Create & save reservation record
            Reservation res = new Reservation(eventId, categoryName, qty);
            db.saveReservation(
                    res.getId().toString(),
                    eventId.toString(),
                    categoryName,
                    qty,
                    res.getReservedAt().toString());

            // 5) Link to user if provided
            if (user != null) {
                user.addReservation(res.getId());
                db.saveUserReservations(
                        user.getId().toString(),
                        user.getPastReservations());
            }

            return res;
        } catch (SQLException e) {
            throw new RuntimeException("Error making reservation", e);
        }
    }

    /** List all reservations for a given user */
    public List<Reservation> listByUser(User user) {
        try {
            return db.loadReservationsByUser(user.getId().toString());
        } catch (SQLException e) {
            throw new RuntimeException("Error loading reservations", e);
        }
    }

    /** Lookup single reservation by ID */
    public Reservation findById(UUID reservationId) {
        try {
            return db.loadReservationById(reservationId.toString());
        } catch (SQLException e) {
            throw new RuntimeException("Error loading reservation", e);
        }
    }
}
