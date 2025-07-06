package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.country.AIManager;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AIStatusPanel extends JPanel {
    private GameEngine engine;
    private JTextArea aiStatusArea;
    private JScrollPane scrollPane;
    private JLabel titleLabel;
    
    public AIStatusPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    private void setupPanel() {
        setBorder(BorderFactory.createTitledBorder("AI Nations Status"));
        setPreferredSize(new Dimension(300, 400));
    }
    
    private void createComponents() {
        titleLabel = new JLabel("AI Nations Activity");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        aiStatusArea = new JTextArea();
        aiStatusArea.setEditable(false);
        aiStatusArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        aiStatusArea.setLineWrap(true);
        aiStatusArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(aiStatusArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void updatePanel() {
        StringBuilder sb = new StringBuilder();
        String playerCountry = engine.getCountryManager().getPlayerCountry().getName();
        AIManager aiManager = engine.getCountryManager().getAIManager();
        
        List<Country> allCountries = engine.getAllCountries();
        int aiCount = 0;
        
        for (Country country : allCountries) {
            if (!country.getName().equals(playerCountry) && 
                !country.getName().equals("Ocean") && 
                !country.getName().equals("Uncolonized")) {
                
                aiCount++;
                sb.append("=== ").append(country.getName()).append(" ===\n");
                sb.append("Personality: ").append(aiManager.getPersonality(country.getName())).append("\n");
                sb.append("Treasury: ").append(String.format("%.1f", country.getTreasury())).append("\n");
                sb.append("Stability: ").append(String.format("%.2f", country.getStability())).append("\n");
                sb.append("Prestige: ").append(String.format("%.1f", country.getPrestige())).append("\n");
                
                // Show military strength
                sb.append("Military: ");
                boolean first = true;
                for (var entry : country.getMilitary().entrySet()) {
                    if (entry.getValue() > 0) {
                        if (!first) sb.append(", ");
                        sb.append(entry.getKey()).append(": ").append(entry.getValue());
                        first = false;
                    }
                }
                if (first) sb.append("None");
                sb.append("\n");
                
                // Show trade goods
                sb.append("Trade Goods: ");
                first = true;
                for (var entry : country.getGoods().entrySet()) {
                    if (entry.getValue() > 0) {
                        if (!first) sb.append(", ");
                        sb.append(entry.getKey()).append(": ").append(entry.getValue());
                        first = false;
                    }
                }
                if (first) sb.append("None");
                sb.append("\n\n");
                
                // Limit display to prevent overwhelming
                if (aiCount >= 8) {
                    sb.append("... and ").append(allCountries.size() - aiCount - 1).append(" more AI nations\n");
                    break;
                }
            }
        }
        
        if (aiCount == 0) {
            sb.append("No AI nations found.\n");
        }
        
        aiStatusArea.setText(sb.toString());
    }
} 