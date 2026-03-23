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
    public void splitIntoPair_validInput_success() {
        String[] result = ArgumentParser.splitIntoPair("command args", " ");
        assertEquals("command", result[0]);
        assertEquals("args", result[1]);

        result = ArgumentParser.splitIntoPair("  command   args  ", " ");
        assertEquals("command", result[0]);
        assertEquals("args", result[1]);
    }

    @Test
    public void splitIntoPair_nullInput_returnsEmptyArray() {
        String[] result = ArgumentParser.splitIntoPair(null, " ");
        assertEquals("", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    public void splitIntoPair_noDelimiter_returnsInputAndEmpty() {
        String[] result = ArgumentParser.splitIntoPair("command", " ");
        assertEquals("command", result[0]);
        assertEquals("", result[1]);
    }

    @Test
    public void isValidString_variousInputs() {
        assertTrue(ArgumentParser.isValidString("test"));
        assertFalse(ArgumentParser.isValidString(null));
        assertFalse(ArgumentParser.isValidString(""));
        assertFalse(ArgumentParser.isValidString("   "));
    }

    @Test
    public void toInt_validInput_success() throws InvalidArgumentException, MissingArgumentException {
        assertEquals(123, ArgumentParser.toInt("123"));
    }

    @Test
    public void toInt_invalidInput_throwsException() {
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toInt(null));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toInt(""));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toInt("   "));
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toInt("abc"));
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toInt("12.34"));
    }

    @Test
    public void toDouble_validInput_success() throws InvalidArgumentException, MissingArgumentException {
        assertEquals(12.34, ArgumentParser.toDouble("12.34"), 0.001);
    }

    @Test
    public void toDouble_invalidInput_throwsException() {
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble(null));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble(""));
        assertThrows(MissingArgumentException.class, () -> ArgumentParser.toDouble("   "));
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toDouble("abc"));
        assertThrows(InvalidArgumentException.class, () -> ArgumentParser.toDouble("12.34.56"));
    }
}
