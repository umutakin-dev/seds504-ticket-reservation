package com.myorg.ticket.service;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.model.TicketCategory;
import com.myorg.ticket.model.User;

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
                  event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                  uuid TEXT NOT NULL,
                  name TEXT NOT NULL,
                  date_time TEXT NOT NULL,
                  location TEXT NOT NULL
                )
            """);

            // Ticket categories
            st.execute("""
                CREATE TABLE IF NOT EXISTS ticket_categories (
                  event_id INTEGER NOT NULL,
                  category_name TEXT NOT NULL,
                  price REAL NOT NULL,
                  available INTEGER NOT NULL,
                  PRIMARY KEY (event_id, category_name),
                  FOREIGN KEY(event_id) REFERENCES events(event_id)
                )
            """);

            // Reservations
            st.execute("""
                CREATE TABLE IF NOT EXISTS reservations (
                  id TEXT PRIMARY KEY,
                  event_id INTEGER NOT NULL,
                  category_name TEXT NOT NULL,
                  quantity INTEGER NOT NULL,
                  reserved_at TEXT NOT NULL,
                  FOREIGN KEY(event_id, category_name) REFERENCES ticket_categories(event_id, category_name)
                )
            """);

            // Users table
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                  id TEXT PRIMARY KEY,
                  username TEXT NOT NULL UNIQUE
                )
            """);

            // User → Reservation link table
            st.execute("""
                CREATE TABLE IF NOT EXISTS user_reservations (
                  user_id TEXT NOT NULL,
                  reservation_id TEXT NOT NULL,
                  PRIMARY KEY (user_id, reservation_id),
                  FOREIGN KEY(user_id) REFERENCES users(id),
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

    public Event saveEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events(uuid, name, date_time, location) VALUES(?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, event.getUuid().toString());
            p.setString(2, event.getName());
            p.setString(3, event.getDateTime().toString());
            p.setString(4, event.getLocation());
            p.executeUpdate();

            try (ResultSet generatedKeys = p.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int eventId = generatedKeys.getInt(1);

                    for (TicketCategory cat : event.getCategories()) {
                        saveCategory(eventId, cat.getName(), cat.getPrice(), cat.getAvailable());
                    }

                    return Event.builder()
                            .eventId(eventId)
                            .uuid(event.getUuid())
                            .name(event.getName())
                            .dateTime(event.getDateTime())
                            .location(event.getLocation())
                            .build();
                }
            }
        }
        throw new SQLException("Failed to insert event.");
    }

    public List<Event> loadEvents() throws SQLException {
        String sql = "SELECT event_id, uuid, name, date_time, location FROM events";
        List<Event> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
                String location = rs.getString("location");

                var builder = Event.builder()
                        .eventId(eventId)
                        .uuid(uuid)
                        .name(name)
                        .dateTime(dateTime)
                        .location(location);

                for (TicketCategory cat : loadCategories(eventId)) {
                    builder.addCategory(cat.getName(), cat.getPrice(), cat.getAvailable());
                }
                list.add(builder.build());
            }
        }
        return list;
    }

    public Event loadEventById(int eventId) throws SQLException {
        String sql = "SELECT uuid, name, date_time, location FROM events WHERE event_id=?";
        try (Connection c = getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, eventId);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next())
                    return null;

                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
                String location = rs.getString("location");

                var builder = Event.builder()
                        .eventId(eventId)
                        .uuid(uuid)
                        .name(name)
                        .dateTime(dateTime)
                        .location(location);

                for (TicketCategory cat : loadCategories(eventId)) {
                    builder.addCategory(cat.getName(), cat.getPrice(), cat.getAvailable());
                }
                return builder.build();
            }
        }
    }

    // -- TicketCategory CRUD ----------------------------------------------

    public void saveCategory(int eventId, String categoryName, double price, int available) throws SQLException {
        String sql = """
                INSERT OR REPLACE INTO ticket_categories
                (event_id, category_name, price, available)
                VALUES(?,?,?,?)
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, eventId);
            p.setString(2, categoryName);
            p.setDouble(3, price);
            p.setInt(4, available);
            p.executeUpdate();
        }
    }

    public void updateCategory(int eventId, String categoryName, int available) throws SQLException {
        String sql = """
                UPDATE ticket_categories
                SET available = ?
                WHERE event_id = ? AND category_name = ?
                """;
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, available);
            p.setInt(2, eventId);
            p.setString(3, categoryName);
            p.executeUpdate();
        }
    }

    public List<TicketCategory> loadCategories(int eventId) throws SQLException {
        String sql = """
                SELECT category_name, price, available
                FROM ticket_categories
                WHERE event_id = ?
                """;
        List<TicketCategory> cats = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, eventId);
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
                               int eventId,
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
            p.setInt(2, eventId);
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
                        rs.getInt("event_id"),
                        rs.getString("category_name"),
                        rs.getInt("quantity"));
                return Reservation.withId(id,
                        LocalDateTime.parse(rs.getString("reserved_at")), r);
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
                            rs.getInt("event_id"),
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

    public void deleteReservation(String reservationId) throws SQLException {
        // Must delete from the linking table first due to foreign key constraints
        String deleteLinkSql = "DELETE FROM user_reservations WHERE reservation_id = ?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(deleteLinkSql)) {
            p.setString(1, reservationId);
            p.executeUpdate();
        }

        // Then delete the reservation itself
        String deleteReservationSql = "DELETE FROM reservations WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(deleteReservationSql)) {
            p.setString(1, reservationId);
            p.executeUpdate();
        }
    }

    // -- User CRUD --------------------------------------------------------

    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users(id, username) VALUES(?,?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, user.getId().toString());
            p.setString(2, user.getUsername());
            p.executeUpdate();
        }
    }

    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next()) {
                    return null; // No user found
                }
                UUID userId = UUID.fromString(rs.getString("id"));
                return new User(userId, username);
            }
        }
    }

    public List<UUID> loadReservationIdsForUser(String userId) throws SQLException {
        String sql = "SELECT reservation_id FROM user_reservations WHERE user_id = ?";
        List<UUID> ids = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, userId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    ids.add(UUID.fromString(rs.getString("reservation_id")));
                }
            }
        }
        return ids;
    }

    // -- User ↔ Reservation link ------------------------------------------

    public void saveUserReservations(String userId, List<UUID> reservationIds)
            throws SQLException {
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
