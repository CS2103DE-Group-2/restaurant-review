package application.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.exception.InvalidArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;

/**
 * Tests for txt-file based Storage class.
 */
public class StorageTest {
    private Path tempDirectory;
    private Path storagePath;
    private Storage storage;

    @BeforeEach
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("storage-test-");
        storagePath = tempDirectory.resolve("data").resolve("reviews.txt");
        storage = new Storage(storagePath);
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
    public void constructor_default_success() {
        // Partition: Default constructor (uses default path)
        Storage defaultStorage = new Storage();
        assertNotNull(defaultStorage);
    }

    @Test
    public void loadReviews_missingFile_createsDirectoriesAndFile() throws IOException {
        // Partition: File does not exist initially
        assertFalse(Files.exists(storagePath));

        ReviewList loaded = storage.loadReviews();

        assertTrue(Files.exists(storagePath));
        assertTrue(Files.isRegularFile(storagePath));
        assertEquals(0, loaded.size());
    }

    @Test
    public void saveAndLoad_roundTrip_preservesAllFields() throws InvalidArgumentException, IOException {
        // Partition: Valid ReviewList with various fields
        ReviewList reviews = new ReviewList();
        Review review = new Review(
                "Food was good but waiting time was too long.",
                new Rating(4.5, 4.0, 3.5),
                Tag.toTags("service,slow delivery")
        );
        review.markResolved();
        reviews.addReview(review);

        storage.saveReviews(reviews);
        ReviewList loaded = storage.loadReviews();

        assertEquals(1, loaded.size());
        Review loadedReview = loaded.getReview(1);
        assertEquals(4.5, loadedReview.getFoodScore(), 0.0001);
        assertEquals(4.0, loadedReview.getCleanlinessScore(), 0.0001);
        assertEquals(3.5, loadedReview.getServiceScore(), 0.0001);
        assertTrue(loadedReview.isResolved());
        assertTrue(loadedReview.getTags().contains(new Tag("service")));
        assertTrue(loadedReview.getTags().contains(new Tag("slow delivery")));
        assertEquals("Food was good but waiting time was too long.", loadedReview.getReviewBody());
    }

    @Test
    public void saveAndLoad_bodyEscaping_preservesNewlinesAndBackslashes()
            throws InvalidArgumentException, IOException {
        // Partition: Review body with special characters needing escaping
        String body = "Line1\\folder\\file\nLine2\\next";
        ReviewList reviews = new ReviewList();
        reviews.addReview(new Review(body, new Rating(5.0, 4.0, 3.0), Tag.toTags("path")));

        storage.saveReviews(reviews);
        ReviewList loaded = storage.loadReviews();

        assertEquals(body, loaded.getReview(1).getReviewBody());
    }

    @Test
    public void loadReviews_malformedBlock_skipsOnlyMalformedBlock() throws IOException, InvalidArgumentException {
        // Partition: File contains some malformed blocks (skipping strategy)
        List<String> lines = List.of(
                "MEALMETER_V1",
                "food=4.0",
                "cleanliness=4.0",
                "service=4.0",
                "resolved=false",
                "tags=good",
                "body=valid entry",
                "---",
                "food=INVALID_NUMBER",
                "cleanliness=3.0",
                "service=2.0",
                "resolved=true",
                "tags=bad",
                "body=should be skipped",
                "---",
                "food=2.5",
                "cleanliness=2.0",
                "service=1.5",
                "resolved=true",
                "tags=dirty tables",
                "body=second valid entry",
                "---"
        );

        Files.createDirectories(storagePath.getParent());
        Files.write(storagePath, lines, StandardCharsets.UTF_8);

        ReviewList loaded = storage.loadReviews();

        assertEquals(2, loaded.size());
        assertEquals("valid entry", loaded.getReview(1).getReviewBody());
        assertEquals("second valid entry", loaded.getReview(2).getReviewBody());
        assertTrue(loaded.getReview(2).isResolved());
    }

    @Test
    public void loadReviewsWithWarnings_returnsWarnings() throws IOException {
        // Partition: Check warnings return on malformed data
        List<String> lines = List.of(
                "MEALMETER_V1",
                "food=INVALID",
                "body=test",
                "---"
        );
        Files.createDirectories(storagePath.getParent());
        Files.write(storagePath, lines, StandardCharsets.UTF_8);

        StorageLoadResult result = storage.loadReviewsWithWarnings();
        assertEquals(0, result.reviewList().size());
        assertFalse(result.warnings().isEmpty());
    }

    @Test
    public void saveReviews_multipleCalls_overwritesPreviousContent() throws InvalidArgumentException, IOException {
        // Partition: Overwriting existing file content
        ReviewList first = new ReviewList();
        first.addReview(new Review("first", new Rating(5.0, 5.0, 5.0), Tag.toTags("one")));
        storage.saveReviews(first);

        ReviewList second = new ReviewList();
        second.addReview(new Review("second", new Rating(1.0, 2.0, 3.0), Tag.toTags("two")));
        storage.saveReviews(second);

        ReviewList loaded = storage.loadReviews();

        assertEquals(1, loaded.size());
        assertEquals("second", loaded.getReview(1).getReviewBody());
        assertFalse(loaded.getReview(1).getTags().contains(new Tag("one")));
        assertTrue(loaded.getReview(1).getTags().contains(new Tag("two")));
    }
}
