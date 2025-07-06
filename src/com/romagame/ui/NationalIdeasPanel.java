package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class NationalIdeasPanel extends JPanel {
    private GameEngine engine;
    private JList<String> ideasList;
    private DefaultListModel<String> ideasModel;
    private JTextArea ideaDetails;
    private JButton adoptButton;
    
    public NationalIdeasPanel(GameEngine engine) {
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
            "💡 National Ideas",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        ideasModel = new DefaultListModel<>();
        ideasList = new JList<>(ideasModel);
        ideasList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        ideasList.setBackground(new Color(205, 133, 63));
        ideasList.setForeground(new Color(25, 25, 112));
        
        ideaDetails = new JTextArea();
        ideaDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        ideaDetails.setBackground(new Color(245, 222, 179));
        ideaDetails.setForeground(new Color(25, 25, 112));
        ideaDetails.setEditable(false);
        
        adoptButton = createStyledButton("Adopt Idea", new Color(100, 200, 100));
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
            "Ideas",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(ideasList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(ideaDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(adoptButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        adoptButton.addActionListener(e -> adoptIdea());
    }
    
    private void adoptIdea() {
        JOptionPane.showMessageDialog(this, "Idea adopted!");
    }
    
    public void updatePanel() {
        ideasModel.clear();
        ideasModel.addElement("Roman Virtue - Adopted");
        ideasModel.addElement("Senatorial Tradition - Adopted");
        ideasModel.addElement("Military Discipline - Adopted");
        ideasModel.addElement("Mercantile Spirit - Available");
        ideasModel.addElement("Religious Tolerance - Available");
        ideasModel.addElement("Imperial Ambition - Locked");
        
        ideaDetails.setText("National Ideas Overview:\n\n" +
            "Adopted Ideas: 3\n" +
            "Available Ideas: 2\n" +
            "Idea Slots: 7 total\n\n" +
            "Idea Effects:\n" +
            "- Roman Virtue: +10% stability\n" +
            "- Senatorial Tradition: +15% tax income\n" +
            "- Military Discipline: +20% army efficiency\n" +
            "- Mercantile Spirit: +25% trade income\n" +
            "- Religious Tolerance: +10% cultural unity");
    }
} 