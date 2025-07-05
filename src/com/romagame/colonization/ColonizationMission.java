package com.romagame.colonization;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class ColonizationMission {
    private String countryName;
    private String provinceId;
    private int colonists;
    private int duration;
    private int maxDuration;
    private boolean completed;
    private List<ColonizationEvent> events;
    private Random random;
    
    public ColonizationMission(String countryName, String provinceId, int colonists) {
        this.countryName = countryName;
        this.provinceId = provinceId;
        this.colonists = colonists;
        this.duration = 0;
        this.maxDuration = 30 + (colonists / 100); // Duration based on colonist count
        this.completed = false;
        this.events = new ArrayList<>();
        this.random = new Random();
        
        // Generate initial events
        generateEvents();
    }
    
    private void generateEvents() {
        // Generate 2-4 random events during colonization
        int eventCount = random.nextInt(3) + 2;
        
        for (int i = 0; i < eventCount; i++) {
            double chance = random.nextDouble();
            String type;
            
            if (chance < 0.4) {
                type = "positive";
            } else if (chance < 0.7) {
                type = "negative";
            } else {
                type = "neutral";
            }
            
            events.add(new ColonizationEvent(type, generateEventDescription(type)));
        }
    }
    
    private String generateEventDescription(String type) {
        return switch (type) {
            case "positive" -> "Favorable conditions encountered";
            case "negative" -> "Challenges faced during colonization";
            case "neutral" -> "Standard colonization process";
            default -> "Unknown event";
        };
    }
    
    public void update() {
        if (completed) return;
        
        duration++;
        
        // Check if colonization is complete
        if (duration >= maxDuration) {
            completed = true;
        }
        
        // Random chance for additional events during colonization
        if (random.nextDouble() < 0.1) { // 10% chance per update
            generateAdditionalEvent();
        }
    }
    
    private void generateAdditionalEvent() {
        double chance = random.nextDouble();
        String type;
        
        if (chance < 0.3) {
            type = "positive";
        } else if (chance < 0.6) {
            type = "negative";
        } else {
            type = "neutral";
        }
        
        events.add(new ColonizationEvent(type, "Additional event: " + generateEventDescription(type)));
    }
    
    // Getters
    public String getCountryName() { return countryName; }
    public String getProvinceId() { return provinceId; }
    public int getColonists() { return colonists; }
    public int getDuration() { return duration; }
    public int getMaxDuration() { return maxDuration; }
    public boolean isCompleted() { return completed; }
    public List<ColonizationEvent> getEvents() { return events; }
    public double getProgress() { return (double) duration / maxDuration; }
} 