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
    public void setUp() throws InvalidArgumentException {
        rating = new Rating(4.0, 3.0, 5.0);
        tags = Tag.toTags("Good Service");
    }

    @Test
    public void constructor_withTags_success() throws InvalidArgumentException {
        Review review = new Review("Great place!", rating, tags);
        assertEquals("Great place!", review.getReviewBody());
        assertEquals(rating, review.getRating());
        assertEquals(tags, review.getTags());
        assertFalse(review.isResolved());
    }

    @Test
    public void constructor_withoutTags_success() throws InvalidArgumentException {
        Review review = new Review("Nice.", rating);
        assertEquals("Nice.", review.getReviewBody());
        assertEquals(rating, review.getRating());
        assertTrue(review.getTags().isEmpty());
    }

    @Test
    public void addTag_newTag_success() throws InvalidArgumentException {
        Review review = new Review("Review", rating);
        Tag newTag = new Tag("New");
        review.addTag(newTag);
        assertTrue(review.getTags().contains(newTag));
    }

    @Test
    public void removeTag_existingTag_success() throws InvalidArgumentException {
        Review review = new Review("Review", rating, tags);
        Tag tagToRemove = new Tag("Good");
        review.removeTag(tagToRemove);
        assertFalse(review.getTags().contains(tagToRemove));
    }

    @Test
    public void resolveStatus_toggle_success() throws InvalidArgumentException {
        Review review = new Review("Review", rating);
        assertFalse(review.isResolved());
        review.markResolved();
        assertTrue(review.isResolved());
        review.markOutstanding();
        assertFalse(review.isResolved());
    }

    @Test
    public void tagMatching_variousScenarios() throws InvalidArgumentException {
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
    public void toString_formattedCorrectly() throws InvalidArgumentException {
        Review review = new Review("Good!", rating, Tag.toTags("Tag1"));
        String result = review.toString();
        assertTrue(result.contains("Good!"));
        assertTrue(result.contains("Tag1"));
        assertTrue(result.contains("Overall: 4.0"));
    }

    @Test
    public void constructor_nullRating_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> new Review("Review", null, tags));
        assertThrows(IllegalArgumentException.class, () -> new Review("Review", null));
    }

    @Test
    public void addTag_nullTag_throwsException() throws InvalidArgumentException {
        Review review = new Review("Review", rating);
        assertThrows(IllegalArgumentException.class, () -> review.addTag(null));
    }

    @Test
    public void removeTag_nullTag_throwsException() throws InvalidArgumentException {
        Review review = new Review("Review", rating);
        assertThrows(IllegalArgumentException.class, () -> review.removeTag(null));
    }
}
