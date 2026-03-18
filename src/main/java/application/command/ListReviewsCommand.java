package application.command;

import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a command to list all reviews.
 */
public class ListReviewsCommand extends Command {
    /**
     * Returns a string representation of the review list.
     *
     * @param reviewList the list of reviews
     * @param storage the storage object
     * @return a string representation of the review list
     */
    @Override
    public String execute(ReviewList reviewList, Storage storage) {
        return reviewList.toString();
    }
}
