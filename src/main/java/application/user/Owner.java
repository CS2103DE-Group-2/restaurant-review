package application.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a restaurant owner in the application.
 * An owner can be associated with one or more restaurants.
 */
public class Owner extends User {
    private final List<String> ownedRestaurantIds;

    /**
     * Constructs an Owner object.
     *
     * @param userId unique identifier for the owner
     * @param name name of the owner
     * @param contactNumber contact number of the owner
     */
    public Owner(String userId, String name, String contactNumber) {
        super(userId, name, contactNumber);
        this.ownedRestaurantIds = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable list of restaurant ids owned by this owner.
     *
     * @return list of owned restaurant ids
     */
    public List<String> getOwnedRestaurantIds() {
        return Collections.unmodifiableList(ownedRestaurantIds);
    }

    /**
     * Adds a restaurant id to the owner's list of owned restaurants.
     *
     * @param restaurantId restaurant id to add
     */
    public void addRestaurantId(String restaurantId) {
        if (!ownedRestaurantIds.contains(restaurantId)) {
            ownedRestaurantIds.add(restaurantId);
        }
    }

    /**
     * Removes a restaurant id from the owner's list of owned restaurants.
     * This may be useful in future if ownership is transferred or a restaurant is deleted.
     *
     * @param restaurantId restaurant id to remove
     */
    public void removeRestaurantId(String restaurantId) {
        ownedRestaurantIds.remove(restaurantId);
    }

    /**
     * Checks whether this owner owns a particular restaurant.
     *
     * @param restaurantId restaurant id to check
     * @return true if this owner owns the restaurant
     */
    public boolean ownsRestaurant(String restaurantId) {
        return ownedRestaurantIds.contains(restaurantId);
    }

    /*
     * Future coupling placeholder:
     *
     * public List<Restaurant> getOwnedRestaurants(...){...}
     * 
     * purpose:
     * Return all Restaurant objects owned by this owner by querying the stored
     * restaurant ids against a central RestaurantStorage / repository.
     *
     *
     * Proposed behaviour:
     * - Loop through ownedRestaurantIds
     * - Retrieve matching Restaurant objects from central restaurant storage
     * - Return them as a list
     */
}