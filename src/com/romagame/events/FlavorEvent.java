package com.romagame.events;

import com.romagame.map.Country;
import java.util.*;

public class FlavorEvent {
    private String id;
    private String title;
    private String description;
    private List<EventChoice> choices;
    private EventTrigger trigger;
    private boolean isTriggered;
    private String triggeredBy;
    
    public FlavorEvent(String id, String title, String description, EventTrigger trigger) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.trigger = trigger;
        this.choices = new ArrayList<>();
        this.isTriggered = false;
        this.triggeredBy = null;
    }
    
    public void addChoice(EventChoice choice) {
        choices.add(choice);
    }
    
    public boolean canTrigger(Country country) {
        return !isTriggered && trigger.checkConditions(country);
    }
    
    public void trigger(String triggeredBy) {
        this.isTriggered = true;
        this.triggeredBy = triggeredBy;
    }
    
    public void executeChoice(Country country, int choiceIndex) {
        if (choiceIndex >= 0 && choiceIndex < choices.size()) {
            EventChoice choice = choices.get(choiceIndex);
            choice.execute(country);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<EventChoice> getChoices() { return choices; }
    public EventTrigger getTrigger() { return trigger; }
    public boolean isTriggered() { return isTriggered; }
    public String getTriggeredBy() { return triggeredBy; }
    
    // Static factory methods for common events
    public static FlavorEvent createPlagueEvent() {
        FlavorEvent event = new FlavorEvent(
            "plague_outbreak",
            "A Plague Outbreak",
            "A terrible plague has broken out in our lands. The people are dying in droves and panic is spreading.",
            new EventTrigger("stability < 2", "population > 10000")
        );
        
        event.addChoice(new EventChoice(
            "Quarantine the affected areas",
            "We must isolate the sick to prevent further spread.",
            Map.of("stability", -1.0, "prestige", -10.0, "population", -0.1)
        ));
        
        event.addChoice(new EventChoice(
            "Pray for divine intervention",
            "Only the gods can save us now.",
            Map.of("stability", -0.5, "prestige", -5.0, "religious_unity", 0.1)
        ));
        
        event.addChoice(new EventChoice(
            "Implement strict hygiene measures",
            "We will enforce cleanliness and proper burial practices.",
            Map.of("stability", -0.5, "prestige", 5.0, "population", -0.05)
        ));
        
        return event;
    }
    
    public static FlavorEvent createNobleRevoltEvent() {
        FlavorEvent event = new FlavorEvent(
            "noble_revolt",
            "Noble Revolt",
            "The nobles are dissatisfied with our rule and have risen in rebellion.",
            new EventTrigger("stability < 1", "prestige < 50")
        );
        
        event.addChoice(new EventChoice(
            "Crush the rebellion",
            "We will show them the price of treason.",
            Map.of("stability", -1.0, "prestige", -20.0, "legitimacy", -0.1)
        ));
        
        event.addChoice(new EventChoice(
            "Negotiate with the nobles",
            "Perhaps we can reach a compromise.",
            Map.of("stability", 0.5, "prestige", -10.0, "legitimacy", 0.05)
        ));
        
        event.addChoice(new EventChoice(
            "Grant concessions",
            "We will give them what they want to maintain peace.",
            Map.of("stability", 1.0, "prestige", -15.0, "legitimacy", -0.1)
        ));
        
        return event;
    }
    
    public static FlavorEvent createTradeOpportunityEvent() {
        FlavorEvent event = new FlavorEvent(
            "trade_opportunity",
            "Trade Opportunity",
            "Merchants from distant lands have arrived with exotic goods and trade proposals.",
            new EventTrigger("stability > 1", "prestige > 20")
        );
        
        event.addChoice(new EventChoice(
            "Embrace the opportunity",
            "We will welcome these merchants and their goods.",
            Map.of("stability", 0.5, "prestige", 10.0, "trade_efficiency", 0.1)
        ));
        
        event.addChoice(new EventChoice(
            "Impose strict regulations",
            "We must control this trade carefully.",
            Map.of("stability", 0.5, "prestige", 5.0, "trade_efficiency", 0.05)
        ));
        
        event.addChoice(new EventChoice(
            "Reject the offer",
            "We have no need for foreign goods.",
            Map.of("stability", 0.0, "prestige", -5.0, "trade_efficiency", -0.05)
        ));
        
        return event;
    }
} 