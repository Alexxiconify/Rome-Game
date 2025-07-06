package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.diplomacy.DiplomaticRelation;
import com.romagame.diplomacy.TradeAgreement;
import com.romagame.diplomacy.War;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class DiplomacyPanel extends JPanel {
    private GameEngine engine;
    private JTextArea diplomacyArea;
    private JScrollPane scrollPane;
    private JButton declareWarButton;
    private JButton makePeaceButton;
    private JButton sendGiftButton;
    private JButton sendInsultButton;
    private JButton offerAllianceButton;
    private JButton breakAllianceButton;
    
    public DiplomacyPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(400, 600));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createTitledBorder("Diplomacy & War"));
    }
    
    private void createComponents() {
        diplomacyArea = new JTextArea();
        diplomacyArea.setEditable(false);
        diplomacyArea.setBackground(new Color(50, 50, 50));
        diplomacyArea.setForeground(Color.WHITE);
        diplomacyArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        diplomacyArea.setLineWrap(true);
        diplomacyArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(diplomacyArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Create action buttons
        declareWarButton = new JButton("Declare War");
        makePeaceButton = new JButton("Make Peace");
        sendGiftButton = new JButton("Send Gift");
        sendInsultButton = new JButton("Send Insult");
        offerAllianceButton = new JButton("Offer Alliance");
        breakAllianceButton = new JButton("Break Alliance");
        
        // Style buttons
        styleButton(declareWarButton, new Color(150, 50, 50));
        styleButton(makePeaceButton, new Color(50, 150, 50));
        styleButton(sendGiftButton, new Color(50, 50, 150));
        styleButton(sendInsultButton, new Color(150, 100, 50));
        styleButton(offerAllianceButton, new Color(100, 50, 150));
        styleButton(breakAllianceButton, new Color(150, 150, 50));
        
        // Add action listeners
        setupEventHandlers();
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 10));
    }
    
    private void setupEventHandlers() {
        declareWarButton.addActionListener(e -> showDeclareWarDialog());
        makePeaceButton.addActionListener(e -> showMakePeaceDialog());
        sendGiftButton.addActionListener(e -> showSendGiftDialog());
        sendInsultButton.addActionListener(e -> showSendInsultDialog());
        offerAllianceButton.addActionListener(e -> showOfferAllianceDialog());
        breakAllianceButton.addActionListener(e -> showBreakAllianceDialog());
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Top panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPanel.setBackground(new Color(40, 40, 40));
        buttonPanel.add(declareWarButton);
        buttonPanel.add(makePeaceButton);
        buttonPanel.add(sendGiftButton);
        buttonPanel.add(sendInsultButton);
        buttonPanel.add(offerAllianceButton);
        buttonPanel.add(breakAllianceButton);
        
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void updateDiplomacy() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        StringBuilder sb = new StringBuilder();
        DiplomacyManager diploManager = engine.getDiplomacyManager();
        String playerName = playerCountry.getName();
        
        // Header
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    DIPLOMATIC STATUS                         ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");
        
        // Active Wars
        List<War> wars = diploManager.getWarsInvolving(playerName);
        if (!wars.isEmpty()) {
            sb.append("⚔️  ACTIVE WARS:\n");
            for (War war : wars) {
                String enemy = war.getAttacker().equals(playerName) ? war.getDefender() : war.getAttacker();
                sb.append("   War vs ").append(enemy).append(" (Score: ").append(String.format("%.1f", war.getWarScore())).append(")\n");
                sb.append("   Duration: ").append(war.getDuration()).append(" months\n");
                sb.append("   Participants: ").append(String.join(", ", war.getParticipants())).append("\n");
                sb.append("   Occupied Provinces: ").append(war.getOccupiedProvinces().size()).append("\n\n");
            }
        }
        
        // Alliances
        List<String> allies = diploManager.getAllies(playerName);
        if (!allies.isEmpty()) {
            sb.append("🤝 ALLIES:\n");
            for (String ally : allies) {
                double relation = diploManager.getRelation(playerName, ally);
                sb.append("   ").append(ally).append(" (Opinion: ").append(String.format("%.1f", relation)).append(")\n");
            }
            sb.append("\n");
        }
        
        // Enemies
        List<String> enemies = diploManager.getEnemies(playerName);
        if (!enemies.isEmpty()) {
            sb.append("⚔️  ENEMIES:\n");
            for (String enemy : enemies) {
                double relation = diploManager.getRelation(playerName, enemy);
                sb.append("   ").append(enemy).append(" (Opinion: ").append(String.format("%.1f", relation)).append(")\n");
            }
            sb.append("\n");
        }
        
        // Diplomatic Relations
        Map<String, DiplomaticRelation> relations = diploManager.getRelations(playerName);
        if (!relations.isEmpty()) {
            sb.append("🌍 DIPLOMATIC RELATIONS:\n");
            for (Map.Entry<String, DiplomaticRelation> entry : relations.entrySet()) {
                String country = entry.getKey();
                DiplomaticRelation relation = entry.getValue();
                
                if (!allies.contains(country) && !enemies.contains(country)) {
                    sb.append("   ").append(country).append(": ").append(relation.getStatus())
                      .append(" (Opinion: ").append(String.format("%.1f", relation.getValue())).append(")\n");
                }
            }
            sb.append("\n");
        }
        
        // Trade Agreements
        List<TradeAgreement> tradeAgreements = diploManager.getTradeAgreements();
        if (!tradeAgreements.isEmpty()) {
            sb.append("📦 TRADE AGREEMENTS:\n");
            for (TradeAgreement agreement : tradeAgreements) {
                if (agreement.getCountry1().equals(playerName) || agreement.getCountry2().equals(playerName)) {
                    String partner = agreement.getCountry1().equals(playerName) ? agreement.getCountry2() : agreement.getCountry1();
                    sb.append("   ").append(partner).append(" (Active)\n");
                }
            }
            sb.append("\n");
        }
        
        // Available Actions
        sb.append("🎯 AVAILABLE ACTIONS:\n");
        sb.append("   • Declare War: Start a war with another nation\n");
        sb.append("   • Make Peace: End an active war\n");
        sb.append("   • Send Gift: Improve relations with gold\n");
        sb.append("   • Send Insult: Worsen relations\n");
        sb.append("   • Offer Alliance: Form defensive alliance\n");
        sb.append("   • Break Alliance: End existing alliance\n");
        
        diplomacyArea.setText(sb.toString());
        diplomacyArea.setCaretPosition(0);
    }
    
    private void showDeclareWarDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<Country> allCountries = engine.getCountryManager().getAllCountries();
        List<String> availableTargets = new ArrayList<>();
        
        for (Country country : allCountries) {
            if (!country.getName().equals(playerCountry.getName()) && 
                !engine.getDiplomacyManager().isAtWar(playerCountry.getName(), country.getName())) {
                availableTargets.add(country.getName());
            }
        }
        
        if (availableTargets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid war targets available!", "Declare War", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] targets = availableTargets.toArray(new String[0]);
        String target = (String) JOptionPane.showInputDialog(
            this,
            "Choose target for war declaration:",
            "Declare War",
            JOptionPane.QUESTION_MESSAGE,
            null,
            targets,
            targets[0]
        );
        
        if (target != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Declare war on " + target + "?\nThis will significantly worsen relations.",
                "Confirm War Declaration",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = engine.getDiplomacyManager().declareWar(playerCountry.getName(), target);
                if (success) {
                    JOptionPane.showMessageDialog(this, "War declared on " + target + "!", "War Declared", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to declare war!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showMakePeaceDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<War> playerWars = engine.getDiplomacyManager().getWarsInvolving(playerCountry.getName());
        if (playerWars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You are not at war with anyone!", "Make Peace", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<String> enemies = new ArrayList<>();
        for (War war : playerWars) {
            String enemy = war.getAttacker().equals(playerCountry.getName()) ? war.getDefender() : war.getAttacker();
            enemies.add(enemy);
        }
        
        String[] enemyArray = enemies.toArray(new String[0]);
        String enemy = (String) JOptionPane.showInputDialog(
            this,
            "Choose enemy to make peace with:",
            "Make Peace",
            JOptionPane.QUESTION_MESSAGE,
            null,
            enemyArray,
            enemyArray[0]
        );
        
        if (enemy != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Make peace with " + enemy + "?\nThis will improve relations.",
                "Confirm Peace",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = engine.getDiplomacyManager().makePeace(playerCountry.getName(), enemy);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Peace made with " + enemy + "!", "Peace Made", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to make peace!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showSendGiftDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<Country> allCountries = engine.getCountryManager().getAllCountries();
        List<String> availableTargets = new ArrayList<>();
        
        for (Country country : allCountries) {
            if (!country.getName().equals(playerCountry.getName())) {
                availableTargets.add(country.getName());
            }
        }
        
        String[] targets = availableTargets.toArray(new String[0]);
        String target = (String) JOptionPane.showInputDialog(
            this,
            "Choose target for gift:",
            "Send Gift",
            JOptionPane.QUESTION_MESSAGE,
            null,
            targets,
            targets[0]
        );
        
        if (target != null) {
            String amountStr = JOptionPane.showInputDialog(this, "Gift amount (gold):", "50");
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0 && amount <= playerCountry.getTreasury()) {
                    boolean success = engine.getDiplomacyManager().sendGift(playerCountry.getName(), target, amount);
                    if (success) {
                        playerCountry.setTreasury(playerCountry.getTreasury() - amount);
                        JOptionPane.showMessageDialog(this, "Gift sent to " + target + "!", "Gift Sent", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to send gift!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid amount or insufficient treasury!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showSendInsultDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<Country> allCountries = engine.getCountryManager().getAllCountries();
        List<String> availableTargets = new ArrayList<>();
        
        for (Country country : allCountries) {
            if (!country.getName().equals(playerCountry.getName())) {
                availableTargets.add(country.getName());
            }
        }
        
        String[] targets = availableTargets.toArray(new String[0]);
        String target = (String) JOptionPane.showInputDialog(
            this,
            "Choose target for insult:",
            "Send Insult",
            JOptionPane.QUESTION_MESSAGE,
            null,
            targets,
            targets[0]
        );
        
        if (target != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Send insult to " + target + "?\nThis will worsen relations.",
                "Confirm Insult",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = engine.getDiplomacyManager().sendInsult(playerCountry.getName(), target);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Insult sent to " + target + "!", "Insult Sent", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send insult!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showOfferAllianceDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<Country> allCountries = engine.getCountryManager().getAllCountries();
        List<String> availableTargets = new ArrayList<>();
        
        for (Country country : allCountries) {
            if (!country.getName().equals(playerCountry.getName()) && 
                !engine.getDiplomacyManager().areAllied(playerCountry.getName(), country.getName())) {
                availableTargets.add(country.getName());
            }
        }
        
        if (availableTargets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid alliance targets available!", "Offer Alliance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] targets = availableTargets.toArray(new String[0]);
        String target = (String) JOptionPane.showInputDialog(
            this,
            "Choose target for alliance offer:",
            "Offer Alliance",
            JOptionPane.QUESTION_MESSAGE,
            null,
            targets,
            targets[0]
        );
        
        if (target != null) {
            double relation = engine.getDiplomacyManager().getRelation(playerCountry.getName(), target);
            if (relation >= 25) {
                boolean success = engine.getDiplomacyManager().offerAlliance(playerCountry.getName(), target);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Alliance offered to " + target + "!", "Alliance Offered", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to offer alliance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Relations too low for alliance! Need opinion >= 25", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showBreakAllianceDialog() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) return;
        
        List<String> allies = engine.getDiplomacyManager().getAllies(playerCountry.getName());
        if (allies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no alliances to break!", "Break Alliance", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] allyArray = allies.toArray(new String[0]);
        String ally = (String) JOptionPane.showInputDialog(
            this,
            "Choose ally to break alliance with:",
            "Break Alliance",
            JOptionPane.QUESTION_MESSAGE,
            null,
            allyArray,
            allyArray[0]
        );
        
        if (ally != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Break alliance with " + ally + "?\nThis will worsen relations.",
                "Confirm Break Alliance",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = engine.getDiplomacyManager().breakAlliance(playerCountry.getName(), ally);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Alliance broken with " + ally + "!", "Alliance Broken", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to break alliance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
} 