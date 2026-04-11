package application.command;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Criterion;
import application.review.ReviewList;
import application.review.SortOrder;
import application.storage.Storage;

/**
 * Class representing a command to sort reviews.
 */
public class SortReviewsCommand extends Command {
    private final String sortOrderAsString;
    private final String sortCriterionAsString;

    /**
     * Constructor for SortReviewsCommand class.
     *
     * @param sortOrderAsString the sort order of the reviews
     * @param sortCriterionAsString the sort criterion of the reviews
     */
    public SortReviewsCommand(String sortOrderAsString, String sortCriterionAsString) {
        this.sortOrderAsString = sortOrderAsString;
        this.sortCriterionAsString = sortCriterionAsString;
    }

    /**
     * Executes the command to sort reviews.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string representation of the command result
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public CommandResult execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException {
        SortOrder sortOrder = SortOrder.getSortOrder(sortOrderAsString);
        Criterion sortCriterion = Criterion.getCriterion(sortCriterionAsString);
        ReviewList sortedReviewList = reviews.sort(sortCriterion, sortOrder, reviews);

        return new CommandResult(
                String.format("""
                Sorted reviews by %s in %s order!
                """, sortCriterion, sortOrder),
                isTerminatingCommand(),
                sortedReviewList
        );
    }
}
