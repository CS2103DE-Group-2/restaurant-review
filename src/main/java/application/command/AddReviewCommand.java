package application.command;

import java.io.IOException;
import java.util.Set;

import application.auth.AuthManager;
import application.exception.InvalidArgumentException;
import application.review.Rating;
import application.review.Review;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

/**
 * Class representing a command to add a review.
 */
public class AddReviewCommand extends Command {
    private final String reviewBody;
    private final Double foodScore;
    private final Double cleanlinessScore;
    private final Double serviceScore;
    private final String tagsToAddAsString;

    /**
     * Constructor for AddReviewCommand class.
     *
     * @param reviewBody the body of the review
     * @param foodScore the food score
     * @param cleanlinessScore the cleanliness score
     * @param serviceScore the service score
     * @param tagsToAddAsString the set of tags to add to the review
     */
    public AddReviewCommand(String reviewBody,
                            Double foodScore,
                            Double cleanlinessScore,
                            Double serviceScore,
                            String tagsToAddAsString
    ) {
        this.reviewBody = reviewBody;
        this.foodScore = foodScore;
        this.cleanlinessScore = cleanlinessScore;
        this.serviceScore = serviceScore;
        this.tagsToAddAsString = tagsToAddAsString;
    }

    @Override
    public boolean requiresOwnerAuthentication() {
        return false;
    }

    /**
     * Executes the command to add a review to the list.
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
        if (!Rating.isValidScore(foodScore)) {
            throw new InvalidArgumentException(
                    String.format("Food Score must be numbers between %.1f and %.1f.",
                            Rating.RATING_MIN,
                            Rating.RATING_MAX
                    )
            );
        }

        if (!Rating.isValidScore(cleanlinessScore)) {
            throw new InvalidArgumentException(
                    String.format("Cleanliness Score must be numbers between %.1f and %.1f.",
                            Rating.RATING_MIN,
                            Rating.RATING_MAX
                    )
            );
        }

        if (!Rating.isValidScore(serviceScore)) {
            throw new InvalidArgumentException(
                    String.format("Service Score must be numbers between %.1f and %.1f.",
                            Rating.RATING_MIN,
                            Rating.RATING_MAX
                    )
            );
        }

        Rating rating = new Rating(foodScore, cleanlinessScore, serviceScore);
        Set<Tag> tagsToAdd = Tag.toTags(tagsToAddAsString);
        Review review = new Review(reviewBody, rating, tagsToAdd);

        reviews.addReview(review);
        storage.saveReviews(reviews);

        return String.format("Added review to list:\n%s", review);
    }
}
