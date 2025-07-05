package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.core.GameSpeed;
import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private GameEngine engine;
    private JButton pauseButton;
    private JButton slowButton;
    private JButton normalButton;
    private JButton fastButton;
    private JButton veryFastButton;
    private JButton recruitButton;
    private JButton buildButton;
    private JButton diplomacyButton;
    
    public ControlPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
        setupEventHandlers();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(1400, 100));
        setBackground(new Color(60, 60, 60));
        setBorder(BorderFactory.createTitledBorder("Game Controls"));
    }
    
    private void createComponents() {
        pauseButton = new JButton("⏸ Pause");
        slowButton = new JButton("🐌 Slow");
        normalButton = new JButton("▶ Normal");
        fastButton = new JButton("⚡ Fast");
        veryFastButton = new JButton("🚀 Very Fast");
        recruitButton = new JButton("⚔ Recruit");
        buildButton = new JButton("🏗 Build");
        diplomacyButton = new JButton("🤝 Diplomacy");
        
        // Style buttons
        styleButton(pauseButton, new Color(200, 100, 100));
        styleButton(slowButton, new Color(200, 150, 100));
        styleButton(normalButton, new Color(100, 200, 100));
        styleButton(fastButton, new Color(100, 150, 200));
        styleButton(veryFastButton, new Color(200, 100, 200));
        styleButton(recruitButton, new Color(150, 150, 200));
        styleButton(buildButton, new Color(200, 200, 150));
        styleButton(diplomacyButton, new Color(150, 200, 150));
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }
    
    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Speed controls
        add(new JLabel("Speed:"));
        add(pauseButton);
        add(slowButton);
        add(normalButton);
        add(fastButton);
        add(veryFastButton);
        
        // Action buttons
        add(new JLabel("Actions:"));
        add(recruitButton);
        add(buildButton);
        add(diplomacyButton);
    }
    
    private void setupEventHandlers() {
        pauseButton.addActionListener(e -> engine.setGameSpeed(GameSpeed.PAUSED));
        slowButton.addActionListener(e -> engine.setGameSpeed(GameSpeed.SLOW));
        normalButton.addActionListener(e -> engine.setGameSpeed(GameSpeed.NORMAL));
        fastButton.addActionListener(e -> engine.setGameSpeed(GameSpeed.FAST));
        veryFastButton.addActionListener(e -> engine.setGameSpeed(GameSpeed.VERY_FAST));
        
        recruitButton.addActionListener(e -> showRecruitDialog());
        buildButton.addActionListener(e -> showBuildDialog());
        diplomacyButton.addActionListener(e -> showDiplomacyDialog());
    }
    
    private void showRecruitDialog() {
        String[] options = {"Infantry", "Cavalry", "Artillery"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Choose unit type to recruit:",
            "Recruit Units",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != null) {
            String amountStr = JOptionPane.showInputDialog(this, "How many units?", "1");
            try {
                int amount = Integer.parseInt(amountStr);
                engine.getCountryManager().getPlayerCountry().recruitUnit(choice, amount);
                JOptionPane.showMessageDialog(this, "Recruited " + amount + " " + choice + " units!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!");
            }
        }
    }
    
    private void showBuildDialog() {
        String[] options = {"Market", "Barracks", "Temple", "Workshop"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Choose building to construct:",
            "Build",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != null) {
            JOptionPane.showMessageDialog(this, "Built " + choice + "!");
        }
    }
    
    private void showDiplomacyDialog() {
        String[] options = {"Send Gift", "Improve Relations", "Form Alliance", "Declare War"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Choose diplomatic action:",
            "Diplomacy",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice != null) {
            JOptionPane.showMessageDialog(this, "Diplomatic action: " + choice);
        }
    }
    
    public void updateControls() {
        // Update button states based on game state
        GameSpeed currentSpeed = engine.getGameSpeed();
        
        pauseButton.setEnabled(currentSpeed != GameSpeed.PAUSED);
        slowButton.setEnabled(currentSpeed != GameSpeed.SLOW);
        normalButton.setEnabled(currentSpeed != GameSpeed.NORMAL);
        fastButton.setEnabled(currentSpeed != GameSpeed.FAST);
        veryFastButton.setEnabled(currentSpeed != GameSpeed.VERY_FAST);
    }
} 