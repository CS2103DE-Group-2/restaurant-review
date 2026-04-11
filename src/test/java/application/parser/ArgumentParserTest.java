package application.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;

/**
 * Tests for ArgumentParser class.
 */
public class ArgumentParserTest {

    @Test
    public void isValidString_validString_returnsTrue() {
        // Partition: Valid string
        assertTrue(ArgumentParser.isValidString("valid"));
    }

    @Test
    public void isValidString_nullString_returnsFalse() {
        // Partition: Null string
        assertFalse(ArgumentParser.isValidString(null));
    }

    @Test
    public void isValidString_blankString_returnsFalse() {
        // Partition: Blank or empty string
        assertFalse(ArgumentParser.isValidString("   "));
        assertFalse(ArgumentParser.isValidString(""));
    }

    @Test
    public void toDouble_validNumber_returnsDouble() throws MissingArgumentException, InvalidArgumentException {
        // Partition: Valid numeric string
        assertEquals(12.34, ArgumentParser.toDouble("12.34"), 0.001);
        assertEquals(0.0, ArgumentParser.toDouble("0"));
        assertEquals(-1.5, ArgumentParser.toDouble("-1.5"));
    }

    @Test
    public void toDouble_nullOrBlank_throwsMissingArgumentException() {
        // Partition: Null or blank input
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble(null));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble(""));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble("   "));
    }

    @Test
    public void toDouble_invalidNumber_throwsInvalidArgumentException() {
        // Partition: Non-numeric string
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toDouble("abc"));
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toDouble("12.34.56"));
    }

    @Test
    public void toResolvedStatus_resolved_returnsTrue() {
        // Partition: "Resolved" input
        assertTrue(ArgumentParser.toResolvedStatus("Resolved"));
    }

    @Test
    public void toResolvedStatus_unresolved_returnsFalse() {
        // Partition: "Unresolved" input
        assertFalse(ArgumentParser.toResolvedStatus("Outstanding"));
    }

    @Test
    public void toResolvedStatus_all_returnsNull() {
        // Partition: "All" input
        assertNull(ArgumentParser.toResolvedStatus("All"));
    }

    @Test
    public void toResolvedStatus_invalidInput_returnsNull() {
        // Partition: Invalid or default input
        assertNull(ArgumentParser.toResolvedStatus("SomethingElse"));
        assertNull(ArgumentParser.toResolvedStatus(""));
    }
}
