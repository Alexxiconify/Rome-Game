package com.romagame.technology;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class TechnologyManager {
    private Map<String, Technology> technologies;
    private Map<String, Map<String, TechnologyProgress>> countryResearchProgress;
    private Map<String, List<String>> countryTechnologies;
    
    public TechnologyManager() {
        technologies = new HashMap<>();
        countryResearchProgress = new HashMap<>();
        countryTechnologies = new HashMap<>();
        initializeTechnologies();
    }
    
    private void initializeTechnologies() {
        // Initialize all available technologies with requirements
        addTechnology("Military Tech", "Military", 1, 100, 30);
        addTechnology("Diplomatic Tech", "Diplomatic", 1, 80, 25);
        addTechnology("Administrative Tech", "Administrative", 1, 90, 35);
        addTechnology("Trade Tech", "Trade", 1, 70, 20);
        addTechnology("Naval Tech", "Military", 1, 85, 30);
        addTechnology("Infrastructure Tech", "Administrative", 1, 75, 25);
        
        // Higher tier technologies
        addTechnology("Advanced Military", "Military", 2, 150, 45);
        addTechnology("Advanced Diplomacy", "Diplomatic", 2, 120, 40);
        addTechnology("Advanced Administration", "Administrative", 2, 140, 50);
        addTechnology("Advanced Trade", "Trade", 2, 110, 35);
        addTechnology("Advanced Naval", "Military", 2, 130, 40);
        addTechnology("Advanced Infrastructure", "Administrative", 2, 125, 45);
        
        // Modern technologies
        addTechnology("Modern Military", "Military", 3, 200, 60);
        addTechnology("Modern Diplomacy", "Diplomatic", 3, 180, 55);
        addTechnology("Modern Administration", "Administrative", 3, 190, 65);
        addTechnology("Modern Trade", "Trade", 3, 160, 50);
        addTechnology("Modern Naval", "Military", 3, 175, 55);
        addTechnology("Modern Infrastructure", "Administrative", 3, 170, 60);
    }
    
    private void addTechnology(String name, String category, int level, int researchCost, int researchTime) {
        Technology tech = new Technology(name, category, level, researchCost, researchTime);
        technologies.put(name, tech);
    }
    
    public void update() {
        // Update technology research progress for all countries
        for (Map<String, TechnologyProgress> countryProgress : countryResearchProgress.values()) {
            for (TechnologyProgress progress : countryProgress.values()) {
                progress.update();
            }
        }
    }
    
    public void startResearch(String countryName, String techName) {
        Technology tech = technologies.get(techName);
        if (tech != null && !hasTechnology(countryName, techName)) {
            // Initialize country progress if needed
            countryResearchProgress.computeIfAbsent(countryName, k -> new HashMap<>());
            
            // Check if already researching
            if (!countryResearchProgress.get(countryName).containsKey(techName)) {
                TechnologyProgress progress = new TechnologyProgress(tech, countryName);
                countryResearchProgress.get(countryName).put(techName, progress);
            }
        }
    }
    
    public void stopResearch(String countryName, String techName) {
        Map<String, TechnologyProgress> countryProgress = countryResearchProgress.get(countryName);
        if (countryProgress != null) {
            countryProgress.remove(techName);
        }
    }
    
    public boolean isResearching(String countryName, String techName) {
        Map<String, TechnologyProgress> countryProgress = countryResearchProgress.get(countryName);
        return countryProgress != null && countryProgress.containsKey(techName);
    }
    
    public double getResearchProgress(String countryName, String techName) {
        Map<String, TechnologyProgress> countryProgress = countryResearchProgress.get(countryName);
        if (countryProgress != null && countryProgress.containsKey(techName)) {
            return countryProgress.get(techName).getProgressPercent();
        }
        return 0.0;
    }
    
    public boolean canResearch(String countryName, String techName) {
        Technology tech = technologies.get(techName);
        if (tech == null || hasTechnology(countryName, techName)) {
            return false;
        }
        
        // Check requirements
        return tech.canResearch(getCountryTechnologies(countryName));
    }
    
    public void researchTechnology(String country, String techName) {
        Technology tech = technologies.get(techName);
        if (tech != null && !hasTechnology(country, techName)) {
            countryTechnologies.computeIfAbsent(country, k -> new ArrayList<>());
            countryTechnologies.get(country).add(techName);
        }
    }
    
    public boolean hasTechnology(String country, String techName) {
        List<String> countryTechs = countryTechnologies.get(country);
        return countryTechs != null && countryTechs.contains(techName);
    }
    
    public List<String> getCountryTechnologies(String country) {
        return countryTechnologies.getOrDefault(country, new ArrayList<>());
    }
    
    public List<String> getResearchingTechnologies(String country) {
        Map<String, TechnologyProgress> countryProgress = countryResearchProgress.get(country);
        if (countryProgress != null) {
            return new ArrayList<>(countryProgress.keySet());
        }
        return new ArrayList<>();
    }
    
    public Technology getTechnology(String name) {
        return technologies.get(name);
    }
    
    public List<Technology> getAllTechnologies() {
        return new ArrayList<>(technologies.values());
    }
    
    public List<Technology> getAvailableTechnologies(String country) {
        List<Technology> available = new ArrayList<>();
        for (Technology tech : technologies.values()) {
            if (!hasTechnology(country, tech.getName()) && tech.canResearch(getCountryTechnologies(country))) {
                available.add(tech);
            }
        }
        return available;
    }
    
    // Inner class to track research progress
    private static class TechnologyProgress {
        private Technology technology;
        private double progress;
        private boolean completed;
        
        public TechnologyProgress(Technology tech, String country) {
            this.technology = tech;
            this.progress = 0.0;
            this.completed = false;
        }
        
        public void update() {
            if (!completed && progress < technology.getResearchTime()) {
                progress += 1.0; // Daily progress
                
                if (progress >= technology.getResearchTime()) {
                    completed = true;
                    // Auto-complete research
                    // This would typically be handled by the main game loop
                }
            }
        }
        
        public double getProgressPercent() {
            return Math.min(1.0, progress / technology.getResearchTime());
        }
        
        public boolean isCompleted() {
            return completed;
        }
    }
} 