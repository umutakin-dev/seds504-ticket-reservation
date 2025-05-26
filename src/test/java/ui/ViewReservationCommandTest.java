package ui;

import com.myorg.ticket.model.Reservation;
import com.myorg.ticket.service.ReservationService;
import com.myorg.ticket.ui.ConsoleUI;
import com.myorg.ticket.ui.ViewReservationCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ViewReservationCommandTest {

    private ConsoleUI mockUI;
    private ReservationService mockService;
    private ViewReservationCommand command;

    @BeforeEach
    void setUp() {
        mockUI = mock(ConsoleUI.class);
        mockService = mock(ReservationService.class);
        command = new ViewReservationCommand(mockUI, mockService);
    }

    @Test
    void testReservationFoundPrintsDetails() {
        UUID rid = UUID.randomUUID();
        Reservation reservation = new Reservation(UUID.randomUUID(), "VIP", 2);
        Reservation.withId(rid.toString(), LocalDateTime.of(2025, 1, 1, 12, 0), reservation);

        when(mockUI.prompt("Reservation ID: ")).thenReturn(rid.toString());
        when(mockService.findById(rid)).thenReturn(reservation);

        command.execute();

        verify(mockUI).println(contains("Reservation " + rid));
        verify(mockUI).println(contains("VIP"));
        verify(mockUI).println(contains("qty 2"));
    }

    @Test
    void testReservationNotFoundPrintsNotFound() {
        UUID rid = UUID.randomUUID();

        when(mockUI.prompt("Reservation ID: ")).thenReturn(rid.toString());
        when(mockService.findById(rid)).thenReturn(null);

        command.execute();

        verify(mockUI).println("Not found.");
    }

    @Test
    void testInvalidUUIDPrintsError() {
        when(mockUI.prompt("Reservation ID: ")).thenReturn("invalid-uuid");

        command.execute();

        verify(mockUI).println(startsWith("Error loading reservation"));
    }
}
