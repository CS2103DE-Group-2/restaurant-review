package application.command;

import java.io.IOException;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to resolve a review.
 */
public class ResolveReviewCommand extends Command {
    private final int index;

    /**
     * Constructor for ResolveReviewCommand class.
     *
     * @param index the index of the review to resolve
     */
    public ResolveReviewCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command to resolve a review in the list.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a {@code CommandResult} object containing the result of the command execution
     * @throws InvalidArgumentException if the index is in the wrong format
     */
    @Override
    public CommandResult execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException {
        Review review = reviews.markResolved(index);
        storage.saveReviews(reviews);

        return new CommandResult(
                String.format("Review %d marked as resolved!", index),
                isTerminatingCommand(),
                reviews
        );
    }
}
