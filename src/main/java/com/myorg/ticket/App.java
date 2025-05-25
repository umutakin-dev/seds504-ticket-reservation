// src/main/java/com/myorg/ticket/ui/App.java

package com.myorg.ticket;

import com.myorg.ticket.service.EventService;
import com.myorg.ticket.service.ReservationService;
import com.myorg.ticket.service.UserService;
import com.myorg.ticket.ui.*;

public class App {
    public static void main(String[] args) {
        // 1) Instantiate UI
        ConsoleUI ui = new ConsoleUI();

        // 2) Instantiate services
        EventService eventSvc = new EventService();
        ReservationService resSvc = new ReservationService();
        UserService userSvc = new UserService(); // New service

        // 3) Configure the UI
        // Pass the UserService to the UI so it can handle login/signup
        ui.setUserService(userSvc);

        // 4) Register ALL commands. The UI will decide which ones to show.
        ui.register(new CreateEventCommand(ui, eventSvc));
        ui.register(new SearchEventsCommand(ui, eventSvc));
        ui.register(new ReserveTicketsCommand(ui, resSvc));
        ui.register(new ViewReservationCommand(ui, resSvc));
        ui.register(new ListMyReservationsCommand(ui, resSvc));
        ui.register(new CancelReservationCommand(ui, resSvc));
        ui.register(new ExitCommand(ui));

        // 5) Start the loop
        ui.start();
    }

}
