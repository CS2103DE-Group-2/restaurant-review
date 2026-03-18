package application.command;

import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing an unknown command.
 */
public class UnknownCommand extends Command {
    /**
     * Returns a message indicating that the command is unknown.
     *
     * @param reviewList the list of reviews
     * @param storage the storage object
     * @return a string indicating that the command is unknown
     */
    @Override
    public String execute(ReviewList reviewList, Storage storage) {
        return "I'm sorry, I don't understand that command.";
    }
}
