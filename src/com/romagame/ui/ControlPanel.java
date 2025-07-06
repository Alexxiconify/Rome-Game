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
        setPreferredSize(new Dimension(1600, 120));
        setBackground(new Color(139, 69, 19));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            "⚔ Imperium Controls",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
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
        
        // Style buttons with ancient Rome theme
        styleButton(pauseButton, new Color(205, 133, 63));
        styleButton(slowButton, new Color(210, 105, 30));
        styleButton(normalButton, new Color(160, 82, 45));
        styleButton(fastButton, new Color(139, 69, 19));
        styleButton(veryFastButton, new Color(101, 67, 33));
        styleButton(recruitButton, new Color(205, 133, 63));
        styleButton(buildButton, new Color(210, 105, 30));
        styleButton(diplomacyButton, new Color(160, 82, 45));
        styleButton(colonizeButton, new Color(139, 69, 19));
        
        // Store colonize button for later use
        this.colonizeButton = colonizeButton;
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(new Color(25, 25, 112));
        button.setFont(new Font("Times New Roman", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }
    
    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Speed controls
        JLabel speedLabel = new JLabel("⚡ Speed:");
        speedLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        speedLabel.setForeground(new Color(255, 215, 0));
        add(speedLabel);
        add(pauseButton);
        add(slowButton);
        add(normalButton);
        add(fastButton);
        add(veryFastButton);
        
        // Action buttons
        JLabel actionLabel = new JLabel("⚔ Actions:");
        actionLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        actionLabel.setForeground(new Color(255, 215, 0));
        add(actionLabel);
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
        var playerCountry = engine.getCountryManager().getPlayerCountry();
        var allCountries = engine.getCountryManager().getAllCountries();
        var diploManager = engine.getDiplomacyManager();
        String playerName = playerCountry.getName();
        
        // Fog of war: only show countries within 2000 km (real distance calculation)
        double fogDistance = 2000.0; // 2000 km visibility
        java.util.List<String> visibleCountries = new java.util.ArrayList<>();
        java.util.List<String> distantCountries = new java.util.ArrayList<>();
        
        for (var c : allCountries) {
            if (c == playerCountry) continue;
            
            // Calculate real distance between countries
            double distance = com.romagame.map.DistanceCalculator.calculateFogOfWarDistance(
                playerCountry, c, engine.getWorldMap()
            );
            
            if (distance >= 0 && distance <= fogDistance) {
                visibleCountries.add(c.getName());
            } else {
                distantCountries.add(c.getName());
            }
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Known Nations and Opinions:"));
        for (String country : visibleCountries) {
            double opinion = diploManager.getRelation(playerName, country);
            // Calculate real distance for opinion decay
            Country targetCountry = engine.getCountryManager().getCountry(country);
            double distance = com.romagame.map.DistanceCalculator.calculateFogOfWarDistance(
                playerCountry, targetCountry, engine.getWorldMap()
            );
            double decay = 1.0 - Math.min(1.0, distance / fogDistance) * 0.5; // up to 50% decay
            double shownOpinion = opinion * decay;
            String opinionStr = String.format("%.0f", shownOpinion);
            String status = (opinion >= 50) ? "Friendly" : (opinion >= 25) ? "Cordial" : (opinion >= 0) ? "Neutral" : (opinion >= -25) ? "Hostile" : "Hostile";
            boolean atWar = (opinion <= -100);
            JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
            row.add(new JLabel(country + " | Opinion: " + opinionStr + " (" + status + ") " + (atWar ? "[At War]" : "[At Peace]")));
            JButton warBtn = new JButton(atWar ? "At War" : "Declare War");
            warBtn.setEnabled(!atWar);
            warBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Declare war on " + country + "?", "Confirm War", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    diploManager.setRelation(playerName, country, -100);
                    JOptionPane.showMessageDialog(this, "You are now at war with " + country + "!");
                    showDiplomacyDialog(); // Refresh
                }
            });
            row.add(warBtn);
            panel.add(row);
        }
        // Distant nations (fogged)
        for (String country : distantCountries) {
            JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
            row.add(new JLabel(country + " | Opinion: Unknown (Fog of War) [Status: Unknown]"));
            JButton warBtn = new JButton("Declare War");
            warBtn.setEnabled(false);
            row.add(warBtn);
            panel.add(row);
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setPreferredSize(new java.awt.Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scroll, "Diplomacy", JOptionPane.PLAIN_MESSAGE);
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