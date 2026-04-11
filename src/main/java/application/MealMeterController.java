package application;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import application.auth.AuthManager;
import application.command.*;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;
import application.storage.Storage;
import application.storage.StorageLoadResult;

/**
 * Represents the core MealMeterController application logic.
 *
 * <p>This class acts as the bridge between the user interface
 * and the command-processing logic.</p>
 */
public class MealMeterController {
    private static final String DEFAULT_OWNER_PASSWORD = "password";
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied. Please log in as the owner to use this command.";

    private final Storage storage;
    private final AuthManager authManager;
    private final ReviewList reviews;
    private final boolean hasStorageLoadFailure;
    private final List<String> startupStorageWarnings;

    /**
     * Constructs a MealMeterController application and loads stored reviews.
     */
    public MealMeterController() {
        this(DEFAULT_OWNER_PASSWORD);
    }

    /**
     * Constructs a MealMeterController application with a custom owner password
     * and loads stored reviews.
     *
     * @param ownerPassword the owner password for session login
     */
    public MealMeterController(String ownerPassword) {
        this(new Storage(), new AuthManager(ownerPassword));
    }

    MealMeterController(Storage storage, AuthManager authManager) {
        this.storage = storage;
        this.authManager = authManager;

        ReviewList loadedReviews;
        boolean loadFailure;
        List<String> storageWarnings;

        try {
            StorageLoadResult loadResult = storage.loadReviewsWithWarnings();
            loadedReviews = loadResult.reviewList();
            storageWarnings = loadResult.warnings();
            loadFailure = false;
        } catch (IOException e) {
            loadedReviews = new ReviewList();
            storageWarnings = Collections.emptyList();
            loadFailure = true;
        }

        this.reviews = loadedReviews;
        this.hasStorageLoadFailure = loadFailure;
        this.startupStorageWarnings = storageWarnings;
    }

    /**
     * Returns whether loading reviews from storage failed at startup.
     *
     * @return true if storage loading failed, false otherwise
     */
    public boolean hasStorageLoadFailure() {
        return hasStorageLoadFailure;
    }

    /**
     * Returns non-fatal storage warnings encountered during startup loading.
     *
     * @return immutable list of startup storage warnings
     */
    public List<String> getStartupStorageWarnings() {
        return startupStorageWarnings;
    }

    /**
     * Returns the current review list (read-only view for the UI layer).
     *
     * @return the review list
     */
    public ReviewList getReviewList() {
        return reviews;
    }

    /**
     * Returns whether the owner is currently authenticated.
     *
     * @return true if the owner is authenticated, false otherwise
     */
    public boolean isOwnerAuthenticated() {
        return authManager.isOwnerAuthenticated();
    }

    /**
     * Handles one user input command and returns the command result.
     *
     * @param command the command object to execute
     * @return the result containing output and termination status and the reviews after execution
     */
    public CommandResult handleInput(Command command) {
        try {
            if (command.requiresOwnerAuthentication() && !authManager.isOwnerAuthenticated()) {
                return new CommandResult(ACCESS_DENIED_MESSAGE,
                        false,
                        reviews);
            }

            return command.execute(reviews, storage, authManager);
        } catch (InvalidArgumentException | IOException e) {
            return new CommandResult(e.getMessage(),
                    false,
                    reviews);
        } catch (Exception e) {
            return new CommandResult("An unexpected error occurred: " + e.getMessage(),
                    false,
                    reviews);
        }
    }

    /**
     * Returns the 1-based master-list index of the review at rowIndex in the display list.
     * Uses reference equality since filter() returns the same Review objects.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @return the 1-based index in the master list, or -1 if not found
     */
    public int getMasterIndex(ReviewList displayedReviews, int rowIndex) {
        try {
            Review displayedReview = displayedReviews.getReview(rowIndex);
            for (int i = 1; i <= reviews.size(); i++) {
                if (reviews.getReview(i).equals(displayedReview)) {
                    return i;
                }
            }
        } catch (InvalidArgumentException e) {
            return -1;
        }
        return -1;
    }

    /**
     * Submits a new review.
     *
     * @param reviewBody review body
     * @param foodScore the food score
     * @param cleanlinessScore the cleanliness score
     * @param serviceScore the service score
     * @param tagsAsString the tags to add to the review, as a string
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult submitReview(
            String reviewBody,
            Double foodScore,
            Double cleanlinessScore,
            Double serviceScore,
            String tagsAsString
    ) {
        Command command = new AddReviewCommand(reviewBody, foodScore, cleanlinessScore, serviceScore, tagsAsString);
        return handleInput(command);
    }

    /**
     * Filters the review list based on the given criteria.
     *
     * @param includeTags 1 or more tags to include in the filter, separated by commas
     * @param excludeTags 1 or more tags to exclude from the filter, separated by commas
     * @param status whether to filter by resolved status (true/false)
     * @param conditions 1 or more conditions to filter by, separated by commas
     * @return a {@code CommandResult} object containing the output message and the filtered review list
     */
    public CommandResult filterReviews(String includeTags,
                                       String excludeTags,
                                       String status,
                                       String conditions
    ) {
        Command command = new FilterReviewsCommand(
                includeTags,
                excludeTags,
                status,
                conditions
        );
        return handleInput(command);
    }

    /**
     * Sorts the review list based on the given criterion and sort order.
     *
     * @param sortBy 1 or more criteria to sort by, separated by commas
     * @param sortOrder "Ascending" or "Descending"
     * @return a {@code CommandResult} object containing the output message and the sorted review list
     */
    public CommandResult sortReviews(String sortBy, String sortOrder) {
        Command command = new SortReviewsCommand(sortOrder, sortBy);
        return handleInput(command);
    }

    /**
     * Resolves a review in the displayed review list at the given row index.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult resolveReview(ReviewList displayedReviews, int rowIndex) {
        int index = getMasterIndex(displayedReviews, rowIndex);
        Command command = new ResolveReviewCommand(index);
        return handleInput(command);
    }

    /**
     * Unresolves a review in the displayed review list at the given row index.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult unresolveReview(ReviewList displayedReviews, int rowIndex) {
        int index = getMasterIndex(displayedReviews, rowIndex);
        Command command = new UnresolveReviewCommand(index);
        return handleInput(command);
    }

    /**
     * Adds tags to a review in the displayed review list at the given row index.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @param tags the tags to add, separated by commas
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult addTags(ReviewList displayedReviews, int rowIndex, String tags) {
        int index = getMasterIndex(displayedReviews, rowIndex);
        Command command = new AddTagsCommand(index, tags);
        return handleInput(command);
    }

    /**
     * Deletes tags from a review in the displayed review list at the given row index.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @param tags the tags to delete, separated by commas
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult deleteTags(ReviewList displayedReviews, int rowIndex, String tags) {
        int index = getMasterIndex(displayedReviews, rowIndex);
        Command command = new DeleteTagsCommand(index, tags);
        return handleInput(command);
    }

    /**
     * Deletes a review in the displayed review list at the given row index.
     *
     * @param displayedReviews the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @return a {@code CommandResult} object containing the output message and the updated review list
     */
    public CommandResult deleteReview(ReviewList displayedReviews, int rowIndex) {
        int index = getMasterIndex(displayedReviews, rowIndex);
        Command command = new DeleteReviewCommand(index);
        return handleInput(command);
    }

    /**
     * Logs out the owner.
     *
     * @return a {@code CommandResult} object containing the output message
     */
    public CommandResult logout() {
        Command command = new LogoutCommand();
        return handleInput(command);
    }

    /**
     * Logs in the owner with the given password.
     *
     * @param password the password to use for login
     * @return a {@code CommandResult} object containing the output message
     */
    public CommandResult login(String password) {
        Command command = new LoginCommand(password);
        return handleInput(command);
    }
}
