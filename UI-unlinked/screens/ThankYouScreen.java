package screens;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import models.FeedbackState;
import utils.TimerUtil;

/**
 * Thank-you screen shown after a successful submission.
 *
 * Features:
 *   • Animated "10% OFF" reward tile
 *   • 30-second auto-return countdown
 *   • "Submit Another Review" button for instant reset
 *
 * Navigation is handled via a Runnable callback so this class has no
 * direct dependency on AppNavigator (unnamed package).
 */
public class ThankYouScreen extends VBox {

    private final FeedbackState state;
    private final Runnable      onReset;
    private final TimerUtil     timer = new TimerUtil(30);
    private final Label         countdownLbl;

    public ThankYouScreen(FeedbackState state, Runnable onReset) {
        this.state   = state;
        this.onReset = onReset;

        setAlignment(Pos.CENTER);
        setSpacing(28);
        setPadding(new Insets(60, 80, 60, 80));
        setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #FF6B35 0%, #FF8E53 60%, #FFA07A 100%);"
        );

        // ── Check + title ─────────────────────────────────────────────────────
        Label check = new Label("✅");
        check.setFont(Font.font(72));

        Label titleLbl = new Label("Thank you for visiting!");
        titleLbl.setFont(Font.font("System", FontWeight.BOLD, 34));
        titleLbl.setStyle("-fx-text-fill: white;");

        Label rewardLbl = new Label("Your reward has been unlocked  🎉");
        rewardLbl.setFont(Font.font("System", FontWeight.BOLD, 18));
        rewardLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.92);");

        // ── Discount tile ─────────────────────────────────────────────────────
        VBox discountTile = buildDiscountTile();

        // ── Countdown ─────────────────────────────────────────────────────────
        countdownLbl = new Label("Returning to kiosk in 30s…");
        countdownLbl.setFont(Font.font(13));
        countdownLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.70);");

        timer.secondsLeftProperty().addListener((obs, o, n) ->
            countdownLbl.setText("Returning to kiosk in " + n.intValue() + "s…")
        );

        timer.setOnFinish(() -> {
            state.reset();
            onReset.run();
        });

        // ── Another review button ─────────────────────────────────────────────
        Button anotherBtn = new Button("Submit Another Review");
        anotherBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        anotherBtn.setStyle(
            "-fx-background-color: white; -fx-text-fill: #FF6B35; " +
            "-fx-background-radius: 14; -fx-padding: 14 36 14 36; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        );
        anotherBtn.setOnAction(e -> {
            timer.stop();
            state.reset();
            onReset.run();
        });

        getChildren().addAll(check, titleLbl, rewardLbl, discountTile, countdownLbl, anotherBtn);
    }

    /** Call this each time the screen is shown to restart the countdown. */
    public void startTimer() {
        timer.start();
    }

    // ── Discount tile ─────────────────────────────────────────────────────────

    private VBox buildDiscountTile() {
        Label pctLbl = new Label("10% OFF");
        pctLbl.setFont(Font.font("System", FontWeight.BOLD, 52));
        pctLbl.setStyle("-fx-text-fill: white;");

        Label descLbl = new Label("your next visit");
        descLbl.setFont(Font.font("System", FontWeight.NORMAL, 17));
        descLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.88);");

        Label codeLbl = new Label("Code: THANKS10");
        codeLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        codeLbl.setPadding(new Insets(6, 20, 6, 20));
        codeLbl.setStyle(
            "-fx-text-fill: #FF6B35; " +
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-letter-spacing: 2;"
        );

        VBox tile = new VBox(10, pctLbl, descLbl, codeLbl);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(28, 52, 28, 52));
        tile.setStyle(
            "-fx-background-color: rgba(255,255,255,0.20); " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(255,255,255,0.35); " +
            "-fx-border-radius: 20; " +
            "-fx-border-width: 1.5;"
        );
        return tile;
    }
}
