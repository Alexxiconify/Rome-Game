package com.romagame.military;

import java.util.Map;
import java.util.HashMap;

public class Navy {
    private String country;
    private String name;
    private Map<String, Integer> ships;
    private double morale;
    private String location;
    private boolean isEngaged;
    
    public Navy(String country, String name) {
        this.country = country;
        this.name = name;
        this.ships = new HashMap<>();
        this.morale = 1.0;
        this.location = "Unknown";
        this.isEngaged = false;
        
        // Initialize with basic ships
        ships.put("Light Ships", 0);
        ships.put("Heavy Ships", 0);
        ships.put("Galleys", 0);
        ships.put("Transports", 0);
    }
    
    public void update() {
        // Navy maintenance and recovery
        if (!isEngaged) {
            recoverMorale();
        }
    }
    
    private void recoverMorale() {
        if (morale < 1.0) {
            morale = Math.min(1.0, morale + 0.01);
        }
    }
    
    public void addShip(String type, int amount) {
        ships.merge(type, amount, Integer::sum);
    }
    
    public void removeShip(String type, int amount) {
        if (ships.containsKey(type)) {
            int current = ships.get(type);
            ships.put(type, Math.max(0, current - amount));
        }
    }
    
    public void engage() {
        isEngaged = true;
        morale -= 0.1;
    }
    
    public void disengage() {
        isEngaged = false;
    }
    
    public int getTotalStrength() {
        return ships.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public double getCombatPower() {
        return getTotalStrength() * morale;
    }
    
    // Getters and setters
    public String getCountry() { return country; }
    public String getName() { return name; }
    public Map<String, Integer> getShips() { return ships; }
    public double getMorale() { return morale; }
    public void setMorale(double morale) { this.morale = Math.max(0.0, Math.min(1.0, morale)); }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isEngaged() { return isEngaged; }
} 