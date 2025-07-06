package com.romagame.population;

import com.romagame.map.Province;
import com.romagame.map.Province.PopType;
import com.romagame.map.Country;
import java.util.*;
import java.util.Random;

public class PopulationManager {
    private Random random;
    private Map<String, DevelopmentAction> developmentActions;
    private Map<String, BuildingAction> buildingActions;
    
    public enum DevelopmentType {
        ADMINISTRATIVE, DIPLOMATIC, MILITARY, TRADE, CULTURE, INFRASTRUCTURE
    }
    
    public PopulationManager() {
        this.random = new Random();
        this.developmentActions = new HashMap<>();
        this.buildingActions = new HashMap<>();
        initializeActions();
    }
    
    private void initializeActions() {
        // Development actions that pops can perform
        developmentActions.put("Improve Roads", new DevelopmentAction("Improve Roads", DevelopmentType.INFRASTRUCTURE, 50, 0.5));
        developmentActions.put("Expand Markets", new DevelopmentAction("Expand Markets", DevelopmentType.TRADE, 75, 0.8));
        developmentActions.put("Build Schools", new DevelopmentAction("Build Schools", DevelopmentType.ADMINISTRATIVE, 100, 1.2));
        developmentActions.put("Construct Temples", new DevelopmentAction("Construct Temples", DevelopmentType.CULTURE, 80, 1.0));
        developmentActions.put("Improve Defenses", new DevelopmentAction("Improve Defenses", DevelopmentType.MILITARY, 90, 1.1));
        developmentActions.put("Establish Trade Routes", new DevelopmentAction("Establish Trade Routes", DevelopmentType.DIPLOMATIC, 120, 1.5));
        
        // Building actions
        buildingActions.put("Market", new BuildingAction("Market", "Trade", 50, 0.3, "Increases trade income"));
        buildingActions.put("Temple", new BuildingAction("Temple", "Culture", 60, 0.4, "Increases population happiness"));
        buildingActions.put("Barracks", new BuildingAction("Barracks", "Military", 80, 0.5, "Increases recruitment speed"));
        buildingActions.put("Aqueduct", new BuildingAction("Aqueduct", "Infrastructure", 100, 0.6, "Increases population growth"));
        buildingActions.put("Library", new BuildingAction("Library", "Administrative", 90, 0.5, "Increases administrative efficiency"));
        buildingActions.put("Harbor", new BuildingAction("Harbor", "Trade", 120, 0.7, "Increases naval capacity"));
        buildingActions.put("Walls", new BuildingAction("Walls", "Military", 150, 0.8, "Increases defense bonus"));
        buildingActions.put("University", new BuildingAction("University", "Administrative", 200, 1.0, "Increases technology research"));
    }
    
    public void processPopulationDevelopment(Province province, Country country) {
        // Process development actions based on population types
        Map<PopType, Integer> pops = province.getPops();
        
        for (Map.Entry<PopType, Integer> entry : pops.entrySet()) {
            PopType popType = entry.getKey();
            int popCount = entry.getValue();
            
            if (popCount > 0) {
                processPopDevelopment(province, country, popType, popCount);
            }
        }
    }
    
    private void processPopDevelopment(Province province, Country country, PopType popType, int popCount) {
        // Different pop types have different development preferences
        switch (popType) {
            case NOBLES:
                processNobleDevelopment(province, country, popCount);
                break;
            case CITY_FOLK:
                processCityFolkDevelopment(province, country, popCount);
                break;
            case CRAFTSMEN:
                processCraftsmenDevelopment(province, country, popCount);
                break;
            case PEASANTS:
                processPeasantDevelopment(province, country, popCount);
                break;
            case SLAVES:
                processSlaveDevelopment(province, country, popCount);
                break;
            case SERFS:
                processSerfDevelopment(province, country, popCount);
                break;
            case SOLDIERS:
                processSoldierDevelopment(province, country, popCount);
                break;
        }
    }
    
    private void processNobleDevelopment(Province province, Country country, int popCount) {
        // Nobles focus on administrative and cultural development
        if (random.nextDouble() < 0.3) { // 30% chance
            String action = random.nextDouble() < 0.6 ? "Build Schools" : "Construct Temples";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus());
                
                // Add building if appropriate
                if (action.equals("Construct Temples")) {
                    addBuildingToProvince(province, "Temple");
                }
            }
        }
    }
    
    private void processCityFolkDevelopment(Province province, Country country, int popCount) {
        // City folk focus on trade and infrastructure
        if (random.nextDouble() < 0.4) { // 40% chance
            String action = random.nextDouble() < 0.7 ? "Expand Markets" : "Improve Roads";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus());
                
                // Add building if appropriate
                if (action.equals("Expand Markets")) {
                    addBuildingToProvince(province, "Market");
                }
            }
        }
    }
    
    private void processCraftsmenDevelopment(Province province, Country country, int popCount) {
        // Craftsmen focus on infrastructure and trade
        if (random.nextDouble() < 0.35) { // 35% chance
            String action = random.nextDouble() < 0.5 ? "Improve Roads" : "Expand Markets";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus());
            }
        }
    }
    
    private void processPeasantDevelopment(Province province, Country country, int popCount) {
        // Peasants focus on basic infrastructure
        if (random.nextDouble() < 0.25) { // 25% chance
            String action = "Improve Roads";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus());
            }
        }
    }
    
    private void processSlaveDevelopment(Province province, Country country, int popCount) {
        // Slaves have limited development capabilities
        if (random.nextDouble() < 0.1) { // 10% chance
            String action = "Improve Roads";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus() * 0.5); // Reduced bonus
            }
        }
    }
    
    private void processSerfDevelopment(Province province, Country country, int popCount) {
        // Serfs focus on basic infrastructure
        if (random.nextDouble() < 0.2) { // 20% chance
            String action = "Improve Roads";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus() * 0.7); // Reduced bonus
            }
        }
    }
    
    private void processSoldierDevelopment(Province province, Country country, int popCount) {
        // Soldiers focus on military infrastructure
        if (random.nextDouble() < 0.3) { // 30% chance
            String action = "Improve Defenses";
            DevelopmentAction devAction = developmentActions.get(action);
            
            if (country.getTreasury() >= devAction.getCost()) {
                country.setTreasury(country.getTreasury() - devAction.getCost());
                province.setDevelopment(province.getDevelopment() + devAction.getDevelopmentBonus());
                
                // Add military building
                addBuildingToProvince(province, "Barracks");
            }
        }
    }
    
    private void addBuildingToProvince(Province province, String buildingType) {
        BuildingAction buildingAction = buildingActions.get(buildingType);
        if (buildingAction != null) {
            List<String> buildings = province.getBuildings();
            if (!buildings.contains(buildingType)) {
                buildings.add(buildingType);
                // Apply building effects
                applyBuildingEffects(province, buildingAction);
            }
        }
    }
    
    private void applyBuildingEffects(Province province, BuildingAction building) {
        switch (building.getCategory()) {
            case "Trade":
                province.addModifier("Trade Income +10%");
                break;
            case "Culture":
                province.addModifier("Population Happiness +5%");
                break;
            case "Military":
                province.addModifier("Recruitment Speed +25%");
                break;
            case "Infrastructure":
                province.addModifier("Population Growth +15%");
                break;
            case "Administrative":
                province.addModifier("Administrative Efficiency +10%");
                break;
        }
    }
    
    public List<DevelopmentAction> getAvailableDevelopmentActions() {
        return new ArrayList<>(developmentActions.values());
    }
    
    public List<BuildingAction> getAvailableBuildingActions() {
        return new ArrayList<>(buildingActions.values());
    }
    
    public static class DevelopmentAction {
        private String name;
        private DevelopmentType type;
        private double cost;
        private double developmentBonus;
        
        public DevelopmentAction(String name, DevelopmentType type, double cost, double developmentBonus) {
            this.name = name;
            this.type = type;
            this.cost = cost;
            this.developmentBonus = developmentBonus;
        }
        
        // Getters
        public String getName() { return name; }
        public DevelopmentType getType() { return type; }
        public double getCost() { return cost; }
        public double getDevelopmentBonus() { return developmentBonus; }
    }
    
    public static class BuildingAction {
        private String name;
        private String category;
        private double cost;
        private double maintenance;
        private String description;
        
        public BuildingAction(String name, String category, double cost, double maintenance, String description) {
            this.name = name;
            this.category = category;
            this.cost = cost;
            this.maintenance = maintenance;
            this.description = description;
        }
        
        // Getters
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getCost() { return cost; }
        public double getMaintenance() { return maintenance; }
        public String getDescription() { return description; }
    }
} 