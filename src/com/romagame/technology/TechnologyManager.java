package com.romagame.technology;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TechnologyManager {
    private Map<String, Technology> technologies;
    private Map<String, List<String>> countryTechnologies;
    
    public TechnologyManager() {
        technologies = new HashMap<>();
        countryTechnologies = new HashMap<>();
        initializeTechnologies();
    }
    
    private void initializeTechnologies() {
        // Initialize all available technologies
        addTechnology("Military Tech", "Military", 1);
        addTechnology("Diplomatic Tech", "Diplomatic", 1);
        addTechnology("Administrative Tech", "Administrative", 1);
        addTechnology("Trade Tech", "Trade", 1);
        addTechnology("Naval Tech", "Military", 1);
        addTechnology("Infrastructure Tech", "Administrative", 1);
    }
    
    private void addTechnology(String name, String category, int level) {
        Technology tech = new Technology(name, category, level);
        technologies.put(name, tech);
    }
    
    public void update() {
        // Update technology research progress
        for (Technology tech : technologies.values()) {
            tech.update();
        }
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
    
    public Technology getTechnology(String name) {
        return technologies.get(name);
    }
    
    public List<Technology> getAllTechnologies() {
        return new ArrayList<>(technologies.values());
    }
} 