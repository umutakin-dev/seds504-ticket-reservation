// src/main/java/com/myorg/ticket/ui/Command.java
package com.myorg.ticket.ui;

/** A menu action in the UI. */
public interface Command {
    /** e.g. "1", "2", ..., "0" */
    String key();

    /** description shown next to the key */
    String description();

    /** invoked when the user selects this key */
    void execute();
}
