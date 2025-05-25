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

    /**
     * 
     * Cancels a reservation, restoring ticket counts.
     * 
     * @param reservationId The ID of the reservation to cancel.
     * 
     * @return true if cancellation was successful.
     */
    public boolean cancelReservation(UUID reservationId) {
        try {
            // 1. Find the reservation
            Reservation res = db.loadReservationById(reservationId.toString());
            if (res == null) {
                throw new IllegalArgumentException("Reservation not found.");
            }

            // 2. Find the corresponding event and ticket category
            Event evt = eventSvc.findById(res.getEventId());
            TicketCategory cat = evt.getCategories().stream()
                    .filter(c -> c.getName().equals(res.getCategoryName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Could not find matching ticket category for this old reservation."));

            // 3. Restore the ticket count
            cat.restore(res.getQuantity());

            // 4. Persist the updated ticket availability
            db.updateCategory(evt.getId().toString(), cat.getName(), cat.getAvailable());

            // 5. Delete the reservation from the database
            db.deleteReservation(reservationId.toString());

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("DB error during cancellation", e);
        }
    }

}
