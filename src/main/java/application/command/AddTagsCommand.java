package application.command;

import java.io.IOException;
import java.util.Set;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

/**
 * Class representing a command to add tags to a review.
 */
public class AddTagsCommand extends Command {
    private final int index;
    private final String tagsToAddAsString;

    /**
     * Constructor for AddTagCommand class.
     *
     * @param index the index of the review to add tags to
     * @param tagsToAddAsString the set of tags to add to the review
     */
    public AddTagsCommand(int index, String tagsToAddAsString) {
        this.index = index;
        this.tagsToAddAsString = tagsToAddAsString;
    }

    /**
     * Executes the command to add tags to a review.
     *
     * <p>
     * Tags that already exist in the review are not added again.
     * </p>
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string representation of the command result
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException {
        Set<Tag> tagsToAdd = Tag.toTags(tagsToAddAsString);
        Review review = reviews.getReview(index);

        Set<Tag> existingTags = review.getMatchingTags(tagsToAdd);
        Set<Tag> nonExistentTags = review.getNonMatchingTags(tagsToAdd);

        nonExistentTags.forEach(review::addTag);
        storage.saveReviews(reviews);

        return String.format("""
                Existing tags not added: %s
                New tags added: %s
                Updated review:
                %s""", existingTags, nonExistentTags, review);
    }
}
