package application;

import javax.swing.SwingUtilities;

import application.ui.MealMeterGui;

/**
 * Launches the MealMeterController GUI.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MealMeterGui::new);
    }
}
