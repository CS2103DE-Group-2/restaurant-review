package application.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import application.command.CommandResult;
import application.controller.MealMeterController;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;

/**
 * Main GUI window for MealMeterController. Coordinates between patron and owner panels,
 * forwarding all events to the backend via MealMeterController.
 *
 * <p>The GUI contains no business logic. All operations are expressed as
 * command strings passed to the backend (MVC controller layer).</p>
 */
public class MealMeterGui extends JFrame implements
        PatronPanelListener, OwnerPanelListener {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;
    private static final int PATRON_TAB_INDEX = 0;
    private static final int OWNER_TAB_INDEX = 1;

    /** Single backend entry point — no Storage, AuthManager or ReviewList held directly. */
    private final MealMeterController mealMeterController;

    /** Current subset shown in the owner table (may differ from the master list after filter/sort). */
    private ReviewList currentDisplayList;

    private final JTabbedPane tabbedPane;
    private final PatronPanel patronPanel;
    private final OwnerPanel ownerPanel;

    /**
     * Constructs and displays the MealMeterGui window.
     */
    public MealMeterGui() {
        this.mealMeterController = new MealMeterController();
        this.currentDisplayList = mealMeterController.getReviewList();

        setTitle("MealMeterController - Restaurant Feedback System");
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
                    && !mealMeterController.isOwnerAuthenticated()) {
                promptOwnerLogin();
            }
        });

        add(tabbedPane);

        // Show any storage warnings from startup
        for (String warning : mealMeterController.getStartupStorageWarnings()) {
            JOptionPane.showMessageDialog(null, warning, "Storage Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (mealMeterController.hasStorageLoadFailure()) {
            JOptionPane.showMessageDialog(null,
                    "Could not load saved reviews. Starting with an empty list.",
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }

        setVisible(true);
    }

    // ── PatronPanelListener ─────────────────────────────────────────────────

    /**
     * Called when a review is submitted. Returns output message to display.
     *
     * @param body the review body text
     * @param food the food score
     * @param clean the cleanliness score
     * @param service the service score
     * @param tagsAsString the tags to add to the review, as a string
     * @return the output message from the controller
     */
    @Override
    public String onReviewSubmitted(String body, double food, double clean,
                                    double service, String tagsAsString) {
        CommandResult result = mealMeterController.submitReview(
                body, food, clean, service, tagsAsString);

        // Auto-refresh owner table if logged in
        if (mealMeterController.isOwnerAuthenticated()) {
            currentDisplayList = result.reviews();
            ownerPanel.refreshTable(currentDisplayList);
        }

        return result.output();
    }

    // ── OwnerPanelListener ──────────────────────────────────────────────────

    /**
     * Called when a filter is applied.
     *
     * @param includeTags the tags to include in the filter
     * @param excludeTags the tags to exclude from the filter
     * @param status the status of the reviews to include in the filter
     * @param conditions the conditions to filter by, e.g. "food > 3.5"
     */
    @Override
    public void onFilterApplied(String includeTags,
                                String excludeTags,
                                String status,
                                String conditions
    ) {
        CommandResult result = mealMeterController.filterReviews(
                includeTags, excludeTags, status, conditions);
        JOptionPane.showMessageDialog(this, result.output(), "Filter Applied",
                JOptionPane.INFORMATION_MESSAGE);

        currentDisplayList = result.reviews();
        ownerPanel.refreshTable(currentDisplayList);
    }

    /**
     * Called when a sort is applied.
     *
     * @param sortBy the criterion to sort by
     * @param sortOrder the order to sort in, either "Ascending" or "Descending"
     */
    @Override
    public void onSortApplied(String sortBy, String sortOrder) {
        CommandResult result = mealMeterController.sortReviews(sortBy, sortOrder);

        JOptionPane.showMessageDialog(this, result.output(), "Sort Applied",
                JOptionPane.INFORMATION_MESSAGE);

        currentDisplayList = result.reviews();
        ownerPanel.refreshTable(currentDisplayList);
    }

    /**
     * Called when a review is resolved.
     *
     * @param rowIndex the row index of the review to resolve
     */
    @Override
    public void onResolveReview(int rowIndex) {
        CommandResult result = mealMeterController.resolveReview(currentDisplayList, rowIndex);
        JOptionPane.showMessageDialog(this, result.output(), "Resolve",
                JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList);
    }

    /**
     * Called when a review is unresolved.
     *
     * @param rowIndex the row index of the review to unresolve
     */
    @Override
    public void onUnresolveReview(int rowIndex) {
        CommandResult result = mealMeterController.unresolveReview(currentDisplayList, rowIndex);
        JOptionPane.showMessageDialog(this, result.output(), "Unresolve",
                JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList);
    }

    /**
     * Called when a review is tagged.
     *
     * @param rowIndex the row index of the review to add tags to
     */
    @Override
    public void onAddTagReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            String currentTags = review.getTagsAsString();

            String prompt = "Current tags: " + (currentTags.isEmpty() ? "none" : currentTags)
                    + "\n\nEnter a tag name to add it, separated by commas.";
            String input = JOptionPane.showInputDialog(this, prompt, "Add Tags",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            String trimmed = input.trim();
            CommandResult result = mealMeterController.addTags(currentDisplayList, rowIndex, trimmed);

            JOptionPane.showMessageDialog(this, result.output(), "Tags",
                    JOptionPane.INFORMATION_MESSAGE);
            ownerPanel.refreshTable(currentDisplayList);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when a review is untagged.
     *
     * @param rowIndex the row index of the review to delete tags from
     */
    @Override
    public void onDeleteTagReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            String currentTags = review.getTagsAsString();

            String prompt = "Current tags: " + (currentTags.isEmpty() ? "none" : currentTags)
                    + "\n\nEnter a tag name to delete it, separated by commas.";
            String input = JOptionPane.showInputDialog(this, prompt, "Delete Tags",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            String trimmed = input.trim();
            CommandResult result = mealMeterController.deleteTags(currentDisplayList, rowIndex, trimmed);

            JOptionPane.showMessageDialog(this, result.output(), "Tags",
                    JOptionPane.INFORMATION_MESSAGE);
            ownerPanel.refreshTable(currentDisplayList);
        } catch (InvalidArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Called when a review is deleted.
     *
     * @param rowIndex the row index of the review to delete
     */
    @Override
    public void onDeleteReview(int rowIndex) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete this review?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        CommandResult result = mealMeterController.deleteReview(currentDisplayList, rowIndex);
        JOptionPane.showMessageDialog(this, result.output(), "Delete",
                JOptionPane.INFORMATION_MESSAGE);
        currentDisplayList = result.reviews();
        ownerPanel.refreshTable(currentDisplayList);
    }

    /**
     * Called when the refresh button is pressed.
     */
    @Override
    public void onRefresh() {
        currentDisplayList = mealMeterController.getReviewList();
        ownerPanel.refreshTable(currentDisplayList);
        JOptionPane.showMessageDialog(this, "Refreshed.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Called when the logout button is pressed.
     */
    @Override
    public void onLogout() {
        CommandResult result = mealMeterController.logout();
        JOptionPane.showMessageDialog(this, result.output(), "Logout",
                JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
        currentDisplayList = new ReviewList();
        ownerPanel.refreshTable(currentDisplayList);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void promptOwnerLogin() {
        JPasswordField pwField = new JPasswordField(15);
        Object[] msg = {"Owner Password:", pwField};
        int option = JOptionPane.showConfirmDialog(this, msg, "Owner Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String entered = new String(pwField.getPassword());
            CommandResult result = mealMeterController.login(entered);

            if (mealMeterController.isOwnerAuthenticated()) {
                currentDisplayList = mealMeterController.getReviewList();
                ownerPanel.refreshTable(currentDisplayList);
                JOptionPane.showMessageDialog(this, result.output(), "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.output(), "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
            }
        } else {
            tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
        }
    }

    /**
     * Custom tabbed pane UI that highlights the selected tab.
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
}
