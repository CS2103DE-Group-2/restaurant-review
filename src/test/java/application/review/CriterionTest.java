package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;

public class CriterionTest {

    @Test
    public void getCriterion_validStrings_returnsCorrectCriterion() {
        // Partition: Match by startsWith
        assertEquals(Criterion.OVERALL_SCORE, Criterion.getCriterion("overall"));
        assertEquals(Criterion.FOOD_SCORE, Criterion.getCriterion("food"));
        assertEquals(Criterion.CLEANLINESS_SCORE, Criterion.getCriterion("cleanliness"));
        assertEquals(Criterion.CLEANLINESS_SCORE, Criterion.getCriterion("clean")); // prefix
        assertEquals(Criterion.SERVICE_SCORE, Criterion.getCriterion("SERVICE")); // case insensitive
        assertEquals(Criterion.TAG_COUNT, Criterion.getCriterion("tag count"));
    }

    @Test
    public void getCriterion_invalidStrings_returnsUnknown() {
        // Partition: Unknown string
        assertEquals(Criterion.UNKNOWN, Criterion.getCriterion("random"));
    }

    @Test
    public void getCriterion_nullOrBlank_returnsUnknown() {
        // Partition: Null or blank input
        assertEquals(Criterion.UNKNOWN, Criterion.getCriterion(null));
        assertEquals(Criterion.UNKNOWN, Criterion.getCriterion("  "));
    }

    @Test
    public void getFunction_returnsValidFunction() throws InvalidArgumentException {
        // Partition: Check if criterion function is returnable and callable
        // Rating constructor: food, cleanliness, service
        Review review = new Review(
                "Body",
                new Rating(5.0, 4.0, 3.0),
                Tag.toTags("tag1")
        );

        assertEquals(5.0, Criterion.FOOD_SCORE.getFunction().apply(review));
        assertEquals(4.0, Criterion.OVERALL_SCORE.getFunction().apply(review));
        assertEquals(1.0, Criterion.TAG_COUNT.getFunction().apply(review));
        assertEquals(0.0, Criterion.UNKNOWN.getFunction().apply(review));
    }

    @Test
    public void toString_returnsCorrectString() {
        // Partition: Enum toString check
        assertEquals("overall", Criterion.OVERALL_SCORE.toString());
        assertEquals("food", Criterion.FOOD_SCORE.toString());
        assertEquals("unknown", Criterion.UNKNOWN.toString());
    }
}
