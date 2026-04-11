package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;

public class ReviewTest {
    private Rating rating;
    private Set<Tag> tags;

    @BeforeEach
    public void setUp() {
        rating = new Rating(4.0, 3.0, 5.0);
        tags = Tag.toTags("Good Service");
    }

    @Test
    public void constructor_validArguments_success() throws InvalidArgumentException {
        // Partition: Valid review body, rating, and tags
        Review review = new Review("Great place!", rating, tags);
        assertEquals("Great place!", review.getReviewBody());
        assertEquals(rating, review.getRating());
        assertEquals(tags, review.getTags());
        assertFalse(review.isResolved());
    }

    @Test
    public void constructor_withoutTags_success() throws InvalidArgumentException {
        // Partition: Valid review body, rating, empty tags
        Review review = new Review("Nice.", rating, new java.util.HashSet<>());
        assertEquals("Nice.", review.getReviewBody());
        assertEquals(rating, review.getRating());
        assertTrue(review.getTags().isEmpty());
    }

    @Test
    public void constructor_blankReviewBody_success() throws InvalidArgumentException {
        // Partition: Blank review body (handled by setting to empty)
        Review review = new Review("   ", rating, tags);
        assertEquals("", review.getReviewBody());
    }

    @Test
    public void addTag_newTag_success() throws InvalidArgumentException {
        // Partition: Add a new tag to a review
        Review review = new Review("Review", rating, new java.util.HashSet<>());
        Tag newTag = new Tag("New");
        review.addTag(newTag);
        assertTrue(review.getTags().contains(newTag));
    }

    @Test
    public void removeTag_existingTag_success() throws InvalidArgumentException {
        // Partition: Remove an existing tag from a review
        Review review = new Review("Review", rating, Tag.toTags("Good"));
        Tag tagToRemove = new Tag("Good");
        review.removeTag(tagToRemove);
        assertFalse(review.getTags().contains(tagToRemove));
    }

    @Test
    public void resolveStatus_toggle_success() throws InvalidArgumentException {
        // Partition: Mark resolved and mark outstanding
        Review review = new Review("Review", rating, new java.util.HashSet<>());
        assertFalse(review.isResolved());
        review.markResolved();
        assertTrue(review.isResolved());
        review.markOutstanding();
        assertFalse(review.isResolved());
    }

    @Test
    public void tagMatching_variousScenarios() throws InvalidArgumentException {
        // Partition: Check matching and non-matching tags logic
        Review review = new Review("Review", rating, Tag.toTags("A, B, C"));
        Set<Tag> toMatch = Tag.toTags("A, B");
        assertTrue(review.containsAllMatchingTags(toMatch));
        assertFalse(review.containsNoMatchingTags(toMatch));

        Set<Tag> matching = review.getMatchingTags(Tag.toTags("A, B, D"));
        assertEquals(2, matching.size());
        assertTrue(matching.contains(new Tag("A")));
        assertTrue(matching.contains(new Tag("B")));

        Set<Tag> nonMatching = review.getNonMatchingTags(Tag.toTags("A, B, D"));
        assertEquals(1, nonMatching.size());
        assertTrue(nonMatching.contains(new Tag("D")));
    }

    @Test
    public void toRow_validIndex_returnsFormattedRow() throws InvalidArgumentException {
        // Partition: Check table row formatting
        // Rating: food=4.0, cleanliness=3.0, service=5.0 -> overall=4.0
        // toRow returns: [rowIndex, overall, food, clean, service, status, tags, body]
        Review review = new Review("ReviewBody", rating, Tag.toTags("Tag1"));
        Object[] row = review.toRow(1);
        assertEquals(1, row[0]);
        assertEquals("4.0", row[1]); // Overall
        assertEquals("4.0", row[2]); // Food
        assertEquals("3.0", row[3]); // Clean
        assertEquals("5.0", row[4]); // Service
        assertEquals("Outstanding", row[5]);
        assertEquals("Tag1", row[6]);
        assertEquals("ReviewBody", row[7]);
    }

    @Test
    public void getScoreMethods_returnCorrectValues() throws InvalidArgumentException {
        // Partition: Verify all score getter methods (including string versions)
        Review review = new Review("Review", rating, tags);
        assertEquals(4.0, review.getFoodScore());
        assertEquals("4.0", review.getFoodScoreString());
        assertEquals(3.0, review.getCleanlinessScore());
        assertEquals("3.0", review.getCleanlinessScoreString());
        assertEquals(5.0, review.getServiceScore());
        assertEquals("5.0", review.getServiceScoreString());
        assertEquals(4.0, review.getOverallScore());
        assertEquals("4.0", review.getOverallScoreString());
        assertTrue(review.getRatingString().contains("Overall: 4.0"));
    }

    @Test
    public void toString_formattedCorrectly() throws InvalidArgumentException {
        // Partition: Check full string representation
        Review review = new Review("Good!", rating, Tag.toTags("Tag1"));
        String result = review.toString();
        assertTrue(result.contains("Good!"));
        assertTrue(result.contains("Tag1"));
        assertTrue(result.contains("Overall: 4.0"));
    }

    @Test
    public void constructor_nullRating_throwsException() {
        // Partition: Invalid input (null rating)
        assertThrows(InvalidArgumentException.class, () -> new Review("Review", null, tags));
    }

    @Test
    public void addTag_nullTag_throwsException() throws InvalidArgumentException {
        // Partition: Invalid input (null tag)
        Review review = new Review("Review", rating, new java.util.HashSet<>());
        assertThrows(IllegalArgumentException.class, () -> review.addTag(null));
    }

    @Test
    public void removeTag_nullTag_throwsException() throws InvalidArgumentException {
        // Partition: Invalid input (null tag)
        Review review = new Review("Review", rating, new java.util.HashSet<>());
        assertThrows(IllegalArgumentException.class, () -> review.removeTag(null));
    }
}
