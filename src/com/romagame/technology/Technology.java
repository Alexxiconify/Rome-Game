package com.romagame.technology;

import java.util.List;
import java.util.ArrayList;

public class Technology {
    private String name;
    private String category;
    private int level;
    private double researchProgress;
    private double researchCost;
    private int researchTime; // Days required to research
    private String description;
    private List<String> requirements;
    
    public Technology(String name, String category, int level) {
        this(name, category, level, level * 100.0, level * 30);
    }
    
    public Technology(String name, String category, int level, double researchCost, int researchTime) {
        this.name = name;
        this.category = category;
        this.level = level;
        this.researchProgress = 0.0;
        this.researchCost = researchCost;
        this.researchTime = researchTime;
        this.description = generateDescription(name, category, level);
        this.requirements = new ArrayList<>();
        initializeRequirements();
    }
    
    private void initializeRequirements() {
        // Add requirements based on technology level and category
        if (level > 1) {
            requirements.add(category + "_" + (level - 1));
        }
        
        // Add category-specific requirements
        switch (category) {
            case "Military" -> {
                if (level >= 2) requirements.add("Military_1");
                if (level >= 3) requirements.add("Military_2");
            }
            case "Diplomatic" -> {
                if (level >= 2) requirements.add("Diplomatic_1");
                if (level >= 3) requirements.add("Diplomatic_2");
            }
            case "Administrative" -> {
                if (level >= 2) requirements.add("Administrative_1");
                if (level >= 3) requirements.add("Administrative_2");
            }
            case "Trade" -> {
                if (level >= 2) requirements.add("Trade_1");
                if (level >= 3) requirements.add("Trade_2");
            }
        }
    }
    
    private String generateDescription(String name, String category, int level) {
        return String.format("%s level %d - %s technology", name, level, category);
    }
    
    public void update() {
        // Technology research progress (if being researched)
        if (researchProgress > 0 && researchProgress < researchTime) {
            researchProgress += 1.0; // Daily research progress
        }
    }
    
    public void startResearch() {
        researchProgress = 1.0;
    }
    
    public boolean isResearched() {
        return researchProgress >= researchTime;
    }
    
    public double getResearchProgress() {
        return Math.min(1.0, researchProgress / researchTime);
    }
    
    public boolean canResearch(List<String> ownedTechnologies) {
        // Check if all requirements are met
        for (String requirement : requirements) {
            if (!ownedTechnologies.contains(requirement)) {
                return false;
            }
        }
        return true;
    }
    
    public void addRequirement(String requirement) {
        requirements.add(requirement);
    }
    
    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getLevel() { return level; }
    public double getResearchCost() { return researchCost; }
    public int getResearchTime() { return researchTime; }
    public String getDescription() { return description; }
    public List<String> getRequirements() { return new ArrayList<>(requirements); }
} 