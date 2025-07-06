package com.romagame.events;

import com.romagame.map.Country;
import com.romagame.map.Province;
import java.util.*;
import java.util.Random;

public class EventManager {
    private Random random;
    private Map<String, GameEvent> events;
    private Map<String, Ruler> rulers;
    private Map<String, Advisor> advisors;
    private List<GameEvent> activeEvents;
    
    public EventManager() {
        this.random = new Random();
        this.events = new HashMap<>();
        this.rulers = new HashMap<>();
        this.advisors = new HashMap<>();
        this.activeEvents = new ArrayList<>();
        initializeEvents();
        initializeRulers();
        initializeAdvisors();
    }
    
    private void initializeEvents() {
        // Historical flavor events
        events.put("The Great Fire of Rome", new GameEvent(
            "The Great Fire of Rome",
            "A devastating fire has swept through the capital, destroying much of the city.",
            EventType.DISASTER,
            Arrays.asList("Accept the loss", "Organize relief efforts", "Blame the Christians"),
            Arrays.asList(
                new EventOption("Accept the loss", -2.0, -1.0, 0.0, -1.0, "Stability -2, Prestige -1"),
                new EventOption("Organize relief efforts", -1.0, 0.0, -2.0, 1.0, "Stability -1, Treasury -2, Prestige +1"),
                new EventOption("Blame the Christians", 0.0, -1.0, 0.0, -2.0, "Prestige -1, Religious Unity -2")
            )
        ));
        
        events.put("Barbarian Invasion", new GameEvent(
            "Barbarian Invasion",
            "Fierce barbarian tribes have crossed the borders, threatening our provinces.",
            EventType.MILITARY,
            Arrays.asList("Mobilize the legions", "Negotiate peace", "Pay tribute"),
            Arrays.asList(
                new EventOption("Mobilize the legions", 0.0, 0.0, -3.0, 2.0, "Treasury -3, Military Tradition +2"),
                new EventOption("Negotiate peace", -1.0, -1.0, -1.0, 0.0, "Stability -1, Prestige -1, Treasury -1"),
                new EventOption("Pay tribute", 0.0, -2.0, -2.0, 0.0, "Prestige -2, Treasury -2")
            )
        ));
        
        events.put("Plague Outbreak", new GameEvent(
            "Plague Outbreak",
            "A deadly plague has spread through our cities, claiming many lives.",
            EventType.DISASTER,
            Arrays.asList("Quarantine cities", "Pray for divine intervention", "Ignore the crisis"),
            Arrays.asList(
                new EventOption("Quarantine cities", -1.0, 0.0, -2.0, 0.0, "Stability -1, Treasury -2"),
                new EventOption("Pray for divine intervention", 0.0, 0.0, -1.0, 1.0, "Treasury -1, Religious Unity +1"),
                new EventOption("Ignore the crisis", -2.0, -1.0, 0.0, -1.0, "Stability -2, Prestige -1, Population -1")
            )
        ));
        
        events.put("Merchant Guild Petition", new GameEvent(
            "Merchant Guild Petition",
            "The merchant guilds request reduced taxes and trade privileges.",
            EventType.ECONOMIC,
            Arrays.asList("Grant privileges", "Reject demands", "Compromise"),
            Arrays.asList(
                new EventOption("Grant privileges", 0.0, 0.0, -1.0, 1.0, "Treasury -1, Trade Efficiency +1"),
                new EventOption("Reject demands", -1.0, 0.0, 0.0, -1.0, "Stability -1, Trade Efficiency -1"),
                new EventOption("Compromise", 0.0, 0.0, -0.5, 0.5, "Treasury -0.5, Trade Efficiency +0.5")
            )
        ));
        
        events.put("Religious Schism", new GameEvent(
            "Religious Schism",
            "Religious tensions have divided our population between different faiths.",
            EventType.RELIGIOUS,
            Arrays.asList("Enforce orthodoxy", "Allow tolerance", "Seek compromise"),
            Arrays.asList(
                new EventOption("Enforce orthodoxy", 0.0, 0.0, -1.0, 1.0, "Treasury -1, Religious Unity +1"),
                new EventOption("Allow tolerance", 1.0, 0.0, 0.0, -1.0, "Stability +1, Religious Unity -1"),
                new EventOption("Seek compromise", 0.0, 0.0, -0.5, 0.0, "Treasury -0.5")
            )
        ));
        
        events.put("Noble Rebellion", new GameEvent(
            "Noble Rebellion",
            "Powerful nobles have risen in rebellion against the crown.",
            EventType.POLITICAL,
            Arrays.asList("Crush the rebellion", "Negotiate peace", "Grant concessions"),
            Arrays.asList(
                new EventOption("Crush the rebellion", 0.0, 0.0, -2.0, 2.0, "Treasury -2, Military Tradition +2"),
                new EventOption("Negotiate peace", -1.0, 0.0, -1.0, 0.0, "Stability -1, Treasury -1"),
                new EventOption("Grant concessions", 0.0, -1.0, 0.0, -1.0, "Prestige -1, Nobility Influence -1")
            )
        ));
        
        events.put("Golden Age", new GameEvent(
            "Golden Age",
            "Our empire experiences unprecedented prosperity and cultural flourishing.",
            EventType.POSITIVE,
            Arrays.asList("Celebrate achievements", "Invest in future", "Expand influence"),
            Arrays.asList(
                new EventOption("Celebrate achievements", 1.0, 2.0, -1.0, 0.0, "Stability +1, Prestige +2, Treasury -1"),
                new EventOption("Invest in future", 0.0, 1.0, -2.0, 1.0, "Prestige +1, Treasury -2, Development +1"),
                new EventOption("Expand influence", 0.0, 1.0, -1.0, 1.0, "Prestige +1, Treasury -1, Diplomatic Relations +1")
            )
        ));
        
        events.put("Natural Disaster", new GameEvent(
            "Natural Disaster",
            "A devastating earthquake has struck our provinces, causing widespread destruction.",
            EventType.DISASTER,
            Arrays.asList("Organize relief", "Rebuild stronger", "Abandon affected areas"),
            Arrays.asList(
                new EventOption("Organize relief", -1.0, 0.0, -2.0, 1.0, "Stability -1, Treasury -2, Prestige +1"),
                new EventOption("Rebuild stronger", 0.0, 0.0, -3.0, 2.0, "Treasury -3, Development +2"),
                new EventOption("Abandon affected areas", -2.0, -1.0, 0.0, -1.0, "Stability -2, Prestige -1, Development -1")
            )
        ));
    }
    
    private void initializeRulers() {
        // Historical rulers with monarch points
        rulers.put("Trajan", new Ruler("Trajan", "Roman Emperor", 6, 5, 4, "Military", "Conqueror"));
        rulers.put("Hadrian", new Ruler("Hadrian", "Roman Emperor", 4, 6, 5, "Administrative", "Builder"));
        rulers.put("Marcus Aurelius", new Ruler("Marcus Aurelius", "Roman Emperor", 5, 6, 6, "Diplomatic", "Philosopher"));
        rulers.put("Augustus", new Ruler("Augustus", "Roman Emperor", 6, 6, 5, "Administrative", "Founder"));
        rulers.put("Constantine", new Ruler("Constantine", "Roman Emperor", 5, 4, 6, "Diplomatic", "Reformer"));
        rulers.put("Julian", new Ruler("Julian", "Roman Emperor", 4, 5, 4, "Military", "Restorer"));
        rulers.put("Vespasian", new Ruler("Vespasian", "Roman Emperor", 5, 5, 4, "Administrative", "Stabilizer"));
        rulers.put("Titus", new Ruler("Titus", "Roman Emperor", 4, 4, 5, "Diplomatic", "Peacemaker"));
    }
    
    private void initializeAdvisors() {
        // Historical advisors with specialties
        advisors.put("Marcus Agrippa", new Advisor("Marcus Agrippa", "Military", 3, "Naval Commander", "Naval Force Limit +20%"));
        advisors.put("Maecenas", new Advisor("Maecenas", "Diplomatic", 3, "Diplomat", "Diplomatic Relations +1"));
        advisors.put("Vitruvius", new Advisor("Vitruvius", "Administrative", 3, "Architect", "Development Cost -10%"));
        advisors.put("Livy", new Advisor("Livy", "Administrative", 2, "Historian", "Prestige +0.1"));
        advisors.put("Ovid", new Advisor("Ovid", "Diplomatic", 2, "Poet", "Cultural Conversion +10%"));
        advisors.put("Pliny the Elder", new Advisor("Pliny the Elder", "Administrative", 3, "Scholar", "Technology Cost -5%"));
        advisors.put("Seneca", new Advisor("Seneca", "Diplomatic", 3, "Philosopher", "Stability +0.1"));
        advisors.put("Tacitus", new Advisor("Tacitus", "Administrative", 2, "Historian", "Legitimacy +0.1"));
    }
    
    public void processRandomEvents(Country country) {
        // Random chance for events to occur
        if (random.nextDouble() < 0.1) { // 10% chance per turn
            GameEvent event = getRandomEvent();
            if (event != null) {
                activeEvents.add(event);
            }
        }
    }
    
    public GameEvent getRandomEvent() {
        List<String> eventNames = new ArrayList<>(events.keySet());
        if (!eventNames.isEmpty()) {
            String randomEventName = eventNames.get(random.nextInt(eventNames.size()));
            return events.get(randomEventName);
        }
        return null;
    }
    
    public void applyEventChoice(Country country, GameEvent event, int choiceIndex) {
        if (choiceIndex >= 0 && choiceIndex < event.getOptions().size()) {
            EventOption option = event.getOptions().get(choiceIndex);
            
            // Apply effects
            country.setStability(country.getStability() + option.getStabilityEffect());
            country.setPrestige(country.getPrestige() + option.getPrestigeEffect());
            country.setTreasury(country.getTreasury() + option.getTreasuryEffect());
            
            // Apply monarch point costs if applicable
            Ruler currentRuler = getCurrentRuler(country);
            if (currentRuler != null) {
                switch (event.getType()) {
                    case ADMINISTRATIVE:
                        currentRuler.spendAdministrativePoints(1);
                        break;
                    case DIPLOMATIC:
                        currentRuler.spendDiplomaticPoints(1);
                        break;
                    case MILITARY:
                        currentRuler.spendMilitaryPoints(1);
                        break;
                    default:
                        break;
                }
            }
            
            // Remove from active events
            activeEvents.remove(event);
        }
    }
    
    public Ruler getCurrentRuler(Country country) {
        // For now, return a random ruler - in a full implementation, this would track current ruler
        List<String> rulerNames = new ArrayList<>(rulers.keySet());
        if (!rulerNames.isEmpty()) {
            String randomRulerName = rulerNames.get(random.nextInt(rulerNames.size()));
            return rulers.get(randomRulerName);
        }
        return null;
    }
    
    public List<Advisor> getAvailableAdvisors() {
        return new ArrayList<>(advisors.values());
    }
    
    public List<Ruler> getAvailableRulers() {
        return new ArrayList<>(rulers.values());
    }
    
    public List<GameEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents);
    }
    
    public enum EventType {
        ADMINISTRATIVE, DIPLOMATIC, MILITARY, ECONOMIC, RELIGIOUS, POLITICAL, DISASTER, POSITIVE
    }
    
    public static class GameEvent {
        private String name;
        private String description;
        private EventType type;
        private List<String> choices;
        private List<EventOption> options;
        
        public GameEvent(String name, String description, EventType type, 
                        List<String> choices, List<EventOption> options) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.choices = choices;
            this.options = options;
        }
        
        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public EventType getType() { return type; }
        public List<String> getChoices() { return choices; }
        public List<EventOption> getOptions() { return options; }
    }
    
    public static class EventOption {
        private String name;
        private double stabilityEffect;
        private double prestigeEffect;
        private double treasuryEffect;
        private double otherEffect;
        private String description;
        
        public EventOption(String name, double stabilityEffect, double prestigeEffect, 
                         double treasuryEffect, double otherEffect, String description) {
            this.name = name;
            this.stabilityEffect = stabilityEffect;
            this.prestigeEffect = prestigeEffect;
            this.treasuryEffect = treasuryEffect;
            this.otherEffect = otherEffect;
            this.description = description;
        }
        
        // Getters
        public String getName() { return name; }
        public double getStabilityEffect() { return stabilityEffect; }
        public double getPrestigeEffect() { return prestigeEffect; }
        public double getTreasuryEffect() { return treasuryEffect; }
        public double getOtherEffect() { return otherEffect; }
        public String getDescription() { return description; }
    }
    
    public static class Ruler {
        private String name;
        private String title;
        private int administrativePoints;
        private int diplomaticPoints;
        private int militaryPoints;
        private String focus;
        private String personality;
        
        public Ruler(String name, String title, int admin, int diplo, int mil, String focus, String personality) {
            this.name = name;
            this.title = title;
            this.administrativePoints = admin;
            this.diplomaticPoints = diplo;
            this.militaryPoints = mil;
            this.focus = focus;
            this.personality = personality;
        }
        
        public void spendAdministrativePoints(int amount) {
            administrativePoints = Math.max(0, administrativePoints - amount);
        }
        
        public void spendDiplomaticPoints(int amount) {
            diplomaticPoints = Math.max(0, diplomaticPoints - amount);
        }
        
        public void spendMilitaryPoints(int amount) {
            militaryPoints = Math.max(0, militaryPoints - amount);
        }
        
        // Getters
        public String getName() { return name; }
        public String getTitle() { return title; }
        public int getAdministrativePoints() { return administrativePoints; }
        public int getDiplomaticPoints() { return diplomaticPoints; }
        public int getMilitaryPoints() { return militaryPoints; }
        public String getFocus() { return focus; }
        public String getPersonality() { return personality; }
    }
    
    public static class Advisor {
        private String name;
        private String type;
        private int level;
        private String specialty;
        private String effect;
        
        public Advisor(String name, String type, int level, String specialty, String effect) {
            this.name = name;
            this.type = type;
            this.level = level;
            this.specialty = specialty;
            this.effect = effect;
        }
        
        // Getters
        public String getName() { return name; }
        public String getType() { return type; }
        public int getLevel() { return level; }
        public String getSpecialty() { return specialty; }
        public String getEffect() { return effect; }
    }
} 