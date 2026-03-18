package application.command;

import java.io.IOException;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.review.ReviewList;
import application.storage.Storage;

/**
 * Class representing a generic user command.
 */
public abstract class Command {
    /**
     * Returns true if the command should end the main program, else false.
     *
     * @return boolean representing if the command should terminate the main program
     */
    public boolean isTerminatingCommand() {
        return false;
    }

    /**
     * Abstract generic execute method for all commands to complete their specified actions.
     *
     * @return message to be displayed to the user
     * @throws InvalidArgumentException if commands do not receive their expected arguments in the correct format
     * @throws IOException if there is an error reading or writing to the file
     */
    public abstract String execute(
            ReviewList reviewList,
            Storage storage
    ) throws InvalidArgumentException, IOException;
}
