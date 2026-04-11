package application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

public class FilterReviewsCommandTest {
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

        // Review 1: tag1, food=5.0, resolved=false
        reviewList.addReview(
                new Review("R1",
                new Rating(5.0, 5.0, 5.0),
                Tag.toTags("tag1"))
        );
        // Review 2: tag2, food=3.0, resolved=true
        Review r2 = new Review(
                "R2",
                new Rating(3.0, 3.0, 3.0),
                Tag.toTags("tag2")
        );
        r2.markResolved();
        reviewList.addReview(r2);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void constructor_validArguments_success() {
        // Partition: Valid strings for all fields
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "tag1",
                "tag2",
                "true",
                "food > 4"
        );
        assertNotNull(cmd);
    }

    @Test
    public void execute_filterByTag_returnsMatchingReviews()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Filter by includeTags
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "tag1",
                "",
                "All",
                ""
        );
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(1, result.reviews().size());
        assertEquals("R1", result.reviews().getReview(1).getReviewBody());
    }

    @Test
    public void execute_filterByExcludeTag_returnsMatchingReviews()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Filter by excludeTags
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "",
                "tag1",
                "All",
                ""
        );
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(1, result.reviews().size());
        assertEquals("R2", result.reviews().getReview(1).getReviewBody());
    }

    @Test
    public void execute_filterByResolvedStatus_returnsMatchingReviews()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Filter by resolved status (true)
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "",
                "",
                "true",
                ""
        );
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(1, result.reviews().size());
        assertTrue(result.reviews().getReview(1).isResolved());
    }

    @Test
    public void execute_filterByCondition_returnsMatchingReviews()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: Filter by numeric condition (food > 4)
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "",
                "",
                "All",
                "food > 4"
        );
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(1, result.reviews().size());
        assertTrue(result.reviews().getReview(1).getRating().getFoodScore() > 4);
    }

    @Test
    public void execute_noCriteria_returnsAllReviews()
            throws InvalidArgumentException, MissingArgumentException {
        // Partition: All criteria empty/default
        FilterReviewsCommand cmd = new FilterReviewsCommand(
                "",
                "",
                "All",
                ""
        );
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(2, result.reviews().size());
    }
}
