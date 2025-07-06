package com.romagame.diplomacy;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class DiplomacyManager {
    private Map<String, Map<String, DiplomaticRelation>> relations;
    private List<Alliance> alliances;
    private List<TradeAgreement> tradeAgreements;
    private List<War> activeWars;
    private Random random;
    
    public DiplomacyManager() {
        relations = new HashMap<>();
        alliances = new ArrayList<>();
        tradeAgreements = new ArrayList<>();
        activeWars = new ArrayList<>();
        random = new Random();
    }
    
    public void update() {
        // Update diplomatic relations
        updateRelations();
        updateAlliances();
        updateTradeAgreements();
        updateWars();
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
    
    private void updateWars() {
        // Update all active wars
        for (int i = activeWars.size() - 1; i >= 0; i--) {
            War war = activeWars.get(i);
            war.update();
            
            // Check if war should end
            if (war.canMakePeace() && random.nextDouble() < 0.1) { // 10% chance to end war
                endWar(war);
                activeWars.remove(i);
            }
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
    
    public void modifyRelation(String country1, String country2, double change) {
        Map<String, DiplomaticRelation> countryRelations = relations.get(country1);
        if (countryRelations != null) {
            DiplomaticRelation relation = countryRelations.get(country2);
            if (relation != null) {
                relation.modify(change);
            }
        }
    }
    
    // Diplomatic Actions
    public boolean sendGift(String sender, String receiver, double amount) {
        if (amount <= 0) return false;
        
        modifyRelation(sender, receiver, amount * 0.1); // 10% of gift value as relation bonus
        return true;
    }
    
    public boolean sendInsult(String sender, String receiver) {
        modifyRelation(sender, receiver, -10.0);
        return true;
    }
    
    public boolean sendCompliment(String sender, String receiver) {
        modifyRelation(sender, receiver, 5.0);
        return true;
    }
    
    public boolean offerAlliance(String proposer, String target) {
        double relation = getRelation(proposer, target);
        if (relation >= 25) { // Need good relations
            formAlliance(proposer, target);
            modifyRelation(proposer, target, 10.0);
            return true;
        }
        return false;
    }
    
    public boolean breakAlliance(String country1, String country2) {
        // Remove alliance
        alliances.removeIf(a -> 
            (a.getCountry1().equals(country1) && a.getCountry2().equals(country2)) ||
            (a.getCountry1().equals(country2) && a.getCountry2().equals(country1))
        );
        
        // Penalty for breaking alliance
        modifyRelation(country1, country2, -20.0);
        return true;
    }
    
    // War Management
    public boolean declareWar(String attacker, String defender) {
        // Check if already at war
        if (isAtWar(attacker, defender)) {
            return false;
        }
        
        // Create new war
        War war = new War(attacker, defender);
        activeWars.add(war);
        
        // Set relations to hostile
        setRelation(attacker, defender, -50.0);
        
        System.out.println("War declared: " + attacker + " vs " + defender);
        return true;
    }
    
    public boolean makePeace(String country1, String country2) {
        War war = getWarBetween(country1, country2);
        if (war != null) {
            endWar(war);
            activeWars.remove(war);
            
            // Improve relations after peace
            modifyRelation(country1, country2, 10.0);
            return true;
        }
        return false;
    }
    
    private void endWar(War war) {
        Map<String, Object> peaceTerms = war.getPeaceTerms();
        String winner = (String) peaceTerms.get("winner");
        String loser = (String) peaceTerms.get("loser");
        double reparations = (Double) peaceTerms.get("war_reparations");
        
        System.out.println("War ended: " + war.getAttacker() + " vs " + war.getDefender());
        System.out.println("Winner: " + winner + ", Reparations: " + reparations);
        
        war.endWar();
    }
    
    public boolean isAtWar(String country1, String country2) {
        return activeWars.stream().anyMatch(war -> 
            (war.getAttacker().equals(country1) && war.getDefender().equals(country2)) ||
            (war.getAttacker().equals(country2) && war.getDefender().equals(country1))
        );
    }
    
    public War getWarBetween(String country1, String country2) {
        return activeWars.stream()
            .filter(war -> 
                (war.getAttacker().equals(country1) && war.getDefender().equals(country2)) ||
                (war.getAttacker().equals(country2) && war.getDefender().equals(country1))
            )
            .findFirst()
            .orElse(null);
    }
    
    public List<War> getWars() {
        return new ArrayList<>(activeWars);
    }
    
    public List<War> getWarsInvolving(String country) {
        return activeWars.stream()
            .filter(war -> war.getParticipants().contains(country))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Alliance Management
    public void formAlliance(String country1, String country2) {
        Alliance alliance = new Alliance(country1, country2);
        alliances.add(alliance);
        System.out.println("Alliance formed: " + country1 + " and " + country2);
    }
    
    public boolean areAllied(String country1, String country2) {
        return alliances.stream().anyMatch(alliance ->
            (alliance.getCountry1().equals(country1) && alliance.getCountry2().equals(country2)) ||
            (alliance.getCountry1().equals(country2) && alliance.getCountry2().equals(country1))
        );
    }
    
    public void formTradeAgreement(String country1, String country2) {
        TradeAgreement agreement = new TradeAgreement(country1, country2);
        tradeAgreements.add(agreement);
        modifyRelation(country1, country2, 5.0);
    }
    
    // Getters
    public List<Alliance> getAlliances() {
        return new ArrayList<>(alliances);
    }
    
    public List<TradeAgreement> getTradeAgreements() {
        return new ArrayList<>(tradeAgreements);
    }
    
    public Map<String, DiplomaticRelation> getRelations(String country) {
        return relations.getOrDefault(country, new HashMap<>());
    }
    
    public List<String> getEnemies(String country) {
        List<String> enemies = new ArrayList<>();
        for (War war : activeWars) {
            if (war.getParticipants().contains(country)) {
                for (String participant : war.getParticipants()) {
                    if (!participant.equals(country)) {
                        enemies.add(participant);
                    }
                }
            }
        }
        return enemies;
    }
    
    public List<String> getAllies(String country) {
        List<String> allies = new ArrayList<>();
        for (Alliance alliance : alliances) {
            if (alliance.getCountry1().equals(country)) {
                allies.add(alliance.getCountry2());
            } else if (alliance.getCountry2().equals(country)) {
                allies.add(alliance.getCountry1());
            }
        }
        return allies;
    }
} 