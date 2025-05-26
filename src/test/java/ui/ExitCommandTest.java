package ui;

import com.myorg.ticket.ui.ConsoleUI;
import com.myorg.ticket.ui.ExitCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class ExitCommandTest {

    private ConsoleUI mockUI;
    private ExitCommand command;

    @BeforeEach
    void setUp() {
        mockUI = mock(ConsoleUI.class);
        command = new ExitCommand(mockUI);
    }

    @Test
    void testExecutePrintsGoodbye() {
        command.execute();
        verify(mockUI).println("Goodbye!");
    }

    @Test
    void testKeyAndDescription() {
        assert command.key().equals("0");
        assert command.description().equals("Exit");
    }
}
