package com.romagame.military;

import com.romagame.map.Country;
import java.util.List;
import java.util.ArrayList;

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
    }
    
    private void calculateWarScore() {
        // Simplified war score calculation
        // In a real game, this would be based on battles, occupation, etc.
        warScore = Math.random() * 100 - 50; // Random war score for demo
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