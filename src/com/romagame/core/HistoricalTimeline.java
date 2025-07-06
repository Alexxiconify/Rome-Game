package com.romagame.core;

import com.romagame.map.Country;
import com.romagame.map.WorldMap;
import com.romagame.country.CountryManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class HistoricalTimeline {
    private WorldMap worldMap;
    private Map<String, HistoricalNation> historicalNations;
    private List<String> spawnedNations;
    
    public HistoricalTimeline(WorldMap worldMap, CountryManager countryManager) {
        this.worldMap = worldMap;
        this.historicalNations = new HashMap<>();
        this.spawnedNations = new ArrayList<>();
        initializeHistoricalNations();
    }
    
    private void initializeHistoricalNations() {
        // Germanic Tribes (spawn around 100-300 AD)
        addHistoricalNation("Goths", 150, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Vandals", 200, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Franks", 250, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Saxons", 300, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Alemanni", 200, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Burgundians", 250, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        addHistoricalNation("Lombards", 300, "Germanic", "Tribal Confederation", "Germanic Paganism", "Germanic");
        
        // Celtic Nations (spawn around 100-200 AD)
        addHistoricalNation("Picts", 150, "Celtic", "Tribal Confederation", "Celtic Paganism", "Celtic");
        addHistoricalNation("Caledonians", 120, "Celtic", "Tribal Confederation", "Celtic Paganism", "Celtic");
        addHistoricalNation("Brigantes", 100, "Celtic", "Tribal Confederation", "Celtic Paganism", "Celtic");
        
        // Eastern Nations (spawn around 200-400 AD)
        addHistoricalNation("Huns", 350, "Eastern", "Tribal Confederation", "Tengrism", "Eastern");
        addHistoricalNation("Alans", 200, "Eastern", "Tribal Confederation", "Scythian Paganism", "Eastern");
        addHistoricalNation("Sarmatians", 150, "Eastern", "Tribal Confederation", "Scythian Paganism", "Eastern");
        addHistoricalNation("Roxolani", 200, "Eastern", "Tribal Confederation", "Scythian Paganism", "Eastern");
        
        // African Nations (spawn around 100-300 AD)
        addHistoricalNation("Garamantes", 100, "African", "Tribal Confederation", "Libyan Paganism", "African");
        addHistoricalNation("Blemmyes", 200, "African", "Tribal Confederation", "Nubian Paganism", "African");
        addHistoricalNation("Nobatae", 250, "African", "Tribal Confederation", "Nubian Paganism", "African");
        
        // Arabian Nations (spawn around 200-400 AD)
        addHistoricalNation("Lakhmids", 300, "Arabian", "Tribal Confederation", "Arabian Paganism", "Arabian");
        addHistoricalNation("Ghassanids", 350, "Arabian", "Tribal Confederation", "Christianity", "Arabian");
        addHistoricalNation("Kindah", 400, "Arabian", "Tribal Confederation", "Arabian Paganism", "Arabian");
        
        // Indian Subcontinent (spawn around 100-300 AD)
        addHistoricalNation("Gupta Empire", 320, "Indian", "Empire", "Hinduism", "Indian");
        addHistoricalNation("Satavahanas", 100, "Indian", "Empire", "Hinduism", "Indian");
        addHistoricalNation("Cholas", 200, "Indian", "Empire", "Hinduism", "Indian");
        addHistoricalNation("Pandyas", 150, "Indian", "Empire", "Hinduism", "Indian");
        addHistoricalNation("Cheras", 200, "Indian", "Empire", "Hinduism", "Indian");
        
        // East Asian Nations (spawn around 200-400 AD)
        addHistoricalNation("Goguryeo", 37, "Eastern", "Kingdom", "Korean Shamanism", "Eastern");
        addHistoricalNation("Baekje", 18, "Eastern", "Kingdom", "Korean Shamanism", "Eastern");
        addHistoricalNation("Silla", 57, "Eastern", "Kingdom", "Korean Shamanism", "Eastern");
        addHistoricalNation("Buyeo", 200, "Eastern", "Kingdom", "Korean Shamanism", "Eastern");
        addHistoricalNation("Yamato Japan", 250, "Eastern", "Kingdom", "Shinto", "Eastern");
        
        // Central Asian Nations (spawn around 200-400 AD)
        addHistoricalNation("Kushan Empire", 30, "Eastern", "Empire", "Buddhism", "Eastern");
        addHistoricalNation("Parthian Empire", 247, "Eastern", "Empire", "Zoroastrianism", "Eastern");
        addHistoricalNation("Sassanid Empire", 224, "Eastern", "Empire", "Zoroastrianism", "Eastern");
        
        // European Nations (spawn around 200-400 AD)
        addHistoricalNation("Visigoths", 300, "Germanic", "Kingdom", "Arianism", "Germanic");
        addHistoricalNation("Ostrogoths", 350, "Germanic", "Kingdom", "Arianism", "Germanic");
        addHistoricalNation("Vandals", 400, "Germanic", "Kingdom", "Arianism", "Germanic");
        addHistoricalNation("Burgundians", 400, "Germanic", "Kingdom", "Arianism", "Germanic");
    }
    
    private void addHistoricalNation(String name, int spawnYear, String cultureGroup, String governmentType, 
                                   String religion, String culture) {
        HistoricalNation nation = new HistoricalNation(name, spawnYear, cultureGroup, governmentType, religion, culture);
        historicalNations.put(name, nation);
    }
    
    public void update(GameDate currentDate) {
        int currentYear = currentDate.getYear();
        
        for (Map.Entry<String, HistoricalNation> entry : historicalNations.entrySet()) {
            String nationName = entry.getKey();
            HistoricalNation nation = entry.getValue();
            
            if (!spawnedNations.contains(nationName) && currentYear >= nation.getSpawnYear()) {
                spawnNation(nation);
                spawnedNations.add(nationName);
            }
        }
    }
    
    private void spawnNation(HistoricalNation nation) {
        // Find unowned provinces for the new nation
        List<String> availableProvinces = findAvailableProvinces(nation);
        
        if (!availableProvinces.isEmpty()) {
            // Create the new country
            Country newCountry = new Country(nation.getName());
            
            // Note: Country properties are set in constructor, we can't modify them after creation
            // The country will use default values based on the name
            
            // Assign starting provinces (1-3 provinces)
            int provinceCount = Math.min(availableProvinces.size(), 2 + (int)(Math.random() * 2));
            for (int i = 0; i < provinceCount; i++) {
                String provinceId = availableProvinces.get(i);
                var province = worldMap.getProvince(provinceId);
                if (province != null) {
                    province.setOwner(nation.getName());
                    newCountry.addProvince(province);
                }
            }
            
            // Add to countries map (since CountryManager doesn't have addCountry method)
            // This will be handled by the game engine when it processes the new country
            
            System.out.println("Historical nation spawned: " + nation.getName() + " in year " + nation.getSpawnYear());
        }
    }
    
    private List<String> findAvailableProvinces(HistoricalNation nation) {
        List<String> available = new ArrayList<>();
        
        // Define approximate regions for each culture group
        switch (nation.getCultureGroup()) {
            case "Germanic" -> {
                // Northern and Central Europe
                for (int i = 100; i < 200; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
            case "Celtic" -> {
                // British Isles and Gaul
                for (int i = 50; i < 100; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
            case "Eastern" -> {
                // Eastern Europe and Central Asia
                for (int i = 200; i < 300; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
            case "African" -> {
                // North Africa
                for (int i = 300; i < 400; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
            case "Arabian" -> {
                // Arabian Peninsula
                for (int i = 400; i < 500; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
            case "Indian" -> {
                // Indian Subcontinent
                for (int i = 500; i < 600; i++) {
                    var province = worldMap.getProvince(String.valueOf(i));
                    if (province != null && province.getOwner().equals("Uninhabited")) {
                        available.add(String.valueOf(i));
                    }
                }
            }
        }
        
        return available;
    }
    
    public List<String> getSpawnedNations() {
        return new ArrayList<>(spawnedNations);
    }
    
    public List<String> getUpcomingNations(GameDate currentDate) {
        List<String> upcoming = new ArrayList<>();
        int currentYear = currentDate.getYear();
        
        for (Map.Entry<String, HistoricalNation> entry : historicalNations.entrySet()) {
            String nationName = entry.getKey();
            HistoricalNation nation = entry.getValue();
            
            if (!spawnedNations.contains(nationName) && nation.getSpawnYear() > currentYear) {
                upcoming.add(nationName + " (Year " + nation.getSpawnYear() + ")");
            }
        }
        
        return upcoming;
    }
    
    private static class HistoricalNation {
        private String name;
        private int spawnYear;
        private String cultureGroup;
        private String governmentType;
        private String religion;
        private String culture;
        
        public HistoricalNation(String name, int spawnYear, String cultureGroup, String governmentType, 
                              String religion, String culture) {
            this.name = name;
            this.spawnYear = spawnYear;
            this.cultureGroup = cultureGroup;
            this.governmentType = governmentType;
            this.religion = religion;
            this.culture = culture;
        }
        
        // Getters
        public String getName() { return name; }
        public int getSpawnYear() { return spawnYear; }
        public String getCultureGroup() { return cultureGroup; }
        public String getGovernmentType() { return governmentType; }
        public String getReligion() { return religion; }
        public String getCulture() { return culture; }
    }
} 