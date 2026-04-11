package application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

public class SortReviewsCommandTest {
    private ReviewList reviewList;
    private Storage storage;
    private AuthManager authManager;
    private Path tempFile;

    @BeforeEach
    public void setUp() throws IOException, InvalidArgumentException {
        reviewList = new ReviewList();
        tempFile = Files.createTempFile("test-reviews", ".txt");
        storage = new Storage(tempFile);
        authManager = new AuthManager("password");

        // R1: food 5.0
        reviewList.addReview(new Review("R1", new Rating(5.0, 5.0, 5.0), Tag.toTags("tag")));
        // R2: food 3.0
        reviewList.addReview(new Review("R2", new Rating(3.0, 3.0, 3.0), Tag.toTags("tag")));
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void constructor_validArguments_success() {
        // Partition: Valid strings for sort order and criterion
        SortReviewsCommand cmd = new SortReviewsCommand("ascending", "food");
        assertNotNull(cmd);
    }

    @Test
    public void execute_ascendingFood_sortsCorrectly() throws InvalidArgumentException {
        // Partition: Sort by food score, ascending
        SortReviewsCommand cmd = new SortReviewsCommand("asc", "food");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals("R2", result.reviews().getReview(1).getReviewBody());
        assertEquals("R1", result.reviews().getReview(2).getReviewBody());
        assertTrue(result.output().contains("Sorted reviews by food in ascending order"));
    }

    @Test
    public void execute_descendingFood_sortsCorrectly() throws InvalidArgumentException {
        // Partition: Sort by food score, descending
        SortReviewsCommand cmd = new SortReviewsCommand("desc", "food");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals("R1", result.reviews().getReview(1).getReviewBody());
        assertEquals("R2", result.reviews().getReview(2).getReviewBody());
        assertTrue(result.output().contains("Sorted reviews by food in descending order"));
    }

    @Test
    public void execute_unknownCriterion_returnsUnsortedOrSortedByUnknown() throws InvalidArgumentException {
        // Partition: Unknown criterion
        SortReviewsCommand cmd = new SortReviewsCommand("asc", "unknown_crit");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertNotNull(result.reviews());
        assertTrue(result.output().contains("Sorted reviews by unknown"));
    }

    @Test
    public void execute_unknownOrder_throwsException() {
        // Partition: Unknown sort order
        SortReviewsCommand cmd = new SortReviewsCommand("unknown_order", "food");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }
}
