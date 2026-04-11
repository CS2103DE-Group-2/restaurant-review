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

public class NotEqualsToConditionTest {
    private Review review;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        // Overall: 3.0, Food: 4.0, Clean: 3.0, Service: 2.0, Tags: 1
        review = new Review("Review", new Rating(4.0, 3.0, 2.0), Tag.toTags("Tag1"));
    }

    @Test
    public void constructor_validArguments_success() {
        // Partition: Valid Criterion and double value
        NotEqualsToCondition cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 3.0);
        assertNotNull(cond);
    }

    @Test
    public void isSatisfiedBy_nonMatchingValue_returnsTrue() {
        // Partition: Review criterion != condition value
        NotEqualsToCondition cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 3.0);
        assertTrue(cond.isSatisfiedBy(review));
    }

    @Test
    public void isSatisfiedBy_matchingValue_returnsFalse() {
        // Partition: Review criterion == condition value
        NotEqualsToCondition cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 4.0);
        assertFalse(cond.isSatisfiedBy(review));
    }

    @Test
    public void toString_returnsFormattedString() {
        // Partition: Check string representation
        NotEqualsToCondition cond = new NotEqualsToCondition(Criterion.FOOD_SCORE, 4.0);
        assertEquals("food != 4.0", cond.toString());
    }
}
