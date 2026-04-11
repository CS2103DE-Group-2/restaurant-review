package application.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

public class LoginCommandTest {
    private static final String CORRECT_PASSWORD = "secret";
    private ReviewList reviewList;
    private Storage storage;
    private AuthManager authManager;

    @BeforeEach
    public void setUp() {
        reviewList = new ReviewList();
        storage = new Storage();
        authManager = new AuthManager(CORRECT_PASSWORD);
    }

    @Test
    public void constructor_validPassword_success() {
        // Partition: Any string password
        LoginCommand cmd = new LoginCommand("anything");
        assertNotNull(cmd);
    }

    @Test
    public void execute_correctPassword_logsIn() {
        // Partition: Correct password, not already logged in
        LoginCommand cmd = new LoginCommand(CORRECT_PASSWORD);
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertTrue(authManager.isOwnerAuthenticated());
        assertTrue(result.output().contains("Successfully logged in"));
    }

    @Test
    public void execute_incorrectPassword_doesNotLogIn() {
        // Partition: Incorrect password
        LoginCommand cmd = new LoginCommand("wrong");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertFalse(authManager.isOwnerAuthenticated());
        assertTrue(result.output().contains("Incorrect password"));
    }

    @Test
    public void execute_alreadyLoggedIn_returnsMessage() {
        // Partition: Already logged in
        authManager.authenticateOwner(CORRECT_PASSWORD);
        LoginCommand cmd = new LoginCommand(CORRECT_PASSWORD);
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertTrue(result.output().contains("already logged in"));
    }

    @Test
    public void requiresOwnerAuthentication_returnsFalse() {
        // Partition: Default behavior
        LoginCommand cmd = new LoginCommand(CORRECT_PASSWORD);
        assertFalse(cmd.requiresOwnerAuthentication());
    }
}
