package application.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConditionTypeTest {

    @Test
    public void getConditionType_allKnownTypes_returnsCorrectType() {
        // Partition: Each known condition string
        assertEquals(ConditionType.EQUALS, ConditionType.getConditionType("=="));
        assertEquals(ConditionType.GREATER_THAN, ConditionType.getConditionType(">"));
        assertEquals(ConditionType.GREATER_THAN_OR_EQUALS, ConditionType.getConditionType(">="));
        assertEquals(ConditionType.LESS_THAN, ConditionType.getConditionType("<"));
        assertEquals(ConditionType.LESS_THAN_OR_EQUALS, ConditionType.getConditionType("<="));
        assertEquals(ConditionType.NOT_EQUALS, ConditionType.getConditionType("!="));
    }

    @Test
    public void getConditionType_unknownString_returnsUnknown() {
        // Partition: Unknown condition string
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType("??"));
    }

    @Test
    public void getConditionType_nullOrEmptyString_returnsUnknown() {
        // Partition: Null or empty input
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType(null));
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType(""));
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType("  "));
    }

    @Test
    public void toString_returnsCorrectString() {
        // Partition: All enum constants
        assertEquals("==", ConditionType.EQUALS.toString());
        assertEquals(">", ConditionType.GREATER_THAN.toString());
        assertEquals(">=", ConditionType.GREATER_THAN_OR_EQUALS.toString());
        assertEquals("<", ConditionType.LESS_THAN.toString());
        assertEquals("<=", ConditionType.LESS_THAN_OR_EQUALS.toString());
        assertEquals("!=", ConditionType.NOT_EQUALS.toString());
        assertEquals("", ConditionType.UNKNOWN.toString());
    }
}
