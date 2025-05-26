package ui;

import com.myorg.ticket.model.User;
import com.myorg.ticket.service.UserService;
import com.myorg.ticket.ui.Command;
import com.myorg.ticket.ui.ConsoleUI;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsoleUITest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private ByteArrayOutputStream outContent;
    private ByteArrayInputStream inContent;
    private UserService mockUserService;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        mockUserService = mock(UserService.class);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String input) {
        inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);
    }

    @Test
    void testExitFromAuthMenu() {
        provideInput("0\n");

        ConsoleUI ui = new ConsoleUI();
        ui.setUserService(mockUserService);
        ui.start();

        assertTrue(outContent.toString().contains("Goodbye!"));
    }

    @Test
    void testContinueAsGuest() {
        provideInput("3\n0\n"); // Guest -> Exit

        ConsoleUI ui = new ConsoleUI();
        ui.setUserService(mockUserService);
        Command dummyCommand = mock(Command.class);
        when(dummyCommand.key()).thenReturn("0");
        when(dummyCommand.description()).thenReturn("Exit");

        ui.register(dummyCommand);
        ui.start();

        verify(dummyCommand).execute();
    }

    @Test
    void testSuccessfulLogin() {
        provideInput("1\njohn\n0\n");

        User mockUser = new User("john");
        when(mockUserService.login("john")).thenReturn(Optional.of(mockUser));

        ConsoleUI ui = new ConsoleUI();
        ui.setUserService(mockUserService);
        Command dummyCommand = mock(Command.class);
        when(dummyCommand.key()).thenReturn("0");
        when(dummyCommand.description()).thenReturn("Exit");

        ui.register(dummyCommand);
        ui.start();

        assertEquals("john", ui.getCurrentUser().getUsername());
        verify(dummyCommand).execute();
    }

    @Test
    void testFailedLoginThenExit() {
        provideInput("1\nunknown\n0\n");

        when(mockUserService.login("unknown")).thenReturn(Optional.empty());

        ConsoleUI ui = new ConsoleUI();
        ui.setUserService(mockUserService);
        Command dummyCommand = mock(Command.class);
        when(dummyCommand.key()).thenReturn("0");
        when(dummyCommand.description()).thenReturn("Exit");

        ui.register(dummyCommand);
        ui.start();

        assertNull(ui.getCurrentUser());
        assertTrue(outContent.toString().contains("Login failed"));
    }

    @Test
    void testSignUpSuccess() {
        provideInput("2\nnew_user\n0\n");

        User newUser = new User("new_user");
        when(mockUserService.signUp("new_user")).thenReturn(newUser);

        ConsoleUI ui = new ConsoleUI();
        ui.setUserService(mockUserService);
        Command dummyCommand = mock(Command.class);
        when(dummyCommand.key()).thenReturn("0");
        when(dummyCommand.description()).thenReturn("Exit");

        ui.register(dummyCommand);
        ui.start();

        assertEquals("new_user", ui.getCurrentUser().getUsername());
    }
}
