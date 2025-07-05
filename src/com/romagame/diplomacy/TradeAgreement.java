package com.romagame.diplomacy;

public class TradeAgreement {
    private String country1;
    private String country2;
    private boolean isActive;
    private double tradeBonus;
    
    public TradeAgreement(String country1, String country2) {
        this.country1 = country1;
        this.country2 = country2;
        this.isActive = true;
        this.tradeBonus = 0.1; // 10% trade bonus
    }
    
    public void update() {
        if (isActive) {
            // Check if agreement should end (simplified)
            if (Math.random() < 0.0005) { // 0.05% chance per update
                endAgreement();
            }
        }
    }
    
    public void endAgreement() {
        isActive = false;
    }
    
    // Getters
    public String getCountry1() { return country1; }
    public String getCountry2() { return country2; }
    public boolean isActive() { return isActive; }
    public double getTradeBonus() { return tradeBonus; }
} 