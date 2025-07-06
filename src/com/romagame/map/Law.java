package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Law {
    public enum LawType {
        MILITARY,       // Military organization, recruitment
        ECONOMIC,       // Trade, taxation, production
        SOCIAL,         // Population rights, welfare
        RELIGIOUS,      // Religious policies, tolerance
        ADMINISTRATIVE, // Government efficiency, corruption
        LEGAL,          // Legal system, justice
        DIPLOMATIC,     // Foreign relations, treaties
        CULTURAL        // Cultural policies, education
    }
    
    private String name;
    private String description;
    private LawType type;
    private int tier;
    private int enactmentCost;
    private int enactmentTime; // Days required to enact
    private double enactmentProgress;
    private boolean isEnacted;
    private List<String> requirements;
    private Map<String, Double> effects;
    private int yearUnlocked;
    
    public Law(String name, String description, LawType type, int tier, int enactmentCost, int enactmentTime, int yearUnlocked) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.enactmentCost = enactmentCost;
        this.enactmentTime = enactmentTime;
        this.yearUnlocked = yearUnlocked;
        this.enactmentProgress = 0.0;
        this.isEnacted = false;
        this.requirements = new ArrayList<>();
        this.effects = new HashMap<>();
    }
    
    // Create historical laws from ancient times to modern era
    public static List<Law> createHistoricalLaws() {
        List<Law> laws = new ArrayList<>();
        
        // Ancient Laws (Pre-500 BC)
        Law twelveTables = new Law("Twelve Tables", 
            "Foundation of Roman legal system", LawType.LEGAL, 1, 50, 30, -450);
        twelveTables.addEffect("stability", 0.1);
        twelveTables.addEffect("legitimacy", 0.05);
        twelveTables.addRequirement("year_-450");
        laws.add(twelveTables);
        
        Law lexAgraria = new Law("Lex Agraria", 
            "Land distribution and agricultural reform", LawType.ECONOMIC, 1, 40, 25, -133);
        lexAgraria.addEffect("agriculture_income", 0.15);
        lexAgraria.addEffect("unrest", -0.1);
        lexAgraria.addRequirement("year_-133");
        laws.add(lexAgraria);
        
        Law lexMilitaris = new Law("Lex Militaris", 
            "Military organization and discipline", LawType.MILITARY, 1, 60, 40, -100);
        lexMilitaris.addEffect("army_discipline", 0.1);
        lexMilitaris.addEffect("manpower", 0.2);
        lexMilitaris.addRequirement("year_-100");
        laws.add(lexMilitaris);
        
        // Classical Laws (500 BC - 500 AD)
        Law lexMercatoria = new Law("Lex Mercatoria", 
            "Trade regulations and merchant rights", LawType.ECONOMIC, 2, 70, 35, -50);
        lexMercatoria.addEffect("trade_efficiency", 0.2);
        lexMercatoria.addEffect("merchant_slots", 1);
        lexMercatoria.addRequirement("year_-50");
        laws.add(lexMercatoria);
        
        Law lexSacra = new Law("Lex Sacra", 
            "Religious law and temple rights", LawType.RELIGIOUS, 2, 45, 30, 0);
        lexSacra.addEffect("religious_unity", 0.15);
        lexSacra.addEffect("missionary_strength", 0.1);
        lexSacra.addRequirement("year_0");
        laws.add(lexSacra);
        
        Law lexAdministrativa = new Law("Lex Administrativa", 
            "Administrative efficiency reforms", LawType.ADMINISTRATIVE, 2, 80, 45, 100);
        lexAdministrativa.addEffect("administrative_efficiency", 0.1);
        lexAdministrativa.addEffect("corruption", -0.1);
        lexAdministrativa.addRequirement("year_100");
        laws.add(lexAdministrativa);
        
        // Medieval Laws (500-1500)
        Law feudalLaw = new Law("Feudal Law", 
            "Noble privileges and land tenure", LawType.SOCIAL, 3, 90, 50, 800);
        feudalLaw.addEffect("nobility_influence", 0.2);
        feudalLaw.addEffect("cavalry_combat_ability", 0.1);
        feudalLaw.addRequirement("year_800");
        laws.add(feudalLaw);
        
        Law guildLaw = new Law("Guild Law", 
            "Trade guild regulations and monopolies", LawType.ECONOMIC, 3, 75, 40, 1000);
        guildLaw.addEffect("production_efficiency", 0.15);
        guildLaw.addEffect("trade_efficiency", 0.1);
        guildLaw.addRequirement("year_1000");
        laws.add(guildLaw);
        
        Law canonLaw = new Law("Canon Law", 
            "Religious legal system", LawType.RELIGIOUS, 3, 65, 35, 1100);
        canonLaw.addEffect("religious_unity", 0.2);
        canonLaw.addEffect("clergy_influence", 0.25);
        canonLaw.addRequirement("year_1100");
        laws.add(canonLaw);
        
        // Renaissance Laws (1400-1700)
        Law mercantileLaw = new Law("Mercantile Law", 
            "Trade protection and navigation acts", LawType.ECONOMIC, 4, 100, 60, 1500);
        mercantileLaw.addEffect("trade_efficiency", 0.25);
        mercantileLaw.addEffect("merchant_slots", 2);
        mercantileLaw.addRequirement("year_1500");
        laws.add(mercantileLaw);
        
        Law standingArmyLaw = new Law("Standing Army Law", 
            "Professional military establishment", LawType.MILITARY, 4, 120, 70, 1600);
        standingArmyLaw.addEffect("army_tradition", 0.15);
        standingArmyLaw.addEffect("discipline", 0.1);
        standingArmyLaw.addRequirement("year_1600");
        laws.add(standingArmyLaw);
        
        Law absoluteLaw = new Law("Absolute Law", 
            "Centralized monarchical power", LawType.ADMINISTRATIVE, 4, 110, 65, 1650);
        absoluteLaw.addEffect("absolutism", 0.15);
        absoluteLaw.addEffect("administrative_efficiency", 0.1);
        absoluteLaw.addRequirement("year_1650");
        laws.add(absoluteLaw);
        
        // Enlightenment Laws (1700-1800)
        Law constitutionalLaw = new Law("Constitutional Law", 
            "Limited government and rights", LawType.LEGAL, 5, 130, 80, 1750);
        constitutionalLaw.addEffect("stability", 0.15);
        constitutionalLaw.addEffect("legitimacy", 0.2);
        constitutionalLaw.addRequirement("year_1750");
        laws.add(constitutionalLaw);
        
        Law freeTradeLaw = new Law("Free Trade Law", 
            "Removal of trade barriers", LawType.ECONOMIC, 5, 140, 85, 1780);
        freeTradeLaw.addEffect("trade_efficiency", 0.3);
        freeTradeLaw.addEffect("production_efficiency", 0.2);
        freeTradeLaw.addRequirement("year_1780");
        laws.add(freeTradeLaw);
        
        Law religiousToleranceLaw = new Law("Religious Tolerance Law", 
            "Freedom of religion and worship", LawType.RELIGIOUS, 5, 95, 55, 1790);
        religiousToleranceLaw.addEffect("religious_tolerance", 0.25);
        religiousToleranceLaw.addEffect("unrest", -0.15);
        religiousToleranceLaw.addRequirement("year_1790");
        laws.add(religiousToleranceLaw);
        
        // Industrial Laws (1800-1900)
        Law laborLaw = new Law("Labor Law", 
            "Worker rights and protections", LawType.SOCIAL, 6, 150, 90, 1850);
        laborLaw.addEffect("stability", 0.1);
        laborLaw.addEffect("production_efficiency", 0.15);
        laborLaw.addRequirement("year_1850");
        laws.add(laborLaw);
        
        Law educationLaw = new Law("Education Law", 
            "Universal education system", LawType.CULTURAL, 6, 160, 95, 1870);
        educationLaw.addEffect("technology_cost", -0.1);
        educationLaw.addEffect("administrative_efficiency", 0.15);
        educationLaw.addRequirement("year_1870");
        laws.add(educationLaw);
        
        Law suffrageLaw = new Law("Suffrage Law", 
            "Universal voting rights", LawType.SOCIAL, 6, 170, 100, 1890);
        suffrageLaw.addEffect("legitimacy", 0.25);
        suffrageLaw.addEffect("stability", 0.1);
        suffrageLaw.addRequirement("year_1890");
        laws.add(suffrageLaw);
        
        // Modern Laws (1900+)
        Law welfareLaw = new Law("Welfare Law", 
            "Social security and healthcare", LawType.SOCIAL, 7, 180, 110, 1920);
        welfareLaw.addEffect("stability", 0.15);
        welfareLaw.addEffect("population_growth", 0.1);
        welfareLaw.addRequirement("year_1920");
        laws.add(welfareLaw);
        
        Law environmentalLaw = new Law("Environmental Law", 
            "Environmental protection regulations", LawType.ADMINISTRATIVE, 7, 190, 115, 1950);
        environmentalLaw.addEffect("stability", 0.1);
        environmentalLaw.addEffect("prestige", 0.15);
        environmentalLaw.addRequirement("year_1950");
        laws.add(environmentalLaw);
        
        Law digitalLaw = new Law("Digital Law", 
            "Information technology regulations", LawType.CULTURAL, 7, 200, 120, 1980);
        digitalLaw.addEffect("technology_cost", -0.15);
        digitalLaw.addEffect("administrative_efficiency", 0.2);
        digitalLaw.addRequirement("year_1980");
        laws.add(digitalLaw);
        
        return laws;
    }
    
    public void addRequirement(String requirement) {
        requirements.add(requirement);
    }
    
    public void addEffect(String effect, double value) {
        effects.put(effect, value);
    }
    
    public boolean canEnact(Country country) {
        // Check if country meets requirements
        for (String requirement : requirements) {
            if (!checkRequirement(country, requirement)) {
                return false;
            }
        }
        return !isEnacted && country.getTreasury() >= enactmentCost;
    }
    
    private boolean checkRequirement(Country country, String requirement) {
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
            case "year_-450" -> {
                return country.getGameYear() >= -450;
            }
            case "year_-133" -> {
                return country.getGameYear() >= -133;
            }
            case "year_-100" -> {
                return country.getGameYear() >= -100;
            }
            case "year_-50" -> {
                return country.getGameYear() >= -50;
            }
            case "year_0" -> {
                return country.getGameYear() >= 0;
            }
            case "year_100" -> {
                return country.getGameYear() >= 100;
            }
            case "year_800" -> {
                return country.getGameYear() >= 800;
            }
            case "year_1000" -> {
                return country.getGameYear() >= 1000;
            }
            case "year_1100" -> {
                return country.getGameYear() >= 1100;
            }
            case "year_1500" -> {
                return country.getGameYear() >= 1500;
            }
            case "year_1600" -> {
                return country.getGameYear() >= 1600;
            }
            case "year_1650" -> {
                return country.getGameYear() >= 1650;
            }
            case "year_1750" -> {
                return country.getGameYear() >= 1750;
            }
            case "year_1780" -> {
                return country.getGameYear() >= 1780;
            }
            case "year_1790" -> {
                return country.getGameYear() >= 1790;
            }
            case "year_1850" -> {
                return country.getGameYear() >= 1850;
            }
            case "year_1870" -> {
                return country.getGameYear() >= 1870;
            }
            case "year_1890" -> {
                return country.getGameYear() >= 1890;
            }
            case "year_1920" -> {
                return country.getGameYear() >= 1920;
            }
            case "year_1950" -> {
                return country.getGameYear() >= 1950;
            }
            case "year_1980" -> {
                return country.getGameYear() >= 1980;
            }
            default -> {
                return true;
            }
        }
    }
    
    public void startEnactment(Country country) {
        if (canEnact(country)) {
            country.setTreasury(country.getTreasury() - enactmentCost);
            enactmentProgress = 1.0; // Start enactment
        }
    }
    
    public void updateEnactment() {
        if (enactmentProgress > 0 && enactmentProgress < enactmentTime) {
            enactmentProgress += 1.0; // Daily progress
        }
        
        if (enactmentProgress >= enactmentTime && !isEnacted) {
            isEnacted = true;
            enactmentProgress = 0.0; // Reset progress
        }
    }
    
    public void enact(Country country) {
        if (canEnact(country)) {
            // Apply effects to country
            for (Map.Entry<String, Double> effect : effects.entrySet()) {
                applyEffect(country, effect.getKey(), effect.getValue());
            }
            isEnacted = true;
        }
    }
    
    private void applyEffect(Country country, String effect, double value) {
        switch (effect) {
            case "stability" -> {
                country.setStability(country.getStability() + value);
            }
            case "legitimacy" -> {
                country.setLegitimacy(country.getLegitimacy() + value);
            }
            case "agriculture_income" -> {
                country.addModifier("Agriculture Income", value);
            }
            case "unrest" -> {
                country.addModifier("Unrest", value);
            }
            case "army_discipline" -> {
                country.addModifier("Army Discipline", value);
            }
            case "manpower" -> {
                country.addModifier("Manpower", value);
            }
            case "trade_efficiency" -> {
                country.addModifier("Trade Efficiency", value);
            }
            case "merchant_slots" -> {
                country.addModifier("Merchant Slots", value);
            }
            case "religious_unity" -> {
                country.addModifier("Religious Unity", value);
            }
            case "missionary_strength" -> {
                country.addModifier("Missionary Strength", value);
            }
            case "administrative_efficiency" -> {
                country.addModifier("Administrative Efficiency", value);
            }
            case "corruption" -> {
                country.addModifier("Corruption", value);
            }
            case "nobility_influence" -> {
                country.addModifier("Nobility Influence", value);
            }
            case "cavalry_combat_ability" -> {
                country.addModifier("Cavalry Combat Ability", value);
            }
            case "production_efficiency" -> {
                country.addModifier("Production Efficiency", value);
            }
            case "clergy_influence" -> {
                country.addModifier("Clergy Influence", value);
            }
            case "army_tradition" -> {
                country.addModifier("Army Tradition", value);
            }
            case "discipline" -> {
                country.addModifier("Discipline", value);
            }
            case "absolutism" -> {
                country.addModifier("Absolutism", value);
            }
            case "religious_tolerance" -> {
                country.addModifier("Religious Tolerance", value);
            }
            case "technology_cost" -> {
                country.addModifier("Technology Cost", value);
            }
            case "population_growth" -> {
                country.addModifier("Population Growth", value);
            }
            case "prestige" -> {
                country.addModifier("Prestige", value);
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LawType getType() { return type; }
    public int getTier() { return tier; }
    public int getEnactmentCost() { return enactmentCost; }
    public int getEnactmentTime() { return enactmentTime; }
    public double getEnactmentProgress() { return enactmentProgress; }
    public double getEnactmentProgressPercent() { 
        return Math.min(1.0, enactmentProgress / enactmentTime); 
    }
    public boolean isEnacted() { return isEnacted; }
    public int getYearUnlocked() { return yearUnlocked; }
    public List<String> getRequirements() { return new ArrayList<>(requirements); }
    public Map<String, Double> getEffects() { return new HashMap<>(effects); }
} 