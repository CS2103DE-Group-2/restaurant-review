package application.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import application.auth.AuthManager;
import application.condition.Condition;
import application.exception.InvalidArgumentException;
import application.exception.MissingArgumentException;
import application.parser.ConditionParser;
import application.review.Criterion;
import application.review.Review;
import application.review.ReviewList;
import application.review.SortOrder;
import application.review.Tag;
import application.storage.Storage;

/**
 * Main GUI window for MealMeter. Coordinates between patron and owner panels,
 * delegates UI events to backend operations.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class MealMeterGUI extends JFrame implements
        PatronPanel.PatronPanelListener, OwnerPanel.OwnerPanelListener {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;
    private static final String OWNER_PASSWORD = "password";
    private static final String TITLE = "MealMeter - Restaurant Feedback System";
    private static final int PATRON_TAB_INDEX = 0;
    private static final int OWNER_TAB_INDEX = 1;

    private final Storage storage;
    private final AuthManager authManager;
    private final ReviewList reviewList;
    private ReviewList currentDisplayList;
    private final JTabbedPane tabbedPane;
    private final PatronPanel patronPanel;
    private final OwnerPanel ownerPanel;

    /**
     * Constructs and displays the MealMeterGUI window.
     */
    public MealMeterGUI() {
        this.storage = new Storage();
        this.authManager = new AuthManager(OWNER_PASSWORD);

        ReviewList loaded = new ReviewList();
        try {
            loaded = storage.loadReviews();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not load saved reviews: " + e.getMessage()
                    + "\nStarting with an empty list.",
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }
        this.reviewList = loaded;
        this.currentDisplayList = reviewList;

        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);

        this.tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new CustomTabbedPaneUI());

        this.patronPanel = new PatronPanel(this);
        this.ownerPanel = new OwnerPanel(this);

        tabbedPane.addTab("Patron Feedback", patronPanel);
        tabbedPane.addTab("Owner Management", ownerPanel);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == OWNER_TAB_INDEX
                    && !authManager.isOwnerAuthenticated()) {
                promptOwnerLogin();
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    @Override
    public void onReviewSubmitted(Review review) {
        reviewList.addReview(review);
        saveOrWarn();

        if (authManager.isOwnerAuthenticated()) {
            currentDisplayList = reviewList;
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        }
    }

    @Override
    public void onFilterApplied(String includeTags, String excludeTags, String status,
                                double minRating, String conditions) {
        try {
            Set<Tag> includeSet = includeTags.isEmpty() ? new HashSet<>() : Tag.toTags(includeTags);
            Set<Tag> excludeSet = excludeTags.isEmpty() ? new HashSet<>() : Tag.toTags(excludeTags);

            Boolean isResolved = null;
            if ("Resolved".equals(status)) {
                isResolved = true;
            } else if ("Outstanding".equals(status)) {
                isResolved = false;
            }

            Set<Condition> conditionSet = new HashSet<>();
            if (minRating > 1.0) {
                conditionSet.add(new application.condition.GreaterThanOrEqualsToCondition(
                        Criterion.OVERALL_SCORE, minRating));
            }

            if (!conditions.isEmpty()) {
                conditionSet.addAll(ConditionParser.getConditions(conditions));
            }

            currentDisplayList = reviewList.filter(includeSet, excludeSet, conditionSet, isResolved);
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());

            JOptionPane.showMessageDialog(this,
                    "Filter applied. Showing " + currentDisplayList.size() + " review(s).",
                    "Filter", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException | MissingArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Filter error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onSortApplied(String sortBy, String sortOrder) {
        try {
            Criterion criterion = mapSortByCriterion(sortBy);
            SortOrder order = "Descending".equals(sortOrder) ? SortOrder.DESCENDING : SortOrder.ASCENDING;

            currentDisplayList = reviewList.sort(criterion, order, currentDisplayList);
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());

            JOptionPane.showMessageDialog(this,
                    "Sorted by " + sortBy + " (" + sortOrder + ").",
                    "Sort Applied", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Sort error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onResolveReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            review.markResolved();
            saveOrWarn();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
            JOptionPane.showMessageDialog(this, "Marked as Resolved.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onUnresolveReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            review.markOutstanding();
            saveOrWarn();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
            JOptionPane.showMessageDialog(this, "Marked as Outstanding.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onTagReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            String currentTags = review.getTags().stream()
                    .map(Tag::getTagName)
                    .sorted()
                    .collect(Collectors.joining(", "));

            String prompt = "Current tags: " + (currentTags.isEmpty() ? "none" : currentTags)
                    + "\n\nEnter a tag name to ADD it."
                    + "\nPrefix with '-' to REMOVE (e.g. -spicy).";
            String input = JOptionPane.showInputDialog(this, prompt, "Manage Tags",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            String trimmed = input.trim();
            if (trimmed.startsWith("-")) {
                String tagName = trimmed.substring(1).trim();
                review.removeTag(new Tag(tagName));
                JOptionPane.showMessageDialog(this, "Tag removed.", "Done",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                review.addTag(new Tag(trimmed));
                JOptionPane.showMessageDialog(this, "Tag added.", "Done",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            saveOrWarn();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDeleteReview(int rowIndex) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete this review?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Review toDelete = currentDisplayList.getReview(rowIndex);
            int masterIdx = masterIndexOf(toDelete);
            if (masterIdx == -1) {
                JOptionPane.showMessageDialog(this, "Review not found in master list.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            reviewList.deleteReview(masterIdx);

            List<Review> remaining = currentDisplayList.getAllReviews().stream()
                    .filter(r -> r != toDelete)
                    .collect(Collectors.toList());
            currentDisplayList = new ReviewList(remaining);

            saveOrWarn();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
            JOptionPane.showMessageDialog(this, "Review deleted.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onRefresh() {
        currentDisplayList = reviewList;
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        JOptionPane.showMessageDialog(this, "Refreshed.", "Done",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onLogout() {
        authManager.logout();
        JOptionPane.showMessageDialog(this, "You have been logged out.", "Logout Successful",
                JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
    }

    private void promptOwnerLogin() {
        JPasswordField pwField = new JPasswordField(15);
        Object[] msg = {"Owner Password:", pwField};
        int result = JOptionPane.showConfirmDialog(this, msg, "Owner Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String entered = new String(pwField.getPassword());
            if (authManager.authenticateOwner(entered)) {
                ownerPanel.refreshTable(currentDisplayList.getAllReviews());
                JOptionPane.showMessageDialog(this, "Welcome, Owner!", "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password.", "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
            }
        } else {
            tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
        }
    }

    private void saveOrWarn() {
        try {
            storage.saveReviews(reviewList);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save reviews: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int masterIndexOf(Review displayed) {
        List<Review> all = reviewList.getAllReviews();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i) == displayed) {
                return i + 1;
            }
        }
        return -1;
    }

    private Criterion mapSortByCriterion(String sortBy) {
        switch (sortBy) {
        case "Food":
            return Criterion.FOOD_SCORE;
        case "Cleanliness":
            return Criterion.CLEANLINESS_SCORE;
        case "Service":
            return Criterion.SERVICE_SCORE;
        case "Tag Count":
            return Criterion.TAG_COUNT;
        default:
            return Criterion.OVERALL_SCORE;
        }
    }

    /**
     * Custom tabbed pane UI for highlight styling.
     */
    static class CustomTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void paintTabBackground(java.awt.Graphics g, int tabPlacement, int tabIndex,
                int x, int y, int w, int h, boolean isSelected) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(java.awt.Color.WHITE);
                g2.fillRoundRect(x, y, w, h + 3, 8, 8);
            }
        }
    }

    /**
     * Launches the MealMeter GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGUI::new);
    }
}
