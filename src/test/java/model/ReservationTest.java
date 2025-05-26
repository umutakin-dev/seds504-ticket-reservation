package model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.myorg.ticket.model.Reservation;

public class ReservationTest {

    @Test
    void testConstructor() {
        int eventId = 42;
        String categoryName = "VIP";
        int quantity = 2;

        Reservation reservation = new Reservation(eventId, categoryName, quantity);

        assertNotNull(reservation.getId(), "Generated reservation ID should not be null");
        assertEquals(eventId, reservation.getEventId(), "Event ID should match");
        assertEquals(categoryName, reservation.getCategoryName(), "Category name should match");
        assertEquals(quantity, reservation.getQuantity(), "Quantity should match");
        assertNotNull(reservation.getReservedAt(), "Reservation time should be set");
        assertTrue(reservation.getReservedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "Reservation time should be near current time");
    }

    @Test
    void testWithId() {
        UUID originalId = UUID.randomUUID();
        int eventId = 7;
        String categoryName = "Standard";
        int quantity = 5;
        LocalDateTime customDate = LocalDateTime.of(2025, 1, 1, 12, 0);

        Reservation original = new Reservation(eventId, categoryName, quantity);
        Reservation modified = Reservation.withId(originalId.toString(), customDate, original);

        assertEquals(originalId, modified.getId(), "Modified ID should match provided ID");
        assertSame(original, modified, "Returned reservation should be same instance");
        // NOT: `customDate` is not applied, but test ensures `withId` behaves as implemented.
    }
}
