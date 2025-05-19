// src/main/java/com/myorg/ticket/service/EventService.java
package com.myorg.ticket.service;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.TicketCategory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventService {
  private final PersistenceService db = PersistenceService.getInstance();

  /** Persist a new Event (and its categories) into SQLite */
  public void createEvent(Event event) {
    try {
      db.saveEvent(
          event.getId().toString(),
          event.getName(),
          event.getDateTime().toString(),
          event.getLocation());
      for (TicketCategory cat : event.getCategories()) {
        db.saveCategory(
            event.getId().toString(),
            cat.getName(),
            cat.getPrice(),
            cat.getAvailable());
      }
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

  /** Lookup a single event by UUID */
  public Event findById(UUID id) {
    try {
      return db.loadEventById(id.toString());
    } catch (SQLException e) {
      throw new RuntimeException("Error finding event", e);
    }
  }
}
