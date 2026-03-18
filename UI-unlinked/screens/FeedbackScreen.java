package screens;

import components.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import models.FeedbackState;

/**
 * Main feedback screen — the full review journey in one scrollable view:
 *   Header → Star/Emoticon rating → Dish carousel → Tag selector →
 *   Progress bar → Comment button → Reward banner → Submit button.
 *
 * Uses a Runnable callback to trigger navigation so it has no direct
 * dependency on AppNavigator (which lives in the unnamed package).
 */
public class FeedbackScreen extends ScrollPane {

    private final FeedbackState state;
    private final Runnable      onSubmit;

    public FeedbackScreen(FeedbackState state, Runnable onSubmit) {
        this.state    = state;
        this.onSubmit = onSubmit;

        VBox content = buildContent();

        // Wrap in a centering HBox so the card stays centred on wide windows.
        HBox centred = new HBox(content);
        centred.setAlignment(Pos.TOP_CENTER);
        centred.setStyle("-fx-background-color: #F4F4F8;");

        setContent(centred);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setStyle("-fx-background-color: #F4F4F8; -fx-background: #F4F4F8;");
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private VBox buildContent() {
        VBox root = new VBox(18);
        root.setMaxWidth(860);
        root.setPadding(new Insets(36, 44, 36, 44));
        root.setStyle("-fx-background-color: #F4F4F8;");

        // ── Header ────────────────────────────────────────────────────────────
        Label heading = new Label("How was your experience?");
        heading.setFont(Font.font("System", FontWeight.BOLD, 28));
        heading.setStyle("-fx-text-fill: #1A1A2E;");

        Label subheading = new Label("Your feedback helps us serve you better  ✦");
        subheading.setFont(Font.font(14));
        subheading.setStyle("-fx-text-fill: #9E9E9E;");

        VBox header = new VBox(4, heading, subheading);

        // ── Rating card ───────────────────────────────────────────────────────
        Label ratingTitle = new Label("Overall Rating");
        ratingTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        ratingTitle.setStyle("-fx-text-fill: #444;");

        StarRating     stars     = new StarRating(state);
        EmoticonRating emoticons = new EmoticonRating(state);

        VBox ratingCard = card(ratingTitle, stars, emoticons);

        // ── Dish carousel card ────────────────────────────────────────────────
        VBox dishCard = card(new DishCarousel(state));

        // ── Tag selector card ─────────────────────────────────────────────────
        VBox tagCard = card(new TagSelector(state));

        // ── Progress bar ──────────────────────────────────────────────────────
        ProgressBarComponent progressBar = new ProgressBarComponent(state);

        // ── Comment button ────────────────────────────────────────────────────
        CommentPopup commentPopup = new CommentPopup(state);
        Button commentBtn = new Button("💬   Leave a Comment");
        commentBtn.setFont(Font.font("System", FontWeight.NORMAL, 13));
        commentBtn.setStyle(commentBtnStyle(false));
        commentBtn.setOnAction(e -> commentPopup.show());

        // Indicate when a comment has been saved
        state.commentProperty().addListener((obs, o, n) -> {
            boolean hasComment = !n.isBlank();
            commentBtn.setText(hasComment ? "💬   Edit Comment  ✓" : "💬   Leave a Comment");
            commentBtn.setStyle(commentBtnStyle(hasComment));
        });

        // ── Reward banner ─────────────────────────────────────────────────────
        RewardBanner rewardBanner = new RewardBanner(state);

        // ── Submit button ─────────────────────────────────────────────────────
        Button submitBtn = new Button("Submit Review");
        submitBtn.setMaxWidth(Double.MAX_VALUE);
        submitBtn.setFont(Font.font("System", FontWeight.BOLD, 17));
        submitBtn.setStyle(submitDisabled());
        submitBtn.setDisable(true);

        state.progressProperty().addListener((obs, o, n) -> {
            boolean ready = n.doubleValue() >= 100;
            submitBtn.setDisable(!ready);
            submitBtn.setStyle(ready ? submitEnabled() : submitDisabled());
        });

        submitBtn.setOnAction(e -> onSubmit.run());

        // ── Assemble ──────────────────────────────────────────────────────────
        root.getChildren().addAll(
            header,
            ratingCard,
            dishCard,
            tagCard,
            progressBar,
            commentBtn,
            rewardBanner,
            submitBtn
        );

        return root;
    }

    // ── Card factory ──────────────────────────────────────────────────────────

    /** Wrap one or more JavaFX nodes inside a rounded, shadowed card. */
    private VBox card(javafx.scene.Node... nodes) {
        VBox c = new VBox(12);
        c.setPadding(new Insets(22));
        c.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 18; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3);"
        );
        c.getChildren().addAll(nodes);
        return c;
    }

    // ── Style strings ─────────────────────────────────────────────────────────

    private String commentBtnStyle(boolean active) {
        return active
            ? "-fx-background-color: #FFF3EE; -fx-text-fill: #FF6B35; " +
              "-fx-border-color: #FF6B35; -fx-border-radius: 10; -fx-border-width: 1.5; " +
              "-fx-background-radius: 10; -fx-padding: 11 22 11 22; -fx-cursor: hand;"
            : "-fx-background-color: white; -fx-text-fill: #FF6B35; " +
              "-fx-border-color: #FF6B35; -fx-border-radius: 10; -fx-border-width: 1.5; " +
              "-fx-background-radius: 10; -fx-padding: 11 22 11 22; -fx-cursor: hand;";
    }

    private String submitEnabled() {
        return "-fx-background-color: #FF6B35; -fx-text-fill: white; " +
               "-fx-background-radius: 14; -fx-padding: 16 0 16 0; -fx-cursor: hand; " +
               "-fx-effect: dropshadow(gaussian, rgba(255,107,53,0.45), 12, 0, 0, 4);";
    }

    private String submitDisabled() {
        return "-fx-background-color: #D0D0D0; -fx-text-fill: white; " +
               "-fx-background-radius: 14; -fx-padding: 16 0 16 0; -fx-cursor: default;";
    }
}
