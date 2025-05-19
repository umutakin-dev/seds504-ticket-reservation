package com.myorg.ticket;

import com.myorg.ticket.service.EventService;
import com.myorg.ticket.service.ReservationService;
import com.myorg.ticket.ui.*;

public class App {
    public static void main(String[] args) {
        // 1) Instantiate UI
        ConsoleUI ui = new ConsoleUI();

        // 2) Instantiate services
        EventService eventSvc = new EventService();
        ReservationService resSvc = new ReservationService();

        // 3) Register commands
        ui.register(new CreateEventCommand(ui, eventSvc));
        ui.register(new SearchEventsCommand(ui, eventSvc));
        ui.register(new ReserveTicketsCommand(ui, resSvc));
        ui.register(new ViewReservationCommand(ui, resSvc));
        ui.register(new ExitCommand(ui));

        // 4) Start the loop
        ui.start();
    }
}
