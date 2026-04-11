package application.condition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.Tag;

public class UnknownConditionTest {
    private Review review;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        review = new Review("Review", new Rating(4.0, 3.0, 2.0), Tag.toTags("Tag1"));
    }

    @Test
    public void constructor_success() {
        // Partition: Default constructor
        UnknownCondition cond = new UnknownCondition();
        assertNotNull(cond);
    }

    @Test
    public void isSatisfiedBy_alwaysReturnsFalse() {
        // Partition: Always false
        UnknownCondition cond = new UnknownCondition();
        assertFalse(cond.isSatisfiedBy(review));
    }

    @Test
    public void shouldDisplay_alwaysReturnsFalse() {
        // Partition: Always false
        UnknownCondition cond = new UnknownCondition();
        assertFalse(cond.shouldDisplay());
    }

    @Test
    public void toString_returnsFormattedString() {
        // Partition: Check string representation
        UnknownCondition cond = new UnknownCondition();
        assertEquals("Unknown Condition", cond.toString());
    }
}
