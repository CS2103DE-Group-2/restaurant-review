package application.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;

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
}
