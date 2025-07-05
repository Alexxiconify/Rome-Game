package com.romagame.diplomacy;

public class DiplomaticRelation {
    private String country1;
    private String country2;
    private double value;
    private String status;
    
    public DiplomaticRelation(String country1, String country2, double value) {
        this.country1 = country1;
        this.country2 = country2;
        this.value = Math.max(-100, Math.min(100, value));
        this.status = determineStatus(value);
    }
    
    private String determineStatus(double value) {
        if (value >= 50) return "Friendly";
        if (value >= 25) return "Cordial";
        if (value >= 0) return "Neutral";
        if (value >= -25) return "Hostile";
        return "Hostile";
    }
    
    public void update() {
        // Natural relation drift towards neutral
        if (value > 0) {
            value = Math.max(0, value - 0.01);
        } else if (value < 0) {
            value = Math.min(0, value + 0.01);
        }
        status = determineStatus(value);
    }
    
    public void modify(double change) {
        value = Math.max(-100, Math.min(100, value + change));
        status = determineStatus(value);
    }
    
    // Getters
    public String getCountry1() { return country1; }
    public String getCountry2() { return country2; }
    public double getValue() { return value; }
    public String getStatus() { return status; }
} 