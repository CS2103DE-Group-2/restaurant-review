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
    public void isValidString_variousInputs() {
        assertTrue(ArgumentParser.isValidString("test"));
        assertFalse(ArgumentParser.isValidString(null));
        assertFalse(ArgumentParser.isValidString(""));
        assertFalse(ArgumentParser.isValidString("   "));
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
