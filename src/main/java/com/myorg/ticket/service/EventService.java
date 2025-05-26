package com.myorg.ticket.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.TicketCategory;

public class EventService {
    private final PersistenceService db = PersistenceService.getInstance();

    /** Persist a new Event (and its categories) into SQLite */
    public Event createEvent(Event event) {
        try {
            // Save event and get the one with generated eventId
            Event savedEvent = db.saveEvent(event);

            for (TicketCategory cat : event.getCategories()) {
                db.saveCategory(
                        savedEvent.getEventId(),
                        cat.getName(),
                        cat.getPrice(),
                        cat.getAvailable());
            }
            return savedEvent;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving event", e);
        }
    }

    /** Search for events whose date falls in [start, end] */
    public List<Event> search(LocalDate start, LocalDate end) {
        try {
            return db.loadEvents().stream()
                    .filter(e -> {
                        LocalDate d = e.getDateTime().toLocalDate();
                        return !d.isBefore(start) && !d.isAfter(end);
                    })
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error loading events", e);
        }
    }

    /** Lookup a single event by int eventId */
    public Event findById(int eventId) {
        try {
            return db.loadEventById(eventId);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding event", e);
        }
    }
}
