package components;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import models.FeedbackState;

/**
 * Modal popup for leaving a free-text comment.
 * Opens as a centred application-modal window; saves to FeedbackState on submit.
 */
public class CommentPopup {

    private final FeedbackState state;

    public CommentPopup(FeedbackState state) {
        this.state = state;
    }

    /** Open the popup. Blocks until the user closes it (showAndWait). */
    public void show() {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Leave a Comment");
        stage.setResizable(false);

        // ── Layout ────────────────────────────────────────────────────────────
        VBox root = new VBox(16);
        root.setPadding(new Insets(28, 32, 28, 32));
        root.setPrefWidth(420);
        root.setStyle("-fx-background-color: white;");

        Label title = new Label("Share more details");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #222;");

        Label subtitle = new Label("Your feedback helps us keep improving.");
        subtitle.setFont(Font.font(13));
        subtitle.setStyle("-fx-text-fill: #999;");

        TextArea textArea = new TextArea(state.getComment());
        textArea.setPromptText("Tell us what you loved — or what we can do better…");
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);
        textArea.setFont(Font.font(13));
        textArea.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        // Character counter
        Label counter = new Label("0 / 300");
        counter.setFont(Font.font(11));
        counter.setStyle("-fx-text-fill: #BBB;");

        textArea.textProperty().addListener((obs, o, n) -> {
            int len = Math.min(n.length(), 300);
            if (n.length() > 300) textArea.setText(n.substring(0, 300));
            counter.setText(len + " / 300");
        });

        // ── Buttons ───────────────────────────────────────────────────────────
        Button saveBtn = new Button("Save Comment");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        saveBtn.setStyle(
            "-fx-background-color: #FF6B35; -fx-text-fill: white; " +
            "-fx-background-radius: 10; -fx-padding: 12 0 12 0; -fx-cursor: hand;"
        );
        saveBtn.setOnAction(e -> {
            state.setComment(textArea.getText().trim());
            stage.close();
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setFont(Font.font(13));
        cancelBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #999; " +
            "-fx-cursor: hand; -fx-border-color: #DDD; -fx-border-radius: 10; " +
            "-fx-background-radius: 10; -fx-padding: 10 0 10 0;"
        );
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setOnAction(e -> stage.close());

        VBox btnCol = new VBox(8, saveBtn, cancelBtn);

        root.getChildren().addAll(title, subtitle, textArea, counter, btnCol);

        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.showAndWait();
    }
}
