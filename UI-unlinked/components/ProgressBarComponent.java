package components;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import models.FeedbackState;

/**
 * Animated progress bar showing review completion percentage.
 * Turns green when the review is 100 % complete.
 */
public class ProgressBarComponent extends VBox {

    private static final String ACCENT_ORANGE = "-fx-accent: #FF6B35;";
    private static final String ACCENT_GREEN  = "-fx-accent: #4CAF50;";

    private final ProgressBar bar;
    private final Label       percentLabel;
    private final Label       hintLabel;

    public ProgressBarComponent(FeedbackState state) {
        setSpacing(6);
        setPadding(new Insets(10, 0, 10, 0));

        Label title = new Label("Review Progress");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: #333;");

        bar = new ProgressBar(0);
        bar.setPrefHeight(14);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle(ACCENT_ORANGE);

        percentLabel = new Label("0% Complete");
        percentLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        percentLabel.setStyle("-fx-text-fill: #FF6B35;");

        hintLabel = new Label("Rate → Dishes → Tags to unlock your reward");
        hintLabel.setFont(Font.font(11));
        hintLabel.setStyle("-fx-text-fill: #AAA;");

        HBox barRow = new HBox(10, bar, percentLabel);
        barRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bar, Priority.ALWAYS);

        getChildren().addAll(title, barRow, hintLabel);

        // Reactive update
        state.progressProperty().addListener((obs, oldVal, newVal) -> {
            double pct     = newVal.doubleValue();
            double fraction = pct / 100.0;
            int    rounded  = (int) Math.round(pct);

            bar.setProgress(fraction);
            percentLabel.setText(rounded + "% Complete");

            boolean done = rounded >= 100;
            bar.setStyle(         done ? ACCENT_GREEN  : ACCENT_ORANGE);
            percentLabel.setStyle(done ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;"
                                       : "-fx-text-fill: #FF6B35; -fx-font-weight: bold;");
            hintLabel.setText(done
                ? "✅ All done — hit Submit to claim your reward!"
                : "Rate → Dishes → Tags to unlock your reward");
        });
    }
}
