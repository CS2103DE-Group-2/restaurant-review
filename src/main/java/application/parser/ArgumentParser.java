package application.parser;

import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;

/**
 * ArgumentParser class containing generic methods for parsing inputs.
 */
public class ArgumentParser {
    /**
     * Checks if the specified string is null or empty.
     *
     * @param string the string to check
     * @return true if the string is not null or empty, false otherwise
     */
    public static boolean isValidString(String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Returns a score as a float after extracting the argument from the delimiter-argument pair.
     *
     * @param scoreAsString string containing the score
     * @return a float denoting the score
     * @throws MissingArgumentException if the argument is an empty string or null
     * @throws InvalidArgumentException if the argument is not a number or multiple numbers are specified
     */
    public static double toDouble(String scoreAsString) throws MissingArgumentException, InvalidArgumentException {
        if (!isValidString(scoreAsString)) {
            throw new MissingArgumentException("No number given!");
        }

        double score;

        try {
            score = Double.parseDouble(scoreAsString);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("Number provided is not a valid number!");
        }

        return score;
    }

    /**
     * Converts a string to a boolean value.
     * @param resolvedAsString the string to convert
     * @return the boolean value corresponding to the string
     */
    public static Boolean toResolvedStatus(String resolvedAsString) {
        Boolean isResolved;
        //if the user does not specify a value for isResolved, it will be null
        switch (resolvedAsString.toLowerCase().strip()) {
        case "resolved":
            isResolved = true;
            break;
        case "outstanding":
            isResolved = false;
            break;
        case "all":
            //fallthrough
        default:
            isResolved = null;
            break;
        }

        return isResolved;
    }
}
