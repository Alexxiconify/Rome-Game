package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.events.EventManager;
import com.romagame.events.EventManager.GameEvent;
import com.romagame.events.EventManager.EventOption;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EventsPanel extends JPanel {
    private GameEngine engine;
    private EventManager eventManager;
    private JList<String> eventList;
    private DefaultListModel<String> eventListModel;
    private JTextArea eventDetails;
    private JPanel choicesPanel;
    private JButton acceptButton;
    private JButton declineButton;
    private JButton compromiseButton;
    private GameEvent currentEvent;
    
    public EventsPanel(GameEngine engine) {
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
            "⚡ Events & Decisions",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Event list
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        eventList.setBackground(new Color(205, 133, 63));
        eventList.setForeground(new Color(25, 25, 112));
        eventList.setSelectionBackground(new Color(255, 215, 0));
        eventList.setSelectionForeground(new Color(25, 25, 112));
        
        // Event details
        eventDetails = new JTextArea();
        eventDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        eventDetails.setBackground(new Color(245, 222, 179));
        eventDetails.setForeground(new Color(25, 25, 112));
        eventDetails.setEditable(false);
        eventDetails.setLineWrap(true);
        eventDetails.setWrapStyleWord(true);
        
        // Choices panel
        choicesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        choicesPanel.setBackground(new Color(139, 69, 19));
        
        // Choice buttons
        acceptButton = createStyledButton("Accept", new Color(100, 200, 100));
        declineButton = createStyledButton("Decline", new Color(200, 100, 100));
        compromiseButton = createStyledButton("Compromise", new Color(200, 200, 100));
        
        choicesPanel.add(acceptButton);
        choicesPanel.add(declineButton);
        choicesPanel.add(compromiseButton);
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
        // Left panel for event list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(139, 69, 19));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Active Events",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(eventList);
        listScrollPane.getViewport().setBackground(new Color(205, 133, 63));
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Right panel for details and choices
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(new Color(139, 69, 19));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Event Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(eventDetails);
        detailsScrollPane.getViewport().setBackground(new Color(245, 222, 179));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Choices panel
        JPanel choicesContainer = new JPanel(new BorderLayout());
        choicesContainer.setBackground(new Color(139, 69, 19));
        choicesContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0)),
            "Your Decision",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        choicesContainer.add(choicesPanel, BorderLayout.CENTER);
        
        rightPanel.add(detailsPanel, BorderLayout.CENTER);
        rightPanel.add(choicesContainer, BorderLayout.SOUTH);
        
        // Main layout
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateEventDetails();
            }
        });
        
        acceptButton.addActionListener(e -> handleEventChoice(0));
        declineButton.addActionListener(e -> handleEventChoice(1));
        compromiseButton.addActionListener(e -> handleEventChoice(2));
    }
    
    private void handleEventChoice(int choiceIndex) {
        if (currentEvent != null) {
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null) {
                eventManager.applyEventChoice(playerCountry, currentEvent, choiceIndex);
                
                // Show result message
                EventOption chosenOption = currentEvent.getOptions().get(choiceIndex);
                JOptionPane.showMessageDialog(this, 
                    "Decision: " + chosenOption.getName() + "\n\n" + chosenOption.getDescription(),
                    "Event Resolved",
                    JOptionPane.INFORMATION_MESSAGE);
                
                updatePanel();
            }
        }
    }
    
    private void updateEventDetails() {
        int selectedIndex = eventList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedEvent = eventList.getSelectedValue();
            
            // Find the event
            List<GameEvent> activeEvents = eventManager.getActiveEvents();
            for (GameEvent event : activeEvents) {
                if (event.getName().equals(selectedEvent)) {
                    currentEvent = event;
                    break;
                }
            }
            
            if (currentEvent != null) {
                StringBuilder details = new StringBuilder();
                details.append("Event: ").append(currentEvent.getName()).append("\n\n");
                details.append("Type: ").append(currentEvent.getType()).append("\n\n");
                details.append("Description:\n");
                details.append(currentEvent.getDescription()).append("\n\n");
                details.append("Available Choices:\n");
                
                List<EventOption> options = currentEvent.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    EventOption option = options.get(i);
                    details.append(i + 1).append(". ").append(option.getName()).append("\n");
                    details.append("   ").append(option.getDescription()).append("\n\n");
                }
                
                eventDetails.setText(details.toString());
                
                // Update choice buttons
                updateChoiceButtons();
            }
        }
    }
    
    private void updateChoiceButtons() {
        if (currentEvent != null && currentEvent.getOptions().size() >= 3) {
            EventOption option1 = currentEvent.getOptions().get(0);
            EventOption option2 = currentEvent.getOptions().get(1);
            EventOption option3 = currentEvent.getOptions().get(2);
            
            acceptButton.setText(option1.getName());
            declineButton.setText(option2.getName());
            compromiseButton.setText(option3.getName());
            
            // Color code buttons based on effects
            acceptButton.setBackground(new Color(100, 200, 100)); // Green for positive
            declineButton.setBackground(new Color(200, 100, 100)); // Red for negative
            compromiseButton.setBackground(new Color(200, 200, 100)); // Yellow for neutral
        }
    }
    
    public void updatePanel() {
        eventListModel.clear();
        
        List<GameEvent> activeEvents = eventManager.getActiveEvents();
        for (GameEvent event : activeEvents) {
            eventListModel.addElement(event.getName());
        }
        
        if (eventListModel.size() > 0) {
            eventList.setSelectedIndex(0);
            updateEventDetails();
        } else {
            eventDetails.setText("No active events at this time.\n\n" +
                "Events will appear randomly during gameplay.\n" +
                "They may affect your stability, prestige, treasury, and other aspects of your empire.");
            currentEvent = null;
        }
    }
    
    public void processEvents() {
        Country playerCountry = engine.getCountryManager().getPlayerCountry();
        if (playerCountry != null) {
            eventManager.processRandomEvents(playerCountry);
            updatePanel();
        }
    }
} 