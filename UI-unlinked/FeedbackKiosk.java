import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lightweight localised feedback kiosk for a restaurant cashier station.
 * Designed to capture quick ratings and free-form comments before guests leave.
 */
public class FeedbackKiosk extends JFrame {
    private final JSlider satisfactionSlider;
    private final JTextArea commentsArea;
    private final JLabel statusLabel;

    public FeedbackKiosk() {
        super("Restaurant Feedback");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        satisfactionSlider = new JSlider(0, 10, 8);
        satisfactionSlider.setMajorTickSpacing(1);
        satisfactionSlider.setPaintTicks(true);
        satisfactionSlider.setPaintLabels(true);

        commentsArea = new JTextArea(5, 30);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        statusLabel = new JLabel("Ready for new feedback.");

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createFormPanel(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Share quick feedback", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subtitle = new JLabel("Helps us improve your next visit.", SwingConstants.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 14f));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(createRatingSection());
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(createQuickTagsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(createCommentsSection());
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(createSubmitSection());

        return panel;
    }

    private JPanel createRatingSection() {
        JPanel ratingWrapper = new JPanel(new BorderLayout());

        JLabel ratingLabel = new JLabel("How did we do?");
        ratingLabel.setFont(ratingLabel.getFont().deriveFont(Font.BOLD, 16f));
        ratingWrapper.add(ratingLabel, BorderLayout.NORTH);

        ratingWrapper.add(satisfactionSlider, BorderLayout.CENTER);
        return ratingWrapper;
    }

    private JPanel createQuickTagsSection() {
        JPanel tagWrapper = new JPanel(new BorderLayout());
        tagWrapper.setBorder(new EmptyBorder(8, 0, 0, 0));

        JLabel tagLabel = new JLabel("Select one or more quick tags");
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.PLAIN, 14f));
        tagWrapper.add(tagLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));
        String[] tags = {"Fast service", "Friendly host", "Tasty food", "Clean space", "Good value", "Will return"};
        for (String tag : tags) {
            JButton tagButton = new JButton(tag);
            tagButton.setBackground(new Color(255, 255, 255));
            tagButton.addActionListener(new TagToggleListener(tagButton));
            buttonPanel.add(tagButton);
        }

        tagWrapper.add(buttonPanel, BorderLayout.CENTER);
        return tagWrapper;
    }

    private JPanel createCommentsSection() {
        JPanel commentsWrapper = new JPanel(new BorderLayout());
        JLabel commentsLabel = new JLabel("Optional comments");
        commentsLabel.setFont(commentsLabel.getFont().deriveFont(Font.PLAIN, 14f));

        commentsWrapper.add(commentsLabel, BorderLayout.NORTH);
        commentsWrapper.add(new JScrollPane(commentsArea), BorderLayout.CENTER);
        return commentsWrapper;
    }

    private JPanel createSubmitSection() {
        JPanel submitPanel = new JPanel(new BorderLayout());

        JButton submit = new JButton("Submit Feedback");
        submit.setFont(submit.getFont().deriveFont(Font.BOLD, 14f));
        submit.setBackground(new Color(0, 120, 215));
        submit.setForeground(Color.WHITE);
        submit.setFocusPainted(false);
        submit.addActionListener(new SubmitListener());

        submitPanel.add(submit, BorderLayout.CENTER);
        submitPanel.add(statusLabel, BorderLayout.SOUTH);
        return submitPanel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new GridLayout(0, 1));
        footer.setBorder(new EmptyBorder(8, 0, 0, 0));
        JLabel footerText = new JLabel("Tap submit when you're ready—no login needed.", SwingConstants.CENTER);
        footerText.setFont(footerText.getFont().deriveFont(Font.ITALIC, 12f));
        footer.add(footerText);
        return footer;
    }

    private class TagToggleListener implements ActionListener {
        private final JButton button;
        private boolean selected;

        TagToggleListener(JButton button) {
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selected = !selected;
            button.setBackground(selected ? new Color(0, 150, 136) : Color.WHITE);
            button.setForeground(selected ? Color.WHITE : Color.BLACK);
        }
    }

    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int rating = satisfactionSlider.getValue();
            String comments = commentsArea.getText().trim();
            statusLabel.setText("Feedback captured at " + java.time.LocalTime.now().withNano(0));
            JOptionPane.showMessageDialog(
                FeedbackKiosk.this,
                buildSummary(rating, comments),
                "Thank you", JOptionPane.INFORMATION_MESSAGE
            );
            resetForm();
        }

        private String buildSummary(int rating, String comments) {
            String summary = "Rating: " + rating + "/10";
            if (!comments.isEmpty()) {
                summary += "\nComments: " + comments;
            }
            summary += "\nWe appreciate your input.";
            return summary;
        }

        private void resetForm() {
            satisfactionSlider.setValue(8);
            commentsArea.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FeedbackKiosk kiosk = new FeedbackKiosk();
            kiosk.setVisible(true);
        });
    }
}
