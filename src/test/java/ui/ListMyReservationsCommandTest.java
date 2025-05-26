package ui;

import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.model.User;
import com.myorg.ticket.service.ReservationService;
import com.myorg.ticket.ui.ConsoleUI;
import com.myorg.ticket.ui.ListMyReservationsCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ListMyReservationsCommandTest {

    private ConsoleUI mockUI;
    private ReservationService mockReservationService;
    private ListMyReservationsCommand command;

    @BeforeEach
    void setUp() {
        mockUI = mock(ConsoleUI.class);
        mockReservationService = mock(ReservationService.class);
        command = new ListMyReservationsCommand(mockUI, mockReservationService);
    }

    @Test
    void testNotLoggedInPrintsMessage() {
        when(mockUI.getCurrentUser()).thenReturn(null);

        command.execute();

        verify(mockUI).println("This feature is only for logged-in users.");
        verifyNoInteractions(mockReservationService);
    }

    @Test
    void testNoReservationsPrintsEmptyMessage() {
        User user = new User("testuser");
        when(mockUI.getCurrentUser()).thenReturn(user);
        when(mockReservationService.listByUser(user)).thenReturn(List.of());

        command.execute();

        verify(mockUI).println("--- Your Reservations ---");
        verify(mockUI).println("You have no reservations.");
    }

    @Test
    void testReservationsPrinted() {
        User user = new User("testuser");
        Reservation r1 = new Reservation(101, "VIP", 2);
        Reservation.withId(UUID.randomUUID().toString(), null, r1);

        Reservation r2 = new Reservation(102, "Regular", 4);
        Reservation.withId(UUID.randomUUID().toString(), null, r2);

        when(mockUI.getCurrentUser()).thenReturn(user);
        when(mockReservationService.listByUser(user)).thenReturn(List.of(r1, r2));

        command.execute();

        verify(mockUI).println("--- Your Reservations ---");
        verify(mockUI, atLeastOnce()).println(contains("ID: ")); // Basic format check
        verify(mockUI).println(contains("For Event: 101"));
        verify(mockUI).println(contains("Category: VIP"));
        verify(mockUI).println(contains("Qty: 2"));

        verify(mockUI).println(contains("For Event: 102"));
        verify(mockUI).println(contains("Category: Regular"));
        verify(mockUI).println(contains("Qty: 4"));
    }
}
