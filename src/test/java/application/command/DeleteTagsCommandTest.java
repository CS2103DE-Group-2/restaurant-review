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
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

public class DeleteTagsCommandTest {
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

        // Pre-populate with one review at index 1 with tags
        reviewList.addReview(new Review("Body", new Rating(5.0, 5.0, 5.0), Tag.toTags("tag1, tag2")));
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void constructor_validArguments_success() {
        // Partition: Valid index, valid tags string
        DeleteTagsCommand cmd = new DeleteTagsCommand(1, "tag1");
        assertNotNull(cmd);
    }

    @Test
    public void execute_existingTags_removesTagsAndSaves() throws InvalidArgumentException, IOException {
        // Partition: Valid index, tags that exist in the review
        DeleteTagsCommand cmd = new DeleteTagsCommand(1, "tag1");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        Review review = reviewList.getReview(1);
        assertFalse(review.getTags().contains(new Tag("tag1")));
        assertTrue(review.getTags().contains(new Tag("tag2")));
        assertTrue(result.output().contains("Tags deleted: [tag1]"));
    }

    @Test
    public void execute_nonExistentTags_doesNotRemoveAnything() throws InvalidArgumentException, IOException {
        // Partition: Valid index, tags that do not exist in the review
        DeleteTagsCommand cmd = new DeleteTagsCommand(1, "tag3");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        Review review = reviewList.getReview(1);
        assertEquals(2, review.getTags().size());
        assertTrue(result.output().contains("Tags that do not exist in review: [tag3]"));
        assertTrue(result.output().contains("Tags deleted: []"));
    }

    @Test
    public void execute_mixedTags_removesOnlyExistingTags() throws InvalidArgumentException, IOException {
        // Partition: Valid index, mix of existing and non-existing tags
        DeleteTagsCommand cmd = new DeleteTagsCommand(1, "tag1, tag3");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        Review review = reviewList.getReview(1);
        assertFalse(review.getTags().contains(new Tag("tag1")));
        assertTrue(review.getTags().contains(new Tag("tag2")));
        assertEquals(1, review.getTags().size());
        assertTrue(result.output().contains("Tags deleted: [tag1]"));
        assertTrue(result.output().contains("Tags that do not exist in review: [tag3]"));
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        // Partition: Invalid input (index out of bounds)
        DeleteTagsCommand cmd = new DeleteTagsCommand(2, "tag1");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }
}
