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

public class DeleteReviewCommandTest {
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

        // Pre-populate with one review at index 1
        reviewList.addReview(new Review("Body", new Rating(5.0, 5.0, 5.0), Tag.toTags("tag1")));
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void constructor_validIndex_success() {
        // Partition: Valid index
        DeleteReviewCommand cmd = new DeleteReviewCommand(1);
        assertNotNull(cmd);
    }

    @Test
    public void execute_validIndex_deletesReviewAndSaves() throws InvalidArgumentException, IOException {
        // Partition: Valid index (exists in list)
        DeleteReviewCommand cmd = new DeleteReviewCommand(1);
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(0, reviewList.size());
        assertTrue(result.output().contains("Review 1 deleted"));
        assertEquals(0, storage.loadReviews().size());
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        // Partition: Invalid input (index out of bounds)
        DeleteReviewCommand cmd = new DeleteReviewCommand(2);
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }
}
