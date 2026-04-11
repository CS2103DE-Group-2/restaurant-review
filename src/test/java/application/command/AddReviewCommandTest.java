package application.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import application.review.ReviewList;
import application.storage.Storage;

public class AddReviewCommandTest {
    private ReviewList reviewList;
    private Storage storage;
    private AuthManager authManager;
    private Path tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        reviewList = new ReviewList();
        tempFile = Files.createTempFile("test-reviews", ".txt");
        storage = new Storage(tempFile);
        authManager = new AuthManager("password");
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void constructor_allArgumentsValid_success() {
        // Partition: Valid review body, valid scores (min, max, mid), valid tags
        AddReviewCommand cmd = new AddReviewCommand("Good", 5.0, 4.0, 3.0, "tag1, tag2");
        assertNotNull(cmd);
    }

    @Test
    public void execute_validInputs_addsReviewAndSaves() throws InvalidArgumentException, IOException {
        // Partition: Valid inputs, ensures review is added and storage is updated
        AddReviewCommand cmd = new AddReviewCommand("Delicious", 5.0, 5.0, 5.0, "yummy");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        assertEquals(1, reviewList.size());
        assertTrue(result.output().contains("Added review"));
        assertEquals(1, storage.loadReviews().size());
    }

    @Test
    public void execute_invalidFoodScoreLow_throwsException() {
        // Partition: Invalid input (foodScore < RATING_MIN)
        AddReviewCommand cmd = new AddReviewCommand("Body", Rating.RATING_MIN - 0.1, 4.0, 4.0, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void execute_invalidFoodScoreHigh_throwsException() {
        // Partition: Invalid input (foodScore > RATING_MAX)
        AddReviewCommand cmd = new AddReviewCommand("Body", Rating.RATING_MAX + 0.1, 4.0, 4.0, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void execute_invalidCleanlinessScoreLow_throwsException() {
        // Partition: Invalid input (cleanlinessScore < RATING_MIN)
        AddReviewCommand cmd = new AddReviewCommand("Body", 4.0, Rating.RATING_MIN - 0.1, 4.0, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void execute_invalidCleanlinessScoreHigh_throwsException() {
        // Partition: Invalid input (cleanlinessScore > RATING_MAX)
        AddReviewCommand cmd = new AddReviewCommand("Body", 4.0, Rating.RATING_MAX + 0.1, 4.0, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void execute_invalidServiceScoreLow_throwsException() {
        // Partition: Invalid input (serviceScore < RATING_MIN)
        AddReviewCommand cmd = new AddReviewCommand("Body", 4.0, 4.0, Rating.RATING_MIN - 0.1, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void execute_invalidServiceScoreHigh_throwsException() {
        // Partition: Invalid input (serviceScore > RATING_MAX)
        AddReviewCommand cmd = new AddReviewCommand("Body", 4.0, 4.0, Rating.RATING_MAX + 0.1, "tag");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }

    @Test
    public void requiresOwnerAuthentication_returnsFalse() {
        // Partition: Default behavior
        AddReviewCommand cmd = new AddReviewCommand("Body", 5.0, 5.0, 5.0, "tag");
        assertFalse(cmd.requiresOwnerAuthentication());
    }
}
