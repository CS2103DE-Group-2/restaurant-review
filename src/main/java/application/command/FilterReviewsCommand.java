package application.command;

import java.util.Set;

import application.auth.AuthManager;
import application.condition.Condition;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ArgumentParser;
import application.parser.ConditionParser;
import application.review.ReviewList;
import application.review.Tag;
import application.storage.Storage;

/**
 * Class representing a command to filter reviews.
 */
public class FilterReviewsCommand extends Command {
    private final String tagsToIncludeAsString;
    private final String tagsToExcludeAsString;
    private final String filterConditionsAsString;
    private final String isResolvedAsString;

    /**
     * Constructor for FilterReviewsCommand class.
     *
     * @param tagsToIncludeAsString the set of tags to include in the filter
     * @param tagsToExcludeAsString the set of tags to exclude from the filter
     * @param isResolvedAsString whether to filter by resolved status
     * @param filterConditionsAsString the set of conditions to filter by
     */
    public FilterReviewsCommand(
            String tagsToIncludeAsString,
            String tagsToExcludeAsString,
            String isResolvedAsString,
            String filterConditionsAsString
    ) {
        this.tagsToIncludeAsString = tagsToIncludeAsString;
        this.tagsToExcludeAsString = tagsToExcludeAsString;
        this.filterConditionsAsString = filterConditionsAsString;
        this.isResolvedAsString = isResolvedAsString;
    }

    /**
     * Executes the command to filter the list of reviews.
     *
     * @param reviews the list of reviews to filter
     * @param storage the storage object
     * @param manager the authentication manager
     * @return a {@code CommandResult} object containing the result of the command execution
     */
    @Override
    public CommandResult execute(
            ReviewList reviews,
            Storage storage,
            AuthManager manager
    ) throws InvalidArgumentException, MissingArgumentException {
        Set<Tag> tagsToInclude = Tag.toTags(tagsToIncludeAsString);
        Set<Tag> tagsToExclude = Tag.toTags(tagsToExcludeAsString);
        Set<Condition> filterConditions = ConditionParser.getConditions(filterConditionsAsString);
        Boolean isResolved = ArgumentParser.toResolvedStatus(isResolvedAsString);
        ReviewList filteredReviews = reviews.filter(
                tagsToInclude,
                tagsToExclude,
                filterConditions,
                isResolved
        );

        return new CommandResult(
                String.format("""
                Filter criteria:
                Tags to include: %s
                Tags to exclude: %s
                Filter criteria: %s
                Resolved status: %s""",
                        tagsToInclude.isEmpty() ? "None specified" : tagsToInclude,
                        tagsToExclude.isEmpty() ? "None specified" : tagsToExclude,
                        filterConditions.isEmpty() ? "None specified" : filterConditions.stream()
                                .filter(Condition::shouldDisplay).toList(),
                        isResolved == null ? "Not specified" : isResolved
                ),
                isTerminatingCommand(),
                filteredReviews
        );
    }
}
