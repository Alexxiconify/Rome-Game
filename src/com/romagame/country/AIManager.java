package com.romagame.country;

import com.romagame.map.Country;
import com.romagame.map.Country.NationType;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.military.MilitaryManager;
import com.romagame.economy.EconomyManager;
import java.util.*;
import java.util.Random;

public class AIManager {
    private Map<String, AIPersonality> aiPersonalities;
    private Random random;
    private DiplomacyManager diplomacyManager;
    private MilitaryManager militaryManager;
    private EconomyManager economyManager;
    
    public enum AIPersonality {
        AGGRESSIVE, DEFENSIVE, TRADER, BUILDER, BALANCED
    }
    
    public enum AIAction {
        BUILD_MILITARY, IMPROVE_ECONOMY, DIPLOMATIC_ACTION, EXPAND_TERRITORY, MAINTAIN_STABILITY
    }
    
    public AIManager(DiplomacyManager diplomacyManager, MilitaryManager militaryManager, EconomyManager economyManager) {
        this.aiPersonalities = new HashMap<>();
        this.random = new Random();
        this.diplomacyManager = diplomacyManager;
        this.militaryManager = militaryManager;
        this.economyManager = economyManager;
        initializePersonalities();
    }
    
    private void initializePersonalities() {
        // Assign personalities based on nation type and historical behavior
        aiPersonalities.put("Rome", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Parthia", AIPersonality.DEFENSIVE);
        aiPersonalities.put("Armenia", AIPersonality.DEFENSIVE);
        aiPersonalities.put("Dacia", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Sarmatia", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Quadi", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Marcomanni", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Suebi", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Alemanni", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Chatti", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Cherusci", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Hermunduri", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Frisians", AIPersonality.DEFENSIVE);
        aiPersonalities.put("Britons", AIPersonality.DEFENSIVE);
        aiPersonalities.put("Caledonians", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Hibernians", AIPersonality.DEFENSIVE);
        aiPersonalities.put("Picts", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Scoti", AIPersonality.AGGRESSIVE);
        aiPersonalities.put("Garamantes", AIPersonality.TRADER);
        aiPersonalities.put("Nubia", AIPersonality.BUILDER);
        aiPersonalities.put("Axum", AIPersonality.TRADER);
        aiPersonalities.put("Himyar", AIPersonality.TRADER);
        aiPersonalities.put("Saba", AIPersonality.TRADER);
        aiPersonalities.put("Hadramaut", AIPersonality.TRADER);
        aiPersonalities.put("Oman", AIPersonality.TRADER);
        aiPersonalities.put("Kushan", AIPersonality.BALANCED);
        aiPersonalities.put("Indo-Parthian", AIPersonality.BALANCED);
    }
    
    public void processAIDecisions(Country country, String playerCountry) {
        if (country.getName().equals(playerCountry)) {
            return; // Skip player country
        }
        
        AIPersonality personality = aiPersonalities.getOrDefault(country.getName(), AIPersonality.BALANCED);
        List<AIAction> actions = determineActions(country, personality);
        
        for (AIAction action : actions) {
            executeAction(country, action, personality);
        }
    }
    
    private List<AIAction> determineActions(Country country, AIPersonality personality) {
        List<AIAction> actions = new ArrayList<>();
        int actionCount = 1 + random.nextInt(2); // 1-2 actions per tick
        
        // Personality-based action selection
        switch (personality) {
            case AGGRESSIVE:
                if (random.nextDouble() < 0.6) actions.add(AIAction.BUILD_MILITARY);
                if (random.nextDouble() < 0.4) actions.add(AIAction.EXPAND_TERRITORY);
                if (random.nextDouble() < 0.3) actions.add(AIAction.DIPLOMATIC_ACTION);
                break;
            case DEFENSIVE:
                if (random.nextDouble() < 0.7) actions.add(AIAction.BUILD_MILITARY);
                if (random.nextDouble() < 0.5) actions.add(AIAction.IMPROVE_ECONOMY);
                if (random.nextDouble() < 0.4) actions.add(AIAction.MAINTAIN_STABILITY);
                break;
            case TRADER:
                if (random.nextDouble() < 0.8) actions.add(AIAction.IMPROVE_ECONOMY);
                if (random.nextDouble() < 0.5) actions.add(AIAction.DIPLOMATIC_ACTION);
                if (random.nextDouble() < 0.3) actions.add(AIAction.BUILD_MILITARY);
                break;
            case BUILDER:
                if (random.nextDouble() < 0.8) actions.add(AIAction.IMPROVE_ECONOMY);
                if (random.nextDouble() < 0.6) actions.add(AIAction.MAINTAIN_STABILITY);
                if (random.nextDouble() < 0.3) actions.add(AIAction.BUILD_MILITARY);
                break;
            case BALANCED:
                if (random.nextDouble() < 0.5) actions.add(AIAction.IMPROVE_ECONOMY);
                if (random.nextDouble() < 0.4) actions.add(AIAction.BUILD_MILITARY);
                if (random.nextDouble() < 0.3) actions.add(AIAction.MAINTAIN_STABILITY);
                if (random.nextDouble() < 0.2) actions.add(AIAction.DIPLOMATIC_ACTION);
                break;
        }
        
        // Limit actions and ensure variety
        while (actions.size() > actionCount) {
            actions.remove(random.nextInt(actions.size()));
        }
        
        return actions;
    }
    
    private void executeAction(Country country, AIAction action, AIPersonality personality) {
        switch (action) {
            case BUILD_MILITARY:
                buildMilitary(country, personality);
                break;
            case IMPROVE_ECONOMY:
                improveEconomy(country, personality);
                break;
            case DIPLOMATIC_ACTION:
                performDiplomaticAction(country, personality);
                break;
            case EXPAND_TERRITORY:
                expandTerritory(country, personality);
                break;
            case MAINTAIN_STABILITY:
                maintainStability(country, personality);
                break;
        }
    }
    
    private void buildMilitary(Country country, AIPersonality personality) {
        if (country.getTreasury() < 20) return;
        
        // Determine military focus based on personality and nation type
        NationType nationType = country.getNationType();
        String unitType = "Infantry"; // Default
        
        switch (nationType) {
            case ROMAN:
                unitType = random.nextDouble() < 0.7 ? "Infantry" : "Cavalry";
                break;
            case GERMANIC:
                unitType = random.nextDouble() < 0.6 ? "Infantry" : "Cavalry";
                break;
            case EASTERN:
                unitType = random.nextDouble() < 0.3 ? "Infantry" : "Cavalry";
                break;
            case CELTIC:
                unitType = random.nextDouble() < 0.8 ? "Infantry" : "Cavalry";
                break;
            default:
                unitType = random.nextDouble() < 0.5 ? "Infantry" : "Cavalry";
                break;
        }
        
        int recruitAmount = personality == AIPersonality.AGGRESSIVE ? 2 : 1;
        country.recruitUnit(unitType, recruitAmount);
        country.setTreasury(country.getTreasury() - (recruitAmount * 10));
    }
    
    private void improveEconomy(Country country, AIPersonality personality) {
        if (country.getTreasury() < 15) return;
        
        // Add trade goods based on personality
        Map<String, Integer> goods = country.getGoods();
        String[] tradeGoods = {"Grain", "Wool", "Wine", "Spices", "Iron", "Gold"};
        
        if (personality == AIPersonality.TRADER) {
            // Traders focus on multiple goods
            for (int i = 0; i < 3; i++) {
                String good = tradeGoods[random.nextInt(tradeGoods.length)];
                goods.put(good, goods.getOrDefault(good, 0) + 3);
            }
        } else {
            // Others focus on one good
            String good = tradeGoods[random.nextInt(tradeGoods.length)];
            goods.put(good, goods.getOrDefault(good, 0) + 5);
        }
        
        // Improve stability if low
        if (country.getStability() < 0.3) {
            country.setStability(country.getStability() + 0.05);
        }
    }
    
    private void performDiplomaticAction(Country country, AIPersonality personality) {
        // Simple diplomatic actions - in a full implementation this would interact with DiplomacyManager
        if (personality == AIPersonality.TRADER) {
            // Traders are more likely to seek diplomatic relations
            country.setPrestige(country.getPrestige() + 0.1);
        } else if (personality == AIPersonality.AGGRESSIVE) {
            // Aggressive nations might prepare for war
            country.setPrestige(country.getPrestige() + 0.05);
        }
    }
    
    private void expandTerritory(Country country, AIPersonality personality) {
        // Limited expansion logic - in a full implementation this would interact with colonization
        if (personality == AIPersonality.AGGRESSIVE && country.getTreasury() > 50) {
            country.setTreasury(country.getTreasury() - 20);
            country.setPrestige(country.getPrestige() + 0.2);
        }
    }
    
    private void maintainStability(Country country, AIPersonality personality) {
        if (country.getStability() < 0.5) {
            country.setStability(country.getStability() + 0.1);
        }
        
        if (country.getLegitimacy() < 0.8) {
            country.setLegitimacy(country.getLegitimacy() + 0.05);
        }
    }
    
    public AIPersonality getPersonality(String countryName) {
        return aiPersonalities.getOrDefault(countryName, AIPersonality.BALANCED);
    }
    
    public void setPersonality(String countryName, AIPersonality personality) {
        aiPersonalities.put(countryName, personality);
    }
} 