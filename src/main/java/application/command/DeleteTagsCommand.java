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
 * Class representing a command to delete tags from a review.
 */
public class DeleteTagsCommand extends Command {
    private final int index;
    private final String tagsToDeleteAsString;

    /**
     * Constructor for DeleteTagCommand class.
     *
     * @param index the index of the review to delete tags from
     * @param tagsToDeleteAsString the set of tags to delete from the review
     */
    public DeleteTagsCommand(int index, String tagsToDeleteAsString) {
        this.index = index;
        this.tagsToDeleteAsString = tagsToDeleteAsString;
    }

    /**
     * Executes the command to delete tags from a review.
     *
     * <p>
     * Tags that do not exist in the review are not deleted.
     * </p>
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a {@code CommandResult} object containing the result of the command execution
     * @throws InvalidArgumentException if any argument is in the wrong format
     */
    @Override
    public CommandResult execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, IOException {
        Review review = reviews.getReview(index);
        Set<Tag> tagsToDelete = Tag.toTags(tagsToDeleteAsString);

        Set<Tag> existingTags = review.getMatchingTags(tagsToDelete);
        Set<Tag> nonExistentTags = review.getNonMatchingTags(tagsToDelete);

        existingTags.forEach(review::removeTag);
        storage.saveReviews(reviews);

        return new CommandResult(
                String.format("""
                Tags that do not exist in review: %s
                Tags deleted: %s""", nonExistentTags, existingTags),
                isTerminatingCommand(),
                reviews
        );
    }
}
