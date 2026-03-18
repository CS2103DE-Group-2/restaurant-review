import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.FeedbackState;

/**
 * Application entry point.
 *
 * Wires together:
 *   FeedbackState  – shared reactive data model
 *   AppNavigator   – screen-switching coordinator
 *   Scene / Stage  – JavaFX window with optional CSS theme
 *
 * Compile & run instructions are in compile.sh.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        FeedbackState state = new FeedbackState();
        StackPane     root  = new StackPane();

        // Wire screens through the navigator.
        new AppNavigator(root, state);

        Scene scene = new Scene(root, 960, 740);

        // Load theme CSS if available on the classpath.
        try {
            String css = getClass().getResource("/styles/theme.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException ignored) {
            // CSS is optional; inline styles handle all visuals.
        }

        primaryStage.setTitle("Restaurant Feedback Kiosk");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(560);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
