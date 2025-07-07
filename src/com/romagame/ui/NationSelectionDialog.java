package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class NationSelectionDialog extends JDialog {
    private GameEngine engine;
    private String selectedCountry;
    private JComboBox<String> countryComboBox;
    private JTextArea descriptionArea;
    private JButton startButton;
    private JButton cancelButton;
    private static final String DEFAULT_NATION = "Rome";
    private static final String[] PINNED_NATIONS = {
        "Rome", "Kushan", "Axum", "Saba"
    };
    private JPanel pinnedPanel;
    
    public NationSelectionDialog(JFrame parent, GameEngine engine) {
        super(parent, "Choose Your Nation", true);
        this.engine = engine;
        this.selectedCountry = null;
        
        setupDialog();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        populateCountries();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void createComponents() {
        countryComboBox = new JComboBox<>();
        countryComboBox.setPreferredSize(new Dimension(300, 25));
        
        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setPreferredSize(new Dimension(400, 200));
        descriptionArea.setBackground(new Color(240, 240, 240));
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        startButton = new JButton("Start Game");
        startButton.setBackground(new Color(100, 200, 100));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 100, 100));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        pinnedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        for (String nation : PINNED_NATIONS) {
            JButton btn = new JButton(nation);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setFocusable(false);
            btn.addActionListener(e -> selectPinnedNation(nation));
            pinnedPanel.add(btn);
        }
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Select Your Nation (117 AD)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Country selection panel
        JPanel selectionPanel = new JPanel(new BorderLayout(5, 5));
        selectionPanel.add(new JLabel("Choose your nation:"), BorderLayout.NORTH);
        selectionPanel.add(pinnedPanel, BorderLayout.CENTER);
        selectionPanel.add(countryComboBox, BorderLayout.SOUTH);
        
        // Description panel
        JPanel descriptionPanel = new JPanel(new BorderLayout(5, 5));
        descriptionPanel.add(new JLabel("Nation Information:"), BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        contentPanel.add(selectionPanel, BorderLayout.NORTH);
        contentPanel.add(descriptionPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        countryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDescription();
            }
        });
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCountry = (String) countryComboBox.getSelectedItem();
                if (selectedCountry != null) {
                    engine.selectPlayerCountry(selectedCountry);
                    dispose();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCountry = null;
                dispose();
            }
        });
    }
    
    private void populateCountries() {
        List<String> selectableCountries = new ArrayList<>();
        
        try {
            // Load nations from owner_color_name.csv
            java.nio.file.Path csvPath = java.nio.file.Paths.get("src/resources/data/owner_color_name.csv");
            if (java.nio.file.Files.exists(csvPath)) {
                List<String> lines = java.nio.file.Files.readAllLines(csvPath);
                
                // Skip header line
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    
                    // Parse CSV with quoted values properly
                    String ownerName = extractOwnerNameFromCSV(line);
                    if (ownerName != null) {
                        // Filter out unwanted nations
                        if (isSelectableNation(ownerName)) {
                            selectableCountries.add(ownerName);
                            System.out.println("Added nation from CSV: " + ownerName);
                        } else {
                            System.out.println("Filtered out nation from CSV: " + ownerName);
                        }
                    }
                }
                
                System.out.println("Loaded " + selectableCountries.size() + " nations from owner_color_name.csv");
            } else {
                System.out.println("owner_color_name.csv not found, falling back to engine countries");
                // Fallback to engine countries
                List<Country> countries = engine.getAllCountries();
                for (Country country : countries) {
                    String countryName = country.getName();
                    if (isSelectableNation(countryName)) {
                        selectableCountries.add(countryName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading nations from CSV: " + e.getMessage());
            // Fallback to engine countries
            List<Country> countries = engine.getAllCountries();
            for (Country country : countries) {
                String countryName = country.getName();
                if (isSelectableNation(countryName)) {
                    selectableCountries.add(countryName);
                }
            }
        }
        
        // Sort the country names alphabetically
        selectableCountries.sort(String::compareToIgnoreCase);
        
        // Add sorted countries to the combo box
        for (String countryName : selectableCountries) {
            countryComboBox.addItem(countryName);
        }
        
        System.out.println("Total items in combo box: " + countryComboBox.getItemCount());
        
        // Set default selection to Rome if present
        int defaultIdx = 0;
        for (int i = 0; i < countryComboBox.getItemCount(); i++) {
            if (countryComboBox.getItemAt(i).equalsIgnoreCase(DEFAULT_NATION)) {
                defaultIdx = i;
                break;
            }
        }
        if (countryComboBox.getItemCount() > 0) {
            countryComboBox.setSelectedIndex(defaultIdx);
            updateDescription();
        }
    }
    
    private void selectPinnedNation(String nation) {
        for (int i = 0; i < countryComboBox.getItemCount(); i++) {
            if (countryComboBox.getItemAt(i).equalsIgnoreCase(nation)) {
                countryComboBox.setSelectedIndex(i);
                updateDescription();
                break;
            }
        }
    }
    
    private boolean isSelectableNation(String name) {
        if (name == null) return false;
        if (name.equalsIgnoreCase("Ocean") || name.equalsIgnoreCase("Uncolonized")) return false;
        if (name.startsWith("Unknown")) return false;
        if (name.startsWith("Color_")) return false;
        if (name.startsWith("rgb_")) return false;
        return true;
    }
    
    private String extractOwnerNameFromCSV(String line) {
        try {
            // Skip empty or header lines
            if (line.trim().isEmpty() || line.startsWith("owner_color")) return null;
            // Find the last comma (handles quoted color with commas)
            int lastComma = line.lastIndexOf(',');
            if (lastComma == -1) return null;
            String ownerName = line.substring(lastComma + 1).trim();
            // Remove quotes if present
            if (ownerName.startsWith("\"") && ownerName.endsWith("\"")) {
                ownerName = ownerName.substring(1, ownerName.length() - 1);
            }
            return ownerName;
        } catch (Exception e) {
            System.err.println("Error parsing CSV line: " + line + " - " + e.getMessage());
            return null;
        }
    }
    
    private void updateDescription() {
        String selected = (String) countryComboBox.getSelectedItem();
        if (selected != null) {
            Country country = engine.getCountry(selected);
            if (country != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Nation: ").append(country.getName()).append("\n\n");
                sb.append("Government: ").append(country.getGovernmentType()).append("\n");
                sb.append("Religion: ").append(country.getReligion()).append("\n");
                sb.append("Culture: ").append(country.getCulture()).append("\n");
                sb.append("Provinces: ").append(country.getProvinces().size()).append("\n");
                sb.append("Treasury: ").append(String.format("%.1f", country.getTreasury())).append("\n");
                sb.append("Stability: ").append(String.format("%.1f", country.getStability())).append("\n");
                sb.append("Prestige: ").append(String.format("%.1f", country.getPrestige())).append("\n\n");
                
                sb.append("Starting Ideas:\n");
                for (String idea : country.getIdeas()) {
                    sb.append("• ").append(idea).append("\n");
                }
                
                sb.append("\nMilitary:\n");
                for (var entry : country.getMilitary().entrySet()) {
                    sb.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                
                descriptionArea.setText(sb.toString());
            }
        }
    }
    
    public String getSelectedCountry() {
        return selectedCountry;
    }
} 