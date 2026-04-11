package application.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AuthManagerTest {

    @Test
    public void constructor_validPassword_success() {
        // Partition: Valid password string
        AuthManager manager = new AuthManager("password");
        assertNotNull(manager);
        assertFalse(manager.isOwnerAuthenticated());
    }

    @Test
    public void constructor_nullPassword_throwsException() {
        // Partition: Invalid input (null password)
        assertThrows(IllegalArgumentException.class, () -> new AuthManager(null));
    }

    @Test
    public void constructor_blankPassword_throwsException() {
        // Partition: Invalid input (blank password)
        assertThrows(IllegalArgumentException.class, () -> new AuthManager("   "));
    }

    @Test
    public void authenticateOwner_correctPassword_returnsTrue() {
        // Partition: Correct password
        AuthManager manager = new AuthManager("secret");
        assertTrue(manager.authenticateOwner("secret"));
        assertTrue(manager.isOwnerAuthenticated());
    }

    @Test
    public void authenticateOwner_incorrectPassword_returnsFalse() {
        // Partition: Incorrect password
        AuthManager manager = new AuthManager("secret");
        assertFalse(manager.authenticateOwner("wrong"));
        assertFalse(manager.isOwnerAuthenticated());
    }

    @Test
    public void authenticateOwner_nullPassword_returnsFalse() {
        // Partition: Null password input
        AuthManager manager = new AuthManager("secret");
        assertFalse(manager.authenticateOwner(null));
        assertFalse(manager.isOwnerAuthenticated());
    }

    @Test
    public void logout_authenticatedSession_setsAuthenticatedToFalse() {
        // Partition: Logged in session
        AuthManager manager = new AuthManager("secret");
        manager.authenticateOwner("secret");
        assertTrue(manager.isOwnerAuthenticated());
        manager.logout();
        assertFalse(manager.isOwnerAuthenticated());
    }
}
