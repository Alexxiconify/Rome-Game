package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.events.EventManager;
import com.romagame.events.EventManager.Ruler;
import com.romagame.events.EventManager.Advisor;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RulersPanel extends JPanel {
    private GameEngine engine;
    private EventManager eventManager;
    private JList<String> rulerList;
    private DefaultListModel<String> rulerListModel;
    private JTextArea rulerDetails;
    private JList<String> advisorList;
    private DefaultListModel<String> advisorListModel;
    private JTextArea advisorDetails;
    private JPanel monarchPointsPanel;
    private JLabel adminPointsLabel;
    private JLabel diploPointsLabel;
    private JLabel militaryPointsLabel;
    private Ruler currentRuler;
    private Advisor currentAdvisor;
    
    public RulersPanel(GameEngine engine) {
        this.engine = engine;
        this.eventManager = new EventManager();
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
            "👑 Rulers & Advisors",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Ruler list
        rulerListModel = new DefaultListModel<>();
        rulerList = new JList<>(rulerListModel);
        rulerList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        rulerList.setBackground(new Color(205, 133, 63));
        rulerList.setForeground(new Color(25, 25, 112));
        rulerList.setSelectionBackground(new Color(255, 215, 0));
        rulerList.setSelectionForeground(new Color(25, 25, 112));
        
        // Ruler details
        rulerDetails = new JTextArea();
        rulerDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        rulerDetails.setBackground(new Color(245, 222, 179));
        rulerDetails.setForeground(new Color(25, 25, 112));
        rulerDetails.setEditable(false);
        rulerDetails.setLineWrap(true);
        rulerDetails.setWrapStyleWord(true);
        
        // Advisor list
        advisorListModel = new DefaultListModel<>();
        advisorList = new JList<>(advisorListModel);
        advisorList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        advisorList.setBackground(new Color(205, 133, 63));
        advisorList.setForeground(new Color(25, 25, 112));
        advisorList.setSelectionBackground(new Color(255, 215, 0));
        advisorList.setSelectionForeground(new Color(25, 25, 112));
        
        // Advisor details
        advisorDetails = new JTextArea();
        advisorDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        advisorDetails.setBackground(new Color(245, 222, 179));
        advisorDetails.setForeground(new Color(25, 25, 112));
        advisorDetails.setEditable(false);
        advisorDetails.setLineWrap(true);
        advisorDetails.setWrapStyleWord(true);
        
        // Monarch points panel
        monarchPointsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        monarchPointsPanel.setBackground(new Color(139, 69, 19));
        monarchPointsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Monarch Points",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        adminPointsLabel = new JLabel("Administrative: 0");
        diploPointsLabel = new JLabel("Diplomatic: 0");
        militaryPointsLabel = new JLabel("Military: 0");
        
        adminPointsLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        diploPointsLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        militaryPointsLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        
        adminPointsLabel.setForeground(new Color(255, 215, 0));
        diploPointsLabel.setForeground(new Color(255, 215, 0));
        militaryPointsLabel.setForeground(new Color(255, 215, 0));
        
        monarchPointsPanel.add(new JLabel("Administrative:"));
        monarchPointsPanel.add(adminPointsLabel);
        monarchPointsPanel.add(new JLabel("Diplomatic:"));
        monarchPointsPanel.add(diploPointsLabel);
        monarchPointsPanel.add(new JLabel("Military:"));
        monarchPointsPanel.add(militaryPointsLabel);
    }
    
    private void layoutComponents() {
        // Top panel for rulers
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(139, 69, 19));
        
        // Ruler list panel
        JPanel rulerListPanel = new JPanel(new BorderLayout());
        rulerListPanel.setBackground(new Color(139, 69, 19));
        rulerListPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Rulers",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane rulerScrollPane = new JScrollPane(rulerList);
        rulerScrollPane.getViewport().setBackground(new Color(205, 133, 63));
        rulerListPanel.add(rulerScrollPane, BorderLayout.CENTER);
        
        // Ruler details panel
        JPanel rulerDetailsPanel = new JPanel(new BorderLayout());
        rulerDetailsPanel.setBackground(new Color(139, 69, 19));
        rulerDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Ruler Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane rulerDetailsScrollPane = new JScrollPane(rulerDetails);
        rulerDetailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        rulerDetailsPanel.add(rulerDetailsScrollPane, BorderLayout.CENTER);
        
        topPanel.add(rulerListPanel, BorderLayout.WEST);
        topPanel.add(rulerDetailsPanel, BorderLayout.CENTER);
        
        // Bottom panel for advisors
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(139, 69, 19));
        
        // Advisor list panel
        JPanel advisorListPanel = new JPanel(new BorderLayout());
        advisorListPanel.setBackground(new Color(139, 69, 19));
        advisorListPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Advisors",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane advisorScrollPane = new JScrollPane(advisorList);
        advisorScrollPane.getViewport().setBackground(new Color(205, 133, 63));
        advisorListPanel.add(advisorScrollPane, BorderLayout.CENTER);
        
        // Advisor details panel
        JPanel advisorDetailsPanel = new JPanel(new BorderLayout());
        advisorDetailsPanel.setBackground(new Color(139, 69, 19));
        advisorDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Advisor Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane advisorDetailsScrollPane = new JScrollPane(advisorDetails);
        advisorDetailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        advisorDetailsPanel.add(advisorDetailsScrollPane, BorderLayout.CENTER);
        
        bottomPanel.add(advisorListPanel, BorderLayout.WEST);
        bottomPanel.add(advisorDetailsPanel, BorderLayout.CENTER);
        
        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        add(monarchPointsPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        rulerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateRulerDetails();
            }
        });
        
        advisorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateAdvisorDetails();
            }
        });
    }
    
    private void updateRulerDetails() {
        int selectedIndex = rulerList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedRuler = rulerList.getSelectedValue();
            
            // Find the ruler
            List<Ruler> rulers = eventManager.getAvailableRulers();
            for (Ruler ruler : rulers) {
                if (ruler.getName().equals(selectedRuler)) {
                    currentRuler = ruler;
                    break;
                }
            }
            
            if (currentRuler != null) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(currentRuler.getName()).append("\n");
                details.append("Title: ").append(currentRuler.getTitle()).append("\n");
                details.append("Personality: ").append(currentRuler.getPersonality()).append("\n");
                details.append("Focus: ").append(currentRuler.getFocus()).append("\n\n");
                details.append("Monarch Points:\n");
                details.append("- Administrative: ").append(currentRuler.getAdministrativePoints()).append("\n");
                details.append("- Diplomatic: ").append(currentRuler.getDiplomaticPoints()).append("\n");
                details.append("- Military: ").append(currentRuler.getMilitaryPoints()).append("\n\n");
                details.append("Effects:\n");
                details.append("- ").append(currentRuler.getFocus()).append(" actions cost 1 less point\n");
                details.append("- ").append(currentRuler.getPersonality()).append(" provides unique bonuses\n");
                
                rulerDetails.setText(details.toString());
                updateMonarchPoints();
            }
        }
    }
    
    private void updateAdvisorDetails() {
        int selectedIndex = advisorList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedAdvisor = advisorList.getSelectedValue();
            
            // Find the advisor
            List<Advisor> advisors = eventManager.getAvailableAdvisors();
            for (Advisor advisor : advisors) {
                if (advisor.getName().equals(selectedAdvisor)) {
                    currentAdvisor = advisor;
                    break;
                }
            }
            
            if (currentAdvisor != null) {
                StringBuilder details = new StringBuilder();
                details.append("Name: ").append(currentAdvisor.getName()).append("\n");
                details.append("Type: ").append(currentAdvisor.getType()).append("\n");
                details.append("Level: ").append(currentAdvisor.getLevel()).append("\n");
                details.append("Specialty: ").append(currentAdvisor.getSpecialty()).append("\n\n");
                details.append("Effect:\n");
                details.append(currentAdvisor.getEffect()).append("\n\n");
                details.append("Cost: ").append(currentAdvisor.getLevel() * 2).append(" gold per month\n");
                details.append("Provides monarch point generation bonus\n");
                
                advisorDetails.setText(details.toString());
            }
        }
    }
    
    private void updateMonarchPoints() {
        if (currentRuler != null) {
            adminPointsLabel.setText(String.valueOf(currentRuler.getAdministrativePoints()));
            diploPointsLabel.setText(String.valueOf(currentRuler.getDiplomaticPoints()));
            militaryPointsLabel.setText(String.valueOf(currentRuler.getMilitaryPoints()));
        } else {
            adminPointsLabel.setText("0");
            diploPointsLabel.setText("0");
            militaryPointsLabel.setText("0");
        }
    }
    
    public void updatePanel() {
        // Update ruler list
        rulerListModel.clear();
        List<Ruler> rulers = eventManager.getAvailableRulers();
        for (Ruler ruler : rulers) {
            rulerListModel.addElement(ruler.getName());
        }
        
        if (rulerListModel.size() > 0) {
            rulerList.setSelectedIndex(0);
            updateRulerDetails();
        }
        
        // Update advisor list
        advisorListModel.clear();
        List<Advisor> advisors = eventManager.getAvailableAdvisors();
        for (Advisor advisor : advisors) {
            advisorListModel.addElement(advisor.getName());
        }
        
        if (advisorListModel.size() > 0) {
            advisorList.setSelectedIndex(0);
            updateAdvisorDetails();
        }
    }
} 