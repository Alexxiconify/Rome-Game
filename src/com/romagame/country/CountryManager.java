package com.romagame.country;

import com.romagame.map.WorldMap;
import com.romagame.map.Country;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.military.MilitaryManager;
import com.romagame.economy.EconomyManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.romagame.military.War;
import java.util.Random;

public class CountryManager {
    private WorldMap worldMap;
    private Map<String, Country> countries;
    private String playerCountry;
    private AIManager aiManager;
    
    public CountryManager(WorldMap worldMap, DiplomacyManager diplomacyManager, 
                        MilitaryManager militaryManager, EconomyManager economyManager) {
        this.worldMap = worldMap;
        this.countries = new HashMap<>();
        this.playerCountry = "France"; // Default player country
        this.aiManager = new AIManager(diplomacyManager, militaryManager, economyManager);
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
        // Process AI decisions for non-player countries using the new AI manager
        for (Country country : countries.values()) {
            aiManager.processAIDecisions(country, playerCountry);
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