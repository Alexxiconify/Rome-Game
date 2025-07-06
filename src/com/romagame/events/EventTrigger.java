package com.romagame.events;

import com.romagame.map.Country;
import java.util.*;

public class EventTrigger {
    private List<String> conditions;
    private double chance;
    
    public EventTrigger(String... conditions) {
        this.conditions = Arrays.asList(conditions);
        this.chance = 1.0; // Default 100% chance if conditions are met
    }
    
    public EventTrigger(double chance, String... conditions) {
        this.conditions = Arrays.asList(conditions);
        this.chance = chance;
    }
    
    public boolean checkConditions(Country country) {
        for (String condition : conditions) {
            if (!evaluateCondition(country, condition)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean evaluateCondition(Country country, String condition) {
        String[] parts = condition.split(" ");
        if (parts.length < 3) return false;
        
        String attribute = parts[0];
        String operator = parts[1];
        double value = Double.parseDouble(parts[2]);
        
        double countryValue = getCountryValue(country, attribute);
        
        return switch (operator) {
            case "<" -> countryValue < value;
            case "<=" -> countryValue <= value;
            case ">" -> countryValue > value;
            case ">=" -> countryValue >= value;
            case "==" -> countryValue == value;
            case "!=" -> countryValue != value;
            default -> false;
        };
    }
    
    private double getCountryValue(Country country, String attribute) {
        return switch (attribute) {
            case "stability" -> country.getStability();
            case "prestige" -> country.getPrestige();
            case "legitimacy" -> country.getLegitimacy();
            case "population" -> country.getProvinces().stream()
                .mapToInt(province -> province.getPopulation()).sum();
            case "provinces" -> country.getProvinces().size();
            case "treasury" -> country.getTreasury();
            case "income" -> country.getIncome();
            case "expenses" -> country.getExpenses();
            case "development" -> country.getTotalDevelopment();
            case "military" -> country.getMilitary().values().stream()
                .mapToInt(Integer::intValue).sum();
            default -> 0.0;
        };
    }
    
    public boolean shouldTrigger() {
        return Math.random() < chance;
    }
    
    // Getters
    public List<String> getConditions() { return conditions; }
    public double getChance() { return chance; }
} 