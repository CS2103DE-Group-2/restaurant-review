package components;

import javafx.collections.MapChangeListener;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import models.FeedbackState;

/**
 * Horizontally scrollable row of dish cards.
 * Each card has a placeholder emoji, dish name, and 👍/👎 toggle buttons.
 * Multiple dishes can be rated simultaneously; state updates reactively.
 */
public class DishCarousel extends VBox {

    private static final String[][] DISHES = {
        { "🍣", "Sushi Platter"    },
        { "🥑", "Avocado Toast"    },
        { "🐟", "Grilled Salmon"   },
        { "🍕", "Margherita Pizza" },
        { "🥗", "Caesar Salad"     },
        { "🍔", "Wagyu Burger"     },
        { "🍜", "Ramen Bowl"       },
        { "🍰", "Tiramisu"         }
    };

    private final FeedbackState state;

    public DishCarousel(FeedbackState state) {
        this.state = state;
        setSpacing(10);

        Label title = new Label("Rate Your Dishes");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #222;");

        HBox row = new HBox(14);
        row.setPadding(new Insets(8, 4, 8, 4));
        row.setAlignment(Pos.CENTER_LEFT);

        for (String[] dish : DISHES)
            row.getChildren().add(buildCard(dish[0], dish[1]));

        ScrollPane scroll = new ScrollPane(row);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setPrefHeight(175);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        getChildren().addAll(title, scroll);
    }

    // ── Card builder ──────────────────────────────────────────────────────────

    private VBox buildCard(String emoji, String name) {
        Label emojiLbl = new Label(emoji);
        emojiLbl.setFont(Font.font(34));

        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font(11));
        nameLbl.setWrapText(true);
        nameLbl.setTextAlignment(TextAlignment.CENTER);
        nameLbl.setAlignment(Pos.CENTER);
        nameLbl.setMaxWidth(100);

        Label likeBtn    = buildVoteButton("👍");
        Label dislikeBtn = buildVoteButton("👎");

        likeBtn.setOnMouseClicked(e -> {
            Boolean cur = state.getDishRatings().get(name);
            if (Boolean.TRUE.equals(cur)) state.removeDishRating(name);
            else                          state.setDishRating(name, true);
        });

        dislikeBtn.setOnMouseClicked(e -> {
            Boolean cur = state.getDishRatings().get(name);
            if (Boolean.FALSE.equals(cur)) state.removeDishRating(name);
            else                           state.setDishRating(name, false);
        });

        HBox voteRow = new HBox(10, likeBtn, dislikeBtn);
        voteRow.setAlignment(Pos.CENTER);

        VBox card = new VBox(8, emojiLbl, nameLbl, voteRow);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(120);
        card.setPrefHeight(145);
        card.setPadding(new Insets(14));
        card.setStyle(cardDefault());

        // React to state changes for this specific dish (including reset).
        state.getDishRatings().addListener((MapChangeListener<String, Boolean>) change -> {
            if (!name.equals(change.getKey())) return;
            Boolean val = state.getDishRatings().get(name);   // null when removed
            applyCardState(card, likeBtn, dislikeBtn, val);
        });

        return card;
    }

    private Label buildVoteButton(String emoji) {
        Label btn = new Label(emoji);
        btn.setFont(Font.font(20));
        btn.setStyle("-fx-cursor: hand; -fx-padding: 4; -fx-background-radius: 20;");
        return btn;
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private void applyCardState(VBox card, Label like, Label dislike, Boolean liked) {
        if (liked == null) {
            card.setStyle(cardDefault());
            like.setStyle(   btnDefault());
            dislike.setStyle(btnDefault());
        } else if (liked) {
            card.setStyle(cardLiked());
            like.setStyle(   btnActive());
            dislike.setStyle(btnDefault());
        } else {
            card.setStyle(cardDisliked());
            like.setStyle(   btnDefault());
            dislike.setStyle(btnActive());
        }
    }

    private String cardDefault() {
        return "-fx-background-color: white; -fx-background-radius: 16; " +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 6, 0, 0, 2);";
    }

    private String cardLiked() {
        return "-fx-background-color: #E8F5E9; -fx-background-radius: 16; " +
               "-fx-border-color: #4CAF50; -fx-border-radius: 16; -fx-border-width: 2; " +
               "-fx-effect: dropshadow(gaussian, rgba(76,175,80,0.25), 8, 0, 0, 2);";
    }

    private String cardDisliked() {
        return "-fx-background-color: #FFEBEE; -fx-background-radius: 16; " +
               "-fx-border-color: #F44336; -fx-border-radius: 16; -fx-border-width: 2; " +
               "-fx-effect: dropshadow(gaussian, rgba(244,67,54,0.25), 8, 0, 0, 2);";
    }

    private String btnDefault() {
        return "-fx-cursor: hand; -fx-padding: 4; -fx-background-radius: 20;";
    }

    private String btnActive() {
        return "-fx-cursor: hand; -fx-padding: 4; -fx-background-radius: 20; " +
               "-fx-background-color: rgba(0,0,0,0.08);";
    }
}
