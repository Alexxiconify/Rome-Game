package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.diplomacy.War;
import com.romagame.diplomacy.DiplomaticRelation;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InfoPanel extends JPanel {
    private GameEngine engine;
    private JTextArea infoArea;
    private JScrollPane scrollPane;
    private JPanel bottomInfoPanel;
    private JLabel zoomLabel;
    private JLabel dateLabel;
    private JLabel speedLabel;
    private JLabel playingAsLabel;
    
    public InfoPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(350, 700));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createTitledBorder("Country Information"));
    }
    
    private void createComponents() {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(50, 50, 50));
        infoArea.setForeground(Color.WHITE);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(infoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        bottomInfoPanel = new JPanel();
        bottomInfoPanel.setLayout(new BoxLayout(bottomInfoPanel, BoxLayout.Y_AXIS));
        zoomLabel = new JLabel();
        dateLabel = new JLabel();
        speedLabel = new JLabel();
        playingAsLabel = new JLabel();
        bottomInfoPanel.add(zoomLabel);
        bottomInfoPanel.add(dateLabel);
        bottomInfoPanel.add(speedLabel);
        bottomInfoPanel.add(playingAsLabel);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomInfoPanel, BorderLayout.SOUTH);
    }
    
    public void updateInfo() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            updateDetailedInfo(playerCountry);
        }
        // Update bottom info
        zoomLabel.setText("Zoom: 4x"); // Replace with actual zoom if available
        dateLabel.setText("Date: " + engine.getCurrentDate());
        speedLabel.setText("Speed: " + engine.getGameSpeed());
        Country player = engine.getCountryManager().getPlayerCountry();
        playingAsLabel.setText("Playing as: " + (player != null ? player.getName() : "None"));
    }
    
    private void updateDetailedInfo(Country country) {
        StringBuilder sb = new StringBuilder();
        
        // Header section
        sb.append("╔═══════════════════════════╗\n");
        sb.append("║                    ").append(country.getName().toUpperCase()).append("                    ║\n");
        sb.append("╚═══════════════════════════╝\n");
        
        // Basic Information
        sb.append("📅 DATE: ").append(engine.getCurrentDate().getFormattedDate()).append("\n");
        sb.append("🏛️  GOVERNMENT: ").append(country.getGovernmentType()).append("\n");
        sb.append("⛪ RELIGION: ").append(country.getReligion()).append("\n");
        sb.append("🎭 CULTURE: ").append(country.getCulture()).append("\n");
        sb.append("⭐ PRESTIGE: ").append(String.format("%.1f", country.getPrestige())).append("\n");
        sb.append("👑 LEGITIMACY: ").append(String.format("%.1f", country.getLegitimacy())).append("\n");
        sb.append("⚖️  STABILITY: ").append(String.format("%.1f", country.getStability())).append("\n");
        sb.append("🏛️  MILITARY TECH: Level ").append(country.getMilitaryTechLevel()).append("\n\n");
        
        // Economic Information
        sb.append("💰 ECONOMIC STATUS:\n");
        sb.append("   Treasury: ").append(String.format("%.1f", country.getTreasury())).append(" gold\n");
        sb.append("   Income: ").append(String.format("%.1f", country.getIncome())).append(" gold/month\n");
        sb.append("   Expenses: ").append(String.format("%.1f", country.getExpenses())).append(" gold/month\n");
        sb.append("   Net Income: ").append(String.format("%.1f", country.getIncome() - country.getExpenses())).append(" gold/month\n");
        sb.append("   Provinces: ").append(country.getProvinces().size()).append("\n\n");
        
        // Military Information
        sb.append("⚔️  MILITARY FORCES:\n");
        int totalTroops = 0;
        for (var entry : country.getMilitary().entrySet()) {
            sb.append("   ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            totalTroops += entry.getValue();
        }
        sb.append("   Total Forces: ").append(totalTroops).append(" men\n\n");
        
        // Resources
        sb.append("📦 RESOURCES:\n");
        for (var entry : country.getResources().entrySet()) {
            sb.append("   ").append(entry.getKey()).append(": ").append(String.format("%.1f", entry.getValue())).append("\n");
        }
        sb.append("\n");
        
        // Ideas
        if (!country.getIdeas().isEmpty()) {
            sb.append("💡 NATIONAL IDEAS:\n");
            for (String idea : country.getIdeas()) {
                sb.append("   • ").append(idea).append("\n");
            }
            sb.append("\n");
        }
        
        // Laws
        if (!country.getEnactedLaws().isEmpty()) {
            sb.append("📜 ACTIVE LAWS:\n");
            for (var law : country.getEnactedLaws()) {
                sb.append("   • ").append(law.getName()).append("\n");
            }
            sb.append("\n");
        }
        
        // Reforms
        if (!country.getImplementedReforms().isEmpty()) {
            sb.append("🔧 GOVERNMENT REFORMS:\n");
            for (var reform : country.getImplementedReforms()) {
                sb.append("   • ").append(reform.getName()).append("\n");
            }
            sb.append("\n");
        }
        
        // Technologies
        if (!country.getResearchedTechnologies().isEmpty()) {
            sb.append("🔬 TECHNOLOGIES:\n");
            for (String tech : country.getResearchedTechnologies()) {
                sb.append("   • ").append(tech).append("\n");
            }
            sb.append("\n");
        }
        
        // Diplomatic Relations
        DiplomacyManager diplomacyManager = engine.getDiplomacyManager();
        
        // Active Wars
        List<War> playerWars = diplomacyManager.getWarsInvolving(country.getName());
        if (!playerWars.isEmpty()) {
            sb.append("⚔️  ACTIVE WARS:\n");
            for (War war : playerWars) {
                String enemy = war.getAttacker().equals(country.getName()) ? war.getDefender() : war.getAttacker();
                sb.append("   ").append(enemy).append(" (Score: ").append(String.format("%.1f", war.getWarScore())).append(")\n");
                sb.append("   Duration: ").append(war.getDuration()).append(" months\n");
            }
            sb.append("\n");
        }
        
        // Alliances
        List<String> allies = diplomacyManager.getAllies(country.getName());
        if (!allies.isEmpty()) {
            sb.append("🤝 ALLIES:\n");
            for (String ally : allies) {
                double relation = diplomacyManager.getRelation(country.getName(), ally);
                sb.append("   ").append(ally).append(" (Opinion: ").append(String.format("%.1f", relation)).append(")\n");
            }
            sb.append("\n");
        }
        
        // Enemies
        List<String> enemies = diplomacyManager.getEnemies(country.getName());
        if (!enemies.isEmpty()) {
            sb.append("⚔️  ENEMIES:\n");
            for (String enemy : enemies) {
                double relation = diplomacyManager.getRelation(country.getName(), enemy);
                sb.append("   ").append(enemy).append(" (Opinion: ").append(String.format("%.1f", relation)).append(")\n");
            }
            sb.append("\n");
        }
        
        // Other Relations
        Map<String, DiplomaticRelation> relations = diplomacyManager.getRelations(country.getName());
        if (!relations.isEmpty()) {
            sb.append("🌍 DIPLOMATIC RELATIONS:\n");
            for (Map.Entry<String, DiplomaticRelation> entry : relations.entrySet()) {
                String otherCountry = entry.getKey();
                DiplomaticRelation relation = entry.getValue();
                
                if (!allies.contains(otherCountry) && !enemies.contains(otherCountry)) {
                    sb.append("   ").append(otherCountry).append(": ").append(relation.getStatus())
                      .append(" (Opinion: ").append(String.format("%.1f", relation.getValue())).append(")\n");
                }
            }
            sb.append("\n");
        }
        
        // Colonization Missions
        var colonizationManager = engine.getColonizationManager();
        var activeMissions = colonizationManager.getActiveMissions();
        if (!activeMissions.isEmpty()) {
            sb.append("🌍 COLONIZATION MISSIONS:\n");
            for (var mission : activeMissions) {
                if (mission.getCountryName().equals(country.getName())) {
                    sb.append("   Province ").append(mission.getProvinceId()).append(": ")
                      .append(String.format("%.1f%%", mission.getProgress() * 100))
                      .append(" (").append(mission.getColonists()).append(" colonists)\n");
                }
            }
            sb.append("\n");
        }
        
        // Population and Development
        sb.append("👥 POPULATION & DEVELOPMENT:\n");
        int totalPopulation = 0;
        for (var province : country.getProvinces()) {
            totalPopulation += province.getPopulation();
        }
        sb.append("   Total Population: ").append(String.format("%,d", totalPopulation)).append("\n");
        sb.append("   Average Development: ").append(String.format("%.1f", 
            country.getProvinces().stream().mapToDouble(p -> p.getDevelopment()).average().orElse(0.0))).append("\n\n");
        
        // Buildings
        sb.append("🏗️  BUILDINGS:\n");
        for (var province : country.getProvinces()) {
            if (!province.getBuildings().isEmpty()) {
                sb.append("   ").append(province.getName()).append(":\n");
                for (String building : province.getBuildings()) {
                    sb.append("     • ").append(building).append("\n");
                }
            }
        }
        
        infoArea.setText(sb.toString());
        infoArea.setCaretPosition(0); // Scroll to top
    }
    
    public String getRelationType(int value) {
        if (value >= 100) return "Alliance";
        else if (value >= 50) return "Friendly";
        else if (value >= 0) return "Neutral";
        else if (value >= -50) return "Hostile";
        else return "At War";
    }
} 