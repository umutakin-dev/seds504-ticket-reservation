# SEDS504 - TERM PROJECT

GROUP 01

## PROJECT SETUP

### REQUIREMENTS

- JDK
- Maven

### BUILD

```bash
mvn compile
mvn clean compile
```

## RUNNING THE APPLICATION

```bash
mvn exec:java
```

## ROADMAP

**1. Harden the Console UI & I/O**
Input validation: loop until the user gives a non-empty category name, a valid number, or a properly formatted UUID/date.
Friendly errors: catch DateTimeParseException and NumberFormatException to re-prompt rather than crashing.
Reusable menu code: pull your while(running)…switch logic into a Menu helper class or use enums for choices.

**2. User Accounts & “My Reservations” Flow**
Sign up / log in: ask for a username (and optional password) at startup, persist users in a new users table.
Associate reservations: when logged in, automatically link new bookings to the user—and let them List My Reservations instead of entering an ID.

**3. Reservation Management**
Cancel or modify: add menu options to cancel a reservation (which restores ticket availability) or change quantity.
List all reservations: for admins or guests, a “View all reservations” report.

**4. Event Administration**
Update & delete events: allow changing event details (name, date, location) or removing an event entirely.
Event types & filtering: let users filter by “concert,” “sports,” or “theatre” when searching.

**5. Reporting & Export**
CSV or JSON export: dump your events or reservations tables into a file for outside analysis.
Usage scenarios: generate a quick console‐based “today’s events” or “low-inventory” alert (e.g. categories under 10 tickets).

**6. Automated Testing**
JUnit tests: write unit tests for EventService, ReservationService, and your model logic (e.g., TicketCategory.reserve()).
In-memory DB: for tests, point SQLite at :memory: so you can spin up a fresh schema each run.

**7. Logging & Diagnostics**
Swap out System.out for a lightweight logger (java.util.logging or Log4J).
Log every reservation attempt, failures, and DB errors to a file.

**8. Ticket Generation**
Printable ticket: after booking, write a simple text or PDF “ticket” containing event info, reservation ID, quantity, and timestamp.
QR or barcode (bonus): integrate a small library to generate a barcode image for each reservation.

**9. Architecture & Patterns** _DONE_
Separate UI from logic: move all console prompts into a ConsoleUI class so your services remain pure.
Command pattern: model each menu option as a Command object for cleaner dispatch.
Builder pattern: for constructing Event objects, especially as categories grow.

**10. Future directions**
Web interface: swap the console for a tiny Spark Java or Spring Boot app with REST endpoints.
Concurrency: if you imagine multiple threads booking at once, introduce connection pooling (HikariCP) and synchronize ticket updates.

**11. Unit tests for your new structure**

**12. Cancellation flow (restore tickets)**
