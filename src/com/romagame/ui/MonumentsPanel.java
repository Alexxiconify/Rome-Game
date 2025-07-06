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
        // Create historical world monuments
        monuments.put("Colosseum", new WorldMonument(
            "Colosseum", 
            "The great amphitheater of Rome, symbol of imperial power and entertainment.",
            "Rome, Italy",
            WorldMonument.MonumentType.WONDER,
            1000,
            "Built by Emperor Vespasian and completed by Titus in 80 AD"
        ));
        
        monuments.put("Pantheon", new WorldMonument(
            "Pantheon",
            "The temple to all gods, a masterpiece of Roman architecture.",
            "Rome, Italy",
            WorldMonument.MonumentType.TEMPLE,
            800,
            "Built by Emperor Hadrian between 118-125 AD"
        ));
        
        monuments.put("Hadrian's Wall", new WorldMonument(
            "Hadrian's Wall",
            "The great defensive wall marking the northern frontier of the Roman Empire.",
            "Northern Britain",
            WorldMonument.MonumentType.FORTRESS,
            1200,
            "Built by Emperor Hadrian from 122-128 AD"
        ));
        
        monuments.put("Persepolis", new WorldMonument(
            "Persepolis",
            "The ceremonial capital of the Achaemenid Empire.",
            "Persia",
            WorldMonument.MonumentType.PALACE,
            1500,
            "Built by Darius I and his successors"
        ));
        
        monuments.put("Hanging Gardens", new WorldMonument(
            "Hanging Gardens",
            "One of the Seven Wonders of the Ancient World.",
            "Babylon",
            WorldMonument.MonumentType.WONDER,
            2000,
            "Legendary gardens built by Nebuchadnezzar II"
        ));
        
        monuments.put("Pyramids", new WorldMonument(
            "Pyramids",
            "The great pyramids of Giza, tombs of the pharaohs.",
            "Giza, Egypt",
            WorldMonument.MonumentType.WONDER,
            3000,
            "Built during the Old Kingdom period"
        ));
        
        monuments.put("Great Wall", new WorldMonument(
            "Great Wall",
            "The massive defensive wall protecting China's northern frontier.",
            "Northern China",
            WorldMonument.MonumentType.FORTRESS,
            2500,
            "Built and expanded over centuries by various dynasties"
        ));
        
        monuments.put("Stonehenge", new WorldMonument(
            "Stonehenge",
            "The mysterious stone circle of ancient Britain.",
            "Wiltshire, England",
            WorldMonument.MonumentType.TEMPLE,
            600,
            "Built in multiple phases from 3000-2000 BC"
        ));
        
        monuments.put("Parthenon", new WorldMonument(
            "Parthenon",
            "The temple of Athena on the Acropolis of Athens.",
            "Athens, Greece",
            WorldMonument.MonumentType.TEMPLE,
            1000,
            "Built in the 5th century BC under Pericles"
        ));
        
        monuments.put("Temple of Artemis", new WorldMonument(
            "Temple of Artemis",
            "One of the Seven Wonders of the Ancient World.",
            "Ephesus, Asia Minor",
            WorldMonument.MonumentType.TEMPLE,
            1200,
            "Built and rebuilt multiple times"
        ));
        
        monuments.put("Lighthouse of Alexandria", new WorldMonument(
            "Lighthouse of Alexandria",
            "The great lighthouse guiding ships to Alexandria.",
            "Alexandria, Egypt",
            WorldMonument.MonumentType.INFRASTRUCTURE,
            1500,
            "Built by Ptolemy I and completed by Ptolemy II"
        ));
        
        monuments.put("Mausoleum", new WorldMonument(
            "Mausoleum",
            "The tomb of Mausolus, one of the Seven Wonders.",
            "Halicarnassus, Caria",
            WorldMonument.MonumentType.WONDER,
            1000,
            "Built by Artemisia II in memory of her husband"
        ));
        
        monuments.put("Temple of Solomon", new WorldMonument(
            "Temple of Solomon",
            "The first temple in Jerusalem, center of Jewish worship.",
            "Jerusalem",
            WorldMonument.MonumentType.TEMPLE,
            800,
            "Built by King Solomon in the 10th century BC"
        ));
        
        monuments.put("Petra", new WorldMonument(
            "Petra",
            "The rose-red city carved into the rock.",
            "Jordan",
            WorldMonument.MonumentType.CULTURAL,
            1000,
            "Capital of the Nabataean Kingdom"
        ));
        
        monuments.put("Mohenjo-daro", new WorldMonument(
            "Mohenjo-daro",
            "The great city of the Indus Valley Civilization.",
            "Indus Valley",
            WorldMonument.MonumentType.INFRASTRUCTURE,
            1200,
            "One of the largest cities of the ancient world"
        ));
        
        monuments.put("Angkor Wat", new WorldMonument(
            "Angkor Wat",
            "The temple complex of the Khmer Empire.",
            "Cambodia",
            WorldMonument.MonumentType.TEMPLE,
            2000,
            "Built by King Suryavarman II in the 12th century"
        ));
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
                    currentMonument.advanceConstruction(50); // Advance by 50 points
                    updateMonumentDetails();
                    
                    if (currentMonument.isCompleted()) {
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
                details.append("Status: ").append(currentMonument.isCompleted() ? "Completed" : "Not Built").append("\n\n");
                details.append("Description:\n");
                details.append(currentMonument.getDescription()).append("\n\n");
                details.append("Historical Context:\n");
                details.append(currentMonument.getHistoricalContext()).append("\n\n");
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
                constructionProgress.setValue((int) currentMonument.getCompletionPercentage());
                constructionProgress.setString(String.format("%.1f%%", currentMonument.getCompletionPercentage()));
                
                // Update button states
                startConstructionButton.setEnabled(currentMonument.getOwner() == null);
                advanceConstructionButton.setEnabled(currentMonument.getOwner() != null && !currentMonument.isCompleted());
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