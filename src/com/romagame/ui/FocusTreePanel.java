package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class FocusTreePanel extends JPanel {
    public GameEngine engine;
    private JList<String> focusList;
    private DefaultListModel<String> focusModel;
    private JTextArea focusDetails;
    private JButton startFocusButton;
    
    public FocusTreePanel(GameEngine engine) {
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
            "🎯 Focus Tree",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        focusModel = new DefaultListModel<>();
        focusList = new JList<>(focusModel);
        focusList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        focusList.setBackground(new Color(205, 133, 63));
        focusList.setForeground(new Color(25, 25, 112));
        
        focusDetails = new JTextArea();
        focusDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        focusDetails.setBackground(new Color(245, 222, 179));
        focusDetails.setForeground(new Color(25, 25, 112));
        focusDetails.setEditable(false);
        
        startFocusButton = createStyledButton("Start Focus", new Color(100, 200, 100));
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
            "Focuses",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(focusList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(focusDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(startFocusButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        startFocusButton.addActionListener(e -> startFocus());
    }
    
    private void startFocus() {
        JOptionPane.showMessageDialog(this, "Focus started!");
    }
    
    public void updatePanel() {
        focusModel.clear();
        focusModel.addElement("Unify Italy - Completed");
        focusModel.addElement("Expand Military - Completed");
        focusModel.addElement("Develop Economy - In Progress");
        focusModel.addElement("Cultural Renaissance - Available");
        focusModel.addElement("Imperial Expansion - Available");
        focusModel.addElement("Religious Reformation - Locked");
        
        focusDetails.setText("Focus Tree Overview:\n\n" +
            "Completed Focuses: 2\n" +
            "Current Focus: Develop Economy (70 days)\n" +
            "Available Focuses: 2\n" +
            "Focus Points: 3\n\n" +
            "Focus Effects:\n" +
            "- Unify Italy: +20% core territory bonus\n" +
            "- Expand Military: +25% army size limit\n" +
            "- Develop Economy: +30% tax income\n" +
            "- Cultural Renaissance: +15% cultural unity\n" +
            "- Imperial Expansion: +20% aggressive expansion");
    }
} 