package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GovernmentReform {
    public enum ReformType {
        GOVERNMENT_FORM,      // Monarchy, Republic, Theocracy, etc.
        ADMINISTRATIVE,       // Administrative efficiency, stability
        MILITARY,            // Army quality, discipline
        DIPLOMATIC,          // Diplomatic relations, trade
        ECONOMIC,            // Trade efficiency, production
        RELIGIOUS,           // Religious unity, tolerance
        CULTURAL,            // Cultural acceptance, unrest
        LEGAL,              // Laws, corruption
        SOCIAL               // Social reforms, population happiness
    }
    
    private String name;
    private String description;
    private ReformType type;
    private int cost;
    private List<String> requirements;
    private Map<String, Double> effects;
    private boolean isImplemented;
    
    public GovernmentReform(String name, String description, ReformType type, int cost) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.cost = cost;
        this.requirements = new ArrayList<>();
        this.effects = new HashMap<>();
        this.isImplemented = false;
    }
    
    // Generic government reforms inspired by EU4
    public static List<GovernmentReform> createGenericReforms() {
        List<GovernmentReform> reforms = new ArrayList<>();
        
        // Government Forms
        GovernmentReform monarchy = new GovernmentReform("Absolute Monarchy", 
            "Centralized power in the hands of the monarch", ReformType.GOVERNMENT_FORM, 100);
        monarchy.addEffect("stability", 0.1);
        monarchy.addEffect("diplomatic_relations", 1);
        reforms.add(monarchy);
        
        GovernmentReform republic = new GovernmentReform("Merchant Republic", 
            "Rule by wealthy merchants and trade guilds", ReformType.GOVERNMENT_FORM, 100);
        republic.addEffect("trade_efficiency", 0.15);
        republic.addEffect("merchant_slots", 1);
        reforms.add(republic);
        
        GovernmentReform theocracy = new GovernmentReform("Theocratic State", 
            "Religious leaders hold political power", ReformType.GOVERNMENT_FORM, 100);
        theocracy.addEffect("religious_unity", 0.2);
        theocracy.addEffect("missionary_strength", 0.1);
        reforms.add(theocracy);
        
        // Administrative Reforms
        GovernmentReform bureaucracy = new GovernmentReform("Bureaucratic Administration", 
            "Efficient administrative system", ReformType.ADMINISTRATIVE, 50);
        bureaucracy.addEffect("administrative_efficiency", 0.1);
        bureaucracy.addEffect("corruption", -0.05);
        reforms.add(bureaucracy);
        
        GovernmentReform centralization = new GovernmentReform("Centralized Authority", 
            "Centralized decision making", ReformType.ADMINISTRATIVE, 75);
        centralization.addEffect("stability", 0.15);
        centralization.addEffect("unrest", -0.1);
        reforms.add(centralization);
        
        // Military Reforms
        GovernmentReform standingArmy = new GovernmentReform("Standing Army", 
            "Professional standing army", ReformType.MILITARY, 80);
        standingArmy.addEffect("army_quality", 0.1);
        standingArmy.addEffect("discipline", 0.05);
        reforms.add(standingArmy);
        
        GovernmentReform militaryAcademy = new GovernmentReform("Military Academy", 
            "Professional military training", ReformType.MILITARY, 60);
        militaryAcademy.addEffect("leader_pool", 1);
        militaryAcademy.addEffect("army_tradition", 0.1);
        reforms.add(militaryAcademy);
        
        // Economic Reforms
        GovernmentReform freeTrade = new GovernmentReform("Free Trade Policy", 
            "Reduced trade restrictions", ReformType.ECONOMIC, 70);
        freeTrade.addEffect("trade_efficiency", 0.2);
        freeTrade.addEffect("merchant_slots", 1);
        reforms.add(freeTrade);
        
        GovernmentReform guilds = new GovernmentReform("Guild System", 
            "Organized craft guilds", ReformType.ECONOMIC, 55);
        guilds.addEffect("production_efficiency", 0.1);
        guilds.addEffect("goods_produced", 0.05);
        reforms.add(guilds);
        
        // Religious Reforms
        GovernmentReform religiousTolerance = new GovernmentReform("Religious Tolerance", 
            "Acceptance of different faiths", ReformType.RELIGIOUS, 65);
        religiousTolerance.addEffect("tolerance", 0.2);
        religiousTolerance.addEffect("unrest", -0.15);
        reforms.add(religiousTolerance);
        
        GovernmentReform stateReligion = new GovernmentReform("State Religion", 
            "Official state religion", ReformType.RELIGIOUS, 45);
        stateReligion.addEffect("religious_unity", 0.25);
        stateReligion.addEffect("missionary_strength", 0.15);
        reforms.add(stateReligion);
        
        // Legal Reforms
        GovernmentReform codifiedLaws = new GovernmentReform("Codified Laws", 
            "Written legal system", ReformType.LEGAL, 90);
        codifiedLaws.addEffect("stability", 0.1);
        codifiedLaws.addEffect("corruption", -0.1);
        reforms.add(codifiedLaws);
        
        GovernmentReform judiciary = new GovernmentReform("Independent Judiciary", 
            "Independent court system", ReformType.LEGAL, 85);
        judiciary.addEffect("corruption", -0.15);
        judiciary.addEffect("unrest", -0.1);
        reforms.add(judiciary);
        
        // Social Reforms
        GovernmentReform education = new GovernmentReform("Public Education", 
            "Basic education for citizens", ReformType.SOCIAL, 75);
        education.addEffect("technology_cost", -0.05);
        education.addEffect("innovation", 0.1);
        reforms.add(education);
        
        GovernmentReform healthcare = new GovernmentReform("Public Healthcare", 
            "Basic healthcare system", ReformType.SOCIAL, 80);
        healthcare.addEffect("population_growth", 0.1);
        healthcare.addEffect("unrest", -0.1);
        reforms.add(healthcare);
        
        return reforms;
    }
    
    public void addRequirement(String requirement) {
        requirements.add(requirement);
    }
    
    public void addEffect(String effect, double value) {
        effects.put(effect, value);
    }
    
    public boolean canImplement(Country country) {
        // Check if country meets requirements
        for (String requirement : requirements) {
            if (!checkRequirement(country, requirement)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkRequirement(Country country, String requirement) {
        // Simple requirement checking - can be expanded
        switch (requirement) {
            case "stability_3" -> {
                return country.getStability() >= 3;
            }
            case "treasury_100" -> {
                return country.getTreasury() >= 100;
            }
            case "development_10" -> {
                return country.getTotalDevelopment() >= 10;
            }
            default -> {
                return true; // Default to allowing
            }
        }
    }
    
    public void implement(Country country) {
        if (canImplement(country) && !isImplemented) {
            // Apply effects to country
            for (Map.Entry<String, Double> effect : effects.entrySet()) {
                applyEffect(country, effect.getKey(), effect.getValue());
            }
            isImplemented = true;
        }
    }
    
    private void applyEffect(Country country, String effect, double value) {
        switch (effect) {
            case "stability" -> {
                country.setStability(country.getStability() + value);
            }
            case "trade_efficiency" -> {
                country.addModifier("Trade Efficiency", value);
            }
            case "production_efficiency" -> {
                country.addModifier("Production Efficiency", value);
            }
            case "administrative_efficiency" -> {
                country.addModifier("Administrative Efficiency", value);
            }
            case "army_quality" -> {
                country.addModifier("Army Quality", value);
            }
            case "discipline" -> {
                country.addModifier("Discipline", value);
            }
            case "religious_unity" -> {
                country.addModifier("Religious Unity", value);
            }
            case "corruption" -> {
                country.addModifier("Corruption", value);
            }
            case "unrest" -> {
                country.addModifier("Unrest", value);
            }
            case "technology_cost" -> {
                country.addModifier("Technology Cost", value);
            }
            case "innovation" -> {
                country.addModifier("Innovation", value);
            }
            case "population_growth" -> {
                country.addModifier("Population Growth", value);
            }
            case "merchant_slots" -> {
                country.addModifier("Merchant Slots", (int)value);
            }
            case "leader_pool" -> {
                country.addModifier("Leader Pool", (int)value);
            }
            case "army_tradition" -> {
                country.addModifier("Army Tradition", value);
            }
            case "goods_produced" -> {
                country.addModifier("Goods Produced", value);
            }
            case "tolerance" -> {
                country.addModifier("Tolerance", value);
            }
            case "missionary_strength" -> {
                country.addModifier("Missionary Strength", value);
            }
            case "diplomatic_relations" -> {
                country.addModifier("Diplomatic Relations", (int)value);
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ReformType getType() { return type; }
    public int getCost() { return cost; }
    public List<String> getRequirements() { return new ArrayList<>(requirements); }
    public Map<String, Double> getEffects() { return new HashMap<>(effects); }
    public boolean isImplemented() { return isImplemented; }
    
    @Override
    public String toString() {
        return name + " (" + type + ") - " + description;
    }
} 