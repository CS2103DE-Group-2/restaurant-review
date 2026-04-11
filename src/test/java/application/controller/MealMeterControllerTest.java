package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.auth.AuthManager;
import application.command.AddReviewCommand;
import application.command.Command;
import application.command.CommandResult;
import application.command.LoginCommand;
import application.command.SortReviewsCommand;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

/**
 * Tests for MealMeterController authentication and command gating behaviour.
 */
public class MealMeterControllerTest {
    private static final String OWNER_PASSWORD = "secret";

    private Path tempDirectory;
    private MealMeterController mealMeterController;

    @BeforeEach
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("mealmeter-auth-test-");
        Path storagePath = tempDirectory.resolve("data").resolve("reviews.txt");
        Storage storage = new Storage(storagePath);

        mealMeterController = new MealMeterController(storage, new AuthManager(OWNER_PASSWORD));
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
                        fail("Failed to delete temp path: " + path, ignored);
                    }
                });
    }

    @Test
    public void constructors_workCorrectly() {
        // Partition: Check various constructors (uses default or provided storage/auth)
        assertNotNull(new MealMeterController());
        assertNotNull(new MealMeterController("password"));
    }

    @Test
    public void handleInput_patronCommandWithoutLogin_allowed() {
        // Partition: Public command executed by non-owner
        CommandResult result = mealMeterController.handleInput(
                new AddReviewCommand(
                        "great",
                        4.0,
                        4.0,
                        4.0,
                        ""
                )
        );

        assertTrue(result.output().contains("Added review to list:"));
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void handleInput_ownerCommandWithoutLogin_denied() {
        // Partition: Owner-only command executed by non-owner
        CommandResult result = mealMeterController.handleInput(new SortReviewsCommand("asc", "food"));

        assertEquals("Access denied. Please log in as the owner to use this command.", result.output());
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void handleInput_unknownCommandWithoutLogin_unknownHandledNormally() {
        // Partition: Unknown command (passing an anonymous command that requires owner auth)
        Command unknown = new Command() {
            @Override
            public CommandResult execute(ReviewList reviews, Storage storage, AuthManager manager) {
                return null;
            }
        };

        CommandResult result = mealMeterController.handleInput(unknown);

        assertEquals("Access denied. Please log in as the owner to use this command.", result.output());
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void handleInput_loginSuccessThenAlreadyLoggedIn() {
        // Partition: Login successful, then already logged in
        CommandResult firstResult = mealMeterController.handleInput(new LoginCommand("secret"));
        CommandResult secondResult = mealMeterController.handleInput(new LoginCommand("secret"));

        assertEquals("Successfully logged in!", firstResult.output());
        assertFalse(firstResult.shouldTerminate());

        assertEquals("You are already logged in!", secondResult.output());
        assertFalse(secondResult.shouldTerminate());
    }

    @Test
    public void handleInput_ownerCommandAfterLogin_allowed() {
        // Partition: Owner-only command after login
        mealMeterController.handleInput(new LoginCommand("secret"));
        CommandResult result = mealMeterController.handleInput(new SortReviewsCommand("asc", "food"));

        assertTrue(result.output().contains("Sorted reviews"));
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void helperMethods_returnCorrectValues() {
        // Partition: Test remaining getter methods
        assertFalse(mealMeterController.hasStorageLoadFailure());
        assertTrue(mealMeterController.getStartupStorageWarnings().isEmpty());
        assertNotNull(mealMeterController.getReviewList());
        assertFalse(mealMeterController.isOwnerAuthenticated());

        mealMeterController.login("secret");
        assertTrue(mealMeterController.isOwnerAuthenticated());

        mealMeterController.logout();
        assertFalse(mealMeterController.isOwnerAuthenticated());
    }

    @Test
    public void getMasterIndex_mapsCorrectIndices() {
        // Partition: Correct mapping between row index and master index
        mealMeterController.submitReview(
                "R1",
                5.0,
                5.0,
                5.0,
                "tag1"
        );
        mealMeterController.submitReview(
                "R2",
                3.0,
                3.0,
                3.0,
                "tag2"
        );
        ReviewList master = mealMeterController.getReviewList();

        // Filter to only show R2 (tag2)
        ReviewList filtered = master.filter(
                Tag.toTags("tag2"),
                new java.util.HashSet<>(),
                new java.util.HashSet<>(),
                null
        );

        // rowIndex 1 in filtered is masterIndex 2
        assertEquals(2, mealMeterController.getMasterIndex(filtered, 1));
    }

    @Test
    public void controllerActions_delegateToCommands() {
        // Partition: All direct controller methods (submitReview, sort, filter, resolve, etc.)
        assertNotNull(mealMeterController.submitReview(
                "body",
                5.0,
                5.0,
                5.0,
                ""
                )
        );
        assertNotNull(mealMeterController.sortReviews(
                "asc",
                "food"
                )
        );
        assertNotNull(mealMeterController.filterReviews(
                "",
                "",
                "All",
                ""
                )
        );

        mealMeterController.login("secret");
        ReviewList current = mealMeterController.getReviewList();
        assertNotNull(mealMeterController.resolveReview(current, 1));
        assertNotNull(mealMeterController.unresolveReview(current, 1));
        assertNotNull(mealMeterController.addTags(current, 1, "tag1"));
        assertNotNull(mealMeterController.deleteTags(current, 1, "tag1"));
        assertNotNull(mealMeterController.deleteReview(current, 1));
        assertNotNull(mealMeterController.login("secret"));
        assertNotNull(mealMeterController.logout());
    }
}
