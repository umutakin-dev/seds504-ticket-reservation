// src/main/java/com/myorg/ticket/ui/ExitCommand.java
package com.myorg.ticket.ui;

/** Stops the loop in ConsoleUI. */
public class ExitCommand implements Command {
    private final ConsoleUI ui;

    public ExitCommand(ConsoleUI ui) {
        this.ui = ui;
    }

    @Override
    public String key() {
        return "0";
    }

    @Override
    public String description() {
        return "Exit";
    }

    @Override
    public void execute() {
        ui.println("Goodbye!");
    }
}
