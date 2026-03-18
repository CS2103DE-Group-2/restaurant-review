import javafx.animation.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import models.FeedbackState;
import screens.FeedbackScreen;
import screens.ThankYouScreen;

/**
 * Manages transitions between the two application screens.
 *
 * Both screens are created once and reused for the lifetime of the app.
 * Screens communicate back via Runnable callbacks, avoiding any upward
 * import dependency (named packages cannot import the unnamed package).
 *
 * Transitions: 200 ms fade-out → swap content → 300 ms fade-in.
 */
public class AppNavigator {

    private final StackPane      root;
    private final FeedbackScreen feedbackScreen;
    private final ThankYouScreen thankYouScreen;

    public AppNavigator(StackPane root, FeedbackState state) {
        this.root = root;

        feedbackScreen = new FeedbackScreen(state, this::showThankYou);
        thankYouScreen = new ThankYouScreen(state, this::showFeedback);

        // Boot into the feedback screen without animation.
        root.getChildren().setAll(feedbackScreen);
    }

    // ── Public navigation API ─────────────────────────────────────────────────

    public void showFeedback() {
        crossFade(feedbackScreen);
    }

    public void showThankYou() {
        crossFade(thankYouScreen);
        thankYouScreen.startTimer();
    }

    // ── Fade transition ───────────────────────────────────────────────────────

    private void crossFade(javafx.scene.Node target) {
        FadeTransition out = new FadeTransition(Duration.millis(200), root);
        out.setFromValue(1.0);
        out.setToValue(0.0);
        out.setOnFinished(e -> {
            root.getChildren().setAll(target);
            FadeTransition in = new FadeTransition(Duration.millis(320), root);
            in.setFromValue(0.0);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }
}
