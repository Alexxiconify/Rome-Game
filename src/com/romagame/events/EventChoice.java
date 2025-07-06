package com.romagame.events;

import com.romagame.map.Country;
import com.romagame.map.Province;
import java.util.Map;

public class EventChoice {
    private String text;
    private String description;
    private Map<String, Double> effects;
    
    public EventChoice(String text, String description, Map<String, Double> effects) {
        this.text = text;
        this.description = description;
        this.effects = effects;
    }
    
    public void execute(Country country) {
        for (Map.Entry<String, Double> effect : effects.entrySet()) {
            String modifier = effect.getKey();
            double value = effect.getValue();
            
            switch (modifier) {
                case "stability":
                    country.setStability(country.getStability() + value);
                    break;
                case "prestige":
                    country.setPrestige(country.getPrestige() + value);
                    break;
                case "legitimacy":
                    country.setLegitimacy(country.getLegitimacy() + value);
                    break;
                case "population":
                    // Apply population modifier to all provinces
                    for (var province : country.getProvinces()) {
                        int currentPop = province.getPopulation();
                        int newPop = (int)(currentPop * (1 + value));
                        province.setPop(Province.PopType.PEASANTS, newPop);
                    }
                    break;
                case "religious_unity":
                    country.addModifier("Religious Unity", value);
                    break;
                case "trade_efficiency":
                    country.addModifier("Trade Efficiency", value);
                    break;
                case "diplomatic_reputation":
                    country.addModifier("Diplomatic Reputation", value);
                    break;
                case "army_tradition":
                    country.addModifier("Army Tradition", value);
                    break;
                case "navy_tradition":
                    country.addModifier("Navy Tradition", value);
                    break;
                case "merchant_slots":
                    country.addModifier("Merchant Slots", (int)value);
                    break;
                case "missionary_strength":
                    country.addModifier("Missionary Strength", value);
                    break;
                case "culture_conversion_cost":
                    country.addModifier("Culture Conversion Cost", value);
                    break;
                case "unrest":
                    country.addModifier("Unrest", value);
                    break;
                case "fort_level":
                    country.addModifier("Fort Level", (int)value);
                    break;
                case "defensiveness":
                    country.addModifier("Defensiveness", value);
                    break;
                case "absolutism":
                    country.addModifier("Absolutism", value);
                    break;
                case "diplomatic_relations":
                    country.addModifier("Diplomatic Relations", (int)value);
                    break;
                case "treasury":
                    country.setTreasury(country.getTreasury() + value);
                    break;
            }
        }
    }
    
    // Getters
    public String getText() { return text; }
    public String getDescription() { return description; }
    public Map<String, Double> getEffects() { return effects; }
} 