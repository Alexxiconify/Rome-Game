package com.romagame.population;

import com.romagame.map.Province;
import com.romagame.population.PopulationManager.BuildingType;

public class BuildingProject {
    private String id;
    private Province province;
    private BuildingType type;
    private int workers;
    private int progress;
    private int totalWork;
    private boolean completed;
    
    public BuildingProject(String id, Province province, BuildingType type, int workers) {
        this.id = id;
        this.province = province;
        this.type = type;
        this.workers = workers;
        this.progress = 0;
        this.totalWork = calculateTotalWork();
        this.completed = false;
    }
    
    private int calculateTotalWork() {
        // Base work required depends on building type
        int baseWork = switch (type) {
            case FORUM -> 800;
            case TEMPLE -> 600;
            case AQUEDUCT -> 1200;
            case WALLS -> 1500;
            case BARRACKS -> 1000;
            case MARKET -> 700;
            case BATHHOUSE -> 900;
            case WORKSHOP -> 800;
        };
        
        // More work required for higher development provinces
        double developmentMultiplier = 1.0 + (province.getDevelopment() * 0.05);
        return (int)(baseWork * developmentMultiplier);
    }
    
    public void update() {
        if (completed) return;
        
        // Calculate work done this tick
        int workPerTick = workers * 15; // Craftsmen are more efficient than peasants
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
    public BuildingType getType() { return type; }
    public int getWorkers() { return workers; }
    public int getProgress() { return progress; }
    public int getTotalWork() { return totalWork; }
    
    public String getDescription() {
        return switch (type) {
            case FORUM -> "Forum";
            case TEMPLE -> "Temple";
            case AQUEDUCT -> "Aqueduct";
            case WALLS -> "City Walls";
            case BARRACKS -> "Barracks";
            case MARKET -> "Market";
            case BATHHOUSE -> "Bathhouse";
            case WORKSHOP -> "Workshop";
        };
    }
} 