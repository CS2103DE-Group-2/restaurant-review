package application.review;

import application.exception.InvalidArgumentException;

/**
 * Represents the structured ratings for a dining experience.
 * <p>
 * Each review stores ratings for food, cleanliness, and service.
 * An overall rating can be derived from these category ratings.
 * </p>
 */
public class Rating {
    public static final double RATING_MIN = 1.0;
    public static final double RATING_MAX = 5.0;

    private final double foodScore;
    private final double cleanlinessScore;
    private final double serviceScore;

    /**
     * Constructs a {@code Rating} with scores for food, cleanliness, and service.
     *
     * @param foodScore the food score
     * @param cleanlinessScore the cleanliness rating
     * @param serviceScore the service rating
     */
    public Rating(double foodScore,
                  double cleanlinessScore,
                  double serviceScore
    ) {
        this.foodScore = foodScore;
        this.cleanlinessScore = cleanlinessScore;
        this.serviceScore = serviceScore;
    }

    /**
     * Returns whether the given score is valid.
     *
     * @param score the score to validate
     * @return {@code true} if the rating is between 1 and 5 inclusive,
     *         {@code false} otherwise
     */
    public static boolean isValidScore(double score) {
        return score >= RATING_MIN && score <= RATING_MAX;
    }

    /**
     * Returns the food score.
     *
     * @return the food score
     */
    public double getFoodScore() {
        return foodScore;
    }

    /**
     * Returns the food score as a string.
     *
     * @return the food score as a string
     */
    public String getFoodScoreString() {
        return String.format("%.1f", getFoodScore());
    }

    /**
     * Returns the cleanliness score.
     *
     * @return the cleanliness score
     */
    public double getCleanlinessScore() {
        return cleanlinessScore;
    }

    /**
     * Returns the cleanliness score as a string.
     *
     * @return the cleanliness score as a string
     */
    public String getCleanlinessScoreString() {
        return String.format("%.1f", getCleanlinessScore());
    }

    /**
     * Returns the service score.
     *
     * @return the service score
     */
    public double getServiceScore() {
        return serviceScore;
    }

    /**
     *  Returns the food score as a string.
     *
     *  @return the food score as a string
     */
    public String getServiceScoreString() {
        return String.format("%.1f", getServiceScore());
    }

    /**
     * Returns the derived overall score.
     *
     * @return the average of the three category ratings
     */
    public double getOverallScore() {
        return (foodScore + cleanlinessScore + serviceScore) / 3.0;
    }

    /**
     * Returns the overall score as a string.
     *
     * @return the overall score as a string
     */
    public String getOverallScoreString() {
        return String.format("%.1f", getOverallScore());
    }

    /**
     * Returns a string representation of the rating.
     *
     * @return a formatted string containing all category ratings and the overall rating
     */
    @Override
    public String toString() {
        return String.format(
                "Food: %.1f | Cleanliness: %.1f | Service: %.1f | Overall: %.1f",
                getFoodScore(),
                getCleanlinessScore(),
                getServiceScore(),
                getOverallScore()
        );
    }
}
