package com.romagame.government;

import java.util.*;

public class Ruler {
    private String name;
    private int age;
    private int adminPoints;
    private int diplomaticPoints;
    private int militaryPoints;
    private List<RulerTrait> traits;
    private RulerPersonality personality;
    private boolean isDead;
    private int yearsReigned;
    
    public Ruler(String name, int age) {
        this.name = name;
        this.age = age;
        this.adminPoints = 0;
        this.diplomaticPoints = 0;
        this.militaryPoints = 0;
        this.traits = new ArrayList<>();
        this.personality = generatePersonality();
        this.isDead = false;
        this.yearsReigned = 0;
        generateTraits();
    }
    
    private RulerPersonality generatePersonality() {
        RulerPersonality[] personalities = RulerPersonality.values();
        return personalities[new Random().nextInt(personalities.length)];
    }
    
    private void generateTraits() {
        Random random = new Random();
        int traitCount = random.nextInt(3) + 1; // 1-3 traits
        
        List<RulerTrait> availableTraits = new ArrayList<>(Arrays.asList(RulerTrait.values()));
        Collections.shuffle(availableTraits);
        
        for (int i = 0; i < traitCount && i < availableTraits.size(); i++) {
            traits.add(availableTraits.get(i));
        }
    }
    
    public void update() {
        if (isDead) return;
        
        yearsReigned++;
        age++;
        
        // Generate monarch points
        adminPoints += generateAdminPoints();
        diplomaticPoints += generateDiplomaticPoints();
        militaryPoints += generateMilitaryPoints();
        
        // Cap monarch points
        adminPoints = Math.min(adminPoints, 999);
        diplomaticPoints = Math.min(diplomaticPoints, 999);
        militaryPoints = Math.min(militaryPoints, 999);
        
        // Check for death
        if (age > 70 && new Random().nextDouble() < 0.1) {
            isDead = true;
        }
    }
    
    private int generateAdminPoints() {
        int basePoints = 3;
        int bonusPoints = 0;
        
        // Personality bonuses
        switch (personality) {
            case ADMINISTRATIVE -> bonusPoints += 1;
            case DIPLOMATIC -> bonusPoints += 0;
            case MILITARY -> bonusPoints += 0;
            case BALANCED -> bonusPoints += 0;
        }
        
        // Trait bonuses
        for (RulerTrait trait : traits) {
            switch (trait) {
                case ADMINISTRATIVE_GENIUS -> bonusPoints += 2;
                case DIPLOMATIC_GENIUS -> bonusPoints += 0;
                case MILITARY_GENIUS -> bonusPoints += 0;
                case INCOMPETENT -> bonusPoints -= 1;
                case CRUEL -> bonusPoints += 0;
                case KIND -> bonusPoints += 0;
                case AMBITIOUS -> bonusPoints += 1;
                case LAZY -> bonusPoints -= 1;
                case JUST -> bonusPoints += 1;
                case ARBITRARY -> bonusPoints += 0;
                case CHARITABLE -> bonusPoints += 0;
                case GREEDY -> bonusPoints += 0;
                case BRAVE -> bonusPoints += 0;
                case COWARD -> bonusPoints -= 1;
                case CHASTE -> bonusPoints += 0;
                case LUSTFUL -> bonusPoints -= 1;
                case DILIGENT -> bonusPoints += 1;
                case SLOTHFUL -> bonusPoints -= 1;
                case ENVIOUS -> bonusPoints += 0;
                case PATIENT -> bonusPoints += 0;
                case IMPATIENT -> bonusPoints -= 1;
                case HUMBLE -> bonusPoints += 0;
                case PRIDE -> bonusPoints += 0;
                case TEMPERATE -> bonusPoints += 0;
                case GLUTTONOUS -> bonusPoints -= 1;
                case TRUSTING -> bonusPoints += 0;
                case PARANOID -> bonusPoints -= 1;
                case GENEROUS -> bonusPoints += 0;
                case STINGY -> bonusPoints += 0;
                case ZEALOUS -> bonusPoints += 0;
                case SCEPTICAL -> bonusPoints += 0;
            }
        }
        
        return Math.max(1, basePoints + bonusPoints);
    }
    
    private int generateDiplomaticPoints() {
        int basePoints = 3;
        int bonusPoints = 0;
        
        // Personality bonuses
        switch (personality) {
            case ADMINISTRATIVE -> bonusPoints += 0;
            case DIPLOMATIC -> bonusPoints += 1;
            case MILITARY -> bonusPoints += 0;
            case BALANCED -> bonusPoints += 0;
        }
        
        // Trait bonuses
        for (RulerTrait trait : traits) {
            switch (trait) {
                case ADMINISTRATIVE_GENIUS -> bonusPoints += 0;
                case DIPLOMATIC_GENIUS -> bonusPoints += 2;
                case MILITARY_GENIUS -> bonusPoints += 0;
                case INCOMPETENT -> bonusPoints -= 1;
                case CRUEL -> bonusPoints -= 1;
                case KIND -> bonusPoints += 1;
                case AMBITIOUS -> bonusPoints += 0;
                case LAZY -> bonusPoints -= 1;
                case JUST -> bonusPoints += 0;
                case ARBITRARY -> bonusPoints -= 1;
                case CHARITABLE -> bonusPoints += 1;
                case GREEDY -> bonusPoints -= 1;
                case BRAVE -> bonusPoints += 0;
                case COWARD -> bonusPoints -= 1;
                case CHASTE -> bonusPoints += 0;
                case LUSTFUL -> bonusPoints -= 1;
                case DILIGENT -> bonusPoints += 0;
                case SLOTHFUL -> bonusPoints -= 1;
                case ENVIOUS -> bonusPoints -= 1;
                case PATIENT -> bonusPoints += 1;
                case IMPATIENT -> bonusPoints -= 1;
                case HUMBLE -> bonusPoints += 1;
                case PRIDE -> bonusPoints -= 1;
                case TEMPERATE -> bonusPoints += 0;
                case GLUTTONOUS -> bonusPoints -= 1;
                case TRUSTING -> bonusPoints += 1;
                case PARANOID -> bonusPoints -= 1;
                case GENEROUS -> bonusPoints += 1;
                case STINGY -> bonusPoints -= 1;
                case ZEALOUS -> bonusPoints += 0;
                case SCEPTICAL -> bonusPoints += 0;
            }
        }
        
        return Math.max(1, basePoints + bonusPoints);
    }
    
    private int generateMilitaryPoints() {
        int basePoints = 3;
        int bonusPoints = 0;
        
        // Personality bonuses
        switch (personality) {
            case ADMINISTRATIVE -> bonusPoints += 0;
            case DIPLOMATIC -> bonusPoints += 0;
            case MILITARY -> bonusPoints += 1;
            case BALANCED -> bonusPoints += 0;
        }
        
        // Trait bonuses
        for (RulerTrait trait : traits) {
            switch (trait) {
                case ADMINISTRATIVE_GENIUS -> bonusPoints += 0;
                case DIPLOMATIC_GENIUS -> bonusPoints += 0;
                case MILITARY_GENIUS -> bonusPoints += 2;
                case INCOMPETENT -> bonusPoints -= 1;
                case CRUEL -> bonusPoints += 0;
                case KIND -> bonusPoints -= 1;
                case AMBITIOUS -> bonusPoints += 1;
                case LAZY -> bonusPoints -= 1;
                case JUST -> bonusPoints += 0;
                case ARBITRARY -> bonusPoints += 0;
                case CHARITABLE -> bonusPoints -= 1;
                case GREEDY -> bonusPoints += 0;
                case BRAVE -> bonusPoints += 1;
                case COWARD -> bonusPoints -= 2;
                case CHASTE -> bonusPoints += 0;
                case LUSTFUL -> bonusPoints -= 1;
                case DILIGENT -> bonusPoints += 0;
                case SLOTHFUL -> bonusPoints -= 1;
                case ENVIOUS -> bonusPoints += 0;
                case PATIENT -> bonusPoints += 0;
                case IMPATIENT -> bonusPoints += 1;
                case HUMBLE -> bonusPoints -= 1;
                case PRIDE -> bonusPoints += 1;
                case TEMPERATE -> bonusPoints += 0;
                case GLUTTONOUS -> bonusPoints -= 1;
                case TRUSTING -> bonusPoints -= 1;
                case PARANOID -> bonusPoints += 0;
                case GENEROUS -> bonusPoints -= 1;
                case STINGY -> bonusPoints += 0;
                case ZEALOUS -> bonusPoints += 0;
                case SCEPTICAL -> bonusPoints += 0;
            }
        }
        
        return Math.max(1, basePoints + bonusPoints);
    }
    
    public boolean spendAdminPoints(int amount) {
        if (adminPoints >= amount) {
            adminPoints -= amount;
            return true;
        }
        return false;
    }
    
    public boolean spendDiplomaticPoints(int amount) {
        if (diplomaticPoints >= amount) {
            diplomaticPoints -= amount;
            return true;
        }
        return false;
    }
    
    public boolean spendMilitaryPoints(int amount) {
        if (militaryPoints >= amount) {
            militaryPoints -= amount;
            return true;
        }
        return false;
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public int getAdminPoints() { return adminPoints; }
    public int getDiplomaticPoints() { return diplomaticPoints; }
    public int getMilitaryPoints() { return militaryPoints; }
    public List<RulerTrait> getTraits() { return traits; }
    public RulerPersonality getPersonality() { return personality; }
    public boolean isDead() { return isDead; }
    public int getYearsReigned() { return yearsReigned; }
    
    public enum RulerPersonality {
        ADMINISTRATIVE, DIPLOMATIC, MILITARY, BALANCED
    }
    
    public enum RulerTrait {
        ADMINISTRATIVE_GENIUS, DIPLOMATIC_GENIUS, MILITARY_GENIUS, INCOMPETENT,
        CRUEL, KIND, AMBITIOUS, LAZY, JUST, ARBITRARY, CHARITABLE, GREEDY,
        BRAVE, COWARD, CHASTE, LUSTFUL, DILIGENT, SLOTHFUL, ENVIOUS, PATIENT,
        IMPATIENT, HUMBLE, PRIDE, TEMPERATE, GLUTTONOUS, TRUSTING, PARANOID,
        GENEROUS, STINGY, ZEALOUS, SCEPTICAL
    }
} 