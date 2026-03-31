package application.command;

import application.auth.AuthManager;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing an unknown command.
 */
public class UnknownCommand extends Command {
    @Override
    public boolean requiresOwnerAuthentication() {
        return false;
    }

    /**
     * Returns a message indicating that the command is unknown.
     *
     * @param reviews the list of reviews
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a string indicating that the command is unknown
     */
    @Override
    public String execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) {
        return "I'm sorry, I don't understand that command.";
    }
}
