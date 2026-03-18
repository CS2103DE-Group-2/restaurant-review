package utils;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.util.Duration;

/**
 * Countdown timer backed by a JavaFX Timeline.
 * Runs on the FX thread — safe to bind labels directly to secondsLeftProperty().
 */
public class TimerUtil {

    private final int totalSeconds;
    private final IntegerProperty secondsLeft = new SimpleIntegerProperty(0);
    private Timeline timeline;
    private Runnable onFinish;

    public TimerUtil(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    /** Called on the FX thread when the countdown reaches zero. */
    public void setOnFinish(Runnable callback) {
        this.onFinish = callback;
    }

    /** Restart the countdown from the beginning. */
    public void start() {
        stop();
        secondsLeft.set(totalSeconds);

        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                int left = secondsLeft.get() - 1;
                secondsLeft.set(left);
                if (left <= 0) {
                    stop();
                    if (onFinish != null) onFinish.run();
                }
            })
        );
        timeline.setCycleCount(totalSeconds);
        timeline.play();
    }

    /** Stop the countdown without triggering onFinish. */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public IntegerProperty secondsLeftProperty() { return secondsLeft; }
    public int getSecondsLeft()                  { return secondsLeft.get(); }
}
