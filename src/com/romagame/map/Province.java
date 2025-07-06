package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;

public class Province {
    public enum PopType {
        NOBLES, CITY_FOLK, CRAFTSMEN, PEASANTS, SLAVES, SERFS, SOLDIERS
    }
    private String id;
    private String name;
    private String owner;
    private double latitude;
    private double longitude;
    private String terrain;
    private String climate;
    private double development;
    private List<String> buildings;
    private List<String> modifiers;
    private boolean isCapital;
    private String culture;
    private String religion;
    private List<String> tradeGoods;
    private List<Location> locations;
    private Map<PopType, Integer> pops;
    private int population; // Deprecated, use pops instead
    private List<BuildingSlot> buildingSlots;
    private Map<String, Integer> goods;
    
    public Province(String id, String owner, double lat, double lon, String type) {
        this.id = id;
        this.name = id;
        this.owner = owner;
        this.latitude = lat;
        this.longitude = lon;
        this.terrain = determineTerrain(lat, lon);
        this.climate = determineClimate(lat, lon);
        this.development = 1.0;
        this.buildings = new ArrayList<>();
        this.modifiers = new ArrayList<>();
        this.isCapital = type.equals("Capital");
        this.culture = determineCulture(owner);
        this.religion = determineReligion(owner);
        this.tradeGoods = determineTradeGoods(lat, lon);
        this.locations = new ArrayList<>();
        this.pops = new EnumMap<>(PopType.class);
        this.goods = new HashMap<>();
        initializeLocations();
        initializePops();
        initializeBuildingSlots();
    }
    
    private String determineTerrain(double lat, double lon) {
        // Simplified terrain determination based on coordinates
        if (lat > 60) return "Tundra";
        if (lat > 45) return "Forest";
        if (lat > 30) return "Plains";
        if (lat > 15) return "Savanna";
        return "Desert";
    }
    
    private String determineClimate(double lat, double lon) {
        if (lat > 60) return "Cold";
        if (lat > 30) return "Temperate";
        if (lat > 15) return "Warm";
        return "Tropical";
    }
    
    private String determineCulture(String owner) {
        return switch (owner) {
            case "France", "Castile", "Portugal" -> "Latin";
            case "England", "Brandenburg", "Sweden", "Denmark" -> "Germanic";
            case "Muscovy" -> "Slavic";
            case "Ottomans", "Mamluks", "Tunisia", "Morocco" -> "Arabic";
            case "Ming", "Japan" -> "Chinese";
            case "Delhi" -> "Indian";
            case "Persia" -> "Persian";
            default -> "Tribal";
        };
    }
    
    private String determineReligion(String owner) {
        return switch (owner) {
            case "France", "Castile", "Portugal", "England", "Brandenburg", 
                 "Sweden", "Denmark", "Austria" -> "Catholic";
            case "Muscovy" -> "Orthodox";
            case "Ottomans", "Mamluks", "Tunisia", "Morocco" -> "Sunni";
            case "Ming", "Japan" -> "Confucian";
            case "Delhi" -> "Hindu";
            case "Persia" -> "Shia";
            case "Aztec", "Inca" -> "Pagan";
            default -> "Animist";
        };
    }
    
    private List<String> determineTradeGoods(double lat, double lon) {
        List<String> goods = new ArrayList<>();
        if (lat > 60) {
            goods.add("Furs");
            goods.add("Fish");
        } else if (lat > 45) {
            goods.add("Grain");
            goods.add("Wool");
        } else if (lat > 30) {
            goods.add("Wine");
            goods.add("Cloth");
        } else if (lat > 15) {
            goods.add("Spices");
            goods.add("Tea");
        } else {
            goods.add("Slaves");
            goods.add("Ivory");
        }
        return goods;
    }
    
    private void initializeLocations() {
        // For now, create 3 generic locations per province (can be improved with map data)
        for (int i = 1; i <= 3; i++) {
            locations.add(new Location("Location " + i));
        }
    }

    private void initializePops() {
        // Example starting values, can be customized
        pops.put(PopType.NOBLES, 100);
        pops.put(PopType.CITY_FOLK, 2000);
        pops.put(PopType.CRAFTSMEN, 800);
        pops.put(PopType.PEASANTS, 5000);
        pops.put(PopType.SLAVES, 1200);
        pops.put(PopType.SERFS, 1500);
        pops.put(PopType.SOLDIERS, 300);
        updatePopulation();
    }

    public void updatePopulation() {
        this.population = pops.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<PopType, Integer> getPops() {
        return pops;
    }
    public void setPop(PopType type, int value) {
        pops.put(type, value);
        updatePopulation();
    }
    public int getPop(PopType type) {
        return pops.getOrDefault(type, 0);
    }
    public int getPopulation() {
        return population;
    }

    public List<Location> getLocations() {
        return locations;
    }
    public Location getLocationByName(String name) {
        for (Location loc : locations) {
            if (loc.getName().equals(name)) return loc;
        }
        return null;
    }
    public void moveTroops(String from, String to, int amount) {
        Location src = getLocationByName(from);
        Location dst = getLocationByName(to);
        if (src != null && dst != null && src.troops >= amount) {
            src.troops -= amount;
            dst.troops += amount;
        }
    }

    public static class Location {
        private String name;
        private int troops;
        public Location(String name) {
            this.name = name;
            this.troops = 0;
        }
        public String getName() { return name; }
        public int getTroops() { return troops; }
        public void setTroops(int troops) { this.troops = troops; }
        public void addTroops(int amount) { this.troops += amount; }
        public void removeTroops(int amount) { this.troops = Math.max(0, this.troops - amount); }
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTerrain() { return terrain; }
    public String getClimate() { return climate; }
    public double getDevelopment() { return development; }
    public void setDevelopment(double development) { this.development = development; }
    public List<String> getBuildings() { return buildings; }
    public List<String> getModifiers() { return modifiers; }
    public boolean isCapital() { return isCapital; }
    public String getCulture() { return culture; }
    public String getReligion() { return religion; }
    public List<String> getTradeGoods() { return tradeGoods; }
    
    public void addBuilding(String building) {
        if (!buildings.contains(building)) {
            buildings.add(building);
        }
    }
    
    public void addModifier(String modifier) {
        if (!modifiers.contains(modifier)) {
            modifiers.add(modifier);
        }
    }

    public static class BuildingSlot {
        private String type;
        private int level;
        public BuildingSlot(String type, int level) {
            this.type = type;
            this.level = level;
        }
        public String getType() { return type; }
        public int getLevel() { return level; }
        public void setType(String type) { this.type = type; }
        public void setLevel(int level) { this.level = level; }
    }

    private void initializeBuildingSlots() {
        buildingSlots = new ArrayList<>();
        // Example: 3 empty slots
        for (int i = 0; i < 3; i++) {
            buildingSlots.add(new BuildingSlot("Empty", 0));
        }
    }

    public List<BuildingSlot> getBuildingSlots() { return buildingSlots; }
    public void setBuildingSlot(int idx, String type, int level) {
        if (idx >= 0 && idx < buildingSlots.size()) {
            buildingSlots.get(idx).setType(type);
            buildingSlots.get(idx).setLevel(level);
        }
    }

    public Map<String, Integer> getGoods() { return goods; }
    public void setGood(String good, int amount) { goods.put(good, amount); }
} 