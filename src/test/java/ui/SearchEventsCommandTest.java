package ui;

import com.myorg.ticket.model.Event;
import com.myorg.ticket.service.EventService;
import com.myorg.ticket.ui.ConsoleUI;
import com.myorg.ticket.ui.SearchEventsCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

public class SearchEventsCommandTest {

    private ConsoleUI mockUI;
    private EventService mockEventService;
    private SearchEventsCommand command;

    @BeforeEach
    void setUp() {
        mockUI = mock(ConsoleUI.class);
        mockEventService = mock(EventService.class);
        command = new SearchEventsCommand(mockUI, mockEventService);
    }

    @Test
    void testNoEventsFound() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        when(mockUI.promptDate("Start date (yyyy-MM-dd): ")).thenReturn(start);
        when(mockUI.promptDate("End date   (yyyy-MM-dd): ")).thenReturn(end);
        when(mockEventService.search(start, end)).thenReturn(List.of());

        command.execute();

        verify(mockUI).println("No events found.");
    }

    @Test
    void testEventsFoundPrinted() {
        LocalDate start = LocalDate.of(2025, 5, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);

        Event event = Event.builder()
                .name("Spring Festival")
                .dateTime(LocalDateTime.of(2025, 5, 10, 18, 0))
                .location("Central Park")
                .addCategory("VIP", 150.0, 50)
                .addCategory("Standard", 75.0, 200)
                .build();

        when(mockUI.promptDate("Start date (yyyy-MM-dd): ")).thenReturn(start);
        when(mockUI.promptDate("End date   (yyyy-MM-dd): ")).thenReturn(end);
        when(mockEventService.search(start, end)).thenReturn(List.of(event));

        command.execute();

        verify(mockUI).println(contains("Spring Festival"));
        verify(mockUI).println(contains("VIP: $150.00"));
        verify(mockUI).println(contains("Standard: $75.00"));
    }

    @Test
    void testSearchThrowsException() {
        when(mockUI.promptDate(anyString())).thenThrow(new RuntimeException("Invalid input"));

        command.execute();

        verify(mockUI).println(startsWith("Error searching events: Invalid input"));
    }
}
