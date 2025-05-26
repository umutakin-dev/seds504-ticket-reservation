package model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.model.TicketCategory;

public class EventTest {

    @Test
    void testBuildEventSuccessfully() {
        String name = "Rock Concert";
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 1, 20, 0);
        String location = "Istanbul Arena";

        Event event = Event.builder()
            .name(name)
            .dateTime(dateTime)
            .location(location)
            .addCategory("VIP", 250.0, 100)
            .addCategory("Regular", 100.0, 300)
            .build();

        assertNotNull(event.getId(), "Event ID should be automatically generated");
        assertEquals(name, event.getName());
        assertEquals(dateTime, event.getDateTime());
        assertEquals(location, event.getLocation());

        List<TicketCategory> categories = event.getCategories();
        assertEquals(2, categories.size(), "Event should have two ticket categories");

        TicketCategory vip = categories.get(0);
        assertEquals("VIP", vip.getName());
        assertEquals(250.0, vip.getPrice());
        assertEquals(100, vip.getAvailable());
    }

    @Test
    void testBuildEventWithCustomId() {
        UUID expectedId = UUID.randomUUID();
        Event event = Event.builder()
            .id(expectedId.toString())
            .name("Tech Conference")
            .dateTime(LocalDateTime.now())
            .location("Berlin")
            .build();

        assertEquals(expectedId, event.getId(), "Event ID should match provided ID");
    }

    @Test
    void testBuildFailsWhenNameMissing() {
        assertThrows(IllegalStateException.class, () -> {
            Event.builder()
                .dateTime(LocalDateTime.now())
                .location("Paris")
                .build();
        }, "Event should not build without a name");
    }

    @Test
    void testBuildFailsWhenDateTimeMissing() {
        assertThrows(IllegalStateException.class, () -> {
            Event.builder()
                .name("No Date")
                .location("Paris")
                .build();
        }, "Event should not build without a dateTime");
    }

    @Test
    void testBuildFailsWhenLocationMissing() {
        assertThrows(IllegalStateException.class, () -> {
            Event.builder()
                .name("No Location")
                .dateTime(LocalDateTime.now())
                .build();
        }, "Event should not build without a location");
    }

    @Test
    void testCategoriesAreImmutable() {
        Event event = Event.builder()
            .name("Immutable Test")
            .dateTime(LocalDateTime.now())
            .location("Test City")
            .addCategory("Test", 1.0, 1)
            .build();

        List<TicketCategory> categories = event.getCategories();
        assertThrows(UnsupportedOperationException.class, () -> {
            categories.add(new TicketCategory("Extra", 2.0, 2));
        }, "Category list should be immutable");
    }
}
