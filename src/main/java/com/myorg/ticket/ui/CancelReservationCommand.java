// /src/main/java/com/myorg/ticket/ui/CancelReservationCommand.java

package com.myorg.ticket.ui;

import com.myorg.ticket.service.ReservationService;
import java.util.UUID;

public class CancelReservationCommand implements Command {
    private final ConsoleUI ui;
    private final ReservationService reservationService;

    public CancelReservationCommand(ConsoleUI ui, ReservationService reservationService) {
        this.ui = ui;
        this.reservationService = reservationService;
    }

    @Override
    public String key() {
        return "6"; // The next available key
    }

    @Override
    public String description() {
        return "Cancel Reservation";
    }

    @Override
    public void execute() {
        if (ui.getCurrentUser() == null) {
            ui.println("This feature is only for logged-in users.");
            return;
        }

        // First, show the user their reservations so they can pick one
        new ListMyReservationsCommand(ui, reservationService).execute();

        try {
            String id = ui.prompt("\nEnter ID of reservation to cancel: ");
            if (id.isBlank()) {
                ui.println("Cancellation aborted.");
                return;
            }

            boolean success = reservationService.cancelReservation(UUID.fromString(id));

            if (success) {
                ui.println("Reservation cancelled successfully.");
            } else {
                ui.println("Failed to cancel reservation.");
            }

        } catch (IllegalArgumentException e) {
            ui.println("Error: " + e.getMessage());
        } catch (Exception e) {
            ui.println("An unexpected error occurred: " + e.getMessage());
        }
    }

}
