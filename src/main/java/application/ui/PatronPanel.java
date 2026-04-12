package application.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Patron feedback submission panel for MealMeterController GUI.
 * Handles UI rendering only; delegates all business logic to the controller.
 */
public class PatronPanel extends JPanel {
    private static final Color FOREST_DARK = new Color(34, 139, 91);
    private static final Color FOREST_MID = new Color(0, 188, 145);
    private static final Color FOREST_LIGHT = new Color(144, 238, 144);
    private static final Color BG_WHITE = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);

    private final JSpinner foodSpinner;
    private final JSpinner cleanSpinner;
    private final JSpinner serviceSpinner;
    private final JTextArea reviewTextArea;
    private final JTextField tagInputField;
    private final JLabel tagsLabel;
    private String pendingTagsAsString;
    private final PatronPanelListener listener;

    /**
     * Constructs a PatronPanel with the specified listener for events.
     *
     * @param listener the listener for patron panel events
     */
    public PatronPanel(PatronPanelListener listener) {
        this.listener = listener;
        this.pendingTagsAsString = "";
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel mainPanel = new GradientPanel(FOREST_DARK, FOREST_MID, FOREST_LIGHT);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Submit Your Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Help us improve your dining experience");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 255, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        this.foodSpinner = createRatingSpinner();
        this.cleanSpinner = createRatingSpinner();
        this.serviceSpinner = createRatingSpinner();
        contentPanel.add(createModernCard(createRatingPanel(), "Rate Your Experience"));
        contentPanel.add(Box.createVerticalStrut(20));

        this.reviewTextArea = createReviewTextArea();
        contentPanel.add(createModernCard(createReviewPanel(), "Your Review"));
        contentPanel.add(Box.createVerticalStrut(20));

        this.tagInputField = new JTextField(15);
        tagInputField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagInputField.setBorder(new EmptyBorder(8, 10, 8, 10));
        tagInputField.setBackground(BG_WHITE);

        this.tagsLabel = new JLabel("No tags added yet");
        tagsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagsLabel.setForeground(TEXT_DARK);

        contentPanel.add(createModernCard(createTagsPanel(), "Tags (Optional)"));
        contentPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton submitButton = GuiComponentFactory.createGradientButton(
                "Submit Review", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 45));
        submitButton.addActionListener(e -> handleSubmit());

        JButton clearButton = GuiComponentFactory.createGradientButton(
                "Clear Form", new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        clearButton.setPreferredSize(new Dimension(160, 45));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private JSpinner createRatingSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(3.0, 1.0, 5.0, 0.5));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.0"));
        spinner.setOpaque(false);
        return spinner;
    }

    private JPanel createRatingPanel() {
        JPanel panel = new JPanel(new java.awt.GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);

        String[] labels = {"Food Quality", "Cleanliness", "Service"};
        JSpinner[] spinners = {foodSpinner, cleanSpinner, serviceSpinner};

        for (int i = 0; i < labels.length; i++) {
            JPanel item = new JPanel(new BorderLayout(10, 5));
            item.setOpaque(false);
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXT_DARK);
            item.add(lbl, BorderLayout.WEST);
            item.add(spinners[i], BorderLayout.CENTER);
            panel.add(item);
        }
        return panel;
    }

    private JTextArea createReviewTextArea() {
        JTextArea textArea = new JTextArea(4, 40);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setForeground(TEXT_DARK);
        textArea.setBackground(BG_WHITE);
        textArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        return textArea;
    }

    private JPanel createReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JScrollPane sp = new JScrollPane(reviewTextArea);
        sp.setBorder(new LineBorder(BORDER_LIGHT, 1));
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTagsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JButton addTagButton = GuiComponentFactory.createGradientButton(
                "Add", FOREST_MID, FOREST_LIGHT, Color.WHITE);
        addTagButton.setPreferredSize(new Dimension(80, 35));
        addTagButton.addActionListener(e -> {
            String tag = tagInputField.getText().trim();
            if (!tag.isEmpty()) {
                if (pendingTagsAsString.isEmpty()) {
                    pendingTagsAsString = tag;
                } else {
                    pendingTagsAsString += (", " + tag);
                }
                tagInputField.setText("");
                updateTagsLabel();
            }
        });

        panel.add(tagInputField, BorderLayout.CENTER);
        panel.add(addTagButton, BorderLayout.EAST);
        panel.add(tagsLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void updateTagsLabel() {
        if (pendingTagsAsString.isEmpty()) {
            tagsLabel.setText("No tags added yet");
        } else {
            tagsLabel.setText("Tags: " + pendingTagsAsString);
        }
    }

    private JPanel createModernCard(JPanel content, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(BORDER_LIGHT, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(FOREST_DARK);
        titleLabel.setBorder(new EmptyBorder(15, 15, 10, 15));

        card.add(titleLabel, BorderLayout.NORTH);
        content.setBorder(new EmptyBorder(0, 15, 15, 15));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void handleSubmit() {
        double food = ((Number) foodSpinner.getValue()).doubleValue();
        double clean = ((Number) cleanSpinner.getValue()).doubleValue();
        double service = ((Number) serviceSpinner.getValue()).doubleValue();
        String body = reviewTextArea.getText().trim();

        if (body.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a review before submitting.",
                    "Missing Review", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String result = listener.onReviewSubmitted(body, food, clean, service,
                pendingTagsAsString);

        if (result != null && !result.isEmpty()) {
            JOptionPane.showMessageDialog(this, result, "Submit Review",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        clearForm();
    }

    private void clearForm() {
        foodSpinner.setValue(3.0);
        cleanSpinner.setValue(3.0);
        serviceSpinner.setValue(3.0);
        reviewTextArea.setText("");
        tagInputField.setText("");
        pendingTagsAsString = "";
        updateTagsLabel();
    }

    /**
     * Panel gradient background painter for theme colors.
     */
    static class GradientPanel extends JPanel {
        private final Color c1;
        private final Color c2;
        private final Color c3;

        GradientPanel(Color c1, Color c2, Color c3) {
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp1 = new GradientPaint(0, 0, c1, getWidth() / 2f, getHeight() / 2f, c2);
            g2.setPaint(gp1);
            g2.fillRect(0, 0, getWidth(), getHeight());
            GradientPaint gp2 = new GradientPaint(getWidth() / 2f, getHeight() / 2f, c2,
                    getWidth(), getHeight(), c3);
            g2.setPaint(gp2);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }


}
