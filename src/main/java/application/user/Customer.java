package application.user;

// Future integration note:
// import application.review.Review;
// import java.util.List;

/**
 * Class representing a customer in the application.
 * A customer can submit reviews and may unlock detailed ratings
 * after contributing enough reviews.
 */
public class Customer extends User {
    private int reviewCount;
    private TierType tier;

    /**
     * Constructs a Customer object.
     *
     * @param userId unique identifier for the customer
     * @param name name of the customer
     * @param contactNumber contact number of the customer
     */
    public Customer(String userId, String name, String contactNumber) {
        super(userId, name, contactNumber);
        this.reviewCount = 0;
        this.tier = TierType.BASIC;
    }

    /**
     * Returns the number of reviews written by the customer.
     *
     * @return review count
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * Returns the customer's tier.
     *
     * @return customer tier
     */
    public TierType getTier() {
        return tier;
    }

    /**
     * Returns true if the customer can use detailed ratings.
     * Detailed ratings are unlocked after the customer has written
     * more than 5 reviews.
     *
     * @return true if detailed rating is allowed
     */
    public boolean canUseDetailedRating() {
        return reviewCount > 5;
    }

    /**
     * Increments the customer's review count by one.
     * Afterwards, the customer tier is updated accordingly.
     */
    public void incrementReviewCount() {
        reviewCount++;
        updateTier();
    }

    /**
     * Decrements the customer's review count by one.
     * This may be useful in future if a review is deleted.
     * The review count will not go below zero.
     */
    public void decrementReviewCount() {
        if (reviewCount > 0) {
            reviewCount--;
        }
        updateTier();
    }

    /**
     * Updates the customer's tier based on review count.
     * BASIC for 0 to 5 reviews, CONTRIBUTOR for more than 5 reviews.
     */
    private void updateTier() {
        if (reviewCount > 5) {
            tier = TierType.CONTRIBUTOR;
        } else {
            tier = TierType.BASIC;
        }
    }

    /**
     * Checks whether the customer is a contributor.
     *
     * @return true if tier is CONTRIBUTOR
     */
    public boolean isContributor() {
        return tier == TierType.CONTRIBUTOR;
    }

    /*
     * Future coupling with review and rating placeholder:
     *
     * public List<Review> getReviews(....){...}
     *
     * Purpose:
     * Return all reviews written by this customer by querying the central ReviewList
     * using this customer's userId.
     *
     *
     * Proposed behaviour:
     * - Query the central review storage
     * - Filter reviews by this customer's userId
     * - Return the filtered list
     */
}