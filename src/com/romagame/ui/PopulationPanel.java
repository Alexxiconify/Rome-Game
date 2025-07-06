package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
public class PopulationPanel extends JPanel {
    private GameEngine engine;
    private JList<String> provinceList;
    private DefaultListModel<String> provinceListModel;
    private JTextArea populationDetails;
    private JButton migrateButton;
    private JButton developButton;
    private JSpinner amountSpinner;
    private JComboBox<String> actionCombo;
    
    public PopulationPanel(GameEngine engine) {
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
            "👥 Population Management",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Province list
        provinceListModel = new DefaultListModel<>();
        provinceList = new JList<>(provinceListModel);
        provinceList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        provinceList.setBackground(new Color(205, 133, 63));
        provinceList.setForeground(new Color(25, 25, 112));
        provinceList.setSelectionBackground(new Color(255, 215, 0));
        provinceList.setSelectionForeground(new Color(25, 25, 112));
        
        // Population details
        populationDetails = new JTextArea();
        populationDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        populationDetails.setBackground(new Color(245, 222, 179));
        populationDetails.setForeground(new Color(25, 25, 112));
        populationDetails.setEditable(false);
        populationDetails.setLineWrap(true);
        populationDetails.setWrapStyleWord(true);
        
        // Action combo
        String[] actions = {"Develop Province", "Encourage Migration", "Build Housing", "Improve Sanitation"};
        actionCombo = new JComboBox<>(actions);
        actionCombo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        actionCombo.setBackground(new Color(205, 133, 63));
        actionCombo.setForeground(new Color(25, 25, 112));
        
        // Amount spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1000, 100, 100000, 1000);
        amountSpinner = new JSpinner(spinnerModel);
        amountSpinner.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        
        // Buttons
        migrateButton = createStyledButton("Migrate Population", new Color(100, 200, 100));
        developButton = createStyledButton("Develop Province", new Color(200, 200, 100));
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
        // Left panel for province list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(139, 69, 19));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Provinces",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(provinceList);
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
            "Population Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(populationDetails);
        detailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        controlsPanel.setBackground(new Color(139, 69, 19));
        controlsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Population Actions",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        controlsPanel.add(new JLabel("Action:", SwingConstants.CENTER));
        controlsPanel.add(actionCombo);
        controlsPanel.add(new JLabel("Amount:", SwingConstants.CENTER));
        controlsPanel.add(amountSpinner);
        controlsPanel.add(migrateButton);
        controlsPanel.add(developButton);
        controlsPanel.add(new JLabel("")); // Empty space
        controlsPanel.add(new JLabel("")); // Empty space
        
        rightPanel.add(detailsPanel, BorderLayout.CENTER);
        rightPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        // Main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        provinceList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePopulationDetails();
            }
        });
        
        migrateButton.addActionListener(e -> migratePopulation());
        developButton.addActionListener(e -> developProvince());
    }
    
    public void migratePopulation() {
        int selectedIndex = provinceList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedProvince = provinceList.getSelectedValue();
            String action = (String) actionCombo.getSelectedItem();
            int amount = (Integer) amountSpinner.getValue();
            
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null) {
                double cost = calculateMigrationCost(action, amount);
                if (playerCountry.getTreasury() >= cost) {
                    playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                    updatePanel();
                    JOptionPane.showMessageDialog(this, 
                        "Executed " + action + " for " + amount + " population at cost of " + String.format("%.1f", cost) + " gold!",
                        "Migration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient funds! Cost: " + String.format("%.1f", cost) + " gold",
                        "Insufficient Funds",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a province.");
        }
    }
    
    private void developProvince() {
        int selectedIndex = provinceList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedProvince = provinceList.getSelectedValue();
            String action = (String) actionCombo.getSelectedItem();
            int amount = (Integer) amountSpinner.getValue();
            
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null) {
                double cost = calculateDevelopmentCost(action, amount);
                if (playerCountry.getTreasury() >= cost) {
                    playerCountry.setTreasury(playerCountry.getTreasury() - cost);
                    updatePanel();
                    JOptionPane.showMessageDialog(this, 
                        "Developed " + selectedProvince + " with " + action + " for " + String.format("%.1f", cost) + " gold!",
                        "Development Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient funds! Cost: " + String.format("%.1f", cost) + " gold",
                        "Insufficient Funds",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a province.");
        }
    }
    
    private double calculateMigrationCost(String action, int amount) {
        double baseCost = switch (action) {
            case "Encourage Migration" -> 0.5;
            case "Build Housing" -> 1.0;
            case "Improve Sanitation" -> 0.8;
            default -> 0.5;
        };
        return baseCost * amount;
    }
    
    private double calculateDevelopmentCost(String action, int amount) {
        double baseCost = switch (action) {
            case "Develop Province" -> 2.0;
            case "Encourage Migration" -> 1.5;
            case "Build Housing" -> 1.0;
            case "Improve Sanitation" -> 1.2;
            default -> 1.5;
        };
        return baseCost * amount;
    }
    
    private void updatePopulationDetails() {
        int selectedIndex = provinceList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedProvince = provinceList.getSelectedValue();
            StringBuilder details = new StringBuilder();
            details.append("Province: ").append(selectedProvince).append("\n\n");
            details.append("Population: 25,000\n");
            details.append("Growth Rate: +2.5% per year\n");
            details.append("Happiness: 75%\n");
            details.append("Culture: Latin\n");
            details.append("Religion: Pagan\n");
            details.append("Development: 15\n\n");
            details.append("Population Breakdown:\n");
            details.append("- Citizens: 5,000\n");
            details.append("- Slaves: 10,000\n");
            details.append("- Freedmen: 7,500\n");
            details.append("- Foreigners: 2,500\n\n");
            details.append("Infrastructure:\n");
            details.append("- Housing: Basic\n");
            details.append("- Sanitation: Poor\n");
            details.append("- Education: None\n");
            details.append("- Entertainment: Basic\n");
            
            populationDetails.setText(details.toString());
        }
    }
    
    public void updatePanel() {
        provinceListModel.clear();
        
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            // Add sample provinces (replace with actual province data)
            provinceListModel.addElement("Rome - Capital");
            provinceListModel.addElement("Latium - Core Province");
            provinceListModel.addElement("Etruria - Developed");
            provinceListModel.addElement("Campania - Fertile");
            provinceListModel.addElement("Apulia - Rural");
            provinceListModel.addElement("Lucania - Mountainous");
            provinceListModel.addElement("Bruttium - Remote");
        }
        
        if (provinceListModel.size() > 0) {
            provinceList.setSelectedIndex(0);
            updatePopulationDetails();
        }
    }
} 