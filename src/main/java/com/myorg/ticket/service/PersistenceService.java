package com.myorg.ticket.service;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.model.TicketCategory;
// import com.myorg.ticket.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistenceService {
    private static final String URL = "jdbc:sqlite:data/tickets.db";
    private static PersistenceService instance;

    private PersistenceService() {
        try (Connection conn = DriverManager.getConnection(URL);
                Statement st = conn.createStatement()) {

            // Events table
            st.execute("""
                    CREATE TABLE IF NOT EXISTS events (
                      id TEXT PRIMARY KEY,
                      name TEXT NOT NULL,
                      date_time TEXT NOT NULL,
                      location TEXT NOT NULL
                    )
                    """);

            // Ticket categories
            st.execute("""
                    CREATE TABLE IF NOT EXISTS ticket_categories (
                      event_id TEXT NOT NULL,
                      category_name TEXT NOT NULL,
                      price REAL NOT NULL,
                      available INTEGER NOT NULL,
                      PRIMARY KEY (event_id, category_name),
                      FOREIGN KEY(event_id) REFERENCES events(id)
                    )
                    """);

            // Reservations
            st.execute("""
                    CREATE TABLE IF NOT EXISTS reservations (
                      id TEXT PRIMARY KEY,
                      event_id TEXT NOT NULL,
                      category_name TEXT NOT NULL,
                      quantity INTEGER NOT NULL,
                      reserved_at TEXT NOT NULL,
                      FOREIGN KEY(event_id, category_name)
                        REFERENCES ticket_categories(event_id, category_name)
                    )
                    """);

            // User → Reservation link table
            st.execute("""
                    CREATE TABLE IF NOT EXISTS user_reservations (
                      user_id TEXT NOT NULL,
                      reservation_id TEXT NOT NULL,
                      PRIMARY KEY (user_id, reservation_id),
                      FOREIGN KEY(reservation_id) REFERENCES reservations(id)
                    )
                    """);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }

    public static synchronized PersistenceService getInstance() {
        if (instance == null) {
            instance = new PersistenceService();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // -- Event CRUD --------------------------------------------------------

    public void saveEvent(String id, String name, String dateTime, String location) throws SQLException {
        String sql = "INSERT OR REPLACE INTO events(id,name,date_time,location) VALUES(?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, name);
            p.setString(3, dateTime);
            p.setString(4, location);
            p.executeUpdate();
        }
    }

    public List<Event> loadEvents() throws SQLException {
        String sql = "SELECT id, name, date_time, location FROM events";
        List<Event> list = new ArrayList<>();
        try (Connection c = getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Event e = new Event(
                        rs.getString("name"),
                        LocalDateTime.parse(rs.getString("date_time")),
                        rs.getString("location"));
                // overwrite generated id with persisted one:
                e = Event.withId(rs.getString("id"), e);
                // load categories
                e.getCategories().addAll(loadCategories(e.getId().toString()));
                list.add(e);
            }
        }
        return list;
    }

    public Event loadEventById(String id) throws SQLException {
        String sql = "SELECT name, date_time, location FROM events WHERE id = ?";
        try (Connection c = getConnection();
                PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next())
                    return null;
                Event e = new Event(
                        rs.getString("name"),
                        LocalDateTime.parse(rs.getString("date_time")),
                        rs.getString("location"));
                e = Event.withId(id, e);
                e.getCategories().addAll(loadCategories(id));
                return e;
            }
        }
    }

    // -- TicketCategory CRUD ----------------------------------------------

    public void saveCategory(String eventId, String categoryName, double price, int available) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO ticket_categories
                  (event_id, category_name, price, available)
                VALUES(?,?,?,?)
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, eventId);
            p.setString(2, categoryName);
            p.setDouble(3, price);
            p.setInt(4, available);
            p.executeUpdate();
        }
    }

    public void updateCategory(String eventId, String categoryName, int available) throws SQLException {
        String sql = """
                UPDATE ticket_categories
                   SET available = ?
                 WHERE event_id = ? AND category_name = ?
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, available);
            p.setString(2, eventId);
            p.setString(3, categoryName);
            p.executeUpdate();
        }
    }

    public List<TicketCategory> loadCategories(String eventId) throws SQLException {
        String sql = """
                SELECT category_name, price, available
                  FROM ticket_categories
                 WHERE event_id = ?
                """;
        List<TicketCategory> cats = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, eventId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    cats.add(new TicketCategory(
                            rs.getString("category_name"),
                            rs.getDouble("price"),
                            rs.getInt("available")));
                }
            }
        }
        return cats;
    }

    // -- Reservation CRUD -------------------------------------------------

    public void saveReservation(String id,
            String eventId,
            String categoryName,
            int quantity,
            String reservedAt) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO reservations
                  (id, event_id, category_name, quantity, reserved_at)
                VALUES(?,?,?,?,?)
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.setString(2, eventId);
            p.setString(3, categoryName);
            p.setInt(4, quantity);
            p.setString(5, reservedAt);
            p.executeUpdate();
        }
    }

    public Reservation loadReservationById(String id) throws SQLException {
        String sql = """
                SELECT event_id, category_name, quantity, reserved_at
                  FROM reservations
                 WHERE id = ?
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next())
                    return null;
                Reservation r = new Reservation(
                        UUID.fromString(rs.getString("event_id")),
                        rs.getString("category_name"),
                        rs.getInt("quantity"));
                return Reservation.withId(id, LocalDateTime.parse(rs.getString("reserved_at")), r);
            }
        }
    }

    public List<Reservation> loadReservationsByUser(String userId) throws SQLException {
        String sql = """
                SELECT r.id, r.event_id, r.category_name, r.quantity, r.reserved_at
                  FROM reservations r
                  JOIN user_reservations ur ON r.id = ur.reservation_id
                 WHERE ur.user_id = ?
                """;
        List<Reservation> list = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation(
                            UUID.fromString(rs.getString("event_id")),
                            rs.getString("category_name"),
                            rs.getInt("quantity"));
                    r = Reservation.withId(
                            rs.getString("id"),
                            LocalDateTime.parse(rs.getString("reserved_at")),
                            r);
                    list.add(r);
                }
            }
        }
        return list;
    }

    // -- User ↔ Reservation link ------------------------------------------

    public void saveUserReservations(String userId, List<UUID> reservationIds) throws SQLException {
        // delete old links
        try (Connection c = getConnection();
                PreparedStatement del = c.prepareStatement(
                        "DELETE FROM user_reservations WHERE user_id = ?")) {
            del.setString(1, userId);
            del.executeUpdate();
        }

        // insert current
        String sql = "INSERT INTO user_reservations(user_id, reservation_id) VALUES(?,?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            for (UUID rid : reservationIds) {
                p.setString(1, userId);
                p.setString(2, rid.toString());
                p.addBatch();
            }
            p.executeBatch();
        }
    }
}
