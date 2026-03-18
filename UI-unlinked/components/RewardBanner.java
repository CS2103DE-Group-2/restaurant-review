package components;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.*;
import models.FeedbackState;

/**
 * Sticky banner at the bottom of the feedback form advertising the reward.
 * Transitions from orange (teaser) to green (unlocked) when progress hits 100 %.
 */
public class RewardBanner extends HBox {

    private static final String GRADIENT_ORANGE =
        "-fx-background-color: linear-gradient(to right, #FF6B35, #FF8E53); " +
        "-fx-background-radius: 14;";
    private static final String GRADIENT_GREEN =
        "-fx-background-color: linear-gradient(to right, #43A047, #66BB6A); " +
        "-fx-background-radius: 14;";

    private final Label iconLbl;
    private final Label textLbl;

    public RewardBanner(FeedbackState state) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(14, 22, 14, 22));
        setSpacing(14);
        setStyle(GRADIENT_ORANGE);

        iconLbl = new Label("🎁");
        iconLbl.setFont(Font.font(22));

        textLbl = new Label("Complete your review & unlock a 10% discount on your next visit!");
        textLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        textLbl.setStyle("-fx-text-fill: white;");
        textLbl.setWrapText(true);

        getChildren().addAll(iconLbl, textLbl);
        HBox.setHgrow(textLbl, javafx.scene.layout.Priority.ALWAYS);

        state.progressProperty().addListener((obs, o, n) -> {
            boolean done = n.doubleValue() >= 100;
            setStyle(done ? GRADIENT_GREEN : GRADIENT_ORANGE);
            iconLbl.setText(done ? "🎉" : "🎁");
            textLbl.setText(done
                ? "Reward unlocked! Submit now to claim your 10% discount."
                : "Complete your review & unlock a 10% discount on your next visit!");
        });
    }
}
