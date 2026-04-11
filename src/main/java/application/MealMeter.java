package application;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import application.auth.AuthManager;
import application.command.Command;
import application.command.CommandResult;
import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.Review;
import application.review.ReviewList;
import application.review.SortOrder;
import application.storage.Storage;
import application.storage.StorageLoadResult;

/**
 * Represents the core MealMeter application logic.
 *
 * <p>This class acts as the bridge between the user interface
 * and the command-processing logic.</p>
 */
public class MealMeter {
    private static final String DEFAULT_OWNER_PASSWORD = "password";
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied. Please log in as the owner to use this command.";

    private final Storage storage;
    private final AuthManager authManager;
    private final ReviewList reviewList;
    private final boolean hasStorageLoadFailure;
    private final List<String> startupStorageWarnings;

    /**
     * Constructs a MealMeter application and loads stored reviews.
     */
    public MealMeter() {
        this(DEFAULT_OWNER_PASSWORD);
    }

    /**
     * Constructs a MealMeter application with a custom owner password
     * and loads stored reviews.
     *
     * @param ownerPassword the owner password for session login
     */
    public MealMeter(String ownerPassword) {
        this(new Storage(), new AuthManager(ownerPassword));
    }

    MealMeter(Storage storage, AuthManager authManager) {
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

        this.reviewList = loadedReviews;
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
        return reviewList;
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
                        reviewList);
            }

            String output = command.execute(reviewList, storage, authManager);
            return new CommandResult(output,
                    command.isTerminatingCommand(),
                    reviewList);
        } catch (InvalidArgumentException | IOException e) {
            return new CommandResult(e.getMessage(),
                    false,
                    reviewList);
        } catch (Exception e) {
            return new CommandResult("An unexpected error occurred: " + e.getMessage(),
                    false,
                    reviewList);
        }
    }

    /**
     * Returns the display list sorted by the given criterion label and order string.
     *
     * @param sortBy UI label: "Overall", "Food", "Cleanliness", "Service", or "Tag Count"
     * @param sortOrder "Descending" for descending order, anything else for ascending
     * @param displayList the list to sort
     * @return the sorted ReviewList; returns displayList unchanged if sorting fails
     */
    public ReviewList sortReviews(String sortBy, String sortOrder, ReviewList displayList) {
        try {
            Criterion criterion = mapSortByCriterion(sortBy);
            SortOrder order = "Descending".equals(sortOrder)
                    ? SortOrder.DESCENDING
                    : SortOrder.ASCENDING;
            return reviewList.sort(criterion, order, displayList);
        } catch (InvalidArgumentException e) {
            return displayList;
        }
    }

    /**
     * Returns the 1-based master-list index of the review at rowIndex in the display list.
     * Uses reference equality since filter() returns the same Review objects.
     *
     * @param displayList the filtered or sorted display list
     * @param rowIndex the 1-based row index within the display list
     * @return the 1-based index in the master list, or -1 if not found
     */
    public int getMasterIndex(ReviewList displayList, int rowIndex) {
        try {
            Review displayed = displayList.getReview(rowIndex);
            List<Review> all = reviewList.getAllReviews();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i) == displayed) {
                    return i + 1;
                }
            }
        } catch (InvalidArgumentException e) {
            // rowIndex is out of bounds in the display list; return -1 to signal not found
        }
        return -1;
    }

    private static Criterion mapSortByCriterion(String sortBy) {
        switch (sortBy) {
        case "Food":
            return Criterion.FOOD_SCORE;
        case "Cleanliness":
            return Criterion.CLEANLINESS_SCORE;
        case "Service":
            return Criterion.SERVICE_SCORE;
        case "Tag Count":
            return Criterion.TAG_COUNT;
        default:
            return Criterion.OVERALL_SCORE;
        }
    }
}
