package com.romagame.country;

import com.romagame.map.WorldMap;
import com.romagame.map.Country;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.romagame.military.War;

public class CountryManager {
    private WorldMap worldMap;
    private Map<String, Country> countries;
    private String playerCountry;
    
    public CountryManager(WorldMap worldMap) {
        this.worldMap = worldMap;
        this.countries = new HashMap<>();
        this.playerCountry = "France"; // Default player country
    }
    
    public void initializeCountries() {
        // Initialize all countries from the world map
        for (Country country : worldMap.getAllCountries()) {
            countries.put(country.getName(), country);
        }
    }
    
    public void update() {
        // Update all countries
        for (Country country : countries.values()) {
            country.update();
        }
    }
    
    public void processAI() {
        // Process AI decisions for non-player countries
        for (Country country : countries.values()) {
            if (!country.getName().equals(playerCountry)) {
                processAIDecisions(country);
            }
        }
    }
    
    private void processAIDecisions(Country country) {
        // Simple AI decision making
        if (country.getTreasury() > 50) {
            // Recruit units if treasury is good
            country.recruitUnit("Infantry", 1);
        }
        
        if (country.getStability() < 0) {
            // Try to improve stability
            country.setStability(country.getStability() + 0.1);
        }
    }
    
    public Country getCountry(String name) {
        return countries.get(name);
    }
    
    public List<Country> getAllCountries() {
        return new ArrayList<>(countries.values());
    }
    
    public Country getPlayerCountry() {
        return countries.get(playerCountry);
    }
    
    public void setPlayerCountry(String countryName) {
        if (countries.containsKey(countryName)) {
            this.playerCountry = countryName;
        }
    }
    
    public void declareWar(String attacker, String defender) {
        Country attackerCountry = countries.get(attacker);
        Country defenderCountry = countries.get(defender);
        
        if (attackerCountry != null && defenderCountry != null) {
            // Create war between countries
            War war = new War(attackerCountry, defenderCountry);
            // Add war to war manager
        }
    }
    
    public void makePeace(String country1, String country2) {
        // End war between countries
    }
    
    public void annexProvince(String country, String provinceId) {
        Country targetCountry = countries.get(country);
        if (targetCountry != null) {
            // Transfer province ownership
        }
    }
} 