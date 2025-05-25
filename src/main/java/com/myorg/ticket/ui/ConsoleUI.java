// src/main/java/com/myorg/ticket/ui/ConsoleUI.java

package com.myorg.ticket.ui;

import com.myorg.ticket.model.User;
import com.myorg.ticket.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;

public class ConsoleUI {
    private final Scanner in = new Scanner(System.in);
    private final Map<String, Command> commands = new TreeMap<>();

    // New state fields
    private UserService userService;
    private User currentUser = null;

    /** Register a Command under its key. */
    public void register(Command c) {
        commands.put(c.key(), c);
    }

    // New setters and getters for state
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * 
     * The main entry point. First handles authentication, then runs the main
     * command loop.
     */
    public void start() {
        println("Welcome to the Ticket Reservation System!");

        // Phase 1: Authentication Loop
        if (!performAuthentication()) {
            println("Goodbye!");
            return; // User chose to exit from the auth menu
        }

        // Phase 2: Main Application Loop
        runMainMenu();
    }

    private boolean performAuthentication() {
        while (true) {
            println("\n--- Authentication ---");
            println("1) Login");
            println("2) Sign Up");
            println("3) Continue as Guest");
            println("0) Exit");
            String choice = prompt("Choose: ");

            switch (choice) {
                case "1": // Login
                    String username = prompt("Username: ");
                    Optional<User> userOpt = userService.login(username);
                    if (userOpt.isPresent()) {
                        this.currentUser = userOpt.get();
                        println("Welcome back, " + this.currentUser.getUsername() + "!");
                        return true; // Authentication successful, proceed to main menu
                    } else {
                        println("Login failed: User not found or password incorrect.");
                    }
                    break;
                case "2": // Sign Up
                    try {
                        String newUsername = prompt("Choose a username: ");
                        this.currentUser = userService.signUp(newUsername);
                        println("Account created! Welcome, " + this.currentUser.getUsername() + "!");
                        return true; // Sign up successful, proceed to main menu
                    } catch (Exception e) {
                        println("Sign up failed: " + e.getMessage());
                    }
                    break;
                case "3": // Continue as Guest
                    println("Continuing as guest. Some features will be limited.");
                    this.currentUser = null; // Explicitly set user to null for guest session
                    return true; // Proceed to main menu as guest
                case "0": // Exit
                    return false; // Signal to exit the application
                default:
                    println("Invalid choice.");
                    break;
            }
        }
    }

    private void runMainMenu() {
        boolean running = true;
        while (running) {
            println("\n--- Menu ---");
            // Dynamically show commands based on login status
            for (Command cmd : commands.values()) {
                boolean isGuest = (this.currentUser == null);
                // Define which commands guests can see
                boolean isGuestCommand = "2".equals(cmd.key()) || "4".equals(cmd.key()) || "0".equals(cmd.key());

                if (isGuest && !isGuestCommand) {
                    continue; // Skip commands guests shouldn't see
                }
                println(cmd.key() + ") " + cmd.description());
            }

            String choice = prompt("Choose: ");
            Command cmd = commands.get(choice);

            if (cmd != null) {
                cmd.execute();
                if ("0".equals(choice)) { // The "Exit" command key
                    running = false;
                }
            } else {
                println("Invalid choice.");
            }
        }
    }

    // ─── Input Helpers (No Changes)
    // ──────────────────────────────────────────────────

    public String prompt(String msg) {
        System.out.print(msg);
        return in.nextLine().trim();
    }

    public boolean confirm(String msg) {
        return prompt(msg + " (y/n): ").equalsIgnoreCase("y");
    }

    public int promptInt(String msg) {
        while (true) {
            try {
                return Integer.parseInt(prompt(msg));
            } catch (NumberFormatException e) {
                println("Please enter a valid integer.");
            }
        }
    }

    public double promptDouble(String msg) {
        while (true) {
            try {
                return Double.parseDouble(prompt(msg));
            } catch (NumberFormatException e) {
                println("Please enter a valid number.");
            }
        }
    }

    public LocalDate promptDate(String msg) {
        while (true) {
            try {
                return LocalDate.parse(prompt(msg));
            } catch (DateTimeParseException e) {
                println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    public LocalDateTime promptDateTime(String msg) {
        while (true) {
            try {
                return LocalDateTime.parse(prompt(msg));
            } catch (DateTimeParseException e) {
                println("Invalid date-time format. Use yyyy-MM-ddTHH:mm.");
            }
        }
    }

    public void println(String s) {
        System.out.println(s);
    }

}
