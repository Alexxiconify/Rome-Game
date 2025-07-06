package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class TechTreePanel extends JPanel {
    private GameEngine engine;
    private JList<String> techList;
    private DefaultListModel<String> techModel;
    private JTextArea techDetails;
    private JButton researchButton;
    
    public TechTreePanel(GameEngine engine) {
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
            "🔬 Technology Tree",
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
        techList.setBackground(new Color(205, 133, 63));
        techList.setForeground(new Color(25, 25, 112));
        
        techDetails = new JTextArea();
        techDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        techDetails.setBackground(new Color(245, 222, 179));
        techDetails.setForeground(new Color(25, 25, 112));
        techDetails.setEditable(false);
        
        researchButton = createStyledButton("Research Technology", new Color(100, 200, 100));
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
            "Technologies",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(techList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(techDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(researchButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        researchButton.addActionListener(e -> researchTechnology());
    }
    
    private void researchTechnology() {
        JOptionPane.showMessageDialog(this, "Technology researched!");
    }
    
    public void updatePanel() {
        techModel.clear();
        techModel.addElement("Iron Working - Researched");
        techModel.addElement("Masonry - Researched");
        techModel.addElement("Agriculture - Researched");
        techModel.addElement("Military Tactics - Available");
        techModel.addElement("Engineering - Available");
        techModel.addElement("Medicine - Locked");
        
        techDetails.setText("Technology Overview:\n\n" +
            "Researched Technologies: 3\n" +
            "Available Technologies: 2\n" +
            "Research Points: 5\n\n" +
            "Technology Effects:\n" +
            "- Iron Working: +20% military efficiency\n" +
            "- Masonry: +15% building construction\n" +
            "- Agriculture: +25% food production\n" +
            "- Military Tactics: +30% army morale\n" +
            "- Engineering: +20% infrastructure bonus");
    }
} 