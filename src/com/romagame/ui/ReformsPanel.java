package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class ReformsPanel extends JPanel {
    private GameEngine engine;
    private JList<String> reformsList;
    private DefaultListModel<String> reformsModel;
    private JTextArea reformDetails;
    private JButton implementButton;
    
    public ReformsPanel(GameEngine engine) {
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
            "🔄 Government Reforms",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        reformsModel = new DefaultListModel<>();
        reformsList = new JList<>(reformsModel);
        reformsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        reformsList.setBackground(new Color(205, 133, 63));
        reformsList.setForeground(new Color(25, 25, 112));
        
        reformDetails = new JTextArea();
        reformDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        reformDetails.setBackground(new Color(245, 222, 179));
        reformDetails.setForeground(new Color(25, 25, 112));
        reformDetails.setEditable(false);
        
        implementButton = createStyledButton("Implement Reform", new Color(100, 200, 100));
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
            "Available Reforms",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(reformsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(reformDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(implementButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        implementButton.addActionListener(e -> implementReform());
    }
    
    private void implementReform() {
        JOptionPane.showMessageDialog(this, "Reform implemented!");
    }
    
    public void updatePanel() {
        reformsModel.clear();
        reformsModel.addElement("Senatorial Reform");
        reformsModel.addElement("Military Reform");
        reformsModel.addElement("Economic Reform");
        reformsModel.addElement("Social Reform");
        reformsModel.addElement("Religious Reform");
        
        reformDetails.setText("Government Reforms:\n\n" +
            "Available Reforms: 5\n" +
            "Reform Points: 3\n" +
            "Stability Impact: +15%\n\n" +
            "Reform Effects:\n" +
            "- Senatorial: +10% stability\n" +
            "- Military: +20% army efficiency\n" +
            "- Economic: +25% tax income\n" +
            "- Social: +15% population happiness\n" +
            "- Religious: +10% cultural unity");
    }
} 