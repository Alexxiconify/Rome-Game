package com.romagame.monuments;

import com.romagame.map.Country;
import com.romagame.map.Country.NationType;
import java.util.*;

public class WorldMonument {
    private String name;
    private String description;
    private String location;
    private String owner;
    private MonumentType type;
    private int constructionProgress;
    private int constructionCost;
    private boolean isCompleted;
    private Map<String, Double> effects;
    private List<NationType> buffedNations;
    private String historicalContext;
    
    public enum MonumentType {
        WONDER, TEMPLE, PALACE, FORTRESS, INFRASTRUCTURE, CULTURAL
    }
    
    public WorldMonument(String name, String description, String location, MonumentType type, 
                        int constructionCost, String historicalContext) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.type = type;
        this.constructionCost = constructionCost;
        this.historicalContext = historicalContext;
        this.constructionProgress = 0;
        this.isCompleted = false;
        this.owner = null;
        this.effects = new HashMap<>();
        this.buffedNations = new ArrayList<>();
        initializeEffects();
    }
    
    private void initializeEffects() {
        switch (name) {
            case "Colosseum":
                effects.put("prestige", 0.2);
                effects.put("stability", 0.1);
                effects.put("population_happiness", 0.15);
                buffedNations.add(NationType.ROMAN);
                break;
            case "Pantheon":
                effects.put("religious_unity", 0.25);
                effects.put("missionary_strength", 0.1);
                effects.put("clergy_influence", 0.2);
                buffedNations.add(NationType.ROMAN);
                break;
            case "Hadrian's Wall":
                effects.put("defensiveness", 0.3);
                effects.put("fort_level", 2.0);
                effects.put("separatism", -0.2);
                buffedNations.add(NationType.ROMAN);
                buffedNations.add(NationType.CELTIC);
                break;
            case "Persepolis":
                effects.put("diplomatic_relations", 2.0);
                effects.put("prestige", 0.15);
                effects.put("trade_efficiency", 0.1);
                buffedNations.add(NationType.EASTERN);
                break;
            case "Hanging Gardens":
                effects.put("population_growth", 0.2);
                effects.put("development_cost", -0.1);
                effects.put("agriculture_efficiency", 0.15);
                buffedNations.add(NationType.EASTERN);
                break;
            case "Pyramids":
                effects.put("prestige", 0.3);
                effects.put("stability", 0.15);
                effects.put("religious_unity", 0.2);
                buffedNations.add(NationType.AFRICAN);
                break;
            case "Great Wall":
                effects.put("defensiveness", 0.4);
                effects.put("fort_level", 3.0);
                effects.put("separatism", -0.3);
                buffedNations.add(NationType.EASTERN);
                break;
            case "Stonehenge":
                effects.put("religious_unity", 0.15);
                effects.put("stability", 0.1);
                effects.put("culture_conversion", 0.1);
                buffedNations.add(NationType.CELTIC);
                break;
            case "Parthenon":
                effects.put("culture_conversion", 0.2);
                effects.put("prestige", 0.15);
                effects.put("technology_cost", -0.1);
                buffedNations.add(NationType.GREEK);
                break;
            case "Temple of Artemis":
                effects.put("trade_efficiency", 0.15);
                effects.put("merchant_slots", 1.0);
                effects.put("diplomatic_relations", 1.0);
                buffedNations.add(NationType.GREEK);
                break;
            case "Lighthouse of Alexandria":
                effects.put("naval_force_limit", 0.3);
                effects.put("trade_efficiency", 0.2);
                effects.put("ship_cost", -0.1);
                buffedNations.add(NationType.GREEK);
                buffedNations.add(NationType.AFRICAN);
                break;
            case "Mausoleum":
                effects.put("prestige", 0.25);
                effects.put("stability", 0.1);
                effects.put("legitimacy", 0.15);
                buffedNations.add(NationType.EASTERN);
                break;
            case "Temple of Solomon":
                effects.put("religious_unity", 0.3);
                effects.put("missionary_strength", 0.15);
                effects.put("clergy_influence", 0.25);
                buffedNations.add(NationType.ARABIAN);
                break;
            case "Petra":
                effects.put("trade_efficiency", 0.2);
                effects.put("merchant_slots", 1.0);
                effects.put("diplomatic_relations", 1.0);
                buffedNations.add(NationType.ARABIAN);
                break;
            case "Mohenjo-daro":
                effects.put("development_cost", -0.15);
                effects.put("infrastructure_efficiency", 0.2);
                effects.put("population_growth", 0.1);
                buffedNations.add(NationType.INDIAN);
                break;
            case "Angkor Wat":
                effects.put("religious_unity", 0.25);
                effects.put("culture_conversion", 0.15);
                effects.put("stability", 0.1);
                buffedNations.add(NationType.INDIAN);
                break;
        }
    }
    
    public void startConstruction(String owner) {
        this.owner = owner;
        this.constructionProgress = 0;
        this.isCompleted = false;
    }
    
    public void advanceConstruction(int progress) {
        if (!isCompleted) {
            constructionProgress += progress;
            if (constructionProgress >= constructionCost) {
                completeConstruction();
            }
        }
    }
    
    private void completeConstruction() {
        this.isCompleted = true;
        this.constructionProgress = constructionCost;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public boolean isBuffedNation(Country country) {
        return buffedNations.contains(country.getNationType());
    }
    
    public void applyEffects(Country country) {
        if (!isCompleted || !isBuffedNation(country)) {
            return;
        }
        
        for (Map.Entry<String, Double> effect : effects.entrySet()) {
            String modifier = effect.getKey();
            double value = effect.getValue();
            
            switch (modifier) {
                case "prestige":
                    country.setPrestige(country.getPrestige() + value);
                    break;
                case "stability":
                    country.setStability(country.getStability() + value);
                    break;
                case "religious_unity":
                    country.addModifier("Religious Unity", value);
                    break;
                case "missionary_strength":
                    country.addModifier("Missionary Strength", value);
                    break;
                case "clergy_influence":
                    country.addModifier("Clergy Influence", value);
                    break;
                case "defensiveness":
                    country.addModifier("Defensiveness", value);
                    break;
                case "fort_level":
                    country.addModifier("Fort Level", (int)value);
                    break;
                case "separatism":
                    country.addModifier("Separatism", value);
                    break;
                case "diplomatic_relations":
                    country.addModifier("Diplomatic Relations", (int)value);
                    break;
                case "trade_efficiency":
                    country.addModifier("Trade Efficiency", value);
                    break;
                case "population_growth":
                    country.addModifier("Population Growth", value);
                    break;
                case "development_cost":
                    country.addModifier("Development Cost", value);
                    break;
                case "agriculture_efficiency":
                    country.addModifier("Agriculture Efficiency", value);
                    break;
                case "culture_conversion":
                    country.addModifier("Culture Conversion", value);
                    break;
                case "technology_cost":
                    country.addModifier("Technology Cost", value);
                    break;
                case "merchant_slots":
                    country.addModifier("Merchant Slots", (int)value);
                    break;
                case "naval_force_limit":
                    country.addModifier("Naval Force Limit", value);
                    break;
                case "ship_cost":
                    country.addModifier("Ship Cost", value);
                    break;
                case "legitimacy":
                    country.setLegitimacy(country.getLegitimacy() + value);
                    break;
                case "infrastructure_efficiency":
                    country.addModifier("Infrastructure Efficiency", value);
                    break;
                case "population_happiness":
                    country.addModifier("Population Happiness", value);
                    break;
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getOwner() { return owner; }
    public MonumentType getType() { return type; }
    public int getConstructionProgress() { return constructionProgress; }
    public int getConstructionCost() { return constructionCost; }
    public Map<String, Double> getEffects() { return new HashMap<>(effects); }
    public List<NationType> getBuffedNations() { return new ArrayList<>(buffedNations); }
    public String getHistoricalContext() { return historicalContext; }
    
    public double getCompletionPercentage() {
        return (double) constructionProgress / constructionCost * 100.0;
    }
} 