package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;

public class SoldiersPanel extends JPanel {
    private GameEngine engine;
    private JList<String> armyList;
    private DefaultListModel<String> armyListModel;
    private JTextArea armyDetails;
    private JButton recruitButton;
    private JButton disbandButton;
    private JButton moveButton;
    private JComboBox<String> unitTypeCombo;
    private JSpinner amountSpinner;
    
    public SoldiersPanel(GameEngine engine) {
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
            "⚔ Legion Management",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Army list
        armyListModel = new DefaultListModel<>();
        armyList = new JList<>(armyListModel);
        armyList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        armyList.setBackground(new Color(205, 133, 63));
        armyList.setForeground(new Color(25, 25, 112));
        armyList.setSelectionBackground(new Color(255, 215, 0));
        armyList.setSelectionForeground(new Color(25, 25, 112));
        
        // Army details
        armyDetails = new JTextArea();
        armyDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        armyDetails.setBackground(new Color(245, 222, 179));
        armyDetails.setForeground(new Color(25, 25, 112));
        armyDetails.setEditable(false);
        armyDetails.setLineWrap(true);
        armyDetails.setWrapStyleWord(true);
        
        // Unit type combo
        String[] unitTypes = {"Legionary", "Auxiliary", "Cavalry", "Archer", "Siege Engine"};
        unitTypeCombo = new JComboBox<>(unitTypes);
        unitTypeCombo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        unitTypeCombo.setBackground(new Color(205, 133, 63));
        unitTypeCombo.setForeground(new Color(25, 25, 112));
        
        // Amount spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(100, 1, 10000, 100);
        amountSpinner = new JSpinner(spinnerModel);
        amountSpinner.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        
        // Buttons
        recruitButton = createStyledButton("Recruit Legion", new Color(100, 200, 100));
        disbandButton = createStyledButton("Disband Legion", new Color(200, 100, 100));
        moveButton = createStyledButton("Move Legion", new Color(100, 150, 200));
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
        // Left panel for army list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(139, 69, 19));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Legions",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(armyList);
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
            "Legion Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(armyDetails);
        detailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        controlsPanel.setBackground(new Color(139, 69, 19));
        controlsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Commands",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        controlsPanel.add(new JLabel("Unit Type:", SwingConstants.CENTER));
        controlsPanel.add(unitTypeCombo);
        controlsPanel.add(new JLabel("Amount:", SwingConstants.CENTER));
        controlsPanel.add(amountSpinner);
        controlsPanel.add(recruitButton);
        controlsPanel.add(disbandButton);
        controlsPanel.add(moveButton);
        controlsPanel.add(new JLabel("")); // Empty space
        
        rightPanel.add(detailsPanel, BorderLayout.CENTER);
        rightPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        // Main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        armyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateArmyDetails();
            }
        });
        
        recruitButton.addActionListener(e -> recruitUnits());
        disbandButton.addActionListener(e -> disbandUnits());
        moveButton.addActionListener(e -> moveUnits());
    }
    
    private void recruitUnits() {
        String unitType = (String) unitTypeCombo.getSelectedItem();
        int amount = (Integer) amountSpinner.getValue();
        
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            double cost = calculateRecruitmentCost(unitType, amount);
            if (playerCountry.getTreasury() >= cost) {
                playerCountry.recruitUnit(unitType, amount);
                playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                updatePanel();
                JOptionPane.showMessageDialog(this, 
                    "Recruited " + amount + " " + unitType + " units for " + String.format("%.1f", cost) + " gold!",
                    "Recruitment Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Insufficient funds! Cost: " + String.format("%.1f", cost) + " gold",
                    "Insufficient Funds",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void disbandUnits() {
        int selectedIndex = armyList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedArmy = armyList.getSelectedValue();
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to disband " + selectedArmy + "?",
                "Confirm Disband",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                // TODO: Implement disband logic
                updatePanel();
                JOptionPane.showMessageDialog(this, "Legion disbanded!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a legion to disband.");
        }
    }
    
    private void moveUnits() {
        int selectedIndex = armyList.getSelectedIndex();
        if (selectedIndex >= 0) {
            // TODO: Implement move logic with province selection
            JOptionPane.showMessageDialog(this, "Move functionality to be implemented.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a legion to move.");
        }
    }
    
    private double calculateRecruitmentCost(String unitType, int amount) {
        double baseCost = switch (unitType) {
            case "Legionary" -> 2.0;
            case "Auxiliary" -> 1.5;
            case "Cavalry" -> 3.0;
            case "Archer" -> 2.5;
            case "Siege Engine" -> 5.0;
            default -> 2.0;
        };
        return baseCost * amount;
    }
    
    private void updateArmyDetails() {
        int selectedIndex = armyList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedArmy = armyList.getSelectedValue();
            StringBuilder details = new StringBuilder();
            details.append("Legion: ").append(selectedArmy).append("\n\n");
            details.append("Commander: Marcus Aurelius\n");
            details.append("Experience: Veteran\n");
            details.append("Morale: High\n");
            details.append("Equipment: Standard\n");
            details.append("Location: Rome\n");
            details.append("Status: Ready for battle\n\n");
            details.append("Unit Composition:\n");
            details.append("- Legionaries: 5000\n");
            details.append("- Auxiliaries: 2000\n");
            details.append("- Cavalry: 500\n");
            details.append("- Archers: 1000\n");
            details.append("- Siege Engines: 50\n");
            
            armyDetails.setText(details.toString());
        }
    }
    
    public void updatePanel() {
        armyListModel.clear();
        
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            // Add sample legions (replace with actual army data)
            armyListModel.addElement("I Legion - Prima Legio");
            armyListModel.addElement("II Legion - Secunda Legio");
            armyListModel.addElement("III Legion - Tertia Legio");
            armyListModel.addElement("IV Legion - Quarta Legio");
            armyListModel.addElement("V Legion - Quinta Legio");
        }
        
        if (armyListModel.size() > 0) {
            armyList.setSelectedIndex(0);
            updateArmyDetails();
        }
    }
} 