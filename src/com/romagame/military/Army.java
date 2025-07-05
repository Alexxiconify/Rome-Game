package com.romagame.military;

import java.util.Map;
import java.util.HashMap;

public class Army {
    private String country;
    private String name;
    private Map<String, Integer> units;
    private double morale;
    private double organization;
    private String location;
    private boolean isEngaged;
    
    public Army(String country, String name) {
        this.country = country;
        this.name = name;
        this.units = new HashMap<>();
        this.morale = 1.0;
        this.organization = 1.0;
        this.location = "Unknown";
        this.isEngaged = false;
        
        // Initialize with basic units
        units.put("Infantry", 0);
        units.put("Cavalry", 0);
        units.put("Artillery", 0);
    }
    
    public void update() {
        // Army maintenance and recovery
        if (!isEngaged) {
            recoverMorale();
            recoverOrganization();
        }
    }
    
    private void recoverMorale() {
        if (morale < 1.0) {
            morale = Math.min(1.0, morale + 0.01);
        }
    }
    
    private void recoverOrganization() {
        if (organization < 1.0) {
            organization = Math.min(1.0, organization + 0.02);
        }
    }
    
    public void addUnit(String type, int amount) {
        units.merge(type, amount, Integer::sum);
    }
    
    public void removeUnit(String type, int amount) {
        if (units.containsKey(type)) {
            int current = units.get(type);
            units.put(type, Math.max(0, current - amount));
        }
    }
    
    public void engage() {
        isEngaged = true;
        morale -= 0.1;
        organization -= 0.2;
    }
    
    public void disengage() {
        isEngaged = false;
    }
    
    public int getTotalStrength() {
        return units.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public double getCombatPower() {
        return getTotalStrength() * morale * organization;
    }
    
    // Getters and setters
    public String getCountry() { return country; }
    public String getName() { return name; }
    public Map<String, Integer> getUnits() { return units; }
    public double getMorale() { return morale; }
    public void setMorale(double morale) { this.morale = Math.max(0.0, Math.min(1.0, morale)); }
    public double getOrganization() { return organization; }
    public void setOrganization(double organization) { this.organization = Math.max(0.0, Math.min(1.0, organization)); }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isEngaged() { return isEngaged; }
} 