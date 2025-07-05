package com.romagame.military;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MilitaryManager {
    private List<War> activeWars;
    private Map<String, Army> armies;
    private Map<String, Navy> navies;
    
    public MilitaryManager() {
        activeWars = new ArrayList<>();
        armies = new HashMap<>();
        navies = new HashMap<>();
    }
    
    public void update() {
        // Update all military systems
        updateWars();
        updateArmies();
        updateNavies();
    }
    
    private void updateWars() {
        for (War war : activeWars) {
            war.update();
        }
        // Remove ended wars
        activeWars.removeIf(war -> !war.isActive());
    }
    
    private void updateArmies() {
        for (Army army : armies.values()) {
            army.update();
        }
    }
    
    private void updateNavies() {
        for (Navy navy : navies.values()) {
            navy.update();
        }
    }
    
    public void addWar(War war) {
        activeWars.add(war);
    }
    
    public List<War> getActiveWars() {
        return new ArrayList<>(activeWars);
    }
    
    public Army createArmy(String country, String name) {
        Army army = new Army(country, name);
        armies.put(name, army);
        return army;
    }
    
    public Navy createNavy(String country, String name) {
        Navy navy = new Navy(country, name);
        navies.put(name, navy);
        return navy;
    }
    
    public Army getArmy(String name) {
        return armies.get(name);
    }
    
    public Navy getNavy(String name) {
        return navies.get(name);
    }
} 