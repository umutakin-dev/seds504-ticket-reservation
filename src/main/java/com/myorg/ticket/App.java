package com.myorg.ticket;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.TicketCategory;
import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.service.EventService;
import com.myorg.ticket.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EventService eventService = new EventService();
    private static final ReservationService resService = new ReservationService();

    public static void main(String[] args) {
        System.out.println("=== Ticket Reservation System ===");
        boolean running = true;
        while (running) {
            System.out.println("\n1) Create Event\n" +
                    "2) Search Events\n" +
                    "3) Reserve Tickets\n" +
                    "4) View Reservation\n" +
                    "0) Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> createEvent();
                case "2" -> searchEvents();
                case "3" -> reserveTickets();
                case "4" -> viewReservation();
                case "0" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void createEvent() {
        try {
            System.out.print("Event name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Date & time (yyyy-MM-ddTHH:mm): ");
            LocalDateTime dt = LocalDateTime.parse(scanner.nextLine().trim());

            System.out.print("Location: ");
            String location = scanner.nextLine().trim();

            Event e = new Event(name, dt, location);

            while (true) {
                System.out.print("Add ticket category? (y/n): ");
                if (!scanner.nextLine().equalsIgnoreCase("y"))
                    break;

                System.out.print(" Category name: ");
                String catName = scanner.nextLine().trim();

                System.out.print(" Price: ");
                double price = Double.parseDouble(scanner.nextLine().trim());

                System.out.print(" Available tickets: ");
                int avail = Integer.parseInt(scanner.nextLine().trim());

                e.addCategory(new TicketCategory(catName, price, avail));
            }

            eventService.createEvent(e);
            System.out.println("Event created with ID: " + e.getId());
        } catch (Exception ex) {
            System.out.println("Error creating event: " + ex.getMessage());
        }
    }

    private static void searchEvents() {
        try {
            System.out.print("Start date (yyyy-MM-dd): ");
            LocalDate start = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("End date (yyyy-MM-dd): ");
            LocalDate end = LocalDate.parse(scanner.nextLine().trim());

            List<Event> evts = eventService.search(start, end);
            if (evts.isEmpty()) {
                System.out.println("No events found.");
                return;
            }
            for (Event e : evts) {
                System.out.printf("ID: %s | %s @ %s | %s%n",
                        e.getId(), e.getName(), e.getDateTime(), e.getLocation());
                for (TicketCategory c : e.getCategories()) {
                    System.out.printf("  – %s: $%.2f (%d available)%n",
                            c.getName(), c.getPrice(), c.getAvailable());
                }
            }
        } catch (Exception ex) {
            System.out.println("Error searching events: " + ex.getMessage());
        }
    }

    private static void reserveTickets() {
        try {
            System.out.print("Event ID: ");
            UUID eventId = UUID.fromString(scanner.nextLine().trim());

            System.out.print("Category name: ");
            String catName = scanner.nextLine().trim();

            System.out.print("Quantity: ");
            int qty = Integer.parseInt(scanner.nextLine().trim());

            Reservation r = resService.makeReservation(eventId, catName, qty, null);
            System.out.println("Reserved! Your reservation ID is " + r.getId());
        } catch (Exception ex) {
            System.out.println("Failed to reserve: " + ex.getMessage());
        }
    }

    private static void viewReservation() {
        try {
            System.out.print("Reservation ID: ");
            UUID rid = UUID.fromString(scanner.nextLine().trim());

            Reservation r = resService.findById(rid);
            if (r == null) {
                System.out.println("Reservation not found.");
            } else {
                System.out.printf("Reservation %s: event %s, category %s, qty %d, at %s%n",
                        r.getId(), r.getEventId(), r.getCategoryName(), r.getQuantity(), r.getReservedAt());
            }
        } catch (Exception ex) {
            System.out.println("Error loading reservation: " + ex.getMessage());
        }
    }
}
