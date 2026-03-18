package components;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import models.FeedbackState;

/**
 * Five large clickable stars.
 * Hover highlights in orange; confirmed rating locks in gold.
 * Reacts to external state changes (e.g. reset or emoticon sync).
 */
public class StarRating extends HBox {

    private static final int MAX = 5;
    private final Label[]    stars = new Label[MAX];
    private final FeedbackState state;

    public StarRating(FeedbackState state) {
        this.state = state;
        setSpacing(10);
        setPadding(new Insets(8, 0, 4, 0));
        setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < MAX; i++) {
            final int index = i + 1;          // 1-based star value
            Label star = new Label("★");
            star.setFont(Font.font(44));
            star.setTextFill(Color.LIGHTGRAY);
            star.setStyle("-fx-cursor: hand;");

            star.setOnMouseClicked(  e -> selectStar(index));
            star.setOnMouseEntered(  e -> hoverStars(index));
            star.setOnMouseExited(   e -> refreshStars());

            stars[i] = star;
            getChildren().add(star);
        }

        // Keep in sync with external state changes (reset, emoticon click).
        state.ratingProperty().addListener((obs, o, n) -> refreshStars());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void selectStar(int index) {
        state.setRating(index);
        refreshStars();
    }

    private void hoverStars(int upTo) {
        for (int i = 0; i < MAX; i++)
            stars[i].setTextFill(i < upTo ? Color.ORANGE : Color.LIGHTGRAY);
    }

    private void refreshStars() {
        int r = state.getRating();
        for (int i = 0; i < MAX; i++)
            stars[i].setTextFill(i < r ? Color.GOLD : Color.LIGHTGRAY);
    }
}
