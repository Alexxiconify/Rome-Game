package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.romagame.government.Ruler;

public class Country {
    public enum NationType {
        ROMAN, GREEK, CELTIC, GERMANIC, EASTERN, AFRICAN, ARABIAN, INDIAN, TRIBAL
    }
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
    private NationType nationType;
    private String cultureGroup;
    private Map<String, Integer> goods;
    private Ruler ruler;

    // Group-based mechanics (static for all countries)
    public static final Map<NationType, List<String>> GROUP_IDEAS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_REFORMS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_LAWS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_SOLDIER_TYPES = new HashMap<>();

    static {
        // Example group ideas
        GROUP_IDEAS.put(NationType.ROMAN, List.of("Pax Romana", "Legionary Discipline", "Roman Roads", "Imperial Administration"));
        GROUP_IDEAS.put(NationType.GERMANIC, List.of("Germanic Warriors", "Forest Warfare"));
        GROUP_IDEAS.put(NationType.CELTIC, List.of("Celtic Warriors", "Island Defense"));
        GROUP_IDEAS.put(NationType.EASTERN, List.of("Parthian Shot", "Cavalry Tradition", "Silk Road Control"));
        GROUP_IDEAS.put(NationType.AFRICAN, List.of("Desert Adaptation", "Trade Routes"));
        GROUP_IDEAS.put(NationType.ARABIAN, List.of("Arabian Trade", "Desert Warfare"));
        GROUP_IDEAS.put(NationType.INDIAN, List.of("Silk Road Trade", "Buddhist Influence"));
        GROUP_IDEAS.put(NationType.TRIBAL, List.of("Tribal Unity", "Warrior Spirit"));
        // ... similar for GROUP_REFORMS, GROUP_LAWS, GROUP_SOLDIER_TYPES ...
    }

    public Country(String name) {
        this.name = name;
        this.provinces = new ArrayList<>();
        this.resources = new HashMap<>();
        this.military = new HashMap<>();
        this.modifiers = new HashMap<>();
        this.ideas = new ArrayList<>();
        this.nationType = determineNationType();
        this.cultureGroup = determineCultureGroup();
        this.goods = new HashMap<>();
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
            case "Roman Empire" -> "Imperial";
            case "Parthia" -> "Monarchy";
            case "Armenia", "Iberia", "Albania", "Lazica", "Colchis" -> "Monarchy";
            case "Dacia", "Sarmatia" -> "Tribal";
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> "Tribal";
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> "Tribal";
            case "Garamantes", "Nubia", "Axum" -> "Monarchy";
            case "Himyar", "Saba", "Hadramaut", "Oman" -> "Monarchy";
            case "Kushan", "Indo-Parthian" -> "Monarchy";
            default -> "Tribal";
        };
    }
    
    private String determineReligion() {
        return switch (name) {
            case "Roman Empire" -> "Roman Paganism";
            case "Parthia" -> "Zoroastrianism";
            case "Armenia", "Iberia", "Albania", "Lazica", "Colchis" -> "Christianity";
            case "Dacia", "Sarmatia" -> "Pagan";
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> "Germanic Paganism";
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> "Celtic Paganism";
            case "Garamantes", "Nubia", "Axum" -> "Pagan";
            case "Himyar", "Saba", "Hadramaut", "Oman" -> "Arabian Paganism";
            case "Kushan", "Indo-Parthian" -> "Buddhism";
            default -> "Pagan";
        };
    }
    
    private String determineCulture() {
        return switch (name) {
            case "Roman Empire" -> "Roman";
            case "Parthia" -> "Persian";
            case "Armenia", "Iberia", "Albania", "Lazica", "Colchis" -> "Caucasian";
            case "Dacia", "Sarmatia" -> "Dacian";
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> "Germanic";
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> "Celtic";
            case "Garamantes", "Nubia", "Axum" -> "African";
            case "Himyar", "Saba", "Hadramaut", "Oman" -> "Arabian";
            case "Kushan", "Indo-Parthian" -> "Indo-Iranian";
            default -> "Tribal";
        };
    }
    
    private void addStartingIdeas() {
        // Add country-specific starting ideas for 117 AD
        switch (name) {
            case "Roman Empire" -> {
                ideas.add("Pax Romana");
                ideas.add("Legionary Discipline");
                ideas.add("Roman Roads");
                ideas.add("Imperial Administration");
            }
            case "Parthia" -> {
                ideas.add("Parthian Shot");
                ideas.add("Cavalry Tradition");
                ideas.add("Silk Road Control");
            }
            case "Armenia" -> {
                ideas.add("Mountain Defense");
                ideas.add("Christian Faith");
            }
            case "Dacia" -> {
                ideas.add("Dacian Warriors");
                ideas.add("Mountain Strongholds");
            }
            case "Sarmatia" -> {
                ideas.add("Sarmatian Cavalry");
                ideas.add("Steppe Warfare");
            }
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> {
                ideas.add("Germanic Warriors");
                ideas.add("Forest Warfare");
            }
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> {
                ideas.add("Celtic Warriors");
                ideas.add("Island Defense");
            }
            case "Garamantes", "Nubia", "Axum" -> {
                ideas.add("Desert Adaptation");
                ideas.add("Trade Routes");
            }
            case "Himyar", "Saba", "Hadramaut", "Oman" -> {
                ideas.add("Arabian Trade");
                ideas.add("Desert Warfare");
            }
            case "Kushan", "Indo-Parthian" -> {
                ideas.add("Silk Road Trade");
                ideas.add("Buddhist Influence");
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

    private NationType determineNationType() {
        return switch (name) {
            case "Roman Empire" -> NationType.ROMAN;
            case "Parthia", "Armenia", "Iberia", "Albania", "Lazica", "Colchis" -> NationType.EASTERN;
            case "Dacia", "Sarmatia" -> NationType.TRIBAL;
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> NationType.GERMANIC;
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> NationType.CELTIC;
            case "Garamantes", "Nubia", "Axum" -> NationType.AFRICAN;
            case "Himyar", "Saba", "Hadramaut", "Oman" -> NationType.ARABIAN;
            case "Kushan", "Indo-Parthian" -> NationType.INDIAN;
            default -> NationType.TRIBAL;
        };
    }

    private String determineCultureGroup() {
        return switch (name) {
            case "Roman Empire" -> "Latin";
            case "Parthia" -> "Persian";
            case "Armenia", "Iberia", "Albania", "Lazica", "Colchis" -> "Caucasian";
            case "Dacia", "Sarmatia" -> "Dacian";
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> "Germanic";
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> "Celtic";
            case "Garamantes", "Nubia", "Axum" -> "African";
            case "Himyar", "Saba", "Hadramaut", "Oman" -> "Arabian";
            case "Kushan", "Indo-Parthian" -> "Indo-Iranian";
            default -> "Tribal";
        };
    }

    public NationType getNationType() { return nationType; }
    public String getCultureGroup() { return cultureGroup; }
    public Map<String, Integer> getGoods() { return goods; }
    public void setGood(String good, int amount) { goods.put(good, amount); }
    
    public double getTotalDevelopment() {
        return provinces.stream()
                .mapToDouble(Province::getDevelopment)
                .sum();
    }
    
    public int getGameYear() {
        // For now, return a default year - this should be connected to the game engine
        return 117; // Default to 117 AD for the Roman Empire scenario
    }

    public Ruler getRuler() { return ruler; }
    public void setRuler(Ruler ruler) { this.ruler = ruler; }
} 