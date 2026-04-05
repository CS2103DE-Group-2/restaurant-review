package application.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import application.review.Review;
import application.review.SortOrder;
import application.review.Tag;

/**
 * Owner management panel for MealMeter GUI.
 * Handles UI rendering and event delegation only;
 * business logic delegated to the listener.
 */
public class OwnerPanel extends JPanel {
    private static final Color OCEAN_DARK = new Color(13, 71, 161);
    private static final Color OCEAN_MID = new Color(3, 155, 229);
    private static final Color OCEAN_LIGHT = new Color(0, 188, 212);
    private static final Color BG_WHITE = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);

    private final JTextField includeTagsField;
    private final JTextField excludeTagsField;
    private final JComboBox<String> statusCombo;
    private final JSpinner minRatingSpinner;
    private final JTextField conditionsField;
    private JComboBox<String> sortByCombo;
    private JComboBox<String> sortOrderCombo;
    private final DefaultTableModel tableModel;
    private final JTable reviewsTable;
    private final OwnerPanelListener listener;

    /**
     * Constructs an OwnerPanel with the specified listener.
     *
     * @param listener the listener for owner panel events
     */
    public OwnerPanel(OwnerPanelListener listener) {
        this.listener = listener;
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setBackground(BG_WHITE);

        JPanel mainPanel = new GradientPanel(OCEAN_DARK, OCEAN_MID, OCEAN_LIGHT);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Review Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(BG_WHITE);
        contentPanel.setBorder(new LineBorder(BORDER_LIGHT, 1));

        // Create filter panel
        this.includeTagsField = new JTextField(15);
        this.excludeTagsField = new JTextField(15);
        this.statusCombo = new JComboBox<>(new String[]{"All", "Resolved", "Outstanding"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        this.minRatingSpinner = new JSpinner(new SpinnerNumberModel(1.0, 1.0, 5.0, 0.5));
        this.conditionsField = new JTextField(30);
        conditionsField.setToolTipText(
                "e.g. food scores > 3.5, cleanliness scores >= 4, service scores == 5");

        contentPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Create reviews table
        String[] columns = {"#", "Overall", "Food", "Clean", "Service", "Status", "Tags", "Review"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        this.reviewsTable = new JTable(tableModel);
        reviewsTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reviewsTable.setRowHeight(28);
        reviewsTable.setGridColor(BORDER_LIGHT);
        reviewsTable.setSelectionBackground(new Color(200, 220, 255));
        reviewsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JPanel reviewsPanel = new JPanel(new BorderLayout(10, 10));
        reviewsPanel.setOpaque(false);
        reviewsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(reviewsTable);
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        reviewsPanel.add(scrollPane, BorderLayout.CENTER);
        reviewsPanel.add(createActionPanel(), BorderLayout.SOUTH);

        contentPanel.add(reviewsPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter row
        JPanel filterPanel = new JPanel(new GridLayout(3, 2, 15, 10));
        filterPanel.setOpaque(false);

        filterPanel.add(GuiComponentFactory.createLabeledField("Include Tags:", includeTagsField));
        filterPanel.add(GuiComponentFactory.createLabeledField("Exclude Tags:", excludeTagsField));
        filterPanel.add(GuiComponentFactory.createLabeledCombo("Status:", statusCombo));
        filterPanel.add(GuiComponentFactory.createLabeledField("Min Overall:", minRatingSpinner));
        filterPanel.add(GuiComponentFactory.createLabeledField("Conditions:", conditionsField));

        JButton applyFilterButton = GuiComponentFactory.createGradientButton(
                "Apply Filter", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applyFilterButton.setMaximumSize(new Dimension(150, 35));
        applyFilterButton.addActionListener(e -> listener.onFilterApplied(
                includeTagsField.getText().trim(),
                excludeTagsField.getText().trim(),
                (String) statusCombo.getSelectedItem(),
                ((Number) minRatingSpinner.getValue()).doubleValue(),
                conditionsField.getText().trim()));
        filterPanel.add(applyFilterButton);

        panel.add(filterPanel);
        panel.add(Box.createVerticalStrut(10));

        // Sort row
        JPanel sortPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        sortPanel.setOpaque(false);

        this.sortByCombo = new JComboBox<>(
                new String[]{"Overall", "Food", "Cleanliness", "Service", "Tag Count"});
        sortByCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortPanel.add(GuiComponentFactory.createLabeledCombo("Sort By:", sortByCombo));

        this.sortOrderCombo = new JComboBox<>(new String[]{"Ascending", "Descending"});
        sortOrderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortPanel.add(GuiComponentFactory.createLabeledCombo("Order:", sortOrderCombo));

        JButton applySortButton = GuiComponentFactory.createGradientButton(
                "Apply Sort", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        applySortButton.addActionListener(e -> listener.onSortApplied(
                (String) sortByCombo.getSelectedItem(),
                (String) sortOrderCombo.getSelectedItem()
        ));
        sortPanel.add(applySortButton);

        panel.add(sortPanel);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton resolveButton = GuiComponentFactory.createGradientButton(
                "Resolved", new Color(0, 188, 145), new Color(144, 238, 144), Color.WHITE);
        resolveButton.addActionListener(e -> {
            int row = reviewsTable.getSelectedRow();
            if (row >= 0) {
                listener.onResolveReview(row + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a review first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton unresolveButton = GuiComponentFactory.createGradientButton(
                "Outstanding", new Color(255, 152, 0), new Color(255, 193, 7), Color.WHITE);
        unresolveButton.addActionListener(e -> {
            int row = reviewsTable.getSelectedRow();
            if (row >= 0) {
                listener.onUnresolveReview(row + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a review first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton tagButton = GuiComponentFactory.createGradientButton(
                "Tags", OCEAN_MID, OCEAN_LIGHT, Color.WHITE);
        tagButton.addActionListener(e -> {
            int row = reviewsTable.getSelectedRow();
            if (row >= 0) {
                listener.onTagReview(row + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a review first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton deleteButton = GuiComponentFactory.createGradientButton(
                "Delete", new Color(229, 57, 53), new Color(244, 67, 54), Color.WHITE);
        deleteButton.addActionListener(e -> {
            int row = reviewsTable.getSelectedRow();
            if (row >= 0) {
                listener.onDeleteReview(row + 1);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a review first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton refreshButton = GuiComponentFactory.createGradientButton(
                "Refresh", OCEAN_DARK, OCEAN_MID, Color.WHITE);
        refreshButton.addActionListener(e -> listener.onRefresh());

        JButton logoutButton = GuiComponentFactory.createGradientButton(
                "Logout", new Color(229, 57, 53), new Color(244, 67, 54), Color.WHITE);
        logoutButton.addActionListener(e -> listener.onLogout());

        actionPanel.add(resolveButton);
        actionPanel.add(unresolveButton);
        actionPanel.add(tagButton);
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(refreshButton);
        actionPanel.add(logoutButton);

        return actionPanel;
    }

    /**
     * Refreshes the reviews table with the given list.
     *
     * @param reviews the reviews to display
     */
    public void refreshTable(List<Review> reviews) {
        tableModel.setRowCount(0);
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            String tags = r.getTags().stream()
                    .map(Tag::getTagName)
                    .sorted()
                    .collect(Collectors.joining(", "));
            tableModel.addRow(new Object[]{
                i + 1,
                String.format("%.1f", r.getRating().getOverallScore()),
                String.format("%.1f", r.getRating().getFoodScore()),
                String.format("%.1f", r.getRating().getCleanlinessScore()),
                String.format("%.1f", r.getRating().getServiceScore()),
                r.isResolved() ? "Resolved" : "Outstanding",
                tags,
                r.getReviewBody()
            });
        }
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

    /**
     * Listener interface for owner panel events.
     */
    public interface OwnerPanelListener {
        /**
         * Called when filter is applied.
         */
        void onFilterApplied(String includeTags, String excludeTags, String status,
                             double minRating, String conditions);

        /**
         * Called when sort is applied.
         */
        void onSortApplied(String sortBy, String sortOrder);

        /**
         * Called when resolve action is requested.
         */
        void onResolveReview(int rowIndex);

        /**
         * Called when unresolve action is requested.
         */
        void onUnresolveReview(int rowIndex);

        /**
         * Called when tag action is requested.
         */
        void onTagReview(int rowIndex);

        /**
         * Called when delete action is requested.
         */
        void onDeleteReview(int rowIndex);

        /**
         * Called when refresh is requested.
         */
        void onRefresh();

        /**
         * Called when logout is requested.
         */
        void onLogout();
    }
}
