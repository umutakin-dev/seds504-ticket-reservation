// src/main/java/com/myorg/ticket/ui/ListMyReservations.java

package com.myorg.ticket.ui;

import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.service.ReservationService;
import java.util.List;

public class ListMyReservationsCommand implements Command {
    private final ConsoleUI ui;
    private final ReservationService reservationService;

    public ListMyReservationsCommand(ConsoleUI ui, ReservationService reservationService) {
        this.ui = ui;
        this.reservationService = reservationService;
    }

    @Override
    public String key() {
        return "5";
    }

    @Override
    public String description() {
        return "List My Reservations";
    }

    @Override
    public void execute() {
        if (ui.getCurrentUser() == null) {
            ui.println("This feature is only for logged-in users.");
            return;
        }

        ui.println("--- Your Reservations ---");
        List<Reservation> reservations = reservationService.listByUser(ui.getCurrentUser());

        if (reservations.isEmpty()) {
            ui.println("You have no reservations.");
        } else {
            for (Reservation r : reservations) {
                // To make this more useful, we'd also load the Event details
                // But for now, this shows the core information.
                ui.println(String.format("ID: %s | For Event: %s | Category: %s | Qty: %d",
                        r.getId(), r.getEventId(), r.getCategoryName(), r.getQuantity()));
            }
        }
    }

}