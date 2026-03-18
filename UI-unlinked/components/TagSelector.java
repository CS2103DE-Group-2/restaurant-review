package components;

import javafx.collections.ListChangeListener;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import models.FeedbackState;

import java.util.*;

/**
 * Collapsible tag categories. Click a category header to show/hide its chips.
 * Chips are multi-select toggles; two or more selections contribute to progress.
 */
public class TagSelector extends VBox {

    /** Ordered map of category → tag list. */
    private static final Map<String, String[]> CATEGORIES = new LinkedHashMap<>();
    static {
        CATEGORIES.put("🍽  Food",       new String[]{ "Fresh Ingredients", "Great Taste", "Beautiful Presentation", "Generous Portions" });
        CATEGORIES.put("🤝  Service",    new String[]{ "Friendly Staff", "Fast Service", "Very Attentive", "Professional" });
        CATEGORIES.put("✨  Atmosphere", new String[]{ "Cozy Vibe", "Spotlessly Clean", "Great Ambiance", "Romantic Setting" });
        CATEGORIES.put("💰  Value",      new String[]{ "Worth Every Penny", "Great Deals", "Fair Portions" });
        CATEGORIES.put("🍹  Drinks",     new String[]{ "Amazing Cocktails", "Excellent Wine List", "Perfect Coffee", "Fresh Juices" });
    }

    private final FeedbackState state;

    public TagSelector(FeedbackState state) {
        this.state = state;
        setSpacing(10);

        Label title = new Label("Your Experience");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #222;");
        getChildren().add(title);

        for (Map.Entry<String, String[]> entry : CATEGORIES.entrySet())
            getChildren().add(buildCategory(entry.getKey(), entry.getValue()));
    }

    // ── Category section ──────────────────────────────────────────────────────

    private VBox buildCategory(String header, String[] tags) {
        Label headerLbl = new Label(header + "  ▾");
        headerLbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        headerLbl.setStyle("-fx-text-fill: #555; -fx-cursor: hand;");

        FlowPane chipPane = new FlowPane(8, 8);
        chipPane.setPadding(new Insets(4, 0, 4, 0));

        for (String tag : tags)
            chipPane.getChildren().add(buildChip(tag));

        // Toggle visibility on header click (starts expanded)
        headerLbl.setOnMouseClicked(e -> {
            boolean nowVisible = !chipPane.isVisible();
            chipPane.setVisible(nowVisible);
            chipPane.setManaged(nowVisible);
            headerLbl.setText(header + (nowVisible ? "  ▾" : "  ▸"));
        });

        VBox section = new VBox(6, headerLbl, chipPane);
        return section;
    }

    // ── Chip builder ──────────────────────────────────────────────────────────

    private Label buildChip(String tag) {
        Label chip = new Label(tag);
        chip.setPadding(new Insets(6, 16, 6, 16));
        chip.setFont(Font.font(12));
        chip.setStyle("-fx-cursor: hand; " + unselectedChip());

        chip.setOnMouseClicked(e -> {
            state.toggleTag(tag);
            // Style updated via listener below
        });

        // Keep chip appearance in sync with state (handles reset too).
        state.getSelectedTags().addListener((ListChangeListener<String>) c -> {
            boolean sel = state.getSelectedTags().contains(tag);
            chip.setStyle("-fx-cursor: hand; " + (sel ? selectedChip() : unselectedChip()));
        });

        return chip;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private String unselectedChip() {
        return "-fx-background-color: #F5F5F5; -fx-background-radius: 20; " +
               "-fx-text-fill: #444; -fx-border-color: #DDD; " +
               "-fx-border-radius: 20; -fx-border-width: 1;";
    }

    private String selectedChip() {
        return "-fx-background-color: #FF6B35; -fx-background-radius: 20; " +
               "-fx-text-fill: white; -fx-border-color: #FF6B35; " +
               "-fx-border-radius: 20; -fx-border-width: 1;";
    }
}
