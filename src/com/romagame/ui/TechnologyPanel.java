package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import com.romagame.technology.Technology;
import com.romagame.technology.TechnologyManager;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TechnologyPanel extends JPanel {
    private GameEngine engine;
    private JList<Technology> techList;
    private DefaultListModel<Technology> techModel;
    private JTextArea techDetails;
    private JButton researchButton;
    private JButton stopButton;
    private List<Technology> availableTechnologies;
    private TechnologyManager techManager;
    
    public TechnologyPanel(GameEngine engine) {
        this.engine = engine;
        this.techManager = engine.getTechnologyManager();
        setupPanel();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        updatePanel();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(70, 130, 180));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            "🔬 Technology & Research",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        techModel = new DefaultListModel<>();
        techList = new JList<>(techModel);
        techList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        techList.setBackground(new Color(135, 206, 235));
        techList.setForeground(new Color(25, 25, 112));
        
        techDetails = new JTextArea();
        techDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        techDetails.setBackground(new Color(240, 248, 255));
        techDetails.setForeground(new Color(25, 25, 112));
        techDetails.setEditable(false);
        
        researchButton = createStyledButton("Start Research", new Color(100, 200, 100));
        stopButton = createStyledButton("Stop Research", new Color(200, 100, 100));
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
        leftPanel.setBackground(new Color(70, 130, 180));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Available Technologies",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(techList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(70, 130, 180));
        
        JScrollPane detailsScrollPane = new JScrollPane(techDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(70, 130, 180));
        buttonPanel.add(researchButton);
        buttonPanel.add(stopButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        techList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showTechnologyDetails();
            }
        });
        
        researchButton.addActionListener(e -> startResearch());
        stopButton.addActionListener(e -> stopResearch());
    }
    
    private void showTechnologyDetails() {
        Technology selectedTech = techList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedTech == null || playerCountry == null) {
            techDetails.setText("No technology selected or no country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Technology: ").append(selectedTech.getName()).append("\n");
        details.append("Category: ").append(selectedTech.getCategory()).append("\n");
        details.append("Level: ").append(selectedTech.getLevel()).append("\n");
        details.append("Research Cost: ").append(String.format("%.0f", selectedTech.getResearchCost())).append(" points\n");
        details.append("Research Time: ").append(selectedTech.getResearchTime()).append(" days\n");
        details.append("Description: ").append(selectedTech.getDescription()).append("\n\n");
        
        details.append("Requirements:\n");
        List<String> requirements = selectedTech.getRequirements();
        if (requirements.isEmpty()) {
            details.append("- None\n");
        } else {
            for (String requirement : requirements) {
                boolean hasRequirement = playerCountry.hasTechnology(requirement);
                details.append("- ").append(requirement)
                      .append(hasRequirement ? " ✓" : " ✗").append("\n");
            }
        }
        
        boolean canResearch = techManager.canResearch(playerCountry.getName(), selectedTech.getName());
        boolean isResearching = techManager.isResearching(playerCountry.getName(), selectedTech.getName());
        boolean hasResearched = playerCountry.hasTechnology(selectedTech.getName());
        
        details.append("\nCan Research: ").append(canResearch ? "Yes" : "No");
        details.append("\nIs Researching: ").append(isResearching ? "Yes" : "No");
        details.append("\nHas Researched: ").append(hasResearched ? "Yes" : "No");
        
        if (isResearching) {
            double progress = techManager.getResearchProgress(playerCountry.getName(), selectedTech.getName());
            details.append("\nResearch Progress: ").append(String.format("%.1f%%", progress * 100));
        }
        
        techDetails.setText(details.toString());
    }
    
    private void startResearch() {
        Technology selectedTech = techList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedTech == null) {
            JOptionPane.showMessageDialog(this, "Please select a technology to research.");
            return;
        }
        
        if (playerCountry == null) {
            JOptionPane.showMessageDialog(this, "No country selected.");
            return;
        }
        
        if (playerCountry.hasTechnology(selectedTech.getName())) {
            JOptionPane.showMessageDialog(this, "This technology is already researched.");
            return;
        }
        
        if (techManager.isResearching(playerCountry.getName(), selectedTech.getName())) {
            JOptionPane.showMessageDialog(this, "This technology is already being researched.");
            return;
        }
        
        if (!techManager.canResearch(playerCountry.getName(), selectedTech.getName())) {
            JOptionPane.showMessageDialog(this, "Requirements not met for this technology.");
            return;
        }
        
        // Start technology research
        techManager.startResearch(playerCountry.getName(), selectedTech.getName());
        playerCountry.startTechnologyResearch(selectedTech.getName());
        JOptionPane.showMessageDialog(this, "Technology '" + selectedTech.getName() + "' research started!");
        
        // Update the display
        updatePanel();
    }
    
    private void stopResearch() {
        Technology selectedTech = techList.getSelectedValue();
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        
        if (selectedTech == null) {
            JOptionPane.showMessageDialog(this, "Please select a technology to stop researching.");
            return;
        }
        
        if (playerCountry == null) {
            JOptionPane.showMessageDialog(this, "No country selected.");
            return;
        }
        
        if (!techManager.isResearching(playerCountry.getName(), selectedTech.getName())) {
            JOptionPane.showMessageDialog(this, "This technology is not being researched.");
            return;
        }
        
        // Stop technology research
        techManager.stopResearch(playerCountry.getName(), selectedTech.getName());
        playerCountry.getResearchingTechnologies().remove(selectedTech.getName());
        JOptionPane.showMessageDialog(this, "Technology '" + selectedTech.getName() + "' research stopped!");
        
        // Update the display
        updatePanel();
    }
    
    public void updatePanel() {
        techModel.clear();
        
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) {
            techDetails.setText("No country selected");
            return;
        }
        
        // Get available technologies for the country
        availableTechnologies = techManager.getAvailableTechnologies(playerCountry.getName());
        
        // Add technologies to the list
        for (Technology tech : availableTechnologies) {
            techModel.addElement(tech);
        }
        
        // Update details
        showTechnologyDetails();
    }
    
    private void updateTechnologyDetails() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry == null) {
            techDetails.setText("No country selected");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Technology Overview:\n\n");
        details.append("Country: ").append(playerCountry.getName()).append("\n");
        details.append("Researched Technologies: ").append(playerCountry.getResearchedTechnologies().size()).append("\n");
        details.append("Researching Technologies: ").append(playerCountry.getResearchingTechnologies().size()).append("\n\n");
        
        details.append("Technology Categories:\n");
        details.append("- Military Technology\n");
        details.append("- Diplomatic Technology\n");
        details.append("- Administrative Technology\n");
        details.append("- Trade Technology\n");
        details.append("- Naval Technology\n");
        details.append("- Infrastructure Technology\n\n");
        
        details.append("Select a technology to see details and research requirements.");
        
        techDetails.setText(details.toString());
    }
} 