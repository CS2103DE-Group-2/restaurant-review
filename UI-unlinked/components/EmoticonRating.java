package components;

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import models.FeedbackState;

/**
 * Row of five labelled emoticons.
 * Clicking one sets both the emoticon and the matching star rating (1–5).
 * Selecting via stars also highlights the corresponding emoticon.
 */
public class EmoticonRating extends HBox {

    /** {emoji, label, 1-based star rating} */
    private static final String[][] EMOTICONS = {
        { "😡", "Angry",    "1" },
        { "😕", "Sad",      "2" },
        { "😐", "Neutral",  "3" },
        { "🙂", "Happy",    "4" },
        { "😄", "Ecstatic", "5" }
    };

    private final FeedbackState state;
    private final VBox[]        slots = new VBox[EMOTICONS.length];

    public EmoticonRating(FeedbackState state) {
        this.state = state;
        setSpacing(14);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(6, 0, 6, 0));

        for (int i = 0; i < EMOTICONS.length; i++) {
            final String emoji      = EMOTICONS[i][0];
            final String name       = EMOTICONS[i][1];
            final int    ratingVal  = Integer.parseInt(EMOTICONS[i][2]);

            Label emojiLbl = new Label(emoji);
            emojiLbl.setFont(Font.font(30));

            Label nameLbl = new Label(name);
            nameLbl.setFont(Font.font(10));
            nameLbl.setStyle("-fx-text-fill: #999;");

            VBox box = new VBox(3, emojiLbl, nameLbl);
            box.setAlignment(Pos.CENTER);
            box.setStyle(unselectedStyle());
            box.setStyle("-fx-cursor: hand; " + unselectedStyle());

            box.setOnMouseClicked(e -> {
                state.setSelectedEmoticon(emoji);
                state.setRating(ratingVal);
            });

            slots[i] = box;
            getChildren().add(box);
        }

        // Sync highlight when rating changes from stars or from reset
        state.ratingProperty().addListener(           (obs, o, n) -> syncByRating(n.intValue()));
        state.selectedEmoticonProperty().addListener( (obs, o, n) -> syncByEmoticon(n));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void syncByRating(int rating) {
        for (int i = 0; i < slots.length; i++) {
            boolean sel = (i + 1) == rating;
            slots[i].setStyle("-fx-cursor: hand; " + (sel ? selectedStyle() : unselectedStyle()));
        }
    }

    private void syncByEmoticon(String emoji) {
        for (int i = 0; i < slots.length; i++) {
            boolean sel = EMOTICONS[i][0].equals(emoji);
            slots[i].setStyle("-fx-cursor: hand; " + (sel ? selectedStyle() : unselectedStyle()));
        }
    }

    private String unselectedStyle() {
        return "-fx-padding: 8; -fx-background-radius: 10;";
    }

    private String selectedStyle() {
        return "-fx-padding: 8; -fx-background-radius: 10; -fx-background-color: #FFF3E0;";
    }
}
