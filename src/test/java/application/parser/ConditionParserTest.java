package application.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import application.condition.Condition;
import application.condition.EqualsToCondition;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;

public class ConditionParserTest {

    @Test
    public void getConditions_validSingleCondition_success()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Valid single condition
        String input = "food == 4.0";
        Set<Condition> conditions = ConditionParser.getConditions(input);
        assertEquals(1, conditions.size());
        assertTrue(conditions.iterator().next() instanceof EqualsToCondition);
    }

    @Test
    public void getConditions_multipleValidConditions_success()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Multiple valid conditions separated by comma
        String input = "food == 4.0, service > 2";
        Set<Condition> conditions = ConditionParser.getConditions(input);
        assertEquals(2, conditions.size());
    }

    @Test
    public void getConditions_emptyOrNullInput_returnsEmptySet()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Null or empty input
        assertTrue(ConditionParser.getConditions(null).isEmpty());
        assertTrue(ConditionParser.getConditions("").isEmpty());
        assertTrue(ConditionParser.getConditions("   ").isEmpty());
    }

    @Test
    public void getConditions_invalidFormat_throwsException() {
        // Partition: Invalid format (not 3 parts)
        assertThrows(
                InvalidArgumentException.class, () ->
                        ConditionParser.getConditions("food == 4.0 5.0")
        );
        assertThrows(
                InvalidArgumentException.class, () ->
                        ConditionParser.getConditions("food >")
        );
    }

    @Test
    public void getConditions_unknownCriterion_throwsException() {
        // Partition: Unknown criterion
        assertThrows(
                InvalidArgumentException.class, () ->
                        ConditionParser.getConditions("unknown == 4.0")
        );
    }

    @Test
    public void getConditions_unknownOperator_throwsException() {
        // Partition: Unknown operator
        assertThrows(
                InvalidArgumentException.class, () ->
                        ConditionParser.getConditions("food ?? 4.0")
        );
    }

    @Test
    public void getConditions_invalidNumber_throwsException() {
        // Partition: Invalid number value
        assertThrows(
                InvalidArgumentException.class, () ->
                        ConditionParser.getConditions("food == abc")
        );
    }
}
