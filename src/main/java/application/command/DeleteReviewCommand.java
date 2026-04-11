package application.command;

import java.io.IOException;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to delete a review.
 */
public class DeleteReviewCommand extends Command {
    private final int index;

    /**
     * Constructor for DeleteReviewCommand class.
     *
     * @param index the index of the review to delete
     */
    public DeleteReviewCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command to delete a review from the list.
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
        Review review = reviews.deleteReview(index);
        storage.saveReviews(reviews);

        return new CommandResult(
                String.format("Review %d deleted!", index),
                isTerminatingCommand(),
                reviews
        );
    }
}
