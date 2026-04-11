package application.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.Rating;
import application.review.Review;
import application.review.Tag;

public class GreaterThanOrEqualsToConditionTest {
    private Review review;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        // Overall: 3.0, Food: 4.0, Clean: 3.0, Service: 2.0, Tags: 1
        review = new Review("Review", new Rating(4.0, 3.0, 2.0), Tag.toTags("Tag1"));
    }

    @Test
    public void constructor_validArguments_success() {
        // Partition: Valid Criterion and double value
        GreaterThanOrEqualsToCondition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.0);
        assertNotNull(cond);
    }

    @Test
    public void isSatisfiedBy_greaterValue_returnsTrue() {
        // Partition: Review criterion > condition value
        GreaterThanOrEqualsToCondition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 1.9);
        assertTrue(cond.isSatisfiedBy(review));
    }

    @Test
    public void isSatisfiedBy_equalValue_returnsTrue() {
        // Partition: Review criterion == condition value
        GreaterThanOrEqualsToCondition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.0);
        assertTrue(cond.isSatisfiedBy(review));
    }

    @Test
    public void isSatisfiedBy_lesserValue_returnsFalse() {
        // Partition: Review criterion < condition value
        GreaterThanOrEqualsToCondition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.1);
        assertFalse(cond.isSatisfiedBy(review));
    }

    @Test
    public void toString_returnsFormattedString() {
        // Partition: Check string representation
        GreaterThanOrEqualsToCondition cond = new GreaterThanOrEqualsToCondition(Criterion.SERVICE_SCORE, 2.1);
        assertEquals("service >= 2.1", cond.toString());
    }
}
