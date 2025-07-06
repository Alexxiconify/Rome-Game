package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.GovernmentReform;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReformsPanel extends JPanel {
    private GameEngine engine;
    private JList<GovernmentReform> reformsList;
    private DefaultListModel<GovernmentReform> reformsModel;
    private JTextArea reformDetails;
    private JButton implementButton;
    private List<GovernmentReform> availableReforms;
    
    public ReformsPanel(GameEngine engine) {
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
            "🔄 Government Reforms",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        reformsModel = new DefaultListModel<>();
        reformsList = new JList<>(reformsModel);
        reformsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        reformsList.setBackground(new Color(205, 133, 63));
        reformsList.setForeground(new Color(25, 25, 112));
        
        reformDetails = new JTextArea();
        reformDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        reformDetails.setBackground(new Color(245, 222, 179));
        reformDetails.setForeground(new Color(25, 25, 112));
        reformDetails.setEditable(false);
        
        implementButton = createStyledButton("Implement Reform", new Color(100, 200, 100));
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
            "Available Reforms",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(reformsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(reformDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(implementButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        implementButton.addActionListener(e -> implementReform());
        
        reformsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GovernmentReform selectedReform = reformsList.getSelectedValue();
                if (selectedReform != null) {
                    showReformDetails(selectedReform);
                }
            }
        });
    }
    
    private void showReformDetails(GovernmentReform reform) {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) {
            reformDetails.setText("No country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Reform: ").append(reform.getName()).append("\n");
        details.append("Type: ").append(reform.getType()).append("\n");
        details.append("Cost: ").append(reform.getCost()).append(" points\n");
        details.append("Description: ").append(reform.getDescription()).append("\n\n");
        
        details.append("Effects:\n");
        for (Map.Entry<String, Double> effect : reform.getEffects().entrySet()) {
            details.append("- ").append(effect.getKey()).append(": ").append(effect.getValue()).append("\n");
        }
        
        details.append("\nRequirements:\n");
        List<String> requirements = reform.getRequirements();
        if (requirements.isEmpty()) {
            details.append("- None\n");
        } else {
            for (String requirement : requirements) {
                details.append("- ").append(requirement).append("\n");
            }
        }
        
        details.append("\nCan Implement: ").append(reform.canImplement(playerCountry) ? "Yes" : "No");
        details.append("\nImplemented: ").append(reform.isImplemented() ? "Yes" : "No");
        
        reformDetails.setText(details.toString());
    }
    
    private void implementReform() {
        GovernmentReform selectedReform = reformsList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedReform == null) {
            JOptionPane.showMessageDialog(this, "Please select a reform to implement.");
            return;
        }
        
        if (playerCountry == null) {
            JOptionPane.showMessageDialog(this, "No country selected.");
            return;
        }
        
        if (selectedReform.isImplemented()) {
            JOptionPane.showMessageDialog(this, "This reform is already implemented.");
            return;
        }
        
        if (!selectedReform.canImplement(playerCountry)) {
            JOptionPane.showMessageDialog(this, "Requirements not met for this reform.");
            return;
        }
        
        // Implement the reform
        selectedReform.implement(playerCountry);
        JOptionPane.showMessageDialog(this, "Reform '" + selectedReform.getName() + "' implemented successfully!");
        
        // Update the display
        updateReformDetails();
    }
    
    public void updatePanel() {
        reformsModel.clear();
        
        // Get available reforms
        availableReforms = GovernmentReform.createTieredReforms();
        
        // Add reforms to the list
        for (GovernmentReform reform : availableReforms) {
            reformsModel.addElement(reform);
        }
        
        // Update details
        updateReformDetails();
    }
    
    private void updateReformDetails() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) {
            reformDetails.setText("No country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Government Reforms:\n\n");
        details.append("Country: ").append(playerCountry.getName()).append("\n");
        details.append("Government Type: ").append(playerCountry.getGovernmentType()).append("\n");
        details.append("Stability: ").append(String.format("%.1f", playerCountry.getStability())).append("\n");
        details.append("Treasury: ").append(String.format("%.0f", playerCountry.getTreasury())).append("\n");
        details.append("Available Reforms: ").append(availableReforms.size()).append("\n\n");
        
        details.append("Reform Categories:\n");
        details.append("- Government Forms\n");
        details.append("- Administrative\n");
        details.append("- Military\n");
        details.append("- Economic\n");
        details.append("- Religious\n");
        details.append("- Legal\n");
        details.append("- Social\n\n");
        
        details.append("Select a reform to see details and implementation requirements.");
        
        reformDetails.setText(details.toString());
    }
} 