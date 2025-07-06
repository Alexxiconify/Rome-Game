package com.romagame.map;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GovernmentReform {
    public enum ReformType {
        GOVERNMENT_FORM,      // Monarchy, Republic, Theocracy, etc.
        ADMINISTRATIVE,       // Administrative efficiency, stability
        MILITARY,            // Army quality, discipline
        DIPLOMATIC,          // Diplomatic relations, trade
        ECONOMIC,            // Trade efficiency, production
        RELIGIOUS,           // Religious unity, tolerance
        CULTURAL,            // Cultural acceptance, unrest
        LEGAL,              // Laws, corruption
        SOCIAL,              // Social reforms, population happiness
        POLITICAL_PARTY      // Political parties and interest groups
    }
    
    public enum InterestGroup {
        NOBILITY, MERCHANTS, CLERGY, MILITARY, BURGHERS, PEASANTS, 
        INTELLECTUALS, WORKERS, CAPITALISTS, NATIONALISTS, LIBERALS, CONSERVATIVES
    }
    
    private String name;
    private String description;
    private ReformType type;
    private int tier;
    private int cost;
    private List<String> requirements;
    private Map<String, Double> effects;
    private List<InterestGroup> supportedBy;
    private List<InterestGroup> opposedBy;
    private boolean isImplemented;
    private int yearUnlocked;
    
    public GovernmentReform(String name, String description, ReformType type, int tier, int cost, int yearUnlocked) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.cost = cost;
        this.yearUnlocked = yearUnlocked;
        this.requirements = new ArrayList<>();
        this.effects = new HashMap<>();
        this.supportedBy = new ArrayList<>();
        this.opposedBy = new ArrayList<>();
        this.isImplemented = false;
    }
    
    // Create tiered government reforms from early history to modern times
    public static List<GovernmentReform> createTieredReforms() {
        List<GovernmentReform> reforms = new ArrayList<>();
        
        // TIER 1 - Early Historical (Ancient Times)
        GovernmentReform tribalCouncil = new GovernmentReform("Tribal Council", 
            "Rule by council of tribal elders and warriors", ReformType.GOVERNMENT_FORM, 1, 50, -2000);
        tribalCouncil.addEffect("manpower", 0.2);
        tribalCouncil.addEffect("army_tradition", 0.1);
        tribalCouncil.addEffect("diplomatic_relations", -1);
        tribalCouncil.addSupportedBy(InterestGroup.NOBILITY, InterestGroup.MILITARY);
        reforms.add(tribalCouncil);
        
        GovernmentReform absoluteMonarchy = new GovernmentReform("Absolute Monarchy", 
            "Centralized power in the hands of the monarch", ReformType.GOVERNMENT_FORM, 1, 100, -1500);
        absoluteMonarchy.addEffect("stability", 0.1);
        absoluteMonarchy.addEffect("diplomatic_relations", 1);
        absoluteMonarchy.addEffect("absolutism", 0.1);
        absoluteMonarchy.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(absoluteMonarchy);
        
        GovernmentReform merchantRepublic = new GovernmentReform("Merchant Republic", 
            "Rule by wealthy merchants and trade guilds", ReformType.GOVERNMENT_FORM, 1, 100, -1200);
        merchantRepublic.addEffect("trade_efficiency", 0.15);
        merchantRepublic.addEffect("merchant_slots", 1);
        merchantRepublic.addEffect("burghers_influence", 0.2);
        merchantRepublic.addSupportedBy(InterestGroup.MERCHANTS, InterestGroup.BURGHERS);
        reforms.add(merchantRepublic);
        
        GovernmentReform theocracy = new GovernmentReform("Theocratic State", 
            "Religious leaders hold political power", ReformType.GOVERNMENT_FORM, 1, 100, -1000);
        theocracy.addEffect("religious_unity", 0.2);
        theocracy.addEffect("missionary_strength", 0.1);
        theocracy.addEffect("clergy_influence", 0.3);
        theocracy.addSupportedBy(InterestGroup.CLERGY);
        reforms.add(theocracy);
        
        // TIER 2 - Classical Period
        GovernmentReform expandedRoyalCourt = new GovernmentReform("Expanded Royal Court", 
            "Strengthens central administration", ReformType.ADMINISTRATIVE, 2, 75, -800);
        expandedRoyalCourt.addEffect("governing_capacity", 0.1);
        expandedRoyalCourt.addEffect("diplomatic_relations", 1);
        expandedRoyalCourt.addEffect("advisor_cost", -0.1);
        expandedRoyalCourt.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(expandedRoyalCourt);
        
        GovernmentReform compromiseNobility = new GovernmentReform("Compromise with Nobility", 
            "Balance of power between monarch and nobility", ReformType.ADMINISTRATIVE, 2, 60, -700);
        compromiseNobility.addEffect("nobility_influence", 0.2);
        compromiseNobility.addEffect("stability", 0.05);
        compromiseNobility.addEffect("absolutism", -0.05);
        compromiseNobility.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(compromiseNobility);
        
        GovernmentReform strengthenNoblePrivileges = new GovernmentReform("Strengthen Noble Privileges", 
            "Increases noble power and influence", ReformType.ADMINISTRATIVE, 2, 80, -600);
        strengthenNoblePrivileges.addEffect("nobility_influence", 0.3);
        strengthenNoblePrivileges.addEffect("cavalry_combat_ability", 0.1);
        strengthenNoblePrivileges.addEffect("absolutism", -0.1);
        strengthenNoblePrivileges.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(strengthenNoblePrivileges);
        
        // TIER 3 - Medieval Period
        GovernmentReform decentralizedBureaucracy = new GovernmentReform("Decentralized Bureaucracy", 
            "Administrative duties spread out", ReformType.ADMINISTRATIVE, 3, 70, -500);
        decentralizedBureaucracy.addEffect("autonomy", 0.1);
        decentralizedBureaucracy.addEffect("unrest", -0.1);
        decentralizedBureaucracy.addEffect("administrative_efficiency", -0.05);
        decentralizedBureaucracy.addSupportedBy(InterestGroup.BURGHERS);
        reforms.add(decentralizedBureaucracy);
        
        GovernmentReform expandedTempleRights = new GovernmentReform("Expanded Temple Rights", 
            "Greater religious institution autonomy", ReformType.RELIGIOUS, 3, 65, -400);
        expandedTempleRights.addEffect("missionary_strength", 0.15);
        expandedTempleRights.addEffect("religious_tolerance", 0.1);
        expandedTempleRights.addEffect("clergy_influence", 0.2);
        expandedTempleRights.addSupportedBy(InterestGroup.CLERGY);
        reforms.add(expandedTempleRights);
        
        GovernmentReform expandedRoyalCourts = new GovernmentReform("Expanded Royal Courts", 
            "Enhanced administrative capabilities", ReformType.ADMINISTRATIVE, 3, 85, -300);
        expandedRoyalCourts.addEffect("advisor_recruitment", 0.1);
        expandedRoyalCourts.addEffect("stability", 0.1);
        expandedRoyalCourts.addEffect("absolutism", 0.05);
        expandedRoyalCourts.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(expandedRoyalCourts);
        
        // TIER 4 - Late Medieval
        GovernmentReform provincialAutonomy = new GovernmentReform("Provincial Autonomy", 
            "Self-governance in provinces", ReformType.ADMINISTRATIVE, 4, 90, -200);
        provincialAutonomy.addEffect("autonomy", 0.2);
        provincialAutonomy.addEffect("unrest", -0.15);
        provincialAutonomy.addEffect("tax_income", -0.1);
        provincialAutonomy.addSupportedBy(InterestGroup.BURGHERS, InterestGroup.PEASANTS);
        reforms.add(provincialAutonomy);
        
        GovernmentReform mercenaryRecruitment = new GovernmentReform("Mercenary Recruitment", 
            "Improved mercenary utilization", ReformType.MILITARY, 4, 75, -150);
        mercenaryRecruitment.addEffect("mercenary_cost", -0.1);
        mercenaryRecruitment.addEffect("mercenary_maintenance", -0.1);
        mercenaryRecruitment.addEffect("army_tradition", 0.05);
        mercenaryRecruitment.addSupportedBy(InterestGroup.MILITARY);
        reforms.add(mercenaryRecruitment);
        
        // TIER 5 - Renaissance
        GovernmentReform sustainedDiscipline = new GovernmentReform("Sustained Discipline", 
            "Military discipline improvement", ReformType.MILITARY, 5, 100, 1400);
        sustainedDiscipline.addEffect("discipline", 0.05);
        sustainedDiscipline.addEffect("army_tradition", 0.1);
        sustainedDiscipline.addSupportedBy(InterestGroup.MILITARY);
        reforms.add(sustainedDiscipline);
        
        GovernmentReform militaryEngineering = new GovernmentReform("Military Engineering", 
            "Enhanced fortification capabilities", ReformType.MILITARY, 5, 85, 1450);
        militaryEngineering.addEffect("fort_level", 1);
        militaryEngineering.addEffect("siege_ability", 0.1);
        militaryEngineering.addEffect("defensiveness", 0.1);
        militaryEngineering.addSupportedBy(InterestGroup.MILITARY);
        reforms.add(militaryEngineering);
        
        GovernmentReform navalRecruitment = new GovernmentReform("Naval Recruitment", 
            "Improved naval capabilities", ReformType.MILITARY, 5, 90, 1500);
        navalRecruitment.addEffect("naval_force_limit", 0.2);
        navalRecruitment.addEffect("ship_cost", -0.1);
        navalRecruitment.addEffect("naval_tradition", 0.1);
        navalRecruitment.addSupportedBy(InterestGroup.MERCHANTS);
        reforms.add(navalRecruitment);
        
        // TIER 6 - Early Modern
        GovernmentReform statesGeneral = new GovernmentReform("States General", 
            "Representative body for national decisions", ReformType.POLITICAL_PARTY, 6, 120, 1600);
        statesGeneral.addEffect("stability", 0.1);
        statesGeneral.addEffect("legitimacy", 0.1);
        statesGeneral.addEffect("absolutism", -0.05);
        statesGeneral.addSupportedBy(InterestGroup.BURGHERS, InterestGroup.NOBILITY);
        reforms.add(statesGeneral);
        
        GovernmentReform nationalism = new GovernmentReform("Nationalism", 
            "Foster national identity and loyalty", ReformType.CULTURAL, 6, 110, 1650);
        nationalism.addEffect("separatism", -0.2);
        nationalism.addEffect("national_unrest", -0.1);
        nationalism.addEffect("prestige", 0.1);
        nationalism.addSupportedBy(InterestGroup.NATIONALISTS);
        reforms.add(nationalism);
        
        GovernmentReform centralizedBureaucracy = new GovernmentReform("Centralized Bureaucracy", 
            "Strong central administrative system", ReformType.ADMINISTRATIVE, 6, 130, 1700);
        centralizedBureaucracy.addEffect("tax_income", 0.15);
        centralizedBureaucracy.addEffect("production_efficiency", 0.1);
        centralizedBureaucracy.addEffect("absolutism", 0.1);
        centralizedBureaucracy.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(centralizedBureaucracy);
        
        // TIER 7 - Enlightenment
        GovernmentReform dynasticAdministration = new GovernmentReform("Dynastic Administration", 
            "Strengthen ruling dynasty control", ReformType.ADMINISTRATIVE, 7, 140, 1750);
        dynasticAdministration.addEffect("legitimacy", 0.15);
        dynasticAdministration.addEffect("diplomatic_relations", 1);
        dynasticAdministration.addEffect("absolutism", 0.1);
        dynasticAdministration.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(dynasticAdministration);
        
        GovernmentReform meritocraticRecruitment = new GovernmentReform("Meritocratic Recruitment", 
            "Appointment based on merit", ReformType.ADMINISTRATIVE, 7, 125, 1780);
        meritocraticRecruitment.addEffect("administrative_efficiency", 0.1);
        meritocraticRecruitment.addEffect("corruption", -0.1);
        meritocraticRecruitment.addEffect("advisor_cost", -0.15);
        meritocraticRecruitment.addSupportedBy(InterestGroup.INTELLECTUALS, InterestGroup.LIBERALS);
        reforms.add(meritocraticRecruitment);
        
        GovernmentReform religiousState = new GovernmentReform("Religious State", 
            "Reinforce religion in government", ReformType.RELIGIOUS, 7, 115, 1800);
        religiousState.addEffect("religious_unity", 0.25);
        religiousState.addEffect("missionary_strength", 0.15);
        religiousState.addEffect("stability", 0.1);
        religiousState.addSupportedBy(InterestGroup.CLERGY, InterestGroup.CONSERVATIVES);
        reforms.add(religiousState);
        
        // TIER 8 - Industrial Revolution
        GovernmentReform empowerBurghers = new GovernmentReform("Empower the Burghers", 
            "Increase merchant class influence", ReformType.ECONOMIC, 8, 150, 1820);
        empowerBurghers.addEffect("trade_efficiency", 0.2);
        empowerBurghers.addEffect("production_efficiency", 0.15);
        empowerBurghers.addEffect("technology_cost", -0.05);
        empowerBurghers.addSupportedBy(InterestGroup.MERCHANTS, InterestGroup.BURGHERS);
        reforms.add(empowerBurghers);
        
        GovernmentReform curtailBurghers = new GovernmentReform("Curtail the Burghers", 
            "Reduce merchant class power", ReformType.ECONOMIC, 8, 140, 1830);
        curtailBurghers.addEffect("absolutism", 0.15);
        curtailBurghers.addEffect("nobility_influence", 0.2);
        curtailBurghers.addEffect("trade_efficiency", -0.1);
        curtailBurghers.addSupportedBy(InterestGroup.NOBILITY, InterestGroup.CONSERVATIVES);
        reforms.add(curtailBurghers);
        
        GovernmentReform aristocraticReforms = new GovernmentReform("Aristocratic Reforms", 
            "Strengthen aristocracy power", ReformType.ADMINISTRATIVE, 8, 145, 1840);
        aristocraticReforms.addEffect("cavalry_combat_ability", 0.15);
        aristocraticReforms.addEffect("army_tradition", 0.1);
        aristocraticReforms.addEffect("prestige", 0.1);
        aristocraticReforms.addSupportedBy(InterestGroup.NOBILITY);
        reforms.add(aristocraticReforms);
        
        // TIER 9 - Modern Era
        GovernmentReform machiavellianReign = new GovernmentReform("Machiavellian Reign", 
            "Pragmatic and ruthless governance", ReformType.ADMINISTRATIVE, 9, 160, 1860);
        machiavellianReign.addEffect("espionage_efficiency", 0.2);
        machiavellianReign.addEffect("diplomatic_reputation", 0.1);
        machiavellianReign.addEffect("stability", -0.05);
        machiavellianReign.addSupportedBy(InterestGroup.CONSERVATIVES);
        reforms.add(machiavellianReign);
        
        GovernmentReform legalReforms = new GovernmentReform("Legal and Administrative Reforms", 
            "Emphasize legal and administrative reforms", ReformType.LEGAL, 9, 155, 1870);
        legalReforms.addEffect("stability", 0.15);
        legalReforms.addEffect("autonomy", -0.1);
        legalReforms.addEffect("legitimacy", 0.1);
        legalReforms.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.INTELLECTUALS);
        reforms.add(legalReforms);
        
        GovernmentReform socialContract = new GovernmentReform("The Social Contract", 
            "Promote egalitarian society", ReformType.SOCIAL, 9, 165, 1880);
        socialContract.addEffect("stability", 0.1);
        socialContract.addEffect("legitimacy", 0.15);
        socialContract.addEffect("republican_tradition", 0.1);
        socialContract.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.WORKERS);
        reforms.add(socialContract);
        
        // TIER 10 - Late Modern
        GovernmentReform regionalRepresentation = new GovernmentReform("Regional Representation", 
            "Greater regional representation", ReformType.POLITICAL_PARTY, 10, 175, 1900);
        regionalRepresentation.addEffect("separatism", -0.15);
        regionalRepresentation.addEffect("autonomy", 0.1);
        regionalRepresentation.addEffect("unrest", -0.1);
        regionalRepresentation.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.NATIONALISTS);
        reforms.add(regionalRepresentation);
        
        GovernmentReform absolutePower = new GovernmentReform("L'Etat c'est moi", 
            "Strong centralized monarchy", ReformType.GOVERNMENT_FORM, 10, 180, 1910);
        absolutePower.addEffect("absolutism", 0.2);
        absolutePower.addEffect("administrative_efficiency", 0.15);
        absolutePower.addEffect("military_power", 0.1);
        absolutePower.addSupportedBy(InterestGroup.CONSERVATIVES, InterestGroup.NOBILITY);
        reforms.add(absolutePower);
        
        GovernmentReform popularRepresentation = new GovernmentReform("Popular Representation", 
            "Increase common people influence", ReformType.POLITICAL_PARTY, 10, 170, 1920);
        popularRepresentation.addEffect("stability", 0.1);
        popularRepresentation.addEffect("legitimacy", 0.15);
        popularRepresentation.addEffect("republican_tradition", 0.1);
        popularRepresentation.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.WORKERS);
        reforms.add(popularRepresentation);
        
        // TIER 11 - Contemporary
        GovernmentReform politicalAbsolutism = new GovernmentReform("Political Absolutism", 
            "Height of monarchical power", ReformType.GOVERNMENT_FORM, 11, 200, 1930);
        politicalAbsolutism.addEffect("absolutism", 0.25);
        politicalAbsolutism.addEffect("administrative_efficiency", 0.2);
        politicalAbsolutism.addEffect("stability", -0.05);
        politicalAbsolutism.addSupportedBy(InterestGroup.CONSERVATIVES);
        reforms.add(politicalAbsolutism);
        
        GovernmentReform constitutionalMonarchy = new GovernmentReform("Constitutional Monarchy", 
            "Monarch's power limited by constitution", ReformType.GOVERNMENT_FORM, 11, 190, 1940);
        constitutionalMonarchy.addEffect("stability", 0.15);
        constitutionalMonarchy.addEffect("legitimacy", 0.2);
        constitutionalMonarchy.addEffect("absolutism", -0.1);
        constitutionalMonarchy.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.BURGHERS);
        reforms.add(constitutionalMonarchy);
        
        GovernmentReform republic = new GovernmentReform("Republic", 
            "Non-monarchical elected government", ReformType.GOVERNMENT_FORM, 11, 185, 1950);
        republic.addEffect("republican_tradition", 0.2);
        republic.addEffect("stability", 0.1);
        republic.addEffect("absolutism", -0.2);
        republic.addSupportedBy(InterestGroup.LIBERALS, InterestGroup.WORKERS);
        reforms.add(republic);
        
        return reforms;
    }
    
    public void addRequirement(String requirement) {
        requirements.add(requirement);
    }
    
    public void addEffect(String effect, double value) {
        effects.put(effect, value);
    }
    
    public void addSupportedBy(InterestGroup... groups) {
        for (InterestGroup group : groups) {
            supportedBy.add(group);
        }
    }
    
    public void addOpposedBy(InterestGroup... groups) {
        for (InterestGroup group : groups) {
            opposedBy.add(group);
        }
    }
    
    public boolean canImplement(Country country) {
        // Check if country meets requirements
        for (String requirement : requirements) {
            if (!checkRequirement(country, requirement)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkRequirement(Country country, String requirement) {
        // Simple requirement checking - can be expanded
        switch (requirement) {
            case "stability_3" -> {
                return country.getStability() >= 3;
            }
            case "treasury_100" -> {
                return country.getTreasury() >= 100;
            }
            case "development_10" -> {
                return country.getTotalDevelopment() >= 10;
            }
            case "year_1500" -> {
                return country.getGameYear() >= 1500;
            }
            case "year_1700" -> {
                return country.getGameYear() >= 1700;
            }
            case "year_1900" -> {
                return country.getGameYear() >= 1900;
            }
            default -> {
                return true; // Default to allowing
            }
        }
    }
    
    public void implement(Country country) {
        if (canImplement(country) && !isImplemented) {
            // Apply effects to country
            for (Map.Entry<String, Double> effect : effects.entrySet()) {
                applyEffect(country, effect.getKey(), effect.getValue());
            }
            isImplemented = true;
        }
    }
    
    private void applyEffect(Country country, String effect, double value) {
        switch (effect) {
            case "stability" -> {
                country.setStability(country.getStability() + value);
            }
            case "trade_efficiency" -> {
                country.addModifier("Trade Efficiency", value);
            }
            case "production_efficiency" -> {
                country.addModifier("Production Efficiency", value);
            }
            case "administrative_efficiency" -> {
                country.addModifier("Administrative Efficiency", value);
            }
            case "army_quality" -> {
                country.addModifier("Army Quality", value);
            }
            case "discipline" -> {
                country.addModifier("Discipline", value);
            }
            case "religious_unity" -> {
                country.addModifier("Religious Unity", value);
            }
            case "corruption" -> {
                country.addModifier("Corruption", value);
            }
            case "unrest" -> {
                country.addModifier("Unrest", value);
            }
            case "technology_cost" -> {
                country.addModifier("Technology Cost", value);
            }
            case "innovation" -> {
                country.addModifier("Innovation", value);
            }
            case "population_growth" -> {
                country.addModifier("Population Growth", value);
            }
            case "merchant_slots" -> {
                country.addModifier("Merchant Slots", (int)value);
            }
            case "leader_pool" -> {
                country.addModifier("Leader Pool", (int)value);
            }
            case "army_tradition" -> {
                country.addModifier("Army Tradition", value);
            }
            case "goods_produced" -> {
                country.addModifier("Goods Produced", value);
            }
            case "tolerance" -> {
                country.addModifier("Tolerance", value);
            }
            case "missionary_strength" -> {
                country.addModifier("Missionary Strength", value);
            }
            case "diplomatic_relations" -> {
                country.addModifier("Diplomatic Relations", (int)value);
            }
            case "manpower" -> {
                country.addModifier("Manpower", value);
            }
            case "absolutism" -> {
                country.addModifier("Absolutism", value);
            }
            case "governing_capacity" -> {
                country.addModifier("Governing Capacity", value);
            }
            case "advisor_cost" -> {
                country.addModifier("Advisor Cost", value);
            }
            case "nobility_influence" -> {
                country.addModifier("Nobility Influence", value);
            }
            case "cavalry_combat_ability" -> {
                country.addModifier("Cavalry Combat Ability", value);
            }
            case "autonomy" -> {
                country.addModifier("Autonomy", value);
            }
            case "mercenary_cost" -> {
                country.addModifier("Mercenary Cost", value);
            }
            case "mercenary_maintenance" -> {
                country.addModifier("Mercenary Maintenance", value);
            }
            case "fort_level" -> {
                country.addModifier("Fort Level", (int)value);
            }
            case "siege_ability" -> {
                country.addModifier("Siege Ability", value);
            }
            case "defensiveness" -> {
                country.addModifier("Defensiveness", value);
            }
            case "naval_force_limit" -> {
                country.addModifier("Naval Force Limit", value);
            }
            case "ship_cost" -> {
                country.addModifier("Ship Cost", value);
            }
            case "naval_tradition" -> {
                country.addModifier("Naval Tradition", value);
            }
            case "legitimacy" -> {
                country.addModifier("Legitimacy", value);
            }
            case "separatism" -> {
                country.addModifier("Separatism", value);
            }
            case "national_unrest" -> {
                country.addModifier("National Unrest", value);
            }
            case "prestige" -> {
                country.addModifier("Prestige", value);
            }
            case "tax_income" -> {
                country.addModifier("Tax Income", value);
            }
            case "burghers_influence" -> {
                country.addModifier("Burghers Influence", value);
            }
            case "clergy_influence" -> {
                country.addModifier("Clergy Influence", value);
            }
            case "espionage_efficiency" -> {
                country.addModifier("Espionage Efficiency", value);
            }
            case "diplomatic_reputation" -> {
                country.addModifier("Diplomatic Reputation", value);
            }
            case "republican_tradition" -> {
                country.addModifier("Republican Tradition", value);
            }
            case "military_power" -> {
                country.addModifier("Military Power", value);
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ReformType getType() { return type; }
    public int getTier() { return tier; }
    public int getCost() { return cost; }
    public int getYearUnlocked() { return yearUnlocked; }
    public List<String> getRequirements() { return new ArrayList<>(requirements); }
    public Map<String, Double> getEffects() { return new HashMap<>(effects); }
    public List<InterestGroup> getSupportedBy() { return new ArrayList<>(supportedBy); }
    public List<InterestGroup> getOpposedBy() { return new ArrayList<>(opposedBy); }
    public boolean isImplemented() { return isImplemented; }
    
    @Override
    public String toString() {
        return name + " (Tier " + tier + ") - " + description;
    }
} 