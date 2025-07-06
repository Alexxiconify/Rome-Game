package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.monuments.WorldMonument;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MonumentsPanel extends JPanel {
    private GameEngine engine;
    private JList<String> monumentList;
    private DefaultListModel<String> monumentListModel;
    private JTextArea monumentDetails;
    private JProgressBar constructionProgress;
    private JButton startConstructionButton;
    private JButton advanceConstructionButton;
    private WorldMonument currentMonument;
    private Map<String, WorldMonument> monuments;
    
    public MonumentsPanel(GameEngine engine) {
        this.engine = engine;
        this.monuments = new HashMap<>();
        setupPanel();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        initializeMonuments();
        updatePanel();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(139, 69, 19));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            "🏛️ World Monuments",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Monument list
        monumentListModel = new DefaultListModel<>();
        monumentList = new JList<>(monumentListModel);
        monumentList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        monumentList.setBackground(new Color(205, 133, 63));
        monumentList.setForeground(new Color(25, 25, 112));
        monumentList.setSelectionBackground(new Color(255, 215, 0));
        monumentList.setSelectionForeground(new Color(25, 25, 112));
        
        // Monument details
        monumentDetails = new JTextArea();
        monumentDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        monumentDetails.setBackground(new Color(245, 222, 179));
        monumentDetails.setForeground(new Color(25, 25, 112));
        monumentDetails.setEditable(false);
        monumentDetails.setLineWrap(true);
        monumentDetails.setWrapStyleWord(true);
        
        // Construction progress
        constructionProgress = new JProgressBar(0, 100);
        constructionProgress.setStringPainted(true);
        constructionProgress.setFont(new Font("Times New Roman", Font.BOLD, 12));
        constructionProgress.setBackground(new Color(205, 133, 63));
        constructionProgress.setForeground(new Color(255, 215, 0));
        
        // Buttons
        startConstructionButton = createStyledButton("Start Construction", new Color(100, 200, 100));
        advanceConstructionButton = createStyledButton("Advance Construction", new Color(200, 200, 100));
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
        // Left panel for monument list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(139, 69, 19));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Monuments",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(monumentList);
        listScrollPane.getViewport().setBackground(new Color(205, 133, 63));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Right panel for details and controls
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(139, 69, 19));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Monument Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(monumentDetails);
        detailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        controlsPanel.setBackground(new Color(139, 69, 19));
        controlsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Construction",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        controlsPanel.add(constructionProgress);
        controlsPanel.add(startConstructionButton);
        controlsPanel.add(advanceConstructionButton);
        controlsPanel.add(new JLabel("")); // Empty space
        
        rightPanel.add(detailsPanel, BorderLayout.CENTER);
        rightPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        // Main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        monumentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateMonumentDetails();
            }
        });
        
        startConstructionButton.addActionListener(e -> startConstruction());
        advanceConstructionButton.addActionListener(e -> advanceConstruction());
    }
    
    private void initializeMonuments() {
        // Create historical world monuments using factory methods
        monuments.put("Colosseum", WorldMonument.createColosseum());
        monuments.put("Parthenon", WorldMonument.createParthenon());
        monuments.put("Great Wall", WorldMonument.createGreatWall());
        monuments.put("Hanging Gardens", WorldMonument.createHangingGardens());
        monuments.put("Pyramids", WorldMonument.createPyramids());
        monuments.put("Stonehenge", WorldMonument.createStonehenge());
        monuments.put("Persepolis", WorldMonument.createPersepolis());
        monuments.put("Theater of Dionysus", WorldMonument.createTheaterOfDionysus());
    }
    
    private void startConstruction() {
        if (currentMonument != null) {
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null) {
                double cost = currentMonument.getConstructionCost() * 0.1; // 10% initial cost
                if (playerCountry.getTreasury() >= cost) {
                    playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                    currentMonument.startConstruction(playerCountry.getName());
                    updateMonumentDetails();
                    JOptionPane.showMessageDialog(this, 
                        "Started construction of " + currentMonument.getName() + " for " + String.format("%.1f", cost) + " gold!",
                        "Construction Started",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient funds! Cost: " + String.format("%.1f", cost) + " gold",
                        "Insufficient Funds",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void advanceConstruction() {
        if (currentMonument != null && currentMonument.getOwner() != null) {
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null && playerCountry.getName().equals(currentMonument.getOwner())) {
                double cost = currentMonument.getConstructionCost() * 0.05; // 5% progress cost
                if (playerCountry.getTreasury() >= cost) {
                    playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                    currentMonument.updateConstruction(50); // Advance by 50 points
                    updateMonumentDetails();
                    
                    if (currentMonument.isBuilt()) {
                        JOptionPane.showMessageDialog(this, 
                            "Construction of " + currentMonument.getName() + " completed!",
                            "Construction Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Advanced construction of " + currentMonument.getName() + " for " + String.format("%.1f", cost) + " gold!",
                            "Construction Advanced",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient funds! Cost: " + String.format("%.1f", cost) + " gold",
                        "Insufficient Funds",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void updateMonumentDetails() {
        int selectedIndex = monumentList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedMonument = monumentList.getSelectedValue();
            currentMonument = monuments.get(selectedMonument);
            
            if (currentMonument != null) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(currentMonument.getName()).append("\n");
                details.append("Type: ").append(currentMonument.getType()).append("\n");
                details.append("Location: ").append(currentMonument.getLocation()).append("\n");
                details.append("Construction Cost: ").append(currentMonument.getConstructionCost()).append(" points\n");
                details.append("Owner: ").append(currentMonument.getOwner() != null ? currentMonument.getOwner() : "None").append("\n");
                details.append("Status: ").append(currentMonument.isBuilt() ? "Completed" : "Not Built").append("\n\n");
                details.append("Description:\n");
                details.append(currentMonument.getDescription()).append("\n\n");
                details.append("Location: ").append(currentMonument.getLocation()).append("\n\n");
                details.append("Effects (when completed):\n");
                
                Map<String, Double> effects = currentMonument.getEffects();
                for (Map.Entry<String, Double> effect : effects.entrySet()) {
                    details.append("- ").append(effect.getKey()).append(": ").append(effect.getValue()).append("\n");
                }
                
                details.append("\nBuffed Nations:\n");
                for (var nationType : currentMonument.getBuffedNations()) {
                    details.append("- ").append(nationType).append("\n");
                }
                
                monumentDetails.setText(details.toString());
                
                // Update progress bar
                constructionProgress.setValue((int) currentMonument.getProgressPercentage());
                constructionProgress.setString(String.format("%.1f%%", currentMonument.getProgressPercentage()));
                
                // Update button states
                startConstructionButton.setEnabled(currentMonument.getOwner() == null);
                advanceConstructionButton.setEnabled(currentMonument.getOwner() != null && !currentMonument.isBuilt());
            }
        }
    }
    
    public void updatePanel() {
        monumentListModel.clear();
        
        for (WorldMonument monument : monuments.values()) {
            monumentListModel.addElement(monument.getName());
        }
        
        if (monumentListModel.size() > 0) {
            monumentList.setSelectedIndex(0);
            updateMonumentDetails();
        }
    }
} 