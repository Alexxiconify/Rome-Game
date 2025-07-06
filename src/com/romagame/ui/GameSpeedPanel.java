package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.core.GameSpeed;
import javax.swing.*;
import java.awt.*;

public class GameSpeedPanel extends JPanel {
    private final GameEngine engine;
    private JButton pauseButton;
    private JButton playButton;
    private JButton fastButton;
    private JButton veryFastButton;
    private JLabel speedLabel;
    private JLabel fpsLabel;
    private javax.swing.Timer fpsTimer;
    
    public GameSpeedPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        startFPSTimer();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            "⚡ Game Speed Control",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        setBackground(new Color(139, 69, 19));
    }
    
    private void createComponents() {
        // Speed control buttons
        pauseButton = createStyledButton("⏸ Pause", new Color(200, 100, 100));
        playButton = createStyledButton("▶ Play", new Color(100, 200, 100));
        fastButton = createStyledButton("⏩ Fast", new Color(100, 150, 200));
        veryFastButton = createStyledButton("⏭ Very Fast", new Color(150, 100, 200));
        
        // Status labels
        speedLabel = new JLabel("Speed: Normal");
        speedLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        speedLabel.setForeground(new Color(255, 215, 0));
        
        fpsLabel = new JLabel("FPS: --");
        fpsLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        fpsLabel.setForeground(new Color(255, 215, 0));
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(new Color(25, 25, 112));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    private void layoutComponents() {
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setBackground(new Color(139, 69, 19));
        
        buttonPanel.add(pauseButton);
        buttonPanel.add(playButton);
        buttonPanel.add(fastButton);
        buttonPanel.add(veryFastButton);
        
        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(new Color(139, 69, 19));
        statusPanel.add(speedLabel);
        statusPanel.add(fpsLabel);
        
        add(buttonPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        pauseButton.addActionListener(e -> {
            engine.pause();
            updateSpeedLabel("Paused");
        });
        
        playButton.addActionListener(e -> {
            engine.resume();
            engine.setGameSpeed(GameSpeed.NORMAL);
            updateSpeedLabel("Normal");
        });
        
        fastButton.addActionListener(e -> {
            engine.resume();
            engine.setGameSpeed(GameSpeed.FAST);
            updateSpeedLabel("Fast");
        });
        
        veryFastButton.addActionListener(e -> {
            engine.resume();
            engine.setGameSpeed(GameSpeed.VERY_FAST);
            updateSpeedLabel("Very Fast");
        });
    }
    
    private void startFPSTimer() {
        fpsTimer = new javax.swing.Timer(1000, e -> updateFPSLabel());
        fpsTimer.start();
    }
    
    private void updateSpeedLabel(String speed) {
        SwingUtilities.invokeLater(() -> {
            speedLabel.setText("Speed: " + speed);
        });
    }
    
    private void updateFPSLabel() {
        SwingUtilities.invokeLater(() -> {
            if (engine.getGameThread() != null) {
                double fps = engine.getGameThread().getAverageFPS();
                fpsLabel.setText(String.format("FPS: %.1f", fps));
            } else {
                fpsLabel.setText("FPS: --");
            }
        });
    }
    
    public void updatePanel() {
        // Update panel state based on current game state
        GameSpeed currentSpeed = engine.getGameSpeed();
        boolean isRunning = engine.isRunning();
        
        SwingUtilities.invokeLater(() -> {
            pauseButton.setEnabled(isRunning);
            playButton.setEnabled(!isRunning || currentSpeed != GameSpeed.NORMAL);
            fastButton.setEnabled(!isRunning || currentSpeed != GameSpeed.FAST);
            veryFastButton.setEnabled(!isRunning || currentSpeed != GameSpeed.VERY_FAST);
            
            String speedText = switch (currentSpeed) {
                case PAUSED -> "Paused";
                case SLOW -> "Slow";
                case NORMAL -> "Normal";
                case FAST -> "Fast";
                case VERY_FAST -> "Very Fast";
            };
            updateSpeedLabel(speedText);
        });
    }
    
    public void stop() {
        if (fpsTimer != null) {
            fpsTimer.stop();
        }
    }
} 