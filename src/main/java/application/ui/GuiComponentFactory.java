package application.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Factory for creating styled GUI components consistently across panels.
 */
public class GuiComponentFactory {
    private static final Color TEXT_DARK = new Color(33, 33, 33);

    private GuiComponentFactory() {
        // Utility class - no instantiation
    }

    /**
     * Creates a gradient button with the specified colors.
     *
     * @param text the button text
     * @param c1 the gradient start color
     * @param c2 the gradient end color
     * @param textColor the text color
     * @return a styled button
     */
    public static JButton createGradientButton(String text, Color c1, Color c2, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Creates a labeled field panel.
     *
     * @param label the label text
     * @param field the component
     * @return a panel with label and field
     */
    public static JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DARK);
        lbl.setPreferredSize(new Dimension(100, 20));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a labeled combo box panel.
     *
     * @param label the label text
     * @param combo the combo box
     * @return a panel with label and combo
     */
    public static JPanel createLabeledCombo(String label, JComboBox<String> combo) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_DARK);
        lbl.setPreferredSize(new Dimension(80, 20));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }
}
