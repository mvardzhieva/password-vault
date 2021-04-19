package password.vault.command;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandExecutorTest {
    private static final String TEST_REGISTER_COMMAND = "register Pesho asdf asdf";
    private static final String TEST_RETRIEVE_CREDENTIALS_COMMAND = "retrieve_credentials facebook.com pesho@gmail.com";
    private static final String TEST_REMOVE_PASSWORD_COMMAND = "remove_password facebook.com pesho@gmail.com";
    private static final String TEST_ADD_PASSWORD_COMMAND = "add_password facebook.com peshooo@gmail.com 123fgh$%&";
    private static final String TEST_USERNAME_LOGGED = "Pesho";
    private static final String TEST_USERNAME_NOT_LOGGED = null;
    private static final String LOGOUT = "logout";
    private static final String NOT_LOGGED_USER = "You are not logged in";
    private static final String MISSING_WEBSITE_AND_USERNAME = "Command requires website and username";
    public static final String INVALID_WEBSITE_USERNAME = "Invalid website/username combination";
    private static final String NO_WEBSITES = "There are no websites added";

    private CommandExecutor executor;

    @Before
    public void setUp() {
        executor = new CommandExecutor();
    }

    @Test
    public void testExecuteCommandRegisterUsernameAlreadyTaken() {
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND);
        String expected = "Username Pesho is already taken, select another one";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND));
    }

    @Test
    public void testExecuteCommandRegisterNotMatchingPasswords() {
        String expected = "Password and repeated password must match";
        String message = "register Anna 1234 asdf";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandRegisterNotEnoughArguments() {
        String expected = "Command requires username, password and repeated password";
        String message = "register Anna 1234";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandLoginSuccessfully() {
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND);
        String expected = "User Pesho successfully logged in";
        String message = "login Pesho asdf";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandLoginNotEnoughArguments() {
        String expected = "Command requires username and password";
        String message = "login Anna";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandLoginInvalidUsernamePassword() {
        String expected = "Invalid username/password combination";
        String message = "login Anna 1234";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandLogoutSuccessfully() {
        String expected = "Successfully logged out";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_LOGGED, LOGOUT));
    }

    @Test
    public void testExecuteCommandLogoutWhenNotLoggedIn() {
        assertEquals(NOT_LOGGED_USER, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, LOGOUT));
    }

    @Test
    public void testExecuteCommandDisconnect() {
        String expected = "Disconnected from server";
        String message = "disconnect";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandUnknownCommand() {
        String expected = "Unknown command";
        String message = "ala bala";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandGeneratePasswordNotEnoughArguments() {
        String message = "generate_password facebook.com";
        assertEquals(MISSING_WEBSITE_AND_USERNAME, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandGeneratePasswordNotLoggedUser() {
        String message = "generate_password facebook.com pesho@gmail.com";
        assertEquals(NOT_LOGGED_USER, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandRetrieveCredentialsNotEnoughArguments() {
        String message = "retrieve_credentials facebook.com";
        assertEquals(MISSING_WEBSITE_AND_USERNAME, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandRetrieveCredentialsNotLoggedUser() {
        assertEquals(NOT_LOGGED_USER, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_RETRIEVE_CREDENTIALS_COMMAND));
    }

    @Test
    public void testExecuteCommandRetrieveCredentialsInvalidWebsiteUsername() {
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND);
        executor.executeCommand(TEST_USERNAME_LOGGED, "add_password facebook.com peshooo@gmail.com 123fgh$%&");
        assertEquals(INVALID_WEBSITE_USERNAME, executor.executeCommand(TEST_USERNAME_LOGGED, TEST_RETRIEVE_CREDENTIALS_COMMAND));
    }

    @Test
    public void testExecuteCommandRemovePasswordNotEnoughArguments() {
        String message = "remove_password facebook.com";
        assertEquals(MISSING_WEBSITE_AND_USERNAME, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandRemovePasswordNotLoggedUser() {
        assertEquals(NOT_LOGGED_USER, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REMOVE_PASSWORD_COMMAND));
    }

    @Test
    public void testExecuteCommandRemovePasswordUserHasNoAddedWebsites() {
        String registerCommand = "register Ivan 5555 5555";
        String username = "Ivan";
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, registerCommand);
        assertEquals(NO_WEBSITES, executor.executeCommand(username, TEST_REMOVE_PASSWORD_COMMAND));
    }

    @Test
    public void testExecuteCommandRemovePasswordInvalidWebsiteUsername() {
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND);
        executor.executeCommand(TEST_USERNAME_LOGGED, TEST_ADD_PASSWORD_COMMAND);
        assertEquals(INVALID_WEBSITE_USERNAME, executor.executeCommand(TEST_USERNAME_LOGGED, TEST_REMOVE_PASSWORD_COMMAND));
    }

    @Test
    public void testExecuteCommandRemovePasswordSuccessfully() {
        executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_REGISTER_COMMAND);
        executor.executeCommand(TEST_USERNAME_LOGGED, "add_password facebook.com pesho@gmail.com 123fgh$%&");
        String expected = "Successfully removed";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_LOGGED, TEST_REMOVE_PASSWORD_COMMAND));
    }

    @Test
    public void testExecuteCommandAddPasswordNotEnoughArguments() {
        String expected = "Command requires website, username and password";
        String message = "add_password facebook.com";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, message));
    }

    @Test
    public void testExecuteCommandAddPasswordNotLoggedUser() {
        assertEquals(NOT_LOGGED_USER, executor.executeCommand(TEST_USERNAME_NOT_LOGGED, TEST_ADD_PASSWORD_COMMAND));
    }

    @Test
    public void testExecuteCommandAddPasswordCompromisedPassword() {
        String expected = "Password is compromised";
        String message = "add_password facebook.com pesho@gmail.com 1234";
        assertEquals(expected, executor.executeCommand(TEST_USERNAME_LOGGED, message));
    }

}
