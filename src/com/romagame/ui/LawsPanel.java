package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import com.romagame.map.Law;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class LawsPanel extends JPanel {
    private GameEngine engine;
    private JList<Law> lawsList;
    private DefaultListModel<Law> lawsModel;
    private JTextArea lawDetails;
    private JButton enactButton;
    private JButton repealButton;
    private List<Law> availableLaws;
    
    public LawsPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        updatePanel();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(139, 69, 19));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            "⚖ Legal System",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        lawsModel = new DefaultListModel<>();
        lawsList = new JList<>(lawsModel);
        lawsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        lawsList.setBackground(new Color(205, 133, 63));
        lawsList.setForeground(new Color(25, 25, 112));
        
        lawDetails = new JTextArea();
        lawDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        lawDetails.setBackground(new Color(245, 222, 179));
        lawDetails.setForeground(new Color(25, 25, 112));
        lawDetails.setEditable(false);
        
        enactButton = createStyledButton("Enact Law", new Color(100, 200, 100));
        repealButton = createStyledButton("Repeal Law", new Color(200, 100, 100));
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
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(139, 69, 19));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Available Laws",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(lawsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(lawDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(enactButton);
        buttonPanel.add(repealButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        lawsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showLawDetails();
            }
        });
        
        enactButton.addActionListener(e -> enactLaw());
        repealButton.addActionListener(e -> repealLaw());
    }
    
    private void showLawDetails() {
        Law selectedLaw = lawsList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedLaw == null || playerCountry == null) {
            lawDetails.setText("No law selected or no country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Law: ").append(selectedLaw.getName()).append("\n");
        details.append("Type: ").append(selectedLaw.getType()).append("\n");
        details.append("Tier: ").append(selectedLaw.getTier()).append("\n");
        details.append("Cost: ").append(selectedLaw.getEnactmentCost()).append(" gold\n");
        details.append("Time: ").append(selectedLaw.getEnactmentTime()).append(" days\n");
        details.append("Description: ").append(selectedLaw.getDescription()).append("\n\n");
        
        details.append("Effects:\n");
        for (Map.Entry<String, Double> effect : selectedLaw.getEffects().entrySet()) {
            details.append("- ").append(effect.getKey()).append(": ").append(effect.getValue()).append("\n");
        }
        
        details.append("\nRequirements:\n");
        List<String> requirements = selectedLaw.getRequirements();
        if (requirements.isEmpty()) {
            details.append("- None\n");
        } else {
            for (String requirement : requirements) {
                details.append("- ").append(requirement).append("\n");
            }
        }
        
        details.append("\nCan Enact: ").append(selectedLaw.canEnact(playerCountry) ? "Yes" : "No");
        details.append("\nEnacted: ").append(selectedLaw.isEnacted() ? "Yes" : "No");
        
        if (playerCountry.getEnactingLaws().contains(selectedLaw)) {
            details.append("\nEnacting: Yes");
            details.append("\nProgress: ").append(String.format("%.1f%%", selectedLaw.getEnactmentProgressPercent() * 100));
        }
        
        lawDetails.setText(details.toString());
    }
    
    private void enactLaw() {
        Law selectedLaw = lawsList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedLaw == null) {
            JOptionPane.showMessageDialog(this, "Please select a law to enact.");
            return;
        }
        
        if (playerCountry == null) {
            JOptionPane.showMessageDialog(this, "No country selected.");
            return;
        }
        
        if (selectedLaw.isEnacted()) {
            JOptionPane.showMessageDialog(this, "This law is already enacted.");
            return;
        }
        
        if (!selectedLaw.canEnact(playerCountry)) {
            JOptionPane.showMessageDialog(this, "Requirements not met for this law.");
            return;
        }
        
        if (playerCountry.getEnactingLaws().contains(selectedLaw)) {
            JOptionPane.showMessageDialog(this, "This law is already being enacted.");
            return;
        }
        
        // Start law enactment
        playerCountry.startLawEnactment(selectedLaw);
        JOptionPane.showMessageDialog(this, "Law '" + selectedLaw.getName() + "' enactment started!");
        
        // Update the display
        updatePanel();
    }
    
    private void repealLaw() {
        Law selectedLaw = lawsList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedLaw == null) {
            JOptionPane.showMessageDialog(this, "Please select a law to repeal.");
            return;
        }
        
        if (playerCountry == null) {
            JOptionPane.showMessageDialog(this, "No country selected.");
            return;
        }
        
        if (!selectedLaw.isEnacted()) {
            JOptionPane.showMessageDialog(this, "This law is not enacted.");
            return;
        }
        
        // For now, just show a message - repeal functionality would need to be implemented
        JOptionPane.showMessageDialog(this, "Law repeal functionality not yet implemented.");
    }
    
    public void updatePanel() {
        lawsModel.clear();
        
        // Get available laws
        availableLaws = Law.createHistoricalLaws();
        
        // Add laws to the list
        for (Law law : availableLaws) {
            lawsModel.addElement(law);
        }
        
        // Update details
        showLawDetails();
    }
    
    private void updateLawDetails() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) {
            lawDetails.setText("No country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Legal System Overview:\n\n");
        details.append("Country: ").append(playerCountry.getName()).append("\n");
        details.append("Government Type: ").append(playerCountry.getGovernmentType()).append("\n");
        details.append("Stability: ").append(String.format("%.1f", playerCountry.getStability())).append("\n");
        details.append("Treasury: ").append(String.format("%.0f", playerCountry.getTreasury())).append("\n");
        details.append("Enacted Laws: ").append(playerCountry.getEnactedLaws().size()).append("\n");
        details.append("Enacting Laws: ").append(playerCountry.getEnactingLaws().size()).append("\n\n");
        
        details.append("Law Categories:\n");
        details.append("- Military Laws\n");
        details.append("- Economic Laws\n");
        details.append("- Social Laws\n");
        details.append("- Religious Laws\n");
        details.append("- Administrative Laws\n");
        details.append("- Legal Laws\n");
        details.append("- Diplomatic Laws\n");
        details.append("- Cultural Laws\n\n");
        
        details.append("Select a law to see details and enactment requirements.");
        
        lawDetails.setText(details.toString());
    }
} 