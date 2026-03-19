package application.review;

import application.condition.Condition;
import application.exception.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewListTest {
    private ReviewList reviewList;
    private Review review1;
    private Review review2;

    @BeforeEach
    public void setUp() throws InvalidArgumentException {
        reviewList = new ReviewList();
        review1 = new Review("Review 1", new Rating(5.0, 5.0, 5.0), Tag.toTags("Tag1"));
        review2 = new Review("Review 2", new Rating(1.0, 1.0, 1.0), Tag.toTags("Tag2"));
        reviewList.addReview(review1);
        reviewList.addReview(review2);
    }

    @Test
    public void addAndDelete_success() throws InvalidArgumentException {
        Review review3 = new Review("Review 3", new Rating(3.0, 3.0, 3.0));
        reviewList.addReview(review3);
        assertEquals(3, reviewList.size());
        
        Review deleted = reviewList.deleteReview(3);
        assertEquals(review3, deleted);
        assertEquals(2, reviewList.size());
    }

    @Test
    public void getReview_validAndInvalidIndex() throws InvalidArgumentException {
        assertEquals(review1, reviewList.getReview(1));
        assertThrows(InvalidArgumentException.class, () -> reviewList.getReview(3));
        assertThrows(InvalidArgumentException.class, () -> reviewList.getReview(0));
    }

    @Test
    public void markStatus_success() throws InvalidArgumentException {
        reviewList.markResolved(1);
        assertTrue(reviewList.getReview(1).isResolved());
        reviewList.markOutstanding(1);
        assertFalse(reviewList.getReview(1).isResolved());
    }

    @Test
    public void markStatus_invalidIndex_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> reviewList.markResolved(0));
        assertThrows(InvalidArgumentException.class, () -> reviewList.markResolved(3));
        assertThrows(InvalidArgumentException.class, () -> reviewList.markOutstanding(0));
        assertThrows(InvalidArgumentException.class, () -> reviewList.markOutstanding(3));
    }

    @Test
    public void filter_byTags_success() throws InvalidArgumentException {
        ReviewList filtered = reviewList.filter(Tag.toTags("Tag1"), new HashSet<>(), new HashSet<>(), null);
        assertEquals(1, filtered.size());
        assertEquals(review1, filtered.getReview(1));

        filtered = reviewList.filter(new HashSet<>(), Tag.toTags("Tag1"), new HashSet<>(), null);
        assertEquals(1, filtered.size());
        assertEquals(review2, filtered.getReview(1));
    }

    @Test
    public void sort_ascendingAndDescending_success() throws InvalidArgumentException {
        ReviewList sorted = reviewList.sort(Criterion.FOOD_SCORE, SortOrder.ASCENDING, reviewList);
        assertEquals(review2, sorted.getReview(1));
        assertEquals(review1, sorted.getReview(2));

        sorted = reviewList.sort(Criterion.FOOD_SCORE, SortOrder.DESCENDING, reviewList);
        assertEquals(review1, sorted.getReview(1));
        assertEquals(review2, sorted.getReview(2));
    }

    @Test
    public void sort_invalidOrder_throwsException() {
        assertThrows(InvalidArgumentException.class, () -> reviewList.sort(Criterion.FOOD_SCORE, SortOrder.UNKNOWN, reviewList));
    }

    @Test
    public void criterion_toString() {
        assertEquals("overall scores", Criterion.OVERALL_SCORE.toString());
        assertEquals("food scores", Criterion.FOOD_SCORE.toString());
        assertEquals("clean scores", Criterion.CLEANLINESS_SCORE.toString());
        assertEquals("service scores", Criterion.SERVICE_SCORE.toString());
        assertEquals("tag count", Criterion.TAG_COUNT.toString());
        assertEquals("unknown", Criterion.UNKNOWN.toString());
    }

    @Test
    public void sortOrder_toString() {
        assertEquals("ascending", SortOrder.ASCENDING.toString());
        assertEquals("descending", SortOrder.DESCENDING.toString());
        assertEquals("unknown", SortOrder.UNKNOWN.toString());
    }

    @Test
    public void toString_returnsFormattedList() {
        String result = reviewList.toString();
        assertTrue(result.contains("Review 1"));
        assertTrue(result.contains("Review 2"));
    }
}
