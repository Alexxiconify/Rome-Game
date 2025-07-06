//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import com.romagame.core.GameEngine;
import com.romagame.ui.GameWindow;
import com.romagame.ui.NationSelectionDialog;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();
            
            // Show nation selection dialog first
            GameWindow window = new GameWindow(engine);
            NationSelectionDialog dialog = new NationSelectionDialog(window, engine);
            dialog.setVisible(true);
            
            // Check if a nation was selected
            if (dialog.getSelectedCountry() != null) {
                window.setVisible(true);
                // Center map on selected nation
                window.centerOnPlayerRegion();
                engine.start();
            } else {
                // No nation selected, exit
                System.exit(0);
            }
        });
    }
}