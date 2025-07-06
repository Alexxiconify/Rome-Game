package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame {
    private GameEngine engine;
    private MapPanel mapPanel;
    private InfoPanel infoPanel;
    private ControlPanel controlPanel;
    
    public GameWindow(GameEngine engine) {
        this.engine = engine;
        setupWindow();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        setupGameEngineCallback();
    }
    
    private void setupWindow() {
        setTitle("Roma Game - Grand Strategy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void createComponents() {
        mapPanel = new MapPanel(engine);
        infoPanel = new InfoPanel(engine);
        controlPanel = new ControlPanel(engine);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Main map area
        add(mapPanel, BorderLayout.CENTER);
        
        // Right panel for country info
        add(infoPanel, BorderLayout.EAST);
        
        // Bottom panel for controls
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Add keyboard shortcuts
        getRootPane().registerKeyboardAction(
            e -> engine.setGameSpeed(com.romagame.core.GameSpeed.PAUSED),
            "Pause",
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> engine.setGameSpeed(com.romagame.core.GameSpeed.NORMAL),
            "Normal Speed",
            KeyStroke.getKeyStroke(KeyEvent.VK_1, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> engine.setGameSpeed(com.romagame.core.GameSpeed.FAST),
            "Fast Speed",
            KeyStroke.getKeyStroke(KeyEvent.VK_2, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> engine.setGameSpeed(com.romagame.core.GameSpeed.VERY_FAST),
            "Very Fast Speed",
            KeyStroke.getKeyStroke(KeyEvent.VK_3, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void setupGameEngineCallback() {
        // Set up the UI update callback for the game engine
        engine.setUIUpdateCallback(engineInstance -> updateUI());
    }
    
    public void updateUI() {
        mapPanel.repaint();
        infoPanel.updateInfo();
        controlPanel.updateControls();
    }
} 