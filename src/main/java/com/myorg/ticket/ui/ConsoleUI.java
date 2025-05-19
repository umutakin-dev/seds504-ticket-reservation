package com.myorg.ticket.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ConsoleUI {
    private final Scanner in = new Scanner(System.in);
    private final Map<String, Command> commands = new TreeMap<>();

    /** Register a Command under its key. */
    public void register(Command c) {
        commands.put(c.key(), c);
    }

    /** Start the prompt loop. */
    public void start() {
        boolean running = true;
        while (running) {
            println("\n--- Menu ---");
            commands.values().forEach(c -> println(c.key() + ") " + c.description()));
            String choice = prompt("Choose: ");
            Command cmd = commands.get(choice);
            if (cmd != null) {
                cmd.execute();
                if ("0".equals(choice)) {
                    running = false;
                }
            } else {
                println("Invalid choice.");
            }
        }
    }

    // ─── Input Helpers ───────────────────────────────────────────────────────────

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
