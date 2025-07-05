package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Country {
    private String name;
    private String governmentType;
    private String capital;
    private List<Province> provinces;
    private Map<String, Double> resources;
    private Map<String, Integer> military;
    private Map<String, Double> modifiers;
    private List<String> ideas;
    private String religion;
    private String culture;
    private double prestige;
    private double stability;
    private double legitimacy;
    private double treasury;
    private double income;
    private double expenses;
    
    public Country(String name) {
        this.name = name;
        this.provinces = new ArrayList<>();
        this.resources = new HashMap<>();
        this.military = new HashMap<>();
        this.modifiers = new HashMap<>();
        this.ideas = new ArrayList<>();
        initializeCountry();
    }
    
    private void initializeCountry() {
        // Set default values based on country type
        this.governmentType = determineGovernmentType();
        this.religion = determineReligion();
        this.culture = determineCulture();
        this.prestige = 0.0;
        this.stability = 0.0;
        this.legitimacy = 1.0;
        this.treasury = 100.0;
        this.income = 10.0;
        this.expenses = 5.0;
        
        // Initialize resources
        resources.put("Gold", 0.0);
        resources.put("Iron", 0.0);
        resources.put("Grain", 0.0);
        resources.put("Wool", 0.0);
        resources.put("Wine", 0.0);
        resources.put("Spices", 0.0);
        
        // Initialize military
        military.put("Infantry", 0);
        military.put("Cavalry", 0);
        military.put("Artillery", 0);
        military.put("Ships", 0);
        
        // Add starting ideas
        addStartingIdeas();
    }
    
    private String determineGovernmentType() {
        return switch (name) {
            case "France", "England", "Castile", "Portugal" -> "Monarchy";
            case "Brandenburg", "Austria", "Sweden", "Denmark" -> "Monarchy";
            case "Muscovy" -> "Monarchy";
            case "Ottomans", "Mamluks" -> "Monarchy";
            case "Ming", "Japan" -> "Monarchy";
            case "Delhi" -> "Monarchy";
            case "Persia" -> "Monarchy";
            case "Aztec", "Inca" -> "Tribal";
            default -> "Tribal";
        };
    }
    
    private String determineReligion() {
        return switch (name) {
            case "France", "Castile", "Portugal", "England", "Brandenburg", 
                 "Sweden", "Denmark", "Austria" -> "Catholic";
            case "Muscovy" -> "Orthodox";
            case "Ottomans", "Mamluks" -> "Sunni";
            case "Ming", "Japan" -> "Confucian";
            case "Delhi" -> "Hindu";
            case "Persia" -> "Shia";
            case "Aztec", "Inca" -> "Pagan";
            default -> "Animist";
        };
    }
    
    private String determineCulture() {
        return switch (name) {
            case "France", "Castile", "Portugal" -> "Latin";
            case "England", "Brandenburg", "Sweden", "Denmark" -> "Germanic";
            case "Muscovy" -> "Slavic";
            case "Ottomans", "Mamluks" -> "Arabic";
            case "Ming", "Japan" -> "Chinese";
            case "Delhi" -> "Indian";
            case "Persia" -> "Persian";
            default -> "Tribal";
        };
    }
    
    private void addStartingIdeas() {
        // Add country-specific starting ideas
        switch (name) {
            case "France" -> {
                ideas.add("Elan");
                ideas.add("Grand Army");
            }
            case "England" -> {
                ideas.add("Rule Britannia");
                ideas.add("Wooden Wall");
            }
            case "Brandenburg" -> {
                ideas.add("Space Marines");
                ideas.add("Prussian Discipline");
            }
            case "Ottomans" -> {
                ideas.add("Janissaries");
                ideas.add("Sultan of Rum");
            }
            case "Ming" -> {
                ideas.add("Celestial Empire");
                ideas.add("Mandate of Heaven");
            }
        }
    }
    
    public void addProvince(Province province) {
        provinces.add(province);
        if (capital == null && province.isCapital()) {
            capital = province.getId();
        }
        updateResources();
    }
    
    private void updateResources() {
        // Update resources based on provinces
        for (Province province : provinces) {
            for (String good : province.getTradeGoods()) {
                resources.merge(good, 1.0, Double::sum);
            }
        }
    }
    
    public void update() {
        // Daily country update
        calculateIncome();
        calculateExpenses();
        updateTreasury();
        updateStability();
    }
    
    private void calculateIncome() {
        income = provinces.size() * 2.0; // Base income per province
        income += prestige * 0.1; // Prestige bonus
        income += stability * 0.5; // Stability bonus
    }
    
    private void calculateExpenses() {
        expenses = military.values().stream().mapToInt(Integer::intValue).sum() * 0.1;
        expenses += provinces.size() * 0.5; // Maintenance
    }
    
    private void updateTreasury() {
        treasury += income - expenses;
        if (treasury < 0) {
            treasury = 0;
            stability -= 0.1; // Bankruptcy penalty
        }
    }
    
    private void updateStability() {
        // Natural stability drift
        if (stability < 0) stability += 0.01;
        if (stability > 3) stability = 3;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public String getGovernmentType() { return governmentType; }
    public String getCapital() { return capital; }
    public List<Province> getProvinces() { return provinces; }
    public Map<String, Double> getResources() { return resources; }
    public Map<String, Integer> getMilitary() { return military; }
    public Map<String, Double> getModifiers() { return modifiers; }
    public List<String> getIdeas() { return ideas; }
    public String getReligion() { return religion; }
    public String getCulture() { return culture; }
    public double getPrestige() { return prestige; }
    public void setPrestige(double prestige) { this.prestige = prestige; }
    public double getStability() { return stability; }
    public void setStability(double stability) { this.stability = stability; }
    public double getLegitimacy() { return legitimacy; }
    public void setLegitimacy(double legitimacy) { this.legitimacy = legitimacy; }
    public double getTreasury() { return treasury; }
    public void setTreasury(double treasury) { this.treasury = treasury; }
    public double getIncome() { return income; }
    public double getExpenses() { return expenses; }
    
    public void addIdea(String idea) {
        if (!ideas.contains(idea)) {
            ideas.add(idea);
        }
    }
    
    public void addModifier(String name, double value) {
        modifiers.put(name, value);
    }
    
    public void recruitUnit(String type, int amount) {
        military.merge(type, amount, Integer::sum);
    }
} 