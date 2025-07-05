package com.romagame.colonization;

public class ColonizationEvent {
    private String type;
    private String description;
    private int day;
    
    public ColonizationEvent(String type, String description) {
        this.type = type;
        this.description = description;
        this.day = 0;
    }
    
    public void update() {
        day++;
    }
    
    // Getters
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getDay() { return day; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", type.toUpperCase(), description);
    }
} 