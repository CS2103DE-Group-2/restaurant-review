package models;

import javafx.beans.property.*;
import javafx.collections.*;

/**
 * Central reactive state for one feedback session.
 * All UI components bind to these properties — calling reset() clears everything
 * and the UI updates automatically via JavaFX bindings.
 */
public class FeedbackState {

    // ── Core fields ──────────────────────────────────────────────────────────
    private final IntegerProperty rating             = new SimpleIntegerProperty(0);
    private final StringProperty  selectedEmoticon   = new SimpleStringProperty("");
    private final ObservableMap<String, Boolean> dishRatings
                                                     = FXCollections.observableHashMap();
    private final ObservableList<String> selectedTags
                                                     = FXCollections.observableArrayList();
    private final StringProperty  comment            = new SimpleStringProperty("");
    private final DoubleProperty  progress           = new SimpleDoubleProperty(0.0);

    // ── Constructor ───────────────────────────────────────────────────────────
    public FeedbackState() {
        // Recompute progress whenever any tracked field changes.
        rating.addListener((obs, o, n) -> updateProgress());
        dishRatings.addListener((MapChangeListener<String, Boolean>) c -> updateProgress());
        selectedTags.addListener((ListChangeListener<String>) c -> updateProgress());
        comment.addListener((obs, o, n) -> updateProgress());
    }

    // ── Progress computation ──────────────────────────────────────────────────
    /**
     * Progress breakdown (sums to 100):
     *   40 pts – star/emoticon rating selected
     *   30 pts – at least one dish rated
     *   20 pts – at least two tags selected
     *   10 pts – optional comment added
     */
    private void updateProgress() {
        double p = 0;
        if (rating.get() > 0)            p += 40;
        if (!dishRatings.isEmpty())      p += 30;
        if (selectedTags.size() >= 2)    p += 20;
        if (!comment.get().isBlank())    p += 10;
        progress.set(Math.min(100.0, p));
    }

    // ── Reset ─────────────────────────────────────────────────────────────────
    public void reset() {
        rating.set(0);
        selectedEmoticon.set("");
        dishRatings.clear();
        selectedTags.clear();
        comment.set("");
        // progress will auto-reset to 0 via listeners
    }

    // ── Rating ────────────────────────────────────────────────────────────────
    public IntegerProperty ratingProperty()      { return rating; }
    public int  getRating()                      { return rating.get(); }
    public void setRating(int v)                 { rating.set(v); }

    // ── Emoticon ──────────────────────────────────────────────────────────────
    public StringProperty selectedEmoticonProperty() { return selectedEmoticon; }
    public String getSelectedEmoticon()              { return selectedEmoticon.get(); }
    public void   setSelectedEmoticon(String e)      { selectedEmoticon.set(e); }

    // ── Dish ratings ──────────────────────────────────────────────────────────
    public ObservableMap<String, Boolean> getDishRatings()           { return dishRatings; }
    public void setDishRating(String dish, boolean liked)            { dishRatings.put(dish, liked); }
    public void removeDishRating(String dish)                        { dishRatings.remove(dish); }

    // ── Tags ──────────────────────────────────────────────────────────────────
    public ObservableList<String> getSelectedTags() { return selectedTags; }
    public void toggleTag(String tag) {
        if (selectedTags.contains(tag)) selectedTags.remove(tag);
        else                            selectedTags.add(tag);
    }

    // ── Comment ───────────────────────────────────────────────────────────────
    public StringProperty commentProperty()  { return comment; }
    public String getComment()               { return comment.get(); }
    public void   setComment(String c)       { comment.set(c); }

    // ── Progress ──────────────────────────────────────────────────────────────
    public DoubleProperty progressProperty() { return progress; }
    public double getProgress()              { return progress.get(); }
}
