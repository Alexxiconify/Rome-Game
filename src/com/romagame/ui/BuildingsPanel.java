package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class BuildingsPanel extends JPanel {
    public GameEngine engine;
    private JList<String> buildingsList;
    private DefaultListModel<String> buildingsModel;
    private JTextArea buildingDetails;
    private JButton constructButton;
    private JButton upgradeButton;
    private JComboBox<String> buildingTypeCombo;
    
    public BuildingsPanel(GameEngine engine) {
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
            "🏗 Building Management",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        buildingsModel = new DefaultListModel<>();
        buildingsList = new JList<>(buildingsModel);
        buildingsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        buildingsList.setBackground(new Color(205, 133, 63));
        buildingsList.setForeground(new Color(25, 25, 112));
        
        buildingDetails = new JTextArea();
        buildingDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        buildingDetails.setBackground(new Color(245, 222, 179));
        buildingDetails.setForeground(new Color(25, 25, 112));
        buildingDetails.setEditable(false);
        
        String[] buildingTypes = {"Forum", "Temple", "Aqueduct", "Road", "Wall", "Barracks", "Market", "Bathhouse"};
        buildingTypeCombo = new JComboBox<>(buildingTypes);
        buildingTypeCombo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        
        constructButton = createStyledButton("Construct Building", new Color(100, 200, 100));
        upgradeButton = createStyledButton("Upgrade Building", new Color(200, 200, 100));
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
            "Buildings",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(buildingsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(buildingDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        controlPanel.setBackground(new Color(139, 69, 19));
        controlPanel.add(new JLabel("Building Type:"));
        controlPanel.add(buildingTypeCombo);
        controlPanel.add(constructButton);
        controlPanel.add(upgradeButton);
        
        rightPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        constructButton.addActionListener(e -> constructBuilding());
        upgradeButton.addActionListener(e -> upgradeBuilding());
    }
    
    private void constructBuilding() {
        String buildingType = (String) buildingTypeCombo.getSelectedItem();
        JOptionPane.showMessageDialog(this, "Constructed " + buildingType + "!");
    }
    
    private void upgradeBuilding() {
        JOptionPane.showMessageDialog(this, "Building upgraded!");
    }
    
    public void updatePanel() {
        buildingsModel.clear();
        buildingsModel.addElement("Forum - Level 2");
        buildingsModel.addElement("Temple of Jupiter - Level 1");
        buildingsModel.addElement("Aqueduct - Level 3");
        buildingsModel.addElement("City Walls - Level 2");
        buildingsModel.addElement("Barracks - Level 1");
        buildingsModel.addElement("Market Square - Level 2");
        
        buildingDetails.setText("Building Overview:\n\n" +
            "Total Buildings: 15\n" +
            "Construction Slots: 3 available\n" +
            "Maintenance Cost: 500 gold/year\n\n" +
            "Building Effects:\n" +
            "- Forum: +10% trade income\n" +
            "- Temple: +5% population happiness\n" +
            "- Aqueduct: +15% population growth\n" +
            "- Walls: +20% defense bonus\n" +
            "- Barracks: +25% recruitment speed\n" +
            "- Market: +15% tax income");
    }
} 