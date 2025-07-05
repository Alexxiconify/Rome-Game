package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private GameEngine engine;
    private JTextArea infoArea;
    private JLabel dateLabel;
    private JLabel treasuryLabel;
    private JLabel stabilityLabel;
    
    public InfoPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(300, 600));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createTitledBorder("Country Information"));
    }
    
    private void createComponents() {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(50, 50, 50));
        infoArea.setForeground(Color.WHITE);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        dateLabel = new JLabel("Date: ");
        dateLabel.setForeground(Color.WHITE);
        
        treasuryLabel = new JLabel("Treasury: ");
        treasuryLabel.setForeground(Color.WHITE);
        
        stabilityLabel = new JLabel("Stability: ");
        stabilityLabel.setForeground(Color.WHITE);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Top panel for basic info
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.add(dateLabel);
        topPanel.add(treasuryLabel);
        topPanel.add(stabilityLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(infoArea), BorderLayout.CENTER);
    }
    
    public void updateInfo() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            dateLabel.setText("Date: " + engine.getCurrentDate().getFormattedDate());
            treasuryLabel.setText(String.format("Treasury: %.1f", playerCountry.getTreasury()));
            stabilityLabel.setText(String.format("Stability: %.1f", playerCountry.getStability()));
            
            updateDetailedInfo(playerCountry);
        }
    }
    
    private void updateDetailedInfo(Country country) {
        StringBuilder sb = new StringBuilder();
        sb.append("Country: ").append(country.getName()).append("\n");
        sb.append("Government: ").append(country.getGovernmentType()).append("\n");
        sb.append("Religion: ").append(country.getReligion()).append("\n");
        sb.append("Culture: ").append(country.getCulture()).append("\n");
        sb.append("Prestige: ").append(String.format("%.1f", country.getPrestige())).append("\n");
        sb.append("Legitimacy: ").append(String.format("%.1f", country.getLegitimacy())).append("\n");
        sb.append("Income: ").append(String.format("%.1f", country.getIncome())).append("\n");
        sb.append("Expenses: ").append(String.format("%.1f", country.getExpenses())).append("\n");
        sb.append("Provinces: ").append(country.getProvinces().size()).append("\n");
        
        sb.append("\nMilitary:\n");
        for (var entry : country.getMilitary().entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        sb.append("\nResources:\n");
        for (var entry : country.getResources().entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(String.format("%.1f", entry.getValue())).append("\n");
        }
        
        sb.append("\nIdeas:\n");
        for (String idea : country.getIdeas()) {
            sb.append("  ").append(idea).append("\n");
        }
        
        // Add colonization missions info
        var colonizationManager = engine.getColonizationManager();
        var activeMissions = colonizationManager.getActiveMissions();
        
        if (!activeMissions.isEmpty()) {
            sb.append("\nActive Colonization Missions:\n");
            for (var mission : activeMissions) {
                if (mission.getCountryName().equals(country.getName())) {
                    sb.append("  ").append(mission.getProvinceId()).append(": ")
                      .append(String.format("%.1f%%", mission.getProgress() * 100))
                      .append(" (").append(mission.getColonists()).append(" colonists)\n");
                }
            }
        }
        
        infoArea.setText(sb.toString());
    }
} 