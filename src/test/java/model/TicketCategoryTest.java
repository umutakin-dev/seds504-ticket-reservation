package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.myorg.ticket.model.TicketCategory;

public class TicketCategoryTest {

    @Test
    void testReserveSuccess() {
        TicketCategory category = new TicketCategory("Concert-A", 100.0, 10);

        boolean result = category.reserve(3);
        assertTrue(result, "Should return true when reserving less than available");
        assertEquals(7, category.getAvailable(), "Available tickets should decrease by reserved amount");
    }

    @Test
    void testReserveFailure_NotEnoughTickets() {
        TicketCategory category = new TicketCategory("Concert-B", 150.0, 5);

        boolean result = category.reserve(6);
        assertFalse(result, "Should return false when reserving more than available");
        assertEquals(5, category.getAvailable(), "Available tickets should not change on failed reserve");
    }

    @Test
    void testReserveFailure_NegativeQuantity() {
        TicketCategory category = new TicketCategory("Concert-C", 50.0, 10);

        boolean result = category.reserve(-2);
        assertFalse(result, "Should return false when reserving negative amount");
        assertEquals(10, category.getAvailable(), "Available tickets should not change on invalid reserve");
    }

    @Test
    void testRestore() {
        TicketCategory category = new TicketCategory("Concert-D", 80.0, 5);

        category.restore(3);
        assertEquals(8, category.getAvailable(), "Available tickets should increase by restored amount");
    }

    @Test
    void testRestoreNegativeIgnored() {
        TicketCategory category = new TicketCategory("Concert-E", 120.0, 4);

        category.restore(-2);
        assertEquals(4, category.getAvailable(), "Available tickets should not change on invalid restore");
    }
}
