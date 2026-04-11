package application.review;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import application.exception.InvalidArgumentException;

/**
 * Represents a single anonymous customer review.
 * <p>
 * Each review contains a structured rating, a written review body,
 * zero or more tags, and a resolved status for owner-side tracking.
 * </p>
 */
public class Review {
    private final String reviewBody;
    private final Rating rating;
    private final Set<Tag> tags;
    private boolean isResolved;

    /**
     * Constructs a {@code Review} with the specified review body, rating, and tags.
     * <p>
     * The review is created with an unresolved status.
     * </p>
     *
     * @param reviewBody the written review content
     * @param rating the structured rating for the review
     * @param tags the set of tags associated with the review
     * @throws InvalidArgumentException if the review body is null or blank
     */
    public Review(String reviewBody, Rating rating, Set<Tag> tags) throws InvalidArgumentException {
        if (reviewBody == null || reviewBody.isBlank()) {
            reviewBody = ""; //allow empty review body
        }
        if (rating == null) {
            throw new InvalidArgumentException("Rating cannot be null.");
        }

        this.reviewBody = reviewBody.trim();
        this.rating = rating;
        this.tags = tags;
        this.isResolved = false;
    }

    /**
     * Returns the written review body.
     *
     * @return the review body
     */
    public String getReviewBody() {
        return reviewBody;
    }

    /**
     * Returns the structured rating associated with this review.
     *
     * @return the rating
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Returns the food score.
     *
     * @return the food score
     */
    public double getFoodScore() {
        return rating.getFoodScore();
    }

    /**
     * Returns the food score as a string.
     *
     * @return the food score as a string
     */
    public String getFoodScoreString() {
        return rating.getFoodScoreString();
    }

    /**
     * Returns the cleanliness score.
     *
     * @return the cleanliness score
     */
    public double getCleanlinessScore() {
        return rating.getCleanlinessScore();
    }

    /**
     * Returns the cleanliness score as a string.
     *
     * @return the cleanliness score as a string
     */
    public String getCleanlinessScoreString() {
        return rating.getCleanlinessScoreString();
    }

    /**
     * Returns the service score.
     *
     * @return the service score
     */
    public double getServiceScore() {
        return rating.getServiceScore();
    }

    /**
     * Returns the service score as a string.
     *
     * @return the service score as a string
     */
    public String getServiceScoreString() {
        return rating.getServiceScoreString();
    }

    /**
     * Returns the derived overall score.
     *
     * @return the average of the three category ratings
     */
    public double getOverallScore() {
        return rating.getOverallScore();
    }

    /**
     * Returns the overall score as a string.
     *
     * @return the overall score as a string
     */
    public String getOverallScoreString() {
        return rating.getOverallScoreString();
    }

    /**
     * Returns the string representation of the rating.
     *
     * @return the rating string
     */
    public String getRatingString() {
        return rating.toString();
    }

    /**
     * Returns a string containing the tag names, sorted alphabetically
     *
     * @return a string containing the tag names, sorted alphabetically
     */
    public String getTagsAsString() {
        return tags.stream()
                .map(Tag::getTagName)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns an unmodifiable set of tags associated with this review.
     *
     * @return an unmodifiable set of tags associated with this review
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns the number of tags associated with this review.
     * @return the number of tags associated with this review
     */
    public int getTagCount() {
        return tags.size();
    }

    /**
     * Returns whether this review has been marked as resolved.
     *
     * @return {@code true} if the review is resolved, {@code false} otherwise
     */
    public boolean isResolved() {
        return isResolved;
    }

    /**
     * Adds the specified tag to this review.
     *
     * @param tag the tag to add
     * @throws IllegalArgumentException if the tag is null
     */
    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        tags.add(tag);
    }

    /**
     * Removes the specified tag from this review.
     *
     * @param tag the tag to remove
     * @throws IllegalArgumentException if the tag is null
     */
    public void removeTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        tags.remove(tag);
    }

    /**
     * Marks this review as resolved.
     */
    public void markResolved() {
        isResolved = true;
    }

    /**
     * Marks this review as outstanding.
     */
    public void markOutstanding() {
        isResolved = false;
    }

    /**
     * Returns whether this review contains all the specified tags.
     *
     * @param tagsToMatch the set of tags to match
     * @return true if this review contains all the specified tags, false otherwise
     */
    public boolean containsAllMatchingTags(Set<Tag> tagsToMatch) {
        return tags.containsAll(tagsToMatch);
    }

    /**
     * Returns whether this review contains any of the specified tags.
     *
     * @param tagsToMatch the set of tags to match
     * @return true if this review contains any of the specified tags, false otherwise
     */
    public boolean containsNoMatchingTags(Set<Tag> tagsToMatch) {
        return tagsToMatch.stream().noneMatch(tags::contains);
    }

    /**
     * Returns a set of tags that match existing tags in this review.
     *
     * @param tagsToMatch the set of tags to match
     * @return a set of tags that match existing tags in this review
     */
    public Set<Tag> getMatchingTags(Set<Tag> tagsToMatch) {
        return tagsToMatch.stream()
                .filter(tags::contains)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a set of tags that do not match existing tags in this review.
     *
     * @param tagsToMatch the set of tags to match
     * @return a set of tags that do not match existing tags in this review
     */
    public Set<Tag> getNonMatchingTags(Set<Tag> tagsToMatch) {
        return tagsToMatch.stream()
                .filter(tag -> !tags.contains(tag))
                .collect(Collectors.toSet());
    }

    /**
     * Converts this review and its row index into an object array for GUI table display.
     *
     * @param rowIndex the 1-based index to display
     * @return an object array representing the review row
     */
    public Object[] toRow(int rowIndex) {
        String tags = getTagsAsString();
        return new Object[]{
            rowIndex,
            getOverallScoreString(),
            getFoodScoreString(),
            getCleanlinessScoreString(),
            getServiceScoreString(),
            isResolved() ? "Resolved" : "Outstanding",
            tags,
            getReviewBody()
        };
    }

    /**
     * Returns a string representation of this review.
     *
     * @return a formatted string containing rating, resolution status, tags, and review body
     */
    @Override
    public String toString() {
        String resolvedStatus = isResolved ? "Resolved" : "Outstanding";
        String tagDisplay = tags.isEmpty() ? "No tags" : tags.toString();
        String reviewDisplay = reviewBody.isBlank() ? "No review" : reviewBody;

        return String.format(
                "%s%nStatus: %s%nTags: %s%nReview: %s",
                rating,
                resolvedStatus,
                tagDisplay,
                reviewDisplay
        );
    }
}
