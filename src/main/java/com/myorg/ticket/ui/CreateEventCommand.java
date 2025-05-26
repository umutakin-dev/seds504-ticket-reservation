package com.myorg.ticket.ui;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.service.EventService;

public class CreateEventCommand implements Command {
    private final ConsoleUI ui;
    private final EventService eventSvc;

    public CreateEventCommand(ConsoleUI ui, EventService eventSvc) {
        this.ui = ui;
        this.eventSvc = eventSvc;
    }

    @Override
    public String key() {
        return "1";
    }

    @Override
    public String description() {
        return "Create Event";
    }

    @Override
    public void execute() {
        try {
            String name = ui.prompt("Event name: ");
            var dt = ui.promptDateTime("Date & time (yyyy-MM-ddTHH:mm): ");
            String location = ui.prompt("Location: ");

            var builder = Event.builder()
                    .name(name)
                    .dateTime(dt)
                    .location(location);

            while (ui.confirm("Add ticket category?")) {
                String cat = ui.prompt(" Category name: ");
                double price = ui.promptDouble(" Price: ");
                int avail = ui.promptInt(" Available tickets: ");
                builder.addCategory(cat, price, avail);
            }

            var e = builder.build();
            var savedEvent = eventSvc.createEvent(e); // Kaydedilmiş event'i al
            ui.println("Created event with ID: " + savedEvent.getEventId());
        } catch (Exception ex) {
            ui.println("Error creating event: " + ex.getMessage());
        }
    }
}
