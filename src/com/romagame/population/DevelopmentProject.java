package com.romagame.population;

import com.romagame.map.Province;
import com.romagame.population.PopulationManager.DevelopmentType;

public class DevelopmentProject {
    private String id;
    private Province province;
    private DevelopmentType type;
    private int workers;
    private int progress;
    private int totalWork;
    private boolean completed;
    
    public DevelopmentProject(String id, Province province, DevelopmentType type, int workers) {
        this.id = id;
        this.province = province;
        this.type = type;
        this.workers = workers;
        this.progress = 0;
        this.totalWork = calculateTotalWork();
        this.completed = false;
    }
    
    private int calculateTotalWork() {
        // Base work required depends on development type and current province development
        int baseWork = switch (type) {
            case INFRASTRUCTURE -> 1000;
            case AGRICULTURE -> 800;
            case TRADE -> 600;
            case MILITARY -> 1200;
        };
        
        // More work required for higher development provinces
        double developmentMultiplier = 1.0 + (province.getDevelopment() * 0.1);
        return (int)(baseWork * developmentMultiplier);
    }
    
    public void update() {
        if (completed) return;
        
        // Calculate work done this tick
        int workPerTick = workers * 10; // Each worker does 10 work per tick
        progress += workPerTick;
        
        if (progress >= totalWork) {
            completed = true;
        }
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public double getProgressPercentage() {
        return (double) progress / totalWork * 100.0;
    }
    
    // Getters
    public String getId() { return id; }
    public Province getProvince() { return province; }
    public DevelopmentType getType() { return type; }
    public int getWorkers() { return workers; }
    public int getProgress() { return progress; }
    public int getTotalWork() { return totalWork; }
    
    public String getDescription() {
        return switch (type) {
            case INFRASTRUCTURE -> "Infrastructure Development";
            case AGRICULTURE -> "Agricultural Development";
            case TRADE -> "Trade Development";
            case MILITARY -> "Military Infrastructure";
        };
    }
} 