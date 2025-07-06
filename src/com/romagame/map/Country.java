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
    
    // New fields for laws, reforms, and technology
    private List<Law> enactedLaws;
    private List<Law> enactingLaws;
    private List<GovernmentReform> implementedReforms;
    private List<String> researchedTechnologies;
    private List<String> researchingTechnologies;

    // Group-based mechanics (static for all countries)
    public static final Map<NationType, List<String>> GROUP_IDEAS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_REFORMS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_LAWS = new HashMap<>();
    public static final Map<NationType, List<String>> GROUP_SOLDIER_TYPES = new HashMap<>();

    // --- Military Tech System ---
    /**
     * Military tech level (1-20). Higher is better. Impacts combat.
     *
     * Example starting values:
     *   Roman Empire: 5
     *   Parthia: 4
     *   Armenia, Dacia, Sarmatia, Britons, Persia, Eastern_Han_Empire: 3-4
     *   Medium nations: 2-3
     *   Small/minor nations: 1-2
     */
    private int militaryTechLevel;

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
        
        // Group reforms
        GROUP_REFORMS.put(NationType.ROMAN, List.of("Imperial Administration", "Legionary Discipline", "Roman Roads"));
        GROUP_REFORMS.put(NationType.GERMANIC, List.of("Tribal Council", "Germanic Warriors", "Forest Warfare"));
        GROUP_REFORMS.put(NationType.CELTIC, List.of("Tribal Council", "Celtic Warriors", "Island Defense"));
        GROUP_REFORMS.put(NationType.EASTERN, List.of("Absolute Monarchy", "Cavalry Tradition", "Silk Road Control"));
        GROUP_REFORMS.put(NationType.AFRICAN, List.of("Tribal Council", "Desert Adaptation", "Trade Routes"));
        GROUP_REFORMS.put(NationType.ARABIAN, List.of("Merchant Republic", "Arabian Trade", "Desert Warfare"));
        GROUP_REFORMS.put(NationType.INDIAN, List.of("Theocratic State", "Silk Road Trade", "Buddhist Influence"));
        GROUP_REFORMS.put(NationType.TRIBAL, List.of("Tribal Council", "Tribal Unity", "Warrior Spirit"));
        
        // Group laws
        GROUP_LAWS.put(NationType.ROMAN, List.of("Twelve Tables", "Lex Militaris", "Lex Agraria"));
        GROUP_LAWS.put(NationType.GERMANIC, List.of("Tribal Law", "Warrior Code", "Forest Law"));
        GROUP_LAWS.put(NationType.CELTIC, List.of("Tribal Law", "Warrior Code", "Island Law"));
        GROUP_LAWS.put(NationType.EASTERN, List.of("Royal Law", "Cavalry Law", "Trade Law"));
        GROUP_LAWS.put(NationType.AFRICAN, List.of("Tribal Law", "Desert Law", "Trade Law"));
        GROUP_LAWS.put(NationType.ARABIAN, List.of("Merchant Law", "Trade Law", "Desert Law"));
        GROUP_LAWS.put(NationType.INDIAN, List.of("Religious Law", "Trade Law", "Cultural Law"));
        GROUP_LAWS.put(NationType.TRIBAL, List.of("Tribal Law", "Warrior Code", "Unity Law"));
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
        
        // Initialize new collections
        this.enactedLaws = new ArrayList<>();
        this.enactingLaws = new ArrayList<>();
        this.implementedReforms = new ArrayList<>();
        this.researchedTechnologies = new ArrayList<>();
        this.researchingTechnologies = new ArrayList<>();
        
        initializeCountry();
        // Assign starting ruler for major nations
        switch (name) {
            case "Roman Empire" -> {
                setRuler(new com.romagame.government.Ruler("Trajan", 45));
                this.militaryTechLevel = 5;
            }
            case "Parthia" -> {
                setRuler(new com.romagame.government.Ruler("Osroes I", 50));
                this.militaryTechLevel = 4;
            }
            case "Armenia", "Dacia", "Sarmatia", "Britons", "Persia", "Eastern_Han_Empire" -> {
                setRuler(new com.romagame.government.Ruler(name + " Ruler", 40));
                this.militaryTechLevel = 3;
            }
            default -> {
                setRuler(new com.romagame.government.Ruler(name + " Ruler", 40));
                // Assign tech based on size/type (simple heuristic)
                if (provinces.size() >= 8) this.militaryTechLevel = 3;
                else if (provinces.size() >= 4) this.militaryTechLevel = 2;
                else this.militaryTechLevel = 1;
            }
        }
    }
    
    private void initializeCountry() {
        // Set default values based on country type
        this.governmentType = determineGovernmentType();
        this.religion = determineReligion();
        this.culture = determineCulture();
        
        // Set starting values based on nation
        setStartingValues();
        
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
        
        // Add starting laws, reforms, and technologies
        addStartingLaws();
        addStartingReforms();
        addStartingTechnologies();
    }
    
    private void setStartingValues() {
        // Set starting values based on nation name
        switch (name) {
            case "Roman Empire" -> {
                this.prestige = 50.0;
                this.legitimacy = 0.9;
                this.stability = 0.7;
                this.treasury = 500.0;
                this.income = 25.0;
                this.expenses = 15.0;
                // Starting military
                military.put("Infantry", 5000);
                military.put("Cavalry", 1000);
                military.put("Artillery", 50);
                military.put("Ships", 20);
                // Starting resources
                resources.put("Gold", 100.0);
                resources.put("Iron", 200.0);
                resources.put("Grain", 500.0);
                resources.put("Wool", 150.0);
                resources.put("Wine", 300.0);
                resources.put("Spices", 50.0);
            }
            case "Parthia" -> {
                this.prestige = 35.0;
                this.legitimacy = 0.8;
                this.stability = 0.6;
                this.treasury = 300.0;
                this.income = 18.0;
                this.expenses = 12.0;
                // Starting military
                military.put("Infantry", 3000);
                military.put("Cavalry", 2000);
                military.put("Artillery", 30);
                military.put("Ships", 10);
                // Starting resources
                resources.put("Gold", 80.0);
                resources.put("Iron", 120.0);
                resources.put("Grain", 300.0);
                resources.put("Wool", 100.0);
                resources.put("Wine", 150.0);
                resources.put("Spices", 100.0);
            }
            case "Armenia" -> {
                this.prestige = 20.0;
                this.legitimacy = 0.7;
                this.stability = 0.5;
                this.treasury = 150.0;
                this.income = 12.0;
                this.expenses = 8.0;
                // Starting military
                military.put("Infantry", 2000);
                military.put("Cavalry", 800);
                military.put("Artillery", 20);
                military.put("Ships", 5);
                // Starting resources
                resources.put("Gold", 50.0);
                resources.put("Iron", 80.0);
                resources.put("Grain", 200.0);
                resources.put("Wool", 80.0);
                resources.put("Wine", 100.0);
                resources.put("Spices", 30.0);
            }
            case "Dacia" -> {
                this.prestige = 15.0;
                this.legitimacy = 0.6;
                this.stability = 0.4;
                this.treasury = 100.0;
                this.income = 10.0;
                this.expenses = 7.0;
                // Starting military
                military.put("Infantry", 2500);
                military.put("Cavalry", 600);
                military.put("Artillery", 15);
                military.put("Ships", 3);
                // Starting resources
                resources.put("Gold", 40.0);
                resources.put("Iron", 100.0);
                resources.put("Grain", 180.0);
                resources.put("Wool", 120.0);
                resources.put("Wine", 80.0);
                resources.put("Spices", 20.0);
            }
            case "Sarmatia" -> {
                this.prestige = 12.0;
                this.legitimacy = 0.5;
                this.stability = 0.3;
                this.treasury = 80.0;
                this.income = 8.0;
                this.expenses = 6.0;
                // Starting military
                military.put("Infantry", 1800);
                military.put("Cavalry", 1200);
                military.put("Artillery", 10);
                military.put("Ships", 2);
                // Starting resources
                resources.put("Gold", 30.0);
                resources.put("Iron", 60.0);
                resources.put("Grain", 150.0);
                resources.put("Wool", 200.0);
                resources.put("Wine", 50.0);
                resources.put("Spices", 15.0);
            }
            case "Britons" -> {
                this.prestige = 10.0;
                this.legitimacy = 0.4;
                this.stability = 0.3;
                this.treasury = 60.0;
                this.income = 7.0;
                this.expenses = 5.0;
                // Starting military
                military.put("Infantry", 1500);
                military.put("Cavalry", 400);
                military.put("Artillery", 8);
                military.put("Ships", 8);
                // Starting resources
                resources.put("Gold", 25.0);
                resources.put("Iron", 70.0);
                resources.put("Grain", 120.0);
                resources.put("Wool", 250.0);
                resources.put("Wine", 40.0);
                resources.put("Spices", 10.0);
            }
            case "Persia" -> {
                this.prestige = 25.0;
                this.legitimacy = 0.75;
                this.stability = 0.55;
                this.treasury = 200.0;
                this.income = 15.0;
                this.expenses = 10.0;
                // Starting military
                military.put("Infantry", 2200);
                military.put("Cavalry", 1500);
                military.put("Artillery", 25);
                military.put("Ships", 12);
                // Starting resources
                resources.put("Gold", 70.0);
                resources.put("Iron", 90.0);
                resources.put("Grain", 250.0);
                resources.put("Wool", 90.0);
                resources.put("Wine", 120.0);
                resources.put("Spices", 80.0);
            }
            case "Eastern_Han_Empire" -> {
                this.prestige = 40.0;
                this.legitimacy = 0.85;
                this.stability = 0.65;
                this.treasury = 400.0;
                this.income = 22.0;
                this.expenses = 14.0;
                // Starting military
                military.put("Infantry", 4000);
                military.put("Cavalry", 1200);
                military.put("Artillery", 40);
                military.put("Ships", 25);
                // Starting resources
                resources.put("Gold", 120.0);
                resources.put("Iron", 150.0);
                resources.put("Grain", 400.0);
                resources.put("Wool", 110.0);
                resources.put("Wine", 200.0);
                resources.put("Spices", 150.0);
            }
            default -> {
                // Default values for other nations
                this.prestige = 5.0;
                this.legitimacy = 0.5;
                this.stability = 0.3;
                this.treasury = 50.0;
                this.income = 5.0;
                this.expenses = 3.0;
                // Starting military
                military.put("Infantry", 500);
                military.put("Cavalry", 200);
                military.put("Artillery", 5);
                military.put("Ships", 1);
                // Starting resources
                resources.put("Gold", 20.0);
                resources.put("Iron", 30.0);
                resources.put("Grain", 80.0);
                resources.put("Wool", 60.0);
                resources.put("Wine", 30.0);
                resources.put("Spices", 5.0);
            }
        }
    }
    
    private void addStartingLaws() {
        // Add nation-specific starting laws
        List<String> startingLaws = GROUP_LAWS.getOrDefault(nationType, new ArrayList<>());
        for (String lawName : startingLaws) {
            // Find and enact the law
            for (Law law : Law.createHistoricalLaws()) {
                if (law.getName().equals(lawName)) {
                    enactedLaws.add(law);
                    break;
                }
            }
        }
    }
    
    private void addStartingReforms() {
        // Add nation-specific starting reforms
        List<String> startingReforms = GROUP_REFORMS.getOrDefault(nationType, new ArrayList<>());
        for (String reformName : startingReforms) {
            // Find and implement the reform
            for (GovernmentReform reform : GovernmentReform.createTieredReforms()) {
                if (reform.getName().equals(reformName)) {
                    implementedReforms.add(reform);
                    break;
                }
            }
        }
    }
    
    private void addStartingTechnologies() {
        // Add basic technologies based on nation type
        switch (nationType) {
            case ROMAN -> {
                researchedTechnologies.add("Military Tech");
                researchedTechnologies.add("Administrative Tech");
                researchedTechnologies.add("Infrastructure Tech");
            }
            case GERMANIC -> {
                researchedTechnologies.add("Military Tech");
                researchedTechnologies.add("Trade Tech");
            }
            case CELTIC -> {
                researchedTechnologies.add("Military Tech");
                researchedTechnologies.add("Trade Tech");
            }
            case EASTERN -> {
                researchedTechnologies.add("Military Tech");
                researchedTechnologies.add("Diplomatic Tech");
                researchedTechnologies.add("Trade Tech");
            }
            case AFRICAN -> {
                researchedTechnologies.add("Trade Tech");
                researchedTechnologies.add("Diplomatic Tech");
            }
            case ARABIAN -> {
                researchedTechnologies.add("Trade Tech");
                researchedTechnologies.add("Diplomatic Tech");
                researchedTechnologies.add("Naval Tech");
            }
            case INDIAN -> {
                researchedTechnologies.add("Trade Tech");
                researchedTechnologies.add("Administrative Tech");
            }
            default -> {
                researchedTechnologies.add("Military Tech");
            }
        }
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
        
        // Update law enactment progress
        updateLawEnactment();
        
        // Update reform implementation progress
        updateReformImplementation();
        
        // Update technology research progress
        updateTechnologyResearch();
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
    
    private void updateLawEnactment() {
        // Update progress for laws being enacted
        for (Law law : enactingLaws) {
            law.updateEnactment();
            if (law.isEnacted()) {
                enactedLaws.add(law);
            }
        }
        // Remove completed laws from enacting list
        enactingLaws.removeIf(Law::isEnacted);
    }
    
    private void updateReformImplementation() {
        // Update progress for reforms being implemented
        // This would be similar to law enactment
    }
    
    private void updateTechnologyResearch() {
        // Update progress for technologies being researched
        // This would be handled by TechnologyManager
    }
    
    // Law management methods
    public void startLawEnactment(Law law) {
        if (law.canEnact(this) && !enactedLaws.contains(law) && !enactingLaws.contains(law)) {
            law.startEnactment(this);
            enactingLaws.add(law);
        }
    }
    
    public List<Law> getEnactedLaws() {
        return new ArrayList<>(enactedLaws);
    }
    
    public List<Law> getEnactingLaws() {
        return new ArrayList<>(enactingLaws);
    }
    
    public boolean hasLaw(String lawName) {
        return enactedLaws.stream().anyMatch(law -> law.getName().equals(lawName));
    }
    
    // Reform management methods
    public void implementReform(GovernmentReform reform) {
        if (reform.canImplement(this) && !implementedReforms.contains(reform)) {
            reform.implement(this);
            implementedReforms.add(reform);
        }
    }
    
    public List<GovernmentReform> getImplementedReforms() {
        return new ArrayList<>(implementedReforms);
    }
    
    public boolean hasReform(String reformName) {
        return implementedReforms.stream().anyMatch(reform -> reform.getName().equals(reformName));
    }
    
    // Technology management methods
    public void startTechnologyResearch(String techName) {
        if (!researchedTechnologies.contains(techName) && !researchingTechnologies.contains(techName)) {
            researchingTechnologies.add(techName);
        }
    }
    
    public void completeTechnologyResearch(String techName) {
        if (researchingTechnologies.contains(techName)) {
            researchingTechnologies.remove(techName);
            researchedTechnologies.add(techName);
        }
    }
    
    public List<String> getResearchedTechnologies() {
        return new ArrayList<>(researchedTechnologies);
    }
    
    public List<String> getResearchingTechnologies() {
        return new ArrayList<>(researchingTechnologies);
    }
    
    public boolean hasTechnology(String techName) {
        return researchedTechnologies.contains(techName);
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

    // --- Military Tech Getters/Setters ---
    public int getMilitaryTechLevel() { return militaryTechLevel; }
    public void setMilitaryTechLevel(int level) {
        this.militaryTechLevel = Math.max(1, Math.min(20, level));
    }
} 