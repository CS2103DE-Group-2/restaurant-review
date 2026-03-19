package application.parser;

import application.condition.Condition;
import application.condition.EqualsToCondition;
import application.condition.GreaterThanCondition;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.Criterion;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class ConditionParserTest {

    @Test
    public void getConditions_validInput_success() throws InvalidArgumentException, MissingArgumentException {
        String input = "food == 4.0, service > 2";
        Set<Condition> conditions = ConditionParser.getConditions(input);
        assertEquals(2, conditions.size());
        
        boolean foundEquals = false;
        boolean foundGreater = false;
        for (Condition c : conditions) {
            if (c instanceof EqualsToCondition) foundEquals = true;
            if (c instanceof GreaterThanCondition) foundGreater = true;
        }
        assertTrue(foundEquals);
        assertTrue(foundGreater);
    }

    @Test
    public void getConditions_emptyInput_returnsEmptySet() throws InvalidArgumentException, MissingArgumentException {
        assertTrue(ConditionParser.getConditions(null).isEmpty());
        assertTrue(ConditionParser.getConditions("").isEmpty());
    }

    @Test
    public void getConditions_invalidFormat_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("food == 4.0 5.0"));
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("unknown == 4.0"));
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("food ?? 4.0"));
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("food >"));
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("> 3"));
        assertThrows(InvalidArgumentException.class, () -> ConditionParser.getConditions("food > 3 > 2"));
    }
}
