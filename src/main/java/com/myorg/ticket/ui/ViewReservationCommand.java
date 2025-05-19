package com.myorg.ticket.ui;

import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.service.ReservationService;

import java.util.UUID;

public class ViewReservationCommand implements Command {
    private final ConsoleUI ui;
    private final ReservationService svc;

    public ViewReservationCommand(ConsoleUI ui, ReservationService svc) {
        this.ui = ui;
        this.svc = svc;
    }

    @Override
    public String key() {
        return "4";
    }

    @Override
    public String description() {
        return "View Reservation";
    }

    @Override
    public void execute() {
        try {
            UUID rid = UUID.fromString(ui.prompt("Reservation ID: "));
            Reservation r = svc.findById(rid);
            if (r == null) {
                ui.println("Not found.");
            } else {
                ui.println(String.format(
                        "Reservation %s: event %s, category %s, qty %d, at %s",
                        r.getId(), r.getEventId(), r.getCategoryName(),
                        r.getQuantity(), r.getReservedAt()));
            }
        } catch (Exception ex) {
            ui.println("Error loading reservation: " + ex.getMessage());
        }
    }
}
