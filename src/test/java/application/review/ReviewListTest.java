package application.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;

/**
 * Tests for ReviewList class.
 */
public class ReviewListTest {
    private ReviewList reviewList;
    private Review review1;
    private Review review2;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        reviewList = new ReviewList();
        review1 = new Review(
                "Review 1",
                new Rating(5.0, 5.0, 5.0),
                Tag.toTags("Tag1")
        );
        review2 = new Review(
                "Review 2",
                new Rating(1.0, 1.0, 1.0),
                Tag.toTags("Tag2")
        );
        reviewList.addReview(review1);
        reviewList.addReview(review2);
    }

    @Test
    public void constructor_success() {
        // Partition: Default constructor
        ReviewList list = new ReviewList();
        assertEquals(0, list.size());
    }

    @Test
    public void constructor_withList_success() {
        // Partition: Constructor with existing list
        ReviewList list = new ReviewList(List.of(review1, review2));
        assertEquals(2, list.size());
    }

    @Test
    public void addAndDelete_success() throws InvalidArgumentException {
        // Partition: Add and delete reviews
        Review review3 = new Review(
                "Review 3",
                new Rating(3.0, 3.0, 3.0),
                new HashSet<>()
        );
        reviewList.addReview(review3);
        assertEquals(3, reviewList.size());

        Review deleted = reviewList.deleteReview(3);
        assertEquals(review3, deleted);
        assertEquals(2, reviewList.size());
    }

    @Test
    public void getReview_validAndInvalidIndex() throws InvalidArgumentException {
        // Partition: Valid and invalid indices for getReview
        assertEquals(review1, reviewList.getReview(1));
        assertThrows(InvalidArgumentException.class, () -> reviewList.getReview(3));
        assertThrows(InvalidArgumentException.class, () -> reviewList.getReview(0));
    }

    @Test
    public void markStatus_success() throws InvalidArgumentException {
        // Partition: Mark resolved and mark outstanding via ReviewList
        reviewList.markResolved(1);
        assertTrue(reviewList.getReview(1).isResolved());
        reviewList.markOutstanding(1);
        assertFalse(reviewList.getReview(1).isResolved());
    }

    @Test
    public void filter_variousScenarios() throws InvalidArgumentException {
        // Partition: Filter by include tag
        ReviewList filtered = reviewList.filter(
                Tag.toTags("Tag1"),
                new HashSet<>(),
                new HashSet<>(),
                null
        );
        assertEquals(1, filtered.size());
        assertEquals(review1, filtered.getReview(1));

        // Partition: Filter by exclude tag
        filtered = reviewList.filter(
                new HashSet<>(),
                Tag.toTags("Tag1"),
                new HashSet<>(),
                null
        );
        assertEquals(1, filtered.size());
        assertEquals(review2, filtered.getReview(1));

        // Partition: Filter by resolved status
        reviewList.markResolved(1);
        filtered = reviewList.filter(
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                true
        );
        assertEquals(1, filtered.size());
        assertEquals(review1, filtered.getReview(1));
    }

    @Test
    public void sort_ascendingAndDescending_success() throws InvalidArgumentException {
        // Partition: Sort by food score ascending and descending
        ReviewList sorted = reviewList.sort(Criterion.FOOD_SCORE, SortOrder.ASCENDING, reviewList);
        assertEquals(review2, sorted.getReview(1));
        assertEquals(review1, sorted.getReview(2));

        sorted = reviewList.sort(Criterion.FOOD_SCORE, SortOrder.DESCENDING, reviewList);
        assertEquals(review1, sorted.getReview(1));
        assertEquals(review2, sorted.getReview(2));
    }

    @Test
    public void sort_invalidOrder_throwsException() {
        // Partition: Invalid sort order
        assertThrows(InvalidArgumentException.class, () ->
                reviewList.sort(Criterion.FOOD_SCORE, SortOrder.UNKNOWN, reviewList));
    }

    @Test
    public void isEmpty_andSize_workCorrectly() {
        // Partition: Check isEmpty and size
        assertFalse(reviewList.isEmpty());
        assertEquals(2, reviewList.size());
        assertTrue(new ReviewList().isEmpty());
    }

    @Test
    public void toString_returnsFormattedList() {
        // Partition: Check string representation of the list
        String result = reviewList.toString();
        assertTrue(result.contains("Review 1"));
        assertTrue(result.contains("Review 2"));
    }

    @Test
    public void clearReviewList_workCorrectly() {
        // Partition: Clear the list
        reviewList.clear();
        assertEquals(0, reviewList.size());
    }
}
