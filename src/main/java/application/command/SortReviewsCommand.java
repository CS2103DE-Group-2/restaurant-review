package application.command;

import application.exception.InvalidArgumentException;
import application.review.ReviewList;
import application.sorter.ReviewSorter;
import application.sorter.SortCriterion;
import application.sorter.SortOrder;
import application.storage.Storage;

import java.util.Map;
import java.util.Set;

/**
 * Class representing a command to sort reviews.
 */
public class SortReviewsCommand extends Command {
    public static final Set<String> DELIMITERS = Set.of("/default", "/by");
    private final SortOrder sortOrder;
    private final SortCriterion sortCriterion;


    /**
     * Constructor for SortReviewsCommand class.
     *
     * @param commandArgs the arguments of the command
     */
    public SortReviewsCommand(Map<String, String> commandArgs) {
        String sortOrderString = commandArgs.get("/default");
        String sortCriterionString = commandArgs.get("/by");

        this.sortOrder = SortOrder.getSortOrder(sortOrderString);
        this.sortCriterion = SortCriterion.getSortCriterion(sortCriterionString);
    }

    /**
     * Executes the command to sort reviews.
     *
     * @param reviewList the list of reviews
     * @param storage the storage object
     * @return a string representation of the command result
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(
            ReviewList reviewList,
            Storage storage
    ) throws InvalidArgumentException {
        ReviewList sortedReviewList = ReviewSorter.sort(sortCriterion, sortOrder, reviewList);

        return String.format("""
                Sorted by %s in %s order:
                %s
                """,
                sortCriterion,
                sortOrder,
                sortedReviewList
        );
    }
}