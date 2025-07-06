package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Country;
import javax.swing.*;
import java.awt.*;

public class TradePanel extends JPanel {
    private GameEngine engine;
    private JTextArea tradeDetails;
    private JList<String> tradeGoodsList;
    private DefaultListModel<String> tradeGoodsModel;
    private JButton tradeButton;
    private JButton embargoButton;
    
    public TradePanel(GameEngine engine) {
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
            "💰 Trade & Economy",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 16),
            new Color(255, 215, 0)
        ));
    }
    
    private void createComponents() {
        // Trade goods list
        tradeGoodsModel = new DefaultListModel<>();
        tradeGoodsList = new JList<>(tradeGoodsModel);
        tradeGoodsList.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        tradeGoodsList.setBackground(new Color(205, 133, 63));
        tradeGoodsList.setForeground(new Color(25, 25, 112));
        
        // Trade details
        tradeDetails = new JTextArea();
        tradeDetails.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        tradeDetails.setBackground(new Color(245, 222, 179));
        tradeDetails.setForeground(new Color(25, 25, 112));
        tradeDetails.setEditable(false);
        
        // Buttons
        tradeButton = createStyledButton("Establish Trade Route", new Color(100, 200, 100));
        embargoButton = createStyledButton("Embargo Nation", new Color(200, 100, 100));
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
            "Trade Goods",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(255, 215, 0)
        ));
        
        JScrollPane listScrollPane = new JScrollPane(tradeGoodsList);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(139, 69, 19));
        
        JScrollPane detailsScrollPane = new JScrollPane(tradeDetails);
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(139, 69, 19));
        buttonPanel.add(tradeButton);
        buttonPanel.add(embargoButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        tradeButton.addActionListener(e -> establishTrade());
        embargoButton.addActionListener(e -> embargoNation());
    }
    
    private void establishTrade() {
        JOptionPane.showMessageDialog(this, "Trade route established!");
    }
    
    private void embargoNation() {
        JOptionPane.showMessageDialog(this, "Embargo implemented!");
    }
    
    public void updatePanel() {
        tradeGoodsModel.clear();
        tradeGoodsModel.addElement("Grain - 1000 units");
        tradeGoodsModel.addElement("Wine - 500 units");
        tradeGoodsModel.addElement("Olive Oil - 300 units");
        tradeGoodsModel.addElement("Iron - 200 units");
        tradeGoodsModel.addElement("Marble - 100 units");
        
        tradeDetails.setText("Trade Overview:\n\n" +
            "Total Trade Value: 15,000 gold\n" +
            "Trade Routes: 8 active\n" +
            "Trade Partners: 12 nations\n" +
            "Trade Surplus: +2,500 gold/year\n\n" +
            "Major Exports:\n" +
            "- Wine: 5,000 gold\n" +
            "- Marble: 3,000 gold\n" +
            "- Olive Oil: 2,500 gold\n\n" +
            "Major Imports:\n" +
            "- Silk: 2,000 gold\n" +
            "- Spices: 1,500 gold\n" +
            "- Precious Metals: 1,000 gold");
    }
} 