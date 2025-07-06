package com.romagame.colonization;

import com.romagame.map.Province;
import com.romagame.map.WorldMap;
import com.romagame.map.Country;
import com.romagame.map.Province.PopType;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class ColonizationManager {
    private WorldMap worldMap;
    private List<ColonizationMission> activeMissions;
    private Map<String, List<String>> colonizationEvents;
    private Random random;
    
    public ColonizationManager(WorldMap worldMap) {
        this.worldMap = worldMap;
        this.activeMissions = new ArrayList<>();
        this.colonizationEvents = new HashMap<>();
        this.random = new Random();
        initializeColonizationEvents();
    }
    
    private void initializeColonizationEvents() {
        // Positive events
        List<String> positiveEvents = new ArrayList<>();
        positiveEvents.add("Fertile land discovered - +2 development");
        positiveEvents.add("Native cooperation - +1 population");
        positiveEvents.add("Rich resources found - +3 treasury");
        positiveEvents.add("Strategic location - +1 trade power");
        positiveEvents.add("Natural harbor - +2 naval capacity");
        positiveEvents.add("Mountain passes - +1 military defense");
        positiveEvents.add("River system - +2 trade routes");
        positiveEvents.add("Mineral deposits - +1 production");
        colonizationEvents.put("positive", positiveEvents);
        
        // Negative events
        List<String> negativeEvents = new ArrayList<>();
        negativeEvents.add("Disease outbreak - -1 population");
        negativeEvents.add("Hostile natives - -1 stability");
        negativeEvents.add("Poor soil - -1 development");
        negativeEvents.add("Dangerous wildlife - -1 morale");
        negativeEvents.add("Harsh climate - -2 population");
        negativeEvents.add("Isolation - -1 trade power");
        negativeEvents.add("Resource scarcity - -1 treasury");
        negativeEvents.add("Geographic barriers - -1 development");
        colonizationEvents.put("negative", negativeEvents);
        
        // Neutral events
        List<String> neutralEvents = new ArrayList<>();
        neutralEvents.add("Uneventful journey - no effect");
        neutralEvents.add("Standard conditions - normal colonization");
        neutralEvents.add("Mixed results - minor effects");
        neutralEvents.add("Learning experience - +1 experience");
        neutralEvents.add("Cultural exchange - +1 relations");
        colonizationEvents.put("neutral", neutralEvents);
    }
    
    public boolean startColonization(String countryName, String provinceId, int colonists) {
        Province province = worldMap.getProvince(provinceId);
        Country country = worldMap.getCountry(countryName);
        
        System.out.println("Attempting colonization: " + countryName + " -> " + provinceId + " with " + colonists + " colonists");
        
        if (province == null || country == null) {
            System.out.println("Colonization failed: Province or country not found");
            return false;
        }
        
        if (!province.getOwner().equals("Uninhabited")) {
            System.out.println("Colonization failed: Province already owned by " + province.getOwner());
            return false; // Province already owned
        }
        
        if (colonists < 100 || colonists > 1000) {
            System.out.println("Colonization failed: Invalid colonist count " + colonists);
            return false; // Invalid colonist count
        }
        
        // Create colonization mission
        ColonizationMission mission = new ColonizationMission(countryName, provinceId, colonists);
        activeMissions.add(mission);
        
        System.out.println("Colonization started successfully: " + countryName + " -> " + provinceId);
        return true;
    }
    
    public void update() {
        // Update all active colonization missions
        for (int i = activeMissions.size() - 1; i >= 0; i--) {
            ColonizationMission mission = activeMissions.get(i);
            mission.update();
            
            if (mission.isCompleted()) {
                completeColonization(mission);
                activeMissions.remove(i);
            }
        }
    }
    
    private void completeColonization(ColonizationMission mission) {
        Province province = worldMap.getProvince(mission.getProvinceId());
        Country country = worldMap.getCountry(mission.getCountryName());
        
        if (province == null || country == null) {
            return;
        }
        
        // Transfer ownership
        province.setOwner(mission.getCountryName());
        country.addProvince(province);
        
        // Apply colonization effects based on events
        applyColonizationEffects(province, mission);
        
        // Update province development
        province.setDevelopment(province.getDevelopment() + 1);
        // Add colonists as peasants
        province.setPop(PopType.PEASANTS, province.getPop(PopType.PEASANTS) + mission.getColonists());
    }
    
    private void applyColonizationEffects(Province province, ColonizationMission mission) {
        // Generate random events during colonization
        for (ColonizationEvent event : mission.getEvents()) {
            switch (event.getType()) {
                case "positive" -> applyPositiveEvent(province, event);
                case "negative" -> applyNegativeEvent(province, event);
                case "neutral" -> applyNeutralEvent(province, event);
            }
        }
    }
    
    private void applyPositiveEvent(Province province, ColonizationEvent event) {
        String description = event.getDescription();
        
        if (description.contains("development")) {
            province.setDevelopment(province.getDevelopment() + 2);
        }
        if (description.contains("population")) {
            province.setPop(PopType.PEASANTS, province.getPop(PopType.PEASANTS) + 1000);
        }
        if (description.contains("treasury")) {
            Country country = worldMap.getCountry(province.getOwner());
            if (country != null) {
                country.setTreasury(country.getTreasury() + 3);
            }
        }
        if (description.contains("trade power")) {
            province.addModifier("Trade Power +1");
        }
        if (description.contains("naval capacity")) {
            province.addModifier("Naval Capacity +2");
        }
        if (description.contains("military defense")) {
            province.addModifier("Military Defense +1");
        }
        if (description.contains("trade routes")) {
            province.addModifier("Trade Routes +2");
        }
        if (description.contains("production")) {
            province.addModifier("Production +1");
        }
    }
    
    private void applyNegativeEvent(Province province, ColonizationEvent event) {
        String description = event.getDescription();
        
        if (description.contains("population")) {
            province.setPop(PopType.PEASANTS, Math.max(100, province.getPop(PopType.PEASANTS) - 1000));
        }
        if (description.contains("stability")) {
            Country country = worldMap.getCountry(province.getOwner());
            if (country != null) {
                country.setStability(country.getStability() - 1);
            }
        }
        if (description.contains("development")) {
            province.setDevelopment(Math.max(1, province.getDevelopment() - 1));
        }
        if (description.contains("morale")) {
            province.addModifier("Low Morale -1");
        }
        if (description.contains("trade power")) {
            province.addModifier("Trade Power -1");
        }
        if (description.contains("treasury")) {
            Country country = worldMap.getCountry(province.getOwner());
            if (country != null) {
                country.setTreasury(Math.max(0, country.getTreasury() - 1));
            }
        }
    }
    
    private void applyNeutralEvent(Province province, ColonizationEvent event) {
        String description = event.getDescription();
        
        if (description.contains("experience")) {
            province.addModifier("Colonization Experience +1");
        }
        if (description.contains("relations")) {
            province.addModifier("Cultural Exchange +1");
        }
    }
    
    public List<ColonizationMission> getActiveMissions() {
        return new ArrayList<>(activeMissions);
    }
    
    public List<String> getColonizableProvinces() {
        List<String> colonizable = new ArrayList<>();
        for (Province province : worldMap.getAllProvinces()) {
            if (province.getOwner().equals("Uninhabited")) {
                colonizable.add(province.getId());
            }
        }
        return colonizable;
    }
    
    public String generateRandomEvent() {
        double chance = random.nextDouble();
        
        if (chance < 0.4) {
            // 40% chance for positive event
            List<String> events = colonizationEvents.get("positive");
            return events.get(random.nextInt(events.size()));
        } else if (chance < 0.7) {
            // 30% chance for negative event
            List<String> events = colonizationEvents.get("negative");
            return events.get(random.nextInt(events.size()));
        } else {
            // 30% chance for neutral event
            List<String> events = colonizationEvents.get("neutral");
            return events.get(random.nextInt(events.size()));
        }
    }
} 