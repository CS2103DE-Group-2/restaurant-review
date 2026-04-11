package application.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

public class LogoutCommandTest {
    private static final String PASSWORD = "secret";
    private ReviewList reviewList;
    private Storage storage;
    private AuthManager authManager;

    @BeforeEach
    public void setUp() {
        reviewList = new ReviewList();
        storage = new Storage();
        authManager = new AuthManager(PASSWORD);
    }

    @Test
    public void constructor_success() {
        // Partition: Default constructor
        LogoutCommand cmd = new LogoutCommand();
        assertNotNull(cmd);
    }

    @Test
    public void execute_loggedIn_logsOut() {
        // Partition: Logged in
        authManager.authenticateOwner(PASSWORD);
        LogoutCommand cmd = new LogoutCommand();
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertFalse(authManager.isOwnerAuthenticated());
        assertTrue(result.output().contains("Successfully logged out"));
    }

    @Test
    public void execute_notLoggedIn_returnsMessage() {
        // Partition: Not logged in
        LogoutCommand cmd = new LogoutCommand();
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertFalse(authManager.isOwnerAuthenticated());
        assertTrue(result.output().contains("You are not logged in"));
    }

    @Test
    public void requiresOwnerAuthentication_returnsFalse() {
        // Partition: Default behavior
        LogoutCommand cmd = new LogoutCommand();
        assertFalse(cmd.requiresOwnerAuthentication());
    }
}
