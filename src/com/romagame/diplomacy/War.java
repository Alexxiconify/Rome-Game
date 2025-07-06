package com.romagame.diplomacy;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class War {
    private String attacker;
    private String defender;
    private double warScore;
    private int duration;
    private boolean active;
    private List<String> participants;
    private Map<String, Double> warContributions;
    private List<Battle> battles;
    private List<String> occupiedProvinces;
    private Random random;
    
    public War(String attacker, String defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.warScore = 0.0;
        this.duration = 0;
        this.active = true;
        this.participants = new ArrayList<>();
        this.warContributions = new HashMap<>();
        this.battles = new ArrayList<>();
        this.occupiedProvinces = new ArrayList<>();
        this.random = new Random();
        
        // Add initial participants
        participants.add(attacker);
        participants.add(defender);
        warContributions.put(attacker, 0.0);
        warContributions.put(defender, 0.0);
    }
    
    public void update() {
        if (!active) return;
        
        duration++;
        
        // Natural war score drift
        if (warScore > 0) {
            warScore = Math.max(0, warScore - 0.1);
        } else if (warScore < 0) {
            warScore = Math.min(0, warScore + 0.1);
        }
        
        // Check for war exhaustion
        if (duration > 120) { // 10 years
            warScore *= 0.95; // War exhaustion penalty
        }
        
        // Random events
        if (random.nextDouble() < 0.05) { // 5% chance per update
            generateWarEvent();
        }
    }
    
    private void generateWarEvent() {
        double chance = random.nextDouble();
        if (chance < 0.3) {
            // Battle event
            generateBattle();
        } else if (chance < 0.6) {
            // Occupation event
            generateOccupation();
        } else {
            // Diplomatic event
            generateDiplomaticEvent();
        }
    }
    
    private void generateBattle() {
        // Simulate a battle
        double attackerStrength = calculateMilitaryStrength(attacker);
        double defenderStrength = calculateMilitaryStrength(defender);
        
        // Add random factors
        attackerStrength *= 0.8 + random.nextDouble() * 0.4; // 80-120% effectiveness
        defenderStrength *= 0.8 + random.nextDouble() * 0.4;
        
        double battleResult = attackerStrength - defenderStrength;
        
        if (battleResult > 0) {
            // Attacker wins
            warScore += Math.min(5.0, battleResult / 1000.0);
            warContributions.put(attacker, warContributions.get(attacker) + 1.0);
        } else {
            // Defender wins
            warScore -= Math.min(5.0, Math.abs(battleResult) / 1000.0);
            warContributions.put(defender, warContributions.get(defender) + 1.0);
        }
        
        // Record battle
        battles.add(new Battle(attacker, defender, battleResult, duration));
    }
    
    private void generateOccupation() {
        // Simulate province occupation
        if (random.nextDouble() < 0.3) { // 30% chance of occupation
            String occupiedProvince = "province_" + random.nextInt(100);
            if (!occupiedProvinces.contains(occupiedProvince)) {
                occupiedProvinces.add(occupiedProvince);
                if (random.nextBoolean()) {
                    // Attacker occupies
                    warScore += 2.0;
                    warContributions.put(attacker, warContributions.get(attacker) + 2.0);
                } else {
                    // Defender recovers
                    warScore -= 1.0;
                    warContributions.put(defender, warContributions.get(defender) + 1.0);
                }
            }
        }
    }
    
    private void generateDiplomaticEvent() {
        // Random diplomatic events
        double event = random.nextDouble();
        if (event < 0.2) {
            // Alliance formation
            warScore += random.nextDouble() * 3.0;
        } else if (event < 0.4) {
            // Betrayal
            warScore -= random.nextDouble() * 3.0;
        } else if (event < 0.6) {
            // Peace negotiations
            warScore *= 0.9; // Reduce war score
        } else {
            // Escalation
            warScore *= 1.1; // Increase war score
        }
    }
    
    private double calculateMilitaryStrength(String countryName) {
        // This would integrate with the military system
        // For now, return a base strength
        return 1000.0 + random.nextDouble() * 2000.0;
    }
    
    public void addParticipant(String country, boolean isAttacker) {
        if (!participants.contains(country)) {
            participants.add(country);
            warContributions.put(country, 0.0);
            
            if (isAttacker) {
                warScore += 5.0; // Ally joins attacker
            } else {
                warScore -= 5.0; // Ally joins defender
            }
        }
    }
    
    public void removeParticipant(String country) {
        participants.remove(country);
        warContributions.remove(country);
    }
    
    public boolean canMakePeace() {
        return Math.abs(warScore) < 50.0; // Can make peace if war score is moderate
    }
    
    public Map<String, Object> getPeaceTerms() {
        Map<String, Object> terms = new HashMap<>();
        
        if (warScore > 25) {
            // Attacker wins
            terms.put("winner", attacker);
            terms.put("loser", defender);
            terms.put("war_reparations", Math.abs(warScore) * 10.0);
            terms.put("province_annexations", Math.min(3, (int)(warScore / 10)));
        } else if (warScore < -25) {
            // Defender wins
            terms.put("winner", defender);
            terms.put("loser", attacker);
            terms.put("war_reparations", Math.abs(warScore) * 10.0);
            terms.put("province_annexations", Math.min(3, (int)(Math.abs(warScore) / 10)));
        } else {
            // White peace
            terms.put("winner", "none");
            terms.put("loser", "none");
            terms.put("war_reparations", 0.0);
            terms.put("province_annexations", 0);
        }
        
        return terms;
    }
    
    public void endWar() {
        active = false;
    }
    
    // Getters
    public String getAttacker() { return attacker; }
    public String getDefender() { return defender; }
    public double getWarScore() { return warScore; }
    public double getResult() {return result;}
    public int getTurn() {return turn;}
    public int getDuration() { return duration; }
    public boolean isActive() { return active; }
    public List<String> getParticipants() { return new ArrayList<>(participants); }
    public Map<String, Double> getWarContributions() { return new HashMap<>(warContributions); }
    public List<Battle> getBattles() { return new ArrayList<>(battles); }
    public List<String> getOccupiedProvinces() { return new ArrayList<>(occupiedProvinces); }
    
    private static class Battle {
        
        
        public Battle(String attacker, String defender, double result, int turn) {
            this.attacker = attacker;
            this.defender = defender;
            this.result = result;
            this.turn = turn;
        }
        
    }
} 