// src/main/java/com/myorg/ticket/ui/ReserveTicketsCommand.java

package com.myorg.ticket.ui;

import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.service.ReservationService;

import java.util.UUID;

public class ReserveTicketsCommand implements Command {
    private final ConsoleUI ui;
    private final ReservationService svc;

    public ReserveTicketsCommand(ConsoleUI ui, ReservationService svc) {
        this.ui = ui;
        this.svc = svc;
    }

    @Override
    public String key() {
        return "3";
    }

    @Override
    public String description() {
        return "Reserve Tickets";
    }

    @Override
    public void execute() {
        try {
            UUID eventId = UUID.fromString(ui.prompt("Event ID: "));
            String cat = ui.prompt("Category name: ");
            int qty = ui.promptInt("Quantity: ");

            // UPDATED LINE: Pass the current user from the UI
            Reservation r = svc.makeReservation(eventId, cat, qty, ui.getCurrentUser());

            ui.println("Reserved! Your Reservation ID = " + r.getId());
        } catch (Exception ex) {
            ui.println("Error reserving tickets: " + ex.getMessage());
        }
    }

}
