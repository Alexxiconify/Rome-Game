package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.core.GameSpeed;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;

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
    private JButton colonizeButton;
    
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
        JButton colonizeButton = new JButton("🏴 Colonize");
        
        // Style buttons
        styleButton(pauseButton, new Color(200, 100, 100));
        styleButton(slowButton, new Color(200, 150, 100));
        styleButton(normalButton, new Color(100, 200, 100));
        styleButton(fastButton, new Color(100, 150, 200));
        styleButton(veryFastButton, new Color(200, 100, 200));
        styleButton(recruitButton, new Color(150, 150, 200));
        styleButton(buildButton, new Color(200, 200, 150));
        styleButton(diplomacyButton, new Color(150, 200, 150));
        styleButton(colonizeButton, new Color(200, 150, 200));
        
        // Store colonize button for later use
        this.colonizeButton = colonizeButton;
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
        add(colonizeButton);
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
        colonizeButton.addActionListener(e -> showColonizationDialog());
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
    
    private void showColonizationDialog() {
        // Get colonizable provinces
        List<String> colonizableProvinces = engine.getColonizationManager().getColonizableProvinces();
        
        if (colonizableProvinces.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No uncolonized provinces available!", "Colonization", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create province selection dialog
        String[] provinceArray = colonizableProvinces.toArray(new String[0]);
        String selectedProvince = (String) JOptionPane.showInputDialog(
            this,
            "Choose province to colonize:",
            "Colonization",
            JOptionPane.QUESTION_MESSAGE,
            null,
            provinceArray,
            provinceArray[0]
        );
        
        if (selectedProvince != null) {
            // Get colonist count
            String colonistStr = JOptionPane.showInputDialog(this, "How many colonists? (100-1000)", "500");
            try {
                int colonists = Integer.parseInt(colonistStr);
                if (colonists < 100 || colonists > 1000) {
                    JOptionPane.showMessageDialog(this, "Colonists must be between 100 and 1000!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if player has enough treasury
                Country playerCountry = engine.getCountryManager().getPlayerCountry();
                double cost = colonists * 0.1; // Cost per colonist
                
                if (playerCountry.getTreasury() < cost) {
                    JOptionPane.showMessageDialog(this, "Not enough treasury! Cost: " + String.format("%.1f", cost), "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Start colonization
                boolean success = engine.getColonizationManager().startColonization(
                    playerCountry.getName(), selectedProvince, colonists
                );
                
                if (success) {
                    playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                    JOptionPane.showMessageDialog(this, 
                        "Colonization started! Sending " + colonists + " colonists to " + selectedProvince + 
                        "\nCost: " + String.format("%.1f", cost),
                        "Colonization Started", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to start colonization!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
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