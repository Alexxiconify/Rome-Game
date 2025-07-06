package com.romagame.military;

import com.romagame.map.Country;
import com.romagame.map.Province;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class War {
    private Country attacker;
    private Country defender;
    private List<Country> attackers;
    private List<Country> defenders;
    private double warScore;
    private boolean isActive;
    private String warGoal;
    
    public War(Country attacker, Country defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.attackers = new ArrayList<>();
        this.defenders = new ArrayList<>();
        this.attackers.add(attacker);
        this.defenders.add(defender);
        this.warScore = 0.0;
        this.isActive = true;
        this.warGoal = "Conquest";
    }
    
    public void update() {
        if (!isActive) return;
        
        // Calculate war score based on battles and occupation
        calculateWarScore();
        
        // Check for peace conditions
        checkPeaceConditions();
        
        // Update war participants
        updateWarParticipants();
    }
    
    private void calculateWarScore() {
        // More realistic war score calculation based on:
        // - Province occupation
        // - Army strength comparison
        // - Economic factors
        
        double attackerScore = 0.0;
        double defenderScore = 0.0;
        
        // Calculate based on occupied provinces
        int attackerOccupied = countOccupiedProvinces(attacker, defender);
        int defenderOccupied = countOccupiedProvinces(defender, attacker);
        
        attackerScore += attackerOccupied * 10.0;
        defenderScore += defenderOccupied * 10.0;
        
        // Calculate based on army strength
        double attackerArmyStrength = calculateTotalArmyStrength(attacker);
        double defenderArmyStrength = calculateTotalArmyStrength(defender);
        
        attackerScore += Math.min(50.0, attackerArmyStrength * 0.1);
        defenderScore += Math.min(50.0, defenderArmyStrength * 0.1);
        
        // Economic factors
        attackerScore += Math.min(20.0, attacker.getTreasury() * 0.01);
        defenderScore += Math.min(20.0, defender.getTreasury() * 0.01);
        
        // Calculate final war score (attacker perspective)
        warScore = attackerScore - defenderScore;
        
        // Clamp war score to reasonable range
        warScore = Math.max(-100.0, Math.min(100.0, warScore));
    }
    
    private int countOccupiedProvinces(Country occupier, Country occupied) {
        // Count provinces of occupied country that are controlled by occupier
        int count = 0;
        for (Province province : occupied.getProvinces()) {
            // Simplified: assume occupation based on proximity and army presence
            if (hasArmyInProvince(occupier, province.getId())) {
                count++;
            }
        }
        return count;
    }
    
    private boolean hasArmyInProvince(Country country, String provinceId) {
        // Check if country has an army in the specified province
        // This would need to be implemented with actual army location tracking
        return Math.random() < 0.3; // 30% chance for demo
    }
    
    private double calculateTotalArmyStrength(Country country) {
        // Calculate total military strength of a country
        double strength = 0.0;
        
        // Add base military strength from military units
        Map<String, Integer> military = country.getMilitary();
        for (Map.Entry<String, Integer> unit : military.entrySet()) {
            strength += unit.getValue() * 100.0; // Each unit contributes 100 strength
        }
        
        // --- Military Tech Bonus ---
        // Tech factor: Each tech level above 1 gives +10% strength (level 1 = 1.0x, level 20 = 2.9x)
        // This makes tech a decisive factor in combat
        double techFactor = 1.0 + 0.1 * (country.getMilitaryTechLevel() - 1);
        strength *= techFactor;
        // ---
        
        // Add technology bonuses based on researched technologies
        strength += country.getResearchedTechnologies().size() * 50.0;
        
        // Add morale/stability factors
        strength += country.getStability() * 200.0;
        
        // Add prestige bonus
        strength += country.getPrestige() * 10.0;
        
        return strength;
    }
    
    private void updateWarParticipants() {
        // Update war participants based on alliances and diplomatic relations
        // This could include adding allies, checking for peace offers, etc.
        // For now, just a placeholder for future implementation
    }
    
    private void checkPeaceConditions() {
        // Check if war should end
        if (warScore > 25) {
            // Attacker winning
            if (Math.random() < 0.1) { // 10% chance per update
                makePeace();
            }
        } else if (warScore < -25) {
            // Defender winning
            if (Math.random() < 0.1) { // 10% chance per update
                makePeace();
            }
        }
    }
    
    public void makePeace() {
        isActive = false;
        // Apply peace terms based on war score
        if (warScore > 0) {
            // Attacker wins
            applyPeaceTerms(attacker, defender);
        } else {
            // Defender wins
            applyPeaceTerms(defender, attacker);
        }
    }
    
    private void applyPeaceTerms(Country winner, Country loser) {
        // Apply peace terms (province transfers, reparations, etc.)
        double reparations = Math.abs(warScore) * 10;
        loser.setTreasury(loser.getTreasury() - reparations);
        winner.setTreasury(winner.getTreasury() + reparations);
    }
    
    public void addAttacker(Country country) {
        if (!attackers.contains(country)) {
            attackers.add(country);
        }
    }
    
    public void addDefender(Country country) {
        if (!defenders.contains(country)) {
            defenders.add(country);
        }
    }
    
    // Getters
    public Country getAttacker() { return attacker; }
    public Country getDefender() { return defender; }
    public List<Country> getAttackers() { return attackers; }
    public List<Country> getDefenders() { return defenders; }
    public double getWarScore() { return warScore; }
    public boolean isActive() { return isActive; }
    public String getWarGoal() { return warGoal; }
} 