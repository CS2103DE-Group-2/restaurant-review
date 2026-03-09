package application.restaurant;

// Future integration note:
// import application.review.Review;
// import application.review.Rating;

/**
 * Class representing a restaurant in the application.
 * A restaurant contains its identifying information, ownership link,
 * and descriptive metadata such as tags.
 */
public class Restaurant {
    private final String restaurantId;
    private final String ownerId;
    private String name;
    private String address;
    private String contactNumber;
    private String description;
    // private final List<String> tags;  this attribute is for future iterations, useful for filtering and search

    /**
     * Constructs a Restaurant object.
     *
     * @param restaurantId unique identifier for the restaurant
     * @param ownerId unique identifier for the owner of the restaurant
     * @param name restaurant name
     * @param address restaurant address
     * @param contactNumber restaurant contact number
     * @param description restaurant description
     * optional @param tags list of restaurant tags
     */
    public Restaurant(String restaurantId, String ownerId, String name, String address,
                      String contactNumber, String description/*, List<String> tags*/) {
        this.restaurantId = restaurantId;
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.description = description;
        // this.tags = new ArrayList<>(tags);
    }

    /**
     * Returns the restaurant id.
     *
     * @return restaurant id
     */
    public String getRestaurantId() {
        return restaurantId;
    }

    /**
     * Returns the owner id.
     *
     * @return owner id
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Returns the restaurant name.
     *
     * @return restaurant name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the restaurant address.
     *
     * @return restaurant address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the restaurant contact number.
     *
     * @return restaurant contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Returns the restaurant description.
     *
     * @return restaurant description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Updates the restaurant's basic information.
     *
     * @param name new restaurant name
     * @param address new restaurant address
     * @param contactNumber new restaurant contact number
     * @param description new restaurant description
     */
    public void updateInfo(String name, String address, String contactNumber, String description) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.description = description;
    }

    /**
     * Returns a string representation of the restaurant.
     *
     * @return string representation of the restaurant
     */
    @Override
    public String toString() {
        return String.format("Restaurant ID: %s, Name: %s, Address: %s, Contact: %s, Description: %s",
                restaurantId, name, address, contactNumber, description);
    }

    /*
     * Future coupling placeholder:
     *
     * public List<Review> getReviews(....){...}
     * 
     * purpose:
     * Return all reviews associated with this restaurant by querying the central ReviewList
     * using this restaurant's id.
     *
     * proposed behaviour:
     * - Query the central review storage
     * - Filter reviews by this restaurant's id
     * - Return the filtered list
     */


    /*
     * Future coupling placeholder:
     *
     * public double getAverageRating(....){...}
     * 
     * purpose:
     * Calculate the average rating for this restaurant from all associated reviews.
     *
     * proposed behaviour:
     * - Retrieve all reviews for this restaurant
     * - Extract each review's rating
     * - Compute and return the average overall rating
     */

    /*
     * Future coupling placeholder:
     *
     * public int getReviewCount(....){...}
     * 
     * purpose:
     * Return the total number of reviews for this restaurant.
     *
     * proposed behaviour:
     * - Query the central review storage
     * - Count how many reviews belong to this restaurant
     * - Return the count
     */
}