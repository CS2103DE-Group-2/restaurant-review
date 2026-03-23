package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;

public class RatingTest {

    @Test
    public void constructor_validScores_success() throws InvalidArgumentException {
        Rating rating = new Rating(4.0, 3.5, 5.0);
        assertEquals(4.0, rating.getFoodScore());
        assertEquals(3.5, rating.getCleanlinessScore());
        assertEquals(5.0, rating.getServiceScore());
        assertEquals((4.0 + 3.5 + 5.0) / 3.0, rating.getOverallScore(), 0.001);
    }

    @Test
    public void constructor_invalidFoodScore_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> new Rating(0.9, 3.5, 5.0));
        assertThrows(InvalidArgumentException.class, () -> new Rating(5.1, 3.5, 5.0));
    }

    @Test
    public void constructor_invalidCleanlinessScore_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> new Rating(4.0, 0.9, 5.0));
        assertThrows(InvalidArgumentException.class, () -> new Rating(4.0, 5.1, 5.0));
    }

    @Test
    public void constructor_invalidServiceScore_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> new Rating(4.0, 3.5, 0.9));
        assertThrows(InvalidArgumentException.class, () -> new Rating(4.0, 3.5, 5.1));
    }

    @Test
    public void isValidScore_validAndInvalidScores() {
        assertTrue(Rating.isValidScore(1.0));
        assertTrue(Rating.isValidScore(5.0));
        assertTrue(Rating.isValidScore(3.0));
        assertFalse(Rating.isValidScore(0.9));
        assertFalse(Rating.isValidScore(5.1));
    }

    @Test
    public void toString_formattedCorrectly() throws InvalidArgumentException {
        Rating rating = new Rating(4.0, 3.0, 5.0);
        String expected = "Food: 4.0 | Cleanliness: 3.0 | Service: 5.0 | Overall: 4.0";
        assertEquals(expected, rating.toString());
    }
}
