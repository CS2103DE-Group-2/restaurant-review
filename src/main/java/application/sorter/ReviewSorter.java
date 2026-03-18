package application.sorter;

import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;

import java.util.function.Function;

/**
 * Class for sorting reviews based on a given criterion and sort order.
 */
public class ReviewSorter {
    /**
     * Sorts the given review list based on the given criterion and sort order.
     *
     * @param sortCriterion the criterion to sort by
     * @param sortOrder the sort order (ascending or descending)
     * @param reviewList the list of reviews to sort
     * @return a new sorted list of reviews
     * @throws InvalidArgumentException if the sort order is invalid
     */
    public static ReviewList sort(
            SortCriterion sortCriterion,
            SortOrder sortOrder,
            ReviewList reviewList
    ) throws InvalidArgumentException {
        Function<Review, Double> sortCriterionFunction = getSortCriterionFunction(sortCriterion);

        switch (sortOrder) {
        case ASCENDING:
            return reviewList.sortByAscending(sortCriterionFunction);
        case DESCENDING:
            return reviewList.sortByDescending(sortCriterionFunction);
        case UNKNOWN:
        default:
            throw new InvalidArgumentException("Invalid sort order!");
        }
    }

    /**
     * Returns a function that extracts the sort criterion value from a review.
     * @param sortCriterion the sort criterion
     * @return a function that extracts the sort criterion value from a review
     */
    private static Function<Review, Double> getSortCriterionFunction(SortCriterion sortCriterion) {
        Function<Review, Double> sortCriterionFunction;

        switch (sortCriterion) {
        case OVERALL_SCORE:
            sortCriterionFunction = review -> review.getRating().getOverallScore();
            break;
        case FOOD_SCORE:
            sortCriterionFunction = review -> review.getRating().getFoodScore();
            break;
        case SERVICE_SCORE:
            sortCriterionFunction = review -> review.getRating().getServiceScore();
            break;
        case CLEANLINESS_SCORE:
            sortCriterionFunction = review -> review.getRating().getCleanlinessScore();
            break;
        case TAG_COUNT:
            sortCriterionFunction = review -> (double) review.getTags().size();
            break;
        default:
            //default to overall score
            sortCriterionFunction = review -> review.getRating().getOverallScore();
        }

        return sortCriterionFunction;
    }
}
