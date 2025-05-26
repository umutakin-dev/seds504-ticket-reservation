package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.myorg.ticket.model.User;

public class UserTest {

    @Test
    void testNewUserConstructor() {
        String username = "testuser";
        User user = new User(username);

        assertNotNull(user.getId(), "New user should have a generated UUID");
        assertEquals(username, user.getUsername(), "Username should match the input");
        assertNotEquals("username", user.getUsername(), "Username should match the input");
        assertTrue(user.getPastReservations().isEmpty(), "New user should have no past reservations");
    }

    @Test
    void testExistingUserConstructor() {
        UUID id = UUID.randomUUID();
        String username = "existinguser";
        User user = new User(id, username);

        assertEquals(id, user.getId(), "User ID should match the given UUID");
        assertEquals(username, user.getUsername(), "Username should match the input");
    }

    @Test
    void testUsernameSetterAndGetter() {
        User user = new User("guest");

        user.setUsername("updatedUser");
        assertEquals("updatedUser", user.getUsername(), "Username should be updated correctly");
    }

    @Test
    void testAddReservation() {
        User user = new User("tester");
        UUID reservationId = UUID.randomUUID();

        user.addReservation(reservationId);

        List<UUID> reservations = user.getPastReservations();
        assertEquals(1, reservations.size(), "Reservation list should contain one item");
        assertEquals(reservationId, reservations.get(0), "Added reservation ID should match");
    }

    @Test
    void testSetPastReservations() {
        User user = new User("batchUser");
        UUID res1 = UUID.randomUUID();
        UUID res2 = UUID.randomUUID();
        List<UUID> newList = Arrays.asList(res1, res2);

        user.setPastReservations(newList);

        List<UUID> stored = user.getPastReservations();
        assertEquals(2, stored.size(), "Past reservations list should contain all set items");
        assertTrue(stored.contains(res1) && stored.contains(res2), "List should contain all given IDs");
    }

    @Test
    void testSetPastReservationsClearsOld() {
        User user = new User("clearTest");
        UUID old = UUID.randomUUID();
        UUID newOne = UUID.randomUUID();

        user.addReservation(old);
        user.setPastReservations(List.of(newOne));

        List<UUID> reservations = user.getPastReservations();
        assertEquals(1, reservations.size(), "Old reservations should be cleared");
        assertEquals(newOne, reservations.get(0), "Only new reservation should be kept");
    }
}
