package application.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import application.CommandResult;
import application.MealMeter;
import application.command.AddReviewCommand;
import application.command.Command;
import application.exception.InvalidArgumentException;
import application.review.Review;
import application.review.ReviewList;

/**
 * Main GUI window for MealMeter. Coordinates between patron and owner panels,
 * forwarding all events to the backend via MealMeter.handleInput().
 *
 * <p>The GUI contains no business logic. All operations are expressed as
 * command strings passed to the backend (MVC controller layer).</p>
 */
public class MealMeterGui extends JFrame implements
        PatronPanel.PatronPanelListener, OwnerPanel.OwnerPanelListener {

    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 750;
    private static final int PATRON_TAB_INDEX = 0;
    private static final int OWNER_TAB_INDEX = 1;

    /** Single backend entry point — no Storage, AuthManager or ReviewList held directly. */
    private final MealMeter mealMeter;

    /** Current subset shown in the owner table (may differ from the master list after filter/sort). */
    private ReviewList currentDisplayList;

    private final JTabbedPane tabbedPane;
    private final PatronPanel patronPanel;
    private final OwnerPanel ownerPanel;

    /**
     * Constructs and displays the MealMeterGui window.
     */
    public MealMeterGui() {
        this.mealMeter = new MealMeter();
        this.currentDisplayList = mealMeter.getReviewList();

        setTitle("MealMeter - Restaurant Feedback System");
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
                    && !mealMeter.isOwnerAuthenticated()) {
                promptOwnerLogin();
            }
        });

        add(tabbedPane);

        // Show any storage warnings from startup
        for (String warning : mealMeter.getStartupStorageWarnings()) {
            JOptionPane.showMessageDialog(null, warning, "Storage Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (mealMeter.hasStorageLoadFailure()) {
            JOptionPane.showMessageDialog(null,
                    "Could not load saved reviews. Starting with an empty list.",
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }

        setVisible(true);
    }

    // ── PatronPanelListener ─────────────────────────────────────────────────

    @Override
    public String onReviewSubmitted(String body, double food, double clean,
                                    double service, String tagsAsString) {
        Command command = new AddReviewCommand(body, food, clean, service, tagsAsString);
        CommandResult result = mealMeter.handleInput(command);

        // Auto-refresh owner table if logged in
        if (mealMeter.isOwnerAuthenticated()) {
            currentDisplayList = mealMeter.getReviewList();
            ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        }

        return result.output();
    }

    // ── OwnerPanelListener ──────────────────────────────────────────────────

    @Override
    public void onFilterApplied(String includeTags, String excludeTags, String status,
                                double minRating, String conditions) {
        String cmd = buildFilterCommand(includeTags, excludeTags, status, minRating, conditions);
        CommandResult result = mealMeter.handleInput();

        JOptionPane.showMessageDialog(this, result.output(), "Filter Applied",
                JOptionPane.INFORMATION_MESSAGE);

        currentDisplayList = mealMeter.filterReviews(includeTags, excludeTags, status,
                minRating, conditions);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onSortApplied(String sortBy, String sortOrder) {
        String criterionArg = mapSortByToCriterionArg(sortBy);
        String orderArg = sortOrder.toLowerCase();
        CommandResult result = mealMeter.handleInput("sort " + orderArg + " /by " + criterionArg);

        JOptionPane.showMessageDialog(this, result.output(), "Sort Applied",
                JOptionPane.INFORMATION_MESSAGE);

        currentDisplayList = mealMeter.sortReviews(sortBy, sortOrder, currentDisplayList);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onResolveReview(int rowIndex) {
        int masterIdx = mealMeter.getMasterIndex(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }
        CommandResult result = mealMeter.handleInput("resolve " + masterIdx);
        JOptionPane.showMessageDialog(this, result.output(), "Resolve",
                JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onUnresolveReview(int rowIndex) {
        int masterIdx = mealMeter.getMasterIndex(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }
        CommandResult result = mealMeter.handleInput("unresolve " + masterIdx);
        JOptionPane.showMessageDialog(this, result.output(), "Unresolve",
                JOptionPane.INFORMATION_MESSAGE);
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onTagReview(int rowIndex) {
        try {
            Review review = currentDisplayList.getReview(rowIndex);
            String currentTags = review.getTags().stream()
                    .map(t -> t.getTagName())
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

            int masterIdx = mealMeter.getMasterIndex(currentDisplayList, rowIndex);
            if (masterIdx < 0) {
                return;
            }

            String trimmed = input.trim();
            CommandResult result;
            if (trimmed.startsWith("-")) {
                String tagName = trimmed.substring(1).trim();
                result = mealMeter.handleInput("deletetag " + masterIdx + " /tag " + tagName);
            } else {
                result = mealMeter.handleInput("addtag " + masterIdx + " /tag " + trimmed);
            }

            JOptionPane.showMessageDialog(this, result.output(), "Tags",
                    JOptionPane.INFORMATION_MESSAGE);
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

        int masterIdx = mealMeter.getMasterIndex(currentDisplayList, rowIndex);
        if (masterIdx < 0) {
            return;
        }

        CommandResult result = mealMeter.handleInput("delete " + masterIdx);
        JOptionPane.showMessageDialog(this, result.output(), "Delete",
                JOptionPane.INFORMATION_MESSAGE);
        currentDisplayList = mealMeter.getReviewList();
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
    }

    @Override
    public void onRefresh() {
        currentDisplayList = mealMeter.getReviewList();
        ownerPanel.refreshTable(currentDisplayList.getAllReviews());
        JOptionPane.showMessageDialog(this, "Refreshed.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onLogout() {
        CommandResult result = mealMeter.handleInput("logout");
        JOptionPane.showMessageDialog(this, result.output(), "Logout",
                JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(PATRON_TAB_INDEX);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void promptOwnerLogin() {
        JPasswordField pwField = new JPasswordField(15);
        Object[] msg = {"Owner Password:", pwField};
        int option = JOptionPane.showConfirmDialog(this, msg, "Owner Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String entered = new String(pwField.getPassword());
            CommandResult result = mealMeter.handleInput("login " + entered);
            if (mealMeter.isOwnerAuthenticated()) {
                ownerPanel.refreshTable(currentDisplayList.getAllReviews());
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
     * Builds the filter command string from the panel's form inputs.
     */
    private String buildFilterCommand(String includeTags, String excludeTags, String status,
                                      double minRating, String conditions) {
        StringBuilder cmd = new StringBuilder("filter");

        if (!includeTags.isEmpty()) {
            cmd.append(" /hastag ").append(includeTags);
        }
        if (!excludeTags.isEmpty()) {
            cmd.append(" /notag ").append(excludeTags);
        }
        if ("Resolved".equals(status)) {
            cmd.append(" /resolved true");
        } else if ("Outstanding".equals(status)) {
            cmd.append(" /resolved false");
        }

        List<String> condParts = new ArrayList<>();
        if (minRating > 1.0) {
            condParts.add(String.format("overall >= %.1f", minRating));
        }
        if (!conditions.isEmpty()) {
            condParts.add(conditions);
        }
        if (!condParts.isEmpty()) {
            cmd.append(" /condition ").append(String.join(", ", condParts));
        }

        return cmd.toString();
    }

    private String mapSortByToCriterionArg(String sortBy) {
        switch (sortBy) {
        case "Food":
            return "food";
        case "Cleanliness":
            return "clean";
        case "Service":
            return "service";
        case "Tag Count":
            return "tag";
        default:
            return "overall";
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

    /**
     * Launches the MealMeter GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGui::new);
    }
}
