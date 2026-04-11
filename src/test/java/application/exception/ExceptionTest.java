package application.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for custom exception classes.
 */
public class ExceptionTest {
    @Test
    public void invalidArgumentException_message_success() {
        // Partition: Constructor with message
        String message = "Invalid argument!";
        InvalidArgumentException exception = new InvalidArgumentException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void missingArgumentException_message_success() {
        // Partition: Constructor with message
        String message = "Missing argument!";
        MissingArgumentException exception = new MissingArgumentException(message);
        assertEquals(message, exception.getMessage());
    }
}
