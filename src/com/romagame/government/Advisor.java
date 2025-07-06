package com.romagame.government;

import java.util.*;

public class Advisor {
    private String name;
    private AdvisorType type;
    private int level;
    private double salary;
    private List<AdvisorTrait> traits;
    private boolean isActive;
    private int yearsEmployed;
    
    public Advisor(String name, AdvisorType type, int level) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.salary = calculateSalary();
        this.traits = new ArrayList<>();
        this.isActive = false;
        this.yearsEmployed = 0;
        generateTraits();
    }
    
    private double calculateSalary() {
        return level * 2.0; // Base salary per level
    }
    
    private void generateTraits() {
        Random random = new Random();
        int traitCount = random.nextInt(2) + 1; // 1-2 traits
        
        List<AdvisorTrait> availableTraits = new ArrayList<>(Arrays.asList(AdvisorTrait.values()));
        Collections.shuffle(availableTraits);
        
        for (int i = 0; i < traitCount && i < availableTraits.size(); i++) {
            traits.add(availableTraits.get(i));
        }
    }
    
    public void update() {
        if (!isActive) return;
        
        yearsEmployed++;
        
        // Check for advisor death (rare)
        if (yearsEmployed > 20 && new Random().nextDouble() < 0.05) {
            isActive = false;
        }
    }
    
    public Map<String, Double> getBonuses() {
        Map<String, Double> bonuses = new HashMap<>();
        
        // Base bonuses from advisor type and level
        switch (type) {
            case ADMINISTRATIVE_ADVISOR:
                bonuses.put("admin_points", (double) level);
                bonuses.put("stability_cost", -0.1 * level);
                bonuses.put("core_creation_cost", -0.1 * level);
                break;
            case DIPLOMATIC_ADVISOR:
                bonuses.put("diplomatic_points", (double) level);
                bonuses.put("diplomatic_reputation", 0.5 * level);
                bonuses.put("improve_relations_modifier", 0.1 * level);
                break;
            case MILITARY_ADVISOR:
                bonuses.put("military_points", (double) level);
                bonuses.put("army_tradition", 0.1 * level);
                bonuses.put("land_maintenance_modifier", -0.05 * level);
                break;
            case TRADE_ADVISOR:
                bonuses.put("diplomatic_points", 0.5 * level);
                bonuses.put("trade_efficiency", 0.1 * level);
                bonuses.put("merchant_slots", (double) level);
                break;
            case NAVAL_ADVISOR:
                bonuses.put("military_points", 0.5 * level);
                bonuses.put("navy_tradition", 0.1 * level);
                bonuses.put("naval_maintenance_modifier", -0.05 * level);
                break;
            case SPYMASTER:
                bonuses.put("diplomatic_points", 0.5 * level);
                bonuses.put("spy_network_construction", 0.1 * level);
                bonuses.put("embargo_efficiency", 0.1 * level);
                break;
            case THEOLOGIAN:
                bonuses.put("admin_points", 0.5 * level);
                bonuses.put("missionary_strength", 0.1 * level);
                bonuses.put("religious_unity", 0.05 * level);
                break;
            case PHILOSOPHER:
                bonuses.put("admin_points", 0.5 * level);
                bonuses.put("technology_cost", -0.05 * level);
                bonuses.put("idea_cost", -0.05 * level);
                break;
            case NATURAL_SCIENTIST:
                bonuses.put("admin_points", 0.5 * level);
                bonuses.put("technology_cost", -0.1 * level);
                bonuses.put("institution_spread", 0.1 * level);
                break;
            case ARTIST:
                bonuses.put("diplomatic_points", 0.5 * level);
                bonuses.put("prestige", 0.5 * level);
                bonuses.put("culture_conversion_cost", -0.1 * level);
                break;
        }
        
        // Trait bonuses
        for (AdvisorTrait trait : traits) {
            switch (trait) {
                case EFFICIENT -> {
                    bonuses.merge("admin_points", 1.0, Double::sum);
                    bonuses.merge("diplomatic_points", 1.0, Double::sum);
                    bonuses.merge("military_points", 1.0, Double::sum);
                }
                case INCOMPETENT -> {
                    bonuses.merge("admin_points", -1.0, Double::sum);
                    bonuses.merge("diplomatic_points", -1.0, Double::sum);
                    bonuses.merge("military_points", -1.0, Double::sum);
                }
                case EXPENSIVE -> bonuses.merge("salary", 2.0, Double::sum);
                case CHEAP -> bonuses.merge("salary", -1.0, Double::sum);
                case LOYAL -> bonuses.put("loyalty", 0.5);
                case DISLOYAL -> bonuses.put("loyalty", -0.5);
                case YOUNG -> bonuses.put("lifespan", 10.0);
                case OLD -> bonuses.put("lifespan", -5.0);
                case INFLUENTIAL -> bonuses.put("influence", 0.3);
                case UNKNOWN -> bonuses.put("influence", -0.2);
            }
        }
        
        return bonuses;
    }
    
    public void hire() {
        isActive = true;
    }
    
    public void fire() {
        isActive = false;
    }
    
    public void promote() {
        if (level < 5) {
            level++;
            salary = calculateSalary();
        }
    }
    
    // Getters
    public String getName() { return name; }
    public AdvisorType getType() { return type; }
    public int getLevel() { return level; }
    public double getSalary() { return salary; }
    public List<AdvisorTrait> getTraits() { return traits; }
    public boolean isActive() { return isActive; }
    public int getYearsEmployed() { return yearsEmployed; }
    
    public enum AdvisorType {
        ADMINISTRATIVE_ADVISOR, DIPLOMATIC_ADVISOR, MILITARY_ADVISOR,
        TRADE_ADVISOR, NAVAL_ADVISOR, SPYMASTER, THEOLOGIAN,
        PHILOSOPHER, NATURAL_SCIENTIST, ARTIST
    }
    
    public enum AdvisorTrait {
        EFFICIENT, INCOMPETENT, EXPENSIVE, CHEAP, LOYAL, DISLOYAL,
        YOUNG, OLD, INFLUENTIAL, UNKNOWN
    }
    
    // Static factory methods for common advisors
    public static Advisor createAdministrativeAdvisor(String name, int level) {
        return new Advisor(name, AdvisorType.ADMINISTRATIVE_ADVISOR, level);
    }
    
    public static Advisor createDiplomaticAdvisor(String name, int level) {
        return new Advisor(name, AdvisorType.DIPLOMATIC_ADVISOR, level);
    }
    
    public static Advisor createMilitaryAdvisor(String name, int level) {
        return new Advisor(name, AdvisorType.MILITARY_ADVISOR, level);
    }
    
    public static Advisor createTradeAdvisor(String name, int level) {
        return new Advisor(name, AdvisorType.TRADE_ADVISOR, level);
    }
    
    public static Advisor createNavalAdvisor(String name, int level) {
        return new Advisor(name, AdvisorType.NAVAL_ADVISOR, level);
    }
    
    public static Advisor createSpymaster(String name, int level) {
        return new Advisor(name, AdvisorType.SPYMASTER, level);
    }
    
    public static Advisor createTheologian(String name, int level) {
        return new Advisor(name, AdvisorType.THEOLOGIAN, level);
    }
    
    public static Advisor createPhilosopher(String name, int level) {
        return new Advisor(name, AdvisorType.PHILOSOPHER, level);
    }
    
    public static Advisor createNaturalScientist(String name, int level) {
        return new Advisor(name, AdvisorType.NATURAL_SCIENTIST, level);
    }
    
    public static Advisor createArtist(String name, int level) {
        return new Advisor(name, AdvisorType.ARTIST, level);
    }
} 