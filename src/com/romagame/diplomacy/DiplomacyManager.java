package com.romagame.diplomacy;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class DiplomacyManager {
    private Map<String, Map<String, DiplomaticRelation>> relations;
    private List<Alliance> alliances;
    private List<TradeAgreement> tradeAgreements;
    
    public DiplomacyManager() {
        relations = new HashMap<>();
        alliances = new ArrayList<>();
        tradeAgreements = new ArrayList<>();
    }
    
    public void update() {
        // Update diplomatic relations
        updateRelations();
        updateAlliances();
        updateTradeAgreements();
    }
    
    private void updateRelations() {
        // Natural relation drift
        for (Map<String, DiplomaticRelation> countryRelations : relations.values()) {
            for (DiplomaticRelation relation : countryRelations.values()) {
                relation.update();
            }
        }
    }
    
    private void updateAlliances() {
        // Check alliance conditions
        for (Alliance alliance : alliances) {
            alliance.update();
        }
    }
    
    private void updateTradeAgreements() {
        // Update trade agreement effects
        for (TradeAgreement agreement : tradeAgreements) {
            agreement.update();
        }
    }
    
    public void setRelation(String country1, String country2, double value) {
        relations.computeIfAbsent(country1, k -> new HashMap<>());
        relations.computeIfAbsent(country2, k -> new HashMap<>());
        
        DiplomaticRelation relation1 = new DiplomaticRelation(country1, country2, value);
        DiplomaticRelation relation2 = new DiplomaticRelation(country2, country1, value);
        
        relations.get(country1).put(country2, relation1);
        relations.get(country2).put(country1, relation2);
    }
    
    public double getRelation(String country1, String country2) {
        Map<String, DiplomaticRelation> countryRelations = relations.get(country1);
        if (countryRelations != null) {
            DiplomaticRelation relation = countryRelations.get(country2);
            if (relation != null) {
                return relation.getValue();
            }
        }
        return 0.0; // Neutral
    }
    
    public void formAlliance(String country1, String country2) {
        Alliance alliance = new Alliance(country1, country2);
        alliances.add(alliance);
    }
    
    public void formTradeAgreement(String country1, String country2) {
        TradeAgreement agreement = new TradeAgreement(country1, country2);
        tradeAgreements.add(agreement);
    }
    
    public List<Alliance> getAlliances() {
        return new ArrayList<>(alliances);
    }
    
    public List<TradeAgreement> getTradeAgreements() {
        return new ArrayList<>(tradeAgreements);
    }
} 