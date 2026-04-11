package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RatingTest {

    @Test
    public void constructor_validScores_success() {
        // Partition: Valid scores (min, max, mid)
        Rating rating = new Rating(4.0, 3.5, 5.0);
        assertEquals(4.0, rating.getFoodScore());
        assertEquals(3.5, rating.getCleanlinessScore());
        assertEquals(5.0, rating.getServiceScore());
        assertEquals((4.0 + 3.5 + 5.0) / 3.0, rating.getOverallScore(), 0.001);
    }

    @Test
    public void isValidScore_variousScores() {
        // Partition: Valid (min, max, mid) and Invalid (below min, above max)
        assertTrue(Rating.isValidScore(1.0)); // min
        assertTrue(Rating.isValidScore(5.0)); // max
        assertTrue(Rating.isValidScore(3.0)); // mid
        assertFalse(Rating.isValidScore(0.9)); // below min
        assertFalse(Rating.isValidScore(5.1)); // above max
    }

    @Test
    public void getScoreStrings_returnsFormattedStrings() {
        // Partition: Check score to string formatting
        Rating rating = new Rating(4.12, 3.0, 5.0);
        assertEquals("4.1", rating.getFoodScoreString());
        assertEquals("3.0", rating.getCleanlinessScoreString());
        assertEquals("5.0", rating.getServiceScoreString());
        assertEquals("4.0", rating.getOverallScoreString());
    }

    @Test
    public void toString_formattedCorrectly() {
        // Partition: Check full string representation
        Rating rating = new Rating(4.0, 3.0, 5.0);
        String expected = "Food: 4.0 | Cleanliness: 3.0 | Service: 5.0 | Overall: 4.0";
        assertEquals(expected, rating.toString());
    }
}
