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
import application.command.CommandResult;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Tests for MealMeterController wrapper methods and auth gating.
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
    public void submitReview_patronCommandWithoutLogin_allowed() {
        // Partition: Public command executed by non-owner via wrapper method
        CommandResult result = mealMeterController.submitReview(
                "great",
                4.0,
                4.0,
                4.0,
                ""
        );

        assertTrue(result.output().contains("Added review to list:"));
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void sortReviews_ownerCommandWithoutLogin_denied() {
        // Partition: Owner-only command executed by non-owner via wrapper method
        CommandResult result = mealMeterController.sortReviews("food", "ascending");

        assertEquals("Access denied. Please log in as the owner to use this command.", result.output());
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void login_successThenAlreadyLoggedIn() {
        // Partition: Login successful, then already logged in via wrapper method
        CommandResult firstResult = mealMeterController.login("secret");
        CommandResult secondResult = mealMeterController.login("secret");

        assertEquals("Successfully logged in!", firstResult.output());
        assertFalse(firstResult.shouldTerminate());

        assertEquals("You are already logged in!", secondResult.output());
        assertFalse(secondResult.shouldTerminate());
    }

    @Test
    public void sortReviews_ownerCommandAfterLogin_allowed() {
        // Partition: Owner-only command after login via wrapper method
        mealMeterController.login("secret");
        CommandResult result = mealMeterController.sortReviews("food", "ascending");

        assertTrue(result.output().contains("Sorted reviews"));
        assertFalse(result.shouldTerminate());
    }

    @Test
    public void logout_success() {
        // Partition: Logout via wrapper method
        mealMeterController.login("secret");
        assertTrue(mealMeterController.isOwnerAuthenticated());

        CommandResult result = mealMeterController.logout();
        assertEquals("Successfully logged out!", result.output());
        assertFalse(mealMeterController.isOwnerAuthenticated());
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

        // Add a review first so there is something to act on at index 1
        mealMeterController.submitReview(
                "test",
                5.0,
                5.0,
                5.0,
                ""
        );

        assertNotNull(mealMeterController.resolveReview(current, 1));
        assertNotNull(mealMeterController.unresolveReview(current, 1));
        assertNotNull(mealMeterController.addTags(current, 1, "tag1"));
        assertNotNull(mealMeterController.deleteTags(current, 1, "tag1"));
        assertNotNull(mealMeterController.deleteReview(current, 1));
        assertNotNull(mealMeterController.login("secret"));
        assertNotNull(mealMeterController.logout());
    }
}
