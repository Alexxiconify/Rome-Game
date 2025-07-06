package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.awt.*;

public class LawsPanel extends JPanel {
    private GameEngine engine;
    private JList<String> lawsList;
    private DefaultListModel<String> lawsModel;
    private JTextArea lawDetails;
    private JButton enactButton;
    private JButton repealButton;
    
    public LawsPanel(GameEngine engine) {
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
            "⚖ Legal System",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        lawsModel = new DefaultListModel<>();
        lawsList = new JList<>(lawsModel);
        lawsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        lawsList.setBackground(new Color(205, 133, 63));
        lawsList.setForeground(new Color(25, 25, 112));
        
        lawDetails = new JTextArea();
        lawDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        lawDetails.setBackground(new Color(245, 222, 179));
        lawDetails.setForeground(new Color(25, 25, 112));
        lawDetails.setEditable(false);
        
        enactButton = createStyledButton("Enact Law", new Color(100, 200, 100));
        repealButton = createStyledButton("Repeal Law", new Color(200, 100, 100));
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
            "Laws",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(lawsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(lawDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(enactButton);
        buttonPanel.add(repealButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        enactButton.addActionListener(e -> enactLaw());
        repealButton.addActionListener(e -> repealLaw());
    }
    
    private void enactLaw() {
        JOptionPane.showMessageDialog(this, "Law enacted!");
    }
    
    private void repealLaw() {
        JOptionPane.showMessageDialog(this, "Law repealed!");
    }
    
    public void updatePanel() {
        lawsModel.clear();
        lawsModel.addElement("Twelve Tables - Active");
        lawsModel.addElement("Lex Agraria - Active");
        lawsModel.addElement("Lex Militaris - Active");
        lawsModel.addElement("Lex Mercatoria - Active");
        lawsModel.addElement("Lex Sacra - Active");
        
        lawDetails.setText("Legal System Overview:\n\n" +
            "Active Laws: 5\n" +
            "Legal Stability: 85%\n" +
            "Senate Approval: 70%\n\n" +
            "Law Effects:\n" +
            "- Twelve Tables: +10% stability\n" +
            "- Lex Agraria: +15% agriculture income\n" +
            "- Lex Militaris: +20% military efficiency\n" +
            "- Lex Mercatoria: +25% trade income\n" +
            "- Lex Sacra: +5% population happiness");
    }
} 