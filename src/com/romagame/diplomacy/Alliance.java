package com.romagame.diplomacy;

public class Alliance {
    private String country1;
    private String country2;
    private boolean isActive;
    private int duration;
    
    public Alliance(String country1, String country2) {
        this.country1 = country1;
        this.country2 = country2;
        this.isActive = true;
        this.duration = 0;
    }
    
    public void update() {
        if (isActive) {
            duration++;
            // Check if alliance should break (simplified)
            if (Math.random() < 0.001) { // 0.1% chance per update
                breakAlliance();
            }
        }
    }
    
    public void breakAlliance() {
        isActive = false;
    }
    
    // Getters
    public String getCountry1() { return country1; }
    public String getCountry2() { return country2; }
    public boolean isActive() { return isActive; }
    public int getDuration() { return duration; }
} 