package com.myorg.ticket.ui;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.TicketCategory;
import com.myorg.ticket.service.EventService;

import java.time.LocalDate;
import java.util.List;

public class SearchEventsCommand implements Command {
    private final ConsoleUI ui;
    private final EventService eventSvc;

    public SearchEventsCommand(ConsoleUI ui, EventService eventSvc) {
        this.ui = ui;
        this.eventSvc = eventSvc;
    }

    @Override
    public String key() {
        return "2";
    }

    @Override
    public String description() {
        return "Search Events";
    }

    @Override
    public void execute() {
        try {
            LocalDate start = ui.promptDate("Start date (yyyy-MM-dd): ");
            LocalDate end = ui.promptDate("End date   (yyyy-MM-dd): ");

            List<Event> list = eventSvc.search(start, end);
            if (list.isEmpty()) {
                ui.println("No events found.");
                return;
            }
            for (Event e : list) {
                ui.println(String.format("ID: %s | %s @ %s | %s",
                        e.getId(), e.getName(), e.getDateTime(), e.getLocation()));
                for (TicketCategory c : e.getCategories()) {
                    ui.println(String.format("  - %s: $%.2f (%d available)",
                            c.getName(), c.getPrice(), c.getAvailable()));
                }
            }
        } catch (Exception ex) {
            ui.println("Error searching events: " + ex.getMessage());
        }
    }
}
