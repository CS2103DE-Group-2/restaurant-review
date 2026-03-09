package application.user;

/**
 * Abstract class representing a generic user in the application.
 * A user contains basic identity and contact information shared by
 * both customers and restaurant owners.
 */
public abstract class User {
    private final String userId;
    private String name;
    private String contactNumber;

    /**
     * Constructs a User object.
     *
     * @param userId unique identifier for the user
     * @param name name of the user
     * @param contactNumber contact number of the user
     */
    public User(String userId, String name, String contactNumber) {
        this.userId = userId;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    /**
     * Returns the user id.
     *
     * @return user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the user's name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's contact number.
     *
     * @return contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Updates the user's name.
     *
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the user's contact number.
     *
     * @param contactNumber new contact number
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return string representation of the user
     */
    @Override
    public String toString() {
        return String.format("User ID: %s, Name: %s, Contact: %s",
                userId, name, contactNumber);
    }
}