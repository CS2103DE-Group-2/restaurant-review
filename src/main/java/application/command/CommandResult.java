package application.command;

import application.review.ReviewList;

/**
 * Represents the result of handling a single user command.
 * Used in MealMeter to encapsulate both the output message and whether the application
 * should terminate after this command.
 *
 * @param output the output message to display to the user
 * @param shouldTerminate whether the application should terminate after this command
 * @param reviews the updated list of reviews
 */
public record CommandResult(String output, boolean shouldTerminate, ReviewList reviews) {
}
