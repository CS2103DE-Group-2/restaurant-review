package application.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

public class CommandTest {
    private ReviewList reviewList;
    private Storage storage;
    private Path tempDirectory;

    @BeforeEach
    public void setUp() {
        reviewList = new ReviewList();
        storage = new Storage();
        tempDirectory = null;
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (tempDirectory == null || !Files.exists(tempDirectory)) {
            return;
        }

        Files.walk(tempDirectory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        throw new RuntimeException("Failed to delete temporary test file: " + path, ignored);
                    }
                });
    }

    @Test
    public void addReviewCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "Good");
        args.put("/food", "5");
        args.put("/clean", "4");
        args.put("/service", "5");
        args.put("/tag", "Tag1");

        AddReviewCommand cmd = new AddReviewCommand(args);
        String output = cmd.execute(reviewList, storage);

        assertEquals(1, reviewList.size());
        assertTrue(output.contains("Added review"));
    }

    @Test
    public void deleteReviewCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        // Add a review first
        addReviewCommand_execute_success();

        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        DeleteReviewCommand cmd = new DeleteReviewCommand(args);
        String output = cmd.execute(reviewList, storage);

        assertEquals(0, reviewList.size());
        assertTrue(output.contains("deleted"));
    }

    @Test
    public void addTagsCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success();

        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        args.put("/tag", "NewTag");
        AddTagsCommand cmd = new AddTagsCommand(args);
        cmd.execute(reviewList, storage);

        Review review = reviewList.getReview(1);
        assertTrue(review.getTags().contains(new Tag("NewTag")));
    }

    @Test
    public void listReviewsCommand_execute_success() {
        ListReviewsCommand cmd = new ListReviewsCommand();
        String output = cmd.execute(reviewList, storage);
        assertTrue(output.contains("Review list is empty."));
    }

    @Test
    public void exitCommand_isTerminating() {
        ExitCommand cmd = new ExitCommand();
        assertTrue(cmd.isTerminatingCommand());
    }

    @Test
    public void filterReviewsCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success();

        Map<String, String> args = new HashMap<>();
        args.put("/hastag", "Tag1");
        FilterReviewsCommand cmd = new FilterReviewsCommand(args);
        String output = cmd.execute(reviewList, storage);
        assertTrue(output.contains("Filtered reviews:"));
        assertTrue(output.contains("Tag1"));
    }

    @Test
    public void sortReviewsCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success();

        Map<String, String> args = new HashMap<>();
        args.put("/default", "asc");
        args.put("/by", "food");
        SortReviewsCommand cmd = new SortReviewsCommand(args);
        String output = cmd.execute(reviewList, storage);
        assertTrue(output.contains("Sorted by food scores in ascending order"));
    }

    @Test
    public void addReviewCommand_missingArgument_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "Good");
        // Missing /food, /clean, /service

        assertThrows(MissingArgumentException.class, () -> new AddReviewCommand(args));
    }

    @Test
    public void addReviewCommand_invalidNumericArgument_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "Good");
        args.put("/food", "not_a_number");
        args.put("/clean", "4");
        args.put("/service", "5");

        assertThrows(InvalidArgumentException.class, () -> new AddReviewCommand(args));
    }

    @Test
    public void deleteReviewCommand_invalidIndex_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "not_a_number");

        assertThrows(InvalidArgumentException.class, () -> new DeleteReviewCommand(args));
    }

    @Test
    public void deleteReviewCommand_missingIndex_throwsException() {
        Map<String, String> args = new HashMap<>();

        assertThrows(MissingArgumentException.class, () -> new DeleteReviewCommand(args));
    }

    @Test
    public void deleteReviewCommand_outOfBoundsIndex_throwsException()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds 1 review, index 1

        Map<String, String> args = new HashMap<>();
        args.put("/default", "2"); // out of bounds
        DeleteReviewCommand cmd = new DeleteReviewCommand(args);

        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage));
    }

    @Test
    public void addTagsCommand_missingTags_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        // Missing /tag

        assertThrows(InvalidArgumentException.class, () -> new AddTagsCommand(args));
    }

    @Test
    public void sortReviewsCommand_invalidOrder_throwsException()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success();

        Map<String, String> args = new HashMap<>();
        args.put("/default", "invalid_order");
        args.put("/by", "food");
        SortReviewsCommand cmd = new SortReviewsCommand(args);

        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage));
    }

    @Test
    public void deleteTagsCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds Tag1

        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        args.put("/tag", "Tag1");
        DeleteTagsCommand cmd = new DeleteTagsCommand(args);
        cmd.execute(reviewList, storage);

        Review review = reviewList.getReview(1);
        assertFalse(review.getTags().contains(new Tag("Tag1")));
    }

    @Test
    public void deleteTagsCommand_missingTags_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        // Missing /tag

        assertThrows(InvalidArgumentException.class, () -> new DeleteTagsCommand(args));
    }

    @Test
    public void deleteTagsCommand_outOfBoundsIndex_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        args.put("/tag", "Tag1");

        DeleteTagsCommand cmd = assertDoesNotThrow(() -> new DeleteTagsCommand(args));
        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage));
    }

    @Test
    public void unknownCommand_execute_success() throws InvalidArgumentException, IOException {
        Command cmd = new UnknownCommand();
        assertEquals("I'm sorry, I don't understand that command.", cmd.execute(reviewList, storage));
    }

    @Test
    public void exitCommand_execute_success() throws InvalidArgumentException, IOException {
        Command cmd = new ExitCommand();
        assertEquals("Goodbye!", cmd.execute(reviewList, storage));
    }

    @Test
    public void resolveReviewCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds 1 review, index 1

        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        ResolveReviewCommand cmd = new ResolveReviewCommand(args);
        String output = cmd.execute(reviewList, storage);

        assertTrue(output.contains("marked as resolved!"));
        assertTrue(reviewList.getReview(1).isResolved());
    }

    @Test
    public void resolveReviewCommand_invalidIndex_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "not_a_number");

        assertThrows(InvalidArgumentException.class, () -> new ResolveReviewCommand(args));
    }

    @Test
    public void resolveReviewCommand_missingIndex_throwsException() {
        Map<String, String> args = new HashMap<>();

        assertThrows(MissingArgumentException.class, () -> new ResolveReviewCommand(args));
    }

    @Test
    public void resolveReviewCommand_outOfBoundsIndex_throwsException()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds 1 review, index 1

        Map<String, String> args = new HashMap<>();
        args.put("/default", "2"); // out of bounds
        ResolveReviewCommand cmd = new ResolveReviewCommand(args);

        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage));
    }

    @Test
    public void unresolveReviewCommand_execute_success()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds 1 review, index 1
        reviewList.markResolved(1);
        assertTrue(reviewList.getReview(1).isResolved());

        Map<String, String> args = new HashMap<>();
        args.put("/default", "1");
        UnresolveReviewCommand cmd = new UnresolveReviewCommand(args);
        String output = cmd.execute(reviewList, storage);

        assertTrue(output.contains("marked as outstanding!"));
        assertFalse(reviewList.getReview(1).isResolved());
    }

    @Test
    public void unresolveReviewCommand_invalidIndex_throwsException() {
        Map<String, String> args = new HashMap<>();
        args.put("/default", "not_a_number");

        assertThrows(InvalidArgumentException.class, () -> new UnresolveReviewCommand(args));
    }

    @Test
    public void unresolveReviewCommand_missingIndex_throwsException() {
        Map<String, String> args = new HashMap<>();

        assertThrows(MissingArgumentException.class, () -> new UnresolveReviewCommand(args));
    }

    @Test
    public void unresolveReviewCommand_outOfBoundsIndex_throwsException()
            throws InvalidArgumentException, MissingArgumentException, IOException {
        addReviewCommand_execute_success(); // adds 1 review, index 1

        Map<String, String> args = new HashMap<>();
        args.put("/default", "2"); // out of bounds
        UnresolveReviewCommand cmd = new UnresolveReviewCommand(args);

        assertThrows(InvalidArgumentException.class, () -> cmd.execute(reviewList, storage));
    }

    @Test
    public void mutatingCommands_addAndDelete_persistToStorage()
            throws IOException, InvalidArgumentException, MissingArgumentException {
        Storage fileStorage = createFileBackedStorage();

        AddReviewCommand addCommand = new AddReviewCommand(createReviewArgs("first", "tagA"));
        addCommand.execute(reviewList, fileStorage);

        ReviewList loadedAfterAdd = fileStorage.loadReviews();
        assertEquals(1, loadedAfterAdd.size());
        assertEquals("first", loadedAfterAdd.getReview(1).getReviewBody());

        DeleteReviewCommand deleteCommand = new DeleteReviewCommand(Map.of("/default", "1"));
        deleteCommand.execute(reviewList, fileStorage);

        ReviewList loadedAfterDelete = fileStorage.loadReviews();
        assertEquals(0, loadedAfterDelete.size());
    }

    @Test
    public void mutatingCommands_addAndDeleteTags_persistToStorage()
            throws IOException, InvalidArgumentException, MissingArgumentException {
        Storage fileStorage = createFileBackedStorage();

        AddReviewCommand addReview = new AddReviewCommand(createReviewArgs("review", "oldTag"));
        addReview.execute(reviewList, fileStorage);

        AddTagsCommand addTags = new AddTagsCommand(Map.of("/default", "1", "/tag", "newTag"));
        addTags.execute(reviewList, fileStorage);

        ReviewList loadedAfterAddTag = fileStorage.loadReviews();
        assertTrue(loadedAfterAddTag.getReview(1).getTags().contains(new Tag("newTag")));

        DeleteTagsCommand deleteTags = new DeleteTagsCommand(Map.of("/default", "1", "/tag", "oldTag"));
        deleteTags.execute(reviewList, fileStorage);

        ReviewList loadedAfterDeleteTag = fileStorage.loadReviews();
        assertFalse(loadedAfterDeleteTag.getReview(1).getTags().contains(new Tag("oldTag")));
    }

    @Test
    public void mutatingCommands_resolveAndUnresolve_persistToStorage()
            throws IOException, InvalidArgumentException, MissingArgumentException {
        Storage fileStorage = createFileBackedStorage();

        AddReviewCommand addReview = new AddReviewCommand(createReviewArgs("review", "tag"));
        addReview.execute(reviewList, fileStorage);

        ResolveReviewCommand resolveCommand = new ResolveReviewCommand(Map.of("/default", "1"));
        resolveCommand.execute(reviewList, fileStorage);

        ReviewList loadedAfterResolve = fileStorage.loadReviews();
        assertTrue(loadedAfterResolve.getReview(1).isResolved());

        UnresolveReviewCommand unresolveCommand = new UnresolveReviewCommand(Map.of("/default", "1"));
        unresolveCommand.execute(reviewList, fileStorage);

        ReviewList loadedAfterUnresolve = fileStorage.loadReviews();
        assertFalse(loadedAfterUnresolve.getReview(1).isResolved());
    }

    private Storage createFileBackedStorage() throws IOException {
        tempDirectory = Files.createTempDirectory("command-storage-test-");
        Path storagePath = tempDirectory.resolve("data").resolve("reviews.txt");
        return new Storage(storagePath);
    }

    private Map<String, String> createReviewArgs(String body, String tags) {
        Map<String, String> args = new HashMap<>();
        args.put("/default", body);
        args.put("/food", "5");
        args.put("/clean", "4");
        args.put("/service", "3");
        args.put("/tag", tags);
        return args;
    }
}
