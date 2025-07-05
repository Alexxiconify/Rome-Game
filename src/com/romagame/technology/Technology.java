package com.romagame.technology;

public class Technology {
    private String name;
    private String category;
    private int level;
    private double researchProgress;
    private double researchCost;
    private String description;
    
    public Technology(String name, String category, int level) {
        this.name = name;
        this.category = category;
        this.level = level;
        this.researchProgress = 0.0;
        this.researchCost = level * 100.0;
        this.description = generateDescription(name, category, level);
    }
    
    private String generateDescription(String name, String category, int level) {
        return String.format("%s level %d - %s technology", name, level, category);
    }
    
    public void update() {
        // Technology research progress (if being researched)
        if (researchProgress > 0 && researchProgress < researchCost) {
            researchProgress += 1.0; // Daily research progress
        }
    }
    
    public void startResearch() {
        researchProgress = 1.0;
    }
    
    public boolean isResearched() {
        return researchProgress >= researchCost;
    }
    
    public double getResearchProgress() {
        return Math.min(1.0, researchProgress / researchCost);
    }
    
    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getLevel() { return level; }
    public double getResearchCost() { return researchCost; }
    public String getDescription() { return description; }
} 