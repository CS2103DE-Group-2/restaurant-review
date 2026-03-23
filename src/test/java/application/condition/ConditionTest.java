package application.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.Rating;
import application.review.Review;
import application.review.Tag;

public class ConditionTest {
    private Review review;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        // Overall: 3.0, Food: 4.0, Clean: 3.0, Service: 2.0, Tags: 1
        review = new Review("Review", new Rating(4.0, 3.0, 2.0), Tag.toTags("Tag1"));
    }

    @Test
    public void equalsToCondition_test() {
        Condition cond = new EqualsToCondition(Criterion.FOOD_SCORE, 4.0);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new EqualsToCondition(Criterion.FOOD_SCORE, 3.0);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("food scores == 3.0", cond.toString());
    }

    @Test
    public void greaterThanCondition_test() {
        Condition cond = new GreaterThanCondition(Criterion.OVERALL_SCORE, 2.5);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new GreaterThanCondition(Criterion.OVERALL_SCORE, 3.5);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("overall scores > 3.5", cond.toString());
    }

    @Test
    public void greaterThanOrEqualsToCondition_test() {
        Condition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.0);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.1);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("service scores >= 2.1", cond.toString());
    }

    @Test
    public void lessThanCondition_test() {
        Condition cond = new LessThanCondition(Criterion.CLEANLINESS_SCORE, 4.0);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new LessThanCondition(Criterion.CLEANLINESS_SCORE, 3.0);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("clean scores < 3.0", cond.toString());
    }

    @Test
    public void lessThanOrEqualsToCondition_test() {
        Condition cond = new LessThanOrEqualsToCondition(Criterion.TAG_COUNT, 1.0);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new LessThanOrEqualsToCondition(Criterion.TAG_COUNT, 0.0);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("tag count <= 0.0", cond.toString());
    }

    @Test
    public void notEqualsToCondition_test() {
        Condition cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 3.0);
        assertTrue(cond.isSatisfiedBy(review));
        cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 4.0);
        assertFalse(cond.isSatisfiedBy(review));
        assertEquals("food scores != 4.0", cond.toString());
    }

    @Test
    public void unknownCondition_test() {
        Condition cond = new UnknownCondition();
        assertFalse(cond.isSatisfiedBy(review));
        assertFalse(cond.shouldDisplay());
        assertEquals("Unknown Condition", cond.toString());
    }

    @Test
    public void conditionType_getConditionType() {
        assertEquals(ConditionType.EQUALS, ConditionType.getConditionType("=="));
        assertEquals("==", ConditionType.EQUALS.toString());
        assertEquals(ConditionType.GREATER_THAN, ConditionType.getConditionType(">"));
        assertEquals(">", ConditionType.GREATER_THAN.toString());
        assertEquals(ConditionType.GREATER_THAN_OR_EQUALS, ConditionType.getConditionType(">="));
        assertEquals(">=", ConditionType.GREATER_THAN_OR_EQUALS.toString());
        assertEquals(ConditionType.LESS_THAN, ConditionType.getConditionType("<"));
        assertEquals("<", ConditionType.LESS_THAN.toString());
        assertEquals(ConditionType.LESS_THAN_OR_EQUALS, ConditionType.getConditionType("<="));
        assertEquals("<=", ConditionType.LESS_THAN_OR_EQUALS.toString());
        assertEquals(ConditionType.NOT_EQUALS, ConditionType.getConditionType("!="));
        assertEquals("!=", ConditionType.NOT_EQUALS.toString());
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType("??"));
        assertEquals("", ConditionType.UNKNOWN.toString());
        assertEquals(ConditionType.UNKNOWN, ConditionType.getConditionType(null));
    }

    @Test
    public void isSatisfiedBy_nullReview_throwsException() {
        Condition cond = new EqualsToCondition(Criterion.FOOD_SCORE, 4.0);
        assertThrows(NullPointerException.class, () -> cond.isSatisfiedBy(null));
    }
}
