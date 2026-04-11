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

public class AddTagsCommandTest {
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
    public void constructor_allArgumentsValid_success() {
        // Partition: Valid index, valid tags string
        AddTagsCommand cmd = new AddTagsCommand(1, "tag2, tag3");
        assertNotNull(cmd);
    }

    @Test
    public void execute_validNewTags_addsTagsAndSaves() throws InvalidArgumentException, IOException {
        // Partition: Valid index, entirely new tags
        AddTagsCommand cmd = new AddTagsCommand(1, "tag2, tag3");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        Review review = reviewList.getReview(1);
        assertTrue(review.getTags().contains(new Tag("tag2")));
        assertTrue(review.getTags().contains(new Tag("tag3")));
        assertTrue(result.output().contains("New tags added"));
    }

    @Test
    public void execute_existingTags_doesNotDuplicateTags() throws InvalidArgumentException, IOException {
        // Partition: Valid index, some tags already exist
        AddTagsCommand cmd = new AddTagsCommand(1, "tag1, tag2");
        CommandResult result = cmd.execute(reviewList, storage, authManager);

        Review review = reviewList.getReview(1);
        assertEquals(2, review.getTags().size()); // tag1 (existing) and tag2 (new)
        assertTrue(result.output().contains("Existing tags not added: [tag1]"));
        assertTrue(result.output().contains("New tags added: [tag2]"));
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        // Partition: Invalid input (index out of bounds)
        AddTagsCommand cmd = new AddTagsCommand(2, "tag2");
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage, authManager));
    }
}
