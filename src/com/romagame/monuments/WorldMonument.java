package com.romagame.monuments;

import com.romagame.map.Country;
import com.romagame.map.Country.NationType;
import java.util.*;

public class WorldMonument {
    private String name;
    private String description;
    private MonumentType type;
    private String location;
    private String owner;
    private boolean isBuilt;
    private int constructionProgress;
    private int totalWork;
    private Map<String, Double> effects;
    private List<NationType> buffedNations;
    private int constructionCost;
    
    public WorldMonument(String name, String description, MonumentType type, String location, 
                        int constructionCost, List<NationType> buffedNations) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
        this.owner = null;
        this.isBuilt = false;
        this.constructionProgress = 0;
        this.totalWork = calculateTotalWork();
        this.effects = new HashMap<>();
        this.buffedNations = buffedNations;
        this.constructionCost = constructionCost;
        initializeEffects();
    }
    
    private int calculateTotalWork() {
        return switch (type) {
            case WONDER -> 50000;
            case TEMPLE -> 30000;
            case FORTRESS -> 40000;
            case PALACE -> 35000;
            case THEATER -> 25000;
        };
    }
    
    private void initializeEffects() {
        switch (type) {
            case WONDER:
                effects.put("prestige", 50.0);
                effects.put("diplomatic_reputation", 2.0);
                effects.put("trade_efficiency", 0.15);
                break;
            case TEMPLE:
                effects.put("religious_unity", 0.25);
                effects.put("stability", 0.1);
                effects.put("missionary_strength", 0.2);
                break;
            case FORTRESS:
                effects.put("fort_level", 2.0);
                effects.put("defensiveness", 0.25);
                effects.put("army_tradition", 0.1);
                break;
            case PALACE:
                effects.put("legitimacy", 0.2);
                effects.put("absolutism", 0.1);
                effects.put("diplomatic_relations", 2.0);
                break;
            case THEATER:
                effects.put("culture_conversion_cost", -0.25);
                effects.put("unrest", -2.0);
                effects.put("prestige", 25.0);
                break;
        }
    }
    
    public void startConstruction(String owner) {
        this.owner = owner;
        this.constructionProgress = 0;
    }
    
    public void updateConstruction(int workDone) {
        if (isBuilt) return;
        
        constructionProgress += workDone;
        if (constructionProgress >= totalWork) {
            completeConstruction();
        }
    }
    
    private void completeConstruction() {
        isBuilt = true;
        // Apply effects to the owner
        applyEffectsToOwner();
    }
    
    private void applyEffectsToOwner() {
        if (owner == null) return;
        
        // Effects are applied through the country's modifier system
        // This would be handled by the game engine
    }
    
    public boolean canBuild(Country country) {
        // Check if the country's nation type is in the buffed nations list
        return buffedNations.contains(country.getNationType());
    }
    
    public double getProgressPercentage() {
        return (double) constructionProgress / totalWork * 100.0;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public MonumentType getType() { return type; }
    public String getLocation() { return location; }
    public String getOwner() { return owner; }
    public boolean isBuilt() { return isBuilt; }
    public int getConstructionProgress() { return constructionProgress; }
    public int getTotalWork() { return totalWork; }
    public Map<String, Double> getEffects() { return effects; }
    public List<NationType> getBuffedNations() { return buffedNations; }
    public int getConstructionCost() { return constructionCost; }
    
    public enum MonumentType {
        WONDER, TEMPLE, FORTRESS, PALACE, THEATER
    }
    
    // Static factory methods for famous monuments
    public static WorldMonument createColosseum() {
        return new WorldMonument(
            "Colosseum",
            "The great amphitheater of Rome, symbol of imperial power and entertainment",
            MonumentType.WONDER,
            "Rome",
            1000,
            Arrays.asList(NationType.ROMAN)
        );
    }
    
    public static WorldMonument createParthenon() {
        return new WorldMonument(
            "Parthenon",
            "The magnificent temple to Athena, symbol of Greek culture and wisdom",
            MonumentType.TEMPLE,
            "Athens",
            800,
            Arrays.asList(NationType.GREEK, NationType.ROMAN)
        );
    }
    
    public static WorldMonument createGreatWall() {
        return new WorldMonument(
            "Great Wall",
            "The massive defensive wall protecting the empire from northern invaders",
            MonumentType.FORTRESS,
            "Northern China",
            1500,
            Arrays.asList(NationType.EASTERN)
        );
    }
    
    public static WorldMonument createHangingGardens() {
        return new WorldMonument(
            "Hanging Gardens",
            "The legendary gardens of Babylon, wonder of the ancient world",
            MonumentType.WONDER,
            "Babylon",
            1200,
            Arrays.asList(NationType.EASTERN, NationType.ARABIAN)
        );
    }
    
    public static WorldMonument createPyramids() {
        return new WorldMonument(
            "Pyramids of Giza",
            "The eternal tombs of the pharaohs, wonder of the ancient world",
            MonumentType.WONDER,
            "Egypt",
            2000,
            Arrays.asList(NationType.AFRICAN, NationType.ARABIAN)
        );
    }
    
    public static WorldMonument createStonehenge() {
        return new WorldMonument(
            "Stonehenge",
            "The mysterious stone circle, ancient temple to the gods",
            MonumentType.TEMPLE,
            "Britain",
            600,
            Arrays.asList(NationType.CELTIC, NationType.GERMANIC)
        );
    }
    
    public static WorldMonument createPersepolis() {
        return new WorldMonument(
            "Persepolis",
            "The magnificent palace complex of the Persian kings",
            MonumentType.PALACE,
            "Persia",
            900,
            Arrays.asList(NationType.EASTERN, NationType.ARABIAN)
        );
    }
    
    public static WorldMonument createTheaterOfDionysus() {
        return new WorldMonument(
            "Theater of Dionysus",
            "The great theater where Greek drama was performed",
            MonumentType.THEATER,
            "Athens",
            500,
            Arrays.asList(NationType.GREEK, NationType.ROMAN)
        );
    }
} 