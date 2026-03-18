package application.review;

/**
 * Represents the structured ratings for a dining experience.
 *
 * Each review stores ratings for food, cleanliness, and service.
 * An overall rating can be derived from these category ratings.
 */
public class Rating {
    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 5;

    private final int foodRating;
    private final int cleanlinessRating;
    private final int serviceRating;

    /**
     * Constructs a {@code Rating} with ratings for food, cleanliness, and service.
     *
     * @param foodRating the food rating
     * @param cleanlinessRating the cleanliness rating
     * @param serviceRating the service rating
     * @throws IllegalArgumentException if any rating is invalid
     */
    public Rating(int foodRating, int cleanlinessRating, int serviceRating) {
        if (!isValidRating(foodRating)
                || !isValidRating(cleanlinessRating)
                || !isValidRating(serviceRating)) {
            throw new IllegalArgumentException(
                    "All ratings must be integers between " + RATING_MIN + " and " + RATING_MAX + ".");
        }

        this.foodRating = foodRating;
        this.cleanlinessRating = cleanlinessRating;
        this.serviceRating = serviceRating;
    }

    /**
     * Returns whether the given rating is valid.
     *
     * @param rating the rating to validate
     * @return {@code true} if the rating is between 1 and 5 inclusive,
     *         {@code false} otherwise
     */
    public static boolean isValidRating(int rating) {
        return rating >= RATING_MIN && rating <= RATING_MAX;
    }

    /**
     * Returns the food rating.
     *
     * @return the food rating
     */
    public int getFoodRating() {
        return foodRating;
    }

    /**
     * Returns the cleanliness rating.
     *
     * @return the cleanliness rating
     */
    public int getCleanlinessRating() {
        return cleanlinessRating;
    }

    /**
     * Returns the service rating.
     *
     * @return the service rating
     */
    public int getServiceRating() {
        return serviceRating;
    }

    /**
     * Returns the derived overall rating.
     *
     * @return the average of the three category ratings
     */
    public double getOverallRating() {
        return (foodRating + cleanlinessRating + serviceRating) / 3.0;
    }

    /**
     * Returns a string representation of the rating.
     *
     * @return a formatted string containing all category ratings and the overall rating
     */
    @Override
    public String toString() {
        return String.format(
                "Food: %d | Cleanliness: %d | Service: %d | Overall: %.1f",
                foodRating,
                cleanlinessRating,
                serviceRating,
                getOverallRating()
        );
    }
}