package com.romagame.monuments;

import com.romagame.map.Country;
import java.util.*;

public class MonumentManager {
    private Map<String, WorldMonument> monuments;
    private Map<String, WorldMonument> availableMonuments;
    
    public MonumentManager() {
        this.monuments = new HashMap<>();
        this.availableMonuments = new HashMap<>();
        initializeMonuments();
    }
    
    private void initializeMonuments() {
        // Create famous world monuments
        WorldMonument colosseum = WorldMonument.createColosseum();
        WorldMonument parthenon = WorldMonument.createParthenon();
        WorldMonument greatWall = WorldMonument.createGreatWall();
        WorldMonument hangingGardens = WorldMonument.createHangingGardens();
        WorldMonument pyramids = WorldMonument.createPyramids();
        WorldMonument stonehenge = WorldMonument.createStonehenge();
        WorldMonument persepolis = WorldMonument.createPersepolis();
        WorldMonument theaterOfDionysus = WorldMonument.createTheaterOfDionysus();
        
        // Add to available monuments
        availableMonuments.put("Colosseum", colosseum);
        availableMonuments.put("Parthenon", parthenon);
        availableMonuments.put("Great Wall", greatWall);
        availableMonuments.put("Hanging Gardens", hangingGardens);
        availableMonuments.put("Pyramids", pyramids);
        availableMonuments.put("Stonehenge", stonehenge);
        availableMonuments.put("Persepolis", persepolis);
        availableMonuments.put("Theater of Dionysus", theaterOfDionysus);
    }
    
    public boolean startMonumentConstruction(String monumentName, Country country) {
        WorldMonument monument = availableMonuments.get(monumentName);
        if (monument == null || monument.isBuilt()) {
            return false; // Monument not available or already built
        }
        
        if (!monument.canBuild(country)) {
            return false; // Country cannot build this monument
        }
        
        if (country.getTreasury() < monument.getConstructionCost()) {
            return false; // Not enough funds
        }
        
        // Start construction
        monument.startConstruction(country.getName());
        country.setTreasury(country.getTreasury() - monument.getConstructionCost());
        
        // Move from available to active monuments
        availableMonuments.remove(monumentName);
        monuments.put(monumentName, monument);
        
        return true;
    }
    
    public void updateMonumentConstruction(String monumentName, int workDone) {
        WorldMonument monument = monuments.get(monumentName);
        if (monument != null && !monument.isBuilt()) {
            monument.updateConstruction(workDone);
        }
    }
    
    public List<WorldMonument> getAvailableMonuments() {
        return new ArrayList<>(availableMonuments.values());
    }
    
    public List<WorldMonument> getActiveMonuments() {
        return new ArrayList<>(monuments.values());
    }
    
    public List<WorldMonument> getBuiltMonuments() {
        List<WorldMonument> builtMonuments = new ArrayList<>();
        for (WorldMonument monument : monuments.values()) {
            if (monument.isBuilt()) {
                builtMonuments.add(monument);
            }
        }
        return builtMonuments;
    }
    
    public WorldMonument getMonument(String name) {
        WorldMonument monument = monuments.get(name);
        if (monument == null) {
            monument = availableMonuments.get(name);
        }
        return monument;
    }
    
    public List<WorldMonument> getMonumentsForCountry(Country country) {
        List<WorldMonument> countryMonuments = new ArrayList<>();
        
        // Check available monuments
        for (WorldMonument monument : availableMonuments.values()) {
            if (monument.canBuild(country)) {
                countryMonuments.add(monument);
            }
        }
        
        // Check active/built monuments owned by this country
        for (WorldMonument monument : monuments.values()) {
            if (country.getName().equals(monument.getOwner())) {
                countryMonuments.add(monument);
            }
        }
        
        return countryMonuments;
    }
} 