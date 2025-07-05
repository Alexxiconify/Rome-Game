//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package com.romagame;

import com.romagame.core.GameEngine;
import com.romagame.ui.GameWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();
            GameWindow window = new GameWindow(engine);
            window.setVisible(true);
            engine.start();
        });
    }
}