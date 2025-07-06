package com.romagame.country;

import com.romagame.map.WorldMap;
import com.romagame.map.Country;
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
        Random rand = new Random();
        int actions = 1 + rand.nextInt(3); // 1-3 actions per tick
        for (int i = 0; i < actions; i++) {
            int choice = rand.nextInt(4);
            switch (choice) {
                case 0: // Economic: Try to increase GDP
                    if (country.getTreasury() > 30) {
                        // Build or upgrade a building in a random province
                        var provinces = country.getProvinces();
                        if (!provinces.isEmpty()) {
                            var p = provinces.get(rand.nextInt(provinces.size()));
                            var slots = p.getBuildingSlots();
                            for (int s = 0; s < slots.size(); s++) {
                                if (slots.get(s).getType().equals("Empty")) {
                                    p.setBuildingSlot(s, "Market", 1);
                                    country.setTreasury(country.getTreasury() - 20);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case 1: // Well-being: Try to improve pop happiness
                    if (country.getStability() < 0.5) {
                        country.setStability(country.getStability() + 0.1);
                    }
                    break;
                case 2: // Army: Recruit if army is weak
                    if (country.getTreasury() > 50) {
                        country.recruitUnit("Infantry", 1);
                        country.setTreasury(country.getTreasury() - 10);
                    }
                    break;
                case 3: // Trade: Increase goods
                    var goods = country.getGoods();
                    goods.put("Grain", goods.getOrDefault("Grain", 0) + 5);
                    break;
            }
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