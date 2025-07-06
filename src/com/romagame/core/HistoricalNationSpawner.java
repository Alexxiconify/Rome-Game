package com.romagame.core;

import com.romagame.map.Country;
import com.romagame.map.WorldMap;
import com.romagame.map.Country.NationType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class HistoricalNationSpawner {
    private WorldMap worldMap;
    private Map<String, NationSpawnInfo> historicalNations;
    private List<String> spawnedNations;
    private Random random;
    
    public HistoricalNationSpawner(WorldMap worldMap) {
        this.worldMap = worldMap;
        this.historicalNations = new HashMap<>();
        this.spawnedNations = new ArrayList<>();
        this.random = new Random();
        initializeHistoricalNations();
    }
    
    private void initializeHistoricalNations() {
        // Germanic Tribes and Confederations (based on Wikipedia timelines)
        addNation("Alemanni", 213, NationType.GERMANIC, "Germanic Confederation", "Pagan", "Germanic");
        addNation("Franks", 250, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Saxons", 260, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Goths", 238, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Vandals", 270, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Burgundians", 280, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Lombards", 290, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Angles", 300, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Jutes", 310, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Suebi", 320, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Thuringii", 330, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Rugii", 340, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Gepids", 350, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Heruli", 360, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Sciri", 370, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        addNation("Bastarnae", 380, NationType.GERMANIC, "Tribal Confederation", "Pagan", "Germanic");
        
        // Celtic Tribes
        addNation("Picts", 297, NationType.CELTIC, "Tribal Confederation", "Pagan", "Celtic");
        addNation("Caledonii", 310, NationType.CELTIC, "Tribal Confederation", "Pagan", "Celtic");
        addNation("Scoti", 320, NationType.CELTIC, "Tribal Confederation", "Pagan", "Celtic");
        addNation("Hibernians", 330, NationType.CELTIC, "Tribal Confederation", "Pagan", "Celtic");
        
        // Eastern European Tribes
        addNation("Sarmatians", 240, NationType.TRIBAL, "Tribal Confederation", "Pagan", "Sarmatian");
        addNation("Alans", 250, NationType.TRIBAL, "Tribal Confederation", "Pagan", "Sarmatian");
        addNation("Roxolani", 260, NationType.TRIBAL, "Tribal Confederation", "Pagan", "Sarmatian");
        addNation("Iazyges", 270, NationType.TRIBAL, "Tribal Confederation", "Pagan", "Sarmatian");
        
        // African Nations
        addNation("Garamantes", 220, NationType.AFRICAN, "Tribal Kingdom", "Pagan", "Berber");
        addNation("Blemmyes", 230, NationType.AFRICAN, "Tribal Kingdom", "Pagan", "Nubian");
        addNation("Nobatae", 240, NationType.AFRICAN, "Tribal Kingdom", "Pagan", "Nubian");
        addNation("Makuria", 250, NationType.AFRICAN, "Kingdom", "Pagan", "Nubian");
        addNation("Axum", 260, NationType.AFRICAN, "Kingdom", "Pagan", "Ethiopian");
        
        // Arabian Peninsula
        addNation("Lakhmids", 266, NationType.ARABIAN, "Client Kingdom", "Pagan", "Arabian");
        addNation("Ghassanids", 280, NationType.ARABIAN, "Client Kingdom", "Pagan", "Arabian");
        addNation("Kindah", 290, NationType.ARABIAN, "Tribal Confederation", "Pagan", "Arabian");
        addNation("Qedar", 300, NationType.ARABIAN, "Tribal Confederation", "Pagan", "Arabian");
        
        // Indian Subcontinent
        addNation("Gupta Empire", 320, NationType.INDIAN, "Empire", "Hindu", "Indian");
        addNation("Pallava", 275, NationType.INDIAN, "Kingdom", "Hindu", "Indian");
        addNation("Chalukya", 340, NationType.INDIAN, "Kingdom", "Hindu", "Indian");
        addNation("Kadamba", 345, NationType.INDIAN, "Kingdom", "Hindu", "Indian");
        addNation("Western Ganga", 350, NationType.INDIAN, "Kingdom", "Hindu", "Indian");
        
        // Central Asian
        addNation("Kushan Empire", 30, NationType.EASTERN, "Empire", "Buddhist", "Central Asian");
        addNation("Sassanid Empire", 224, NationType.EASTERN, "Empire", "Zoroastrian", "Persian");
        addNation("Hephthalites", 408, NationType.EASTERN, "Empire", "Pagan", "Central Asian");
        addNation("Kidarites", 320, NationType.EASTERN, "Kingdom", "Pagan", "Central Asian");
        
        // East Asian
        addNation("Cao Wei", 220, NationType.EASTERN, "Empire", "Confucian", "Chinese");
        addNation("Shu Han", 221, NationType.EASTERN, "Empire", "Confucian", "Chinese");
        addNation("Eastern Wu", 229, NationType.EASTERN, "Empire", "Confucian", "Chinese");
        addNation("Jin Dynasty", 266, NationType.EASTERN, "Empire", "Confucian", "Chinese");
        addNation("Sixteen Kingdoms", 304, NationType.EASTERN, "Confederation", "Mixed", "Chinese");
        
        // Later Germanic Migrations
        addNation("Ostrogoths", 375, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Visigoths", 376, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Ostrogothic Kingdom", 493, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Visigothic Kingdom", 418, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Vandal Kingdom", 435, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Burgundian Kingdom", 443, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Lombard Kingdom", 568, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Frankish Kingdom", 481, NationType.GERMANIC, "Kingdom", "Pagan", "Germanic");
        addNation("Anglo-Saxon Kingdoms", 450, NationType.GERMANIC, "Confederation", "Pagan", "Germanic");
    }
    
    private void addNation(String name, int spawnYear, NationType type, String government, String religion, String culture) {
        historicalNations.put(name, new NationSpawnInfo(name, spawnYear, type, government, religion, culture));
    }
    
    public void update(GameDate currentDate) {
        int currentYear = currentDate.getYear();
        
        for (Map.Entry<String, NationSpawnInfo> entry : historicalNations.entrySet()) {
            String nationName = entry.getKey();
            NationSpawnInfo spawnInfo = entry.getValue();
            
            if (currentYear >= spawnInfo.spawnYear && !spawnedNations.contains(nationName)) {
                spawnNation(spawnInfo);
                spawnedNations.add(nationName);
            }
        }
    }
    
    private void spawnNation(NationSpawnInfo spawnInfo) {
        // Create the new nation
        Country newCountry = new Country(spawnInfo.name);
        
        // Set historical attributes
        newCountry.setGovernmentType(spawnInfo.government);
        newCountry.setReligion(spawnInfo.religion);
        newCountry.setCulture(spawnInfo.culture);
        
        // Assign starting provinces based on historical locations
        assignHistoricalProvinces(newCountry, spawnInfo);
        
        // Add to world map
        worldMap.addCountry(newCountry);
        
        System.out.println("Historical nation spawned: " + spawnInfo.name + " in " + spawnInfo.spawnYear);
    }
    
    private void assignHistoricalProvinces(Country country, NationSpawnInfo spawnInfo) {
        // This is a simplified assignment - in a full implementation,
        // you would have detailed province mappings for each historical nation
        List<String> provinces = getHistoricalProvinces(spawnInfo);
        
        for (String provinceId : provinces) {
            var province = worldMap.getProvince(provinceId);
            if (province != null && province.getOwner().equals("Uninhabited")) {
                province.setOwner(country.getName());
                country.addProvince(province);
            }
        }
    }
    
    private List<String> getHistoricalProvinces(NationSpawnInfo spawnInfo) {
        List<String> provinces = new ArrayList<>();
        
        // Simplified province assignment based on nation type and name
        switch (spawnInfo.type) {
            case GERMANIC -> {
                // Germanic tribes get provinces in Germany, Scandinavia, etc.
                if (spawnInfo.name.contains("Goth")) {
                    provinces.addAll(List.of("germany_1", "germany_2", "germany_3"));
                } else if (spawnInfo.name.contains("Frank")) {
                    provinces.addAll(List.of("germany_4", "germany_5", "germany_6"));
                } else if (spawnInfo.name.contains("Saxon")) {
                    provinces.addAll(List.of("germany_7", "germany_8", "germany_9"));
                } else {
                    // Generic Germanic provinces
                    provinces.addAll(List.of("germany_" + random.nextInt(20)));
                }
            }
            case CELTIC -> {
                // Celtic tribes get provinces in Britain, Ireland, etc.
                if (spawnInfo.name.contains("Pict")) {
                    provinces.addAll(List.of("scotland_1", "scotland_2"));
                } else if (spawnInfo.name.contains("Scot")) {
                    provinces.addAll(List.of("scotland_3", "scotland_4"));
                } else {
                    provinces.addAll(List.of("britain_" + random.nextInt(10)));
                }
            }
            case TRIBAL -> {
                // Sarmatian tribes get provinces in Eastern Europe
                provinces.addAll(List.of("eastern_europe_" + random.nextInt(15)));
            }
            case AFRICAN -> {
                // African nations get provinces in North Africa
                provinces.addAll(List.of("africa_" + random.nextInt(20)));
            }
            case ARABIAN -> {
                // Arabian nations get provinces in Arabia
                provinces.addAll(List.of("arabia_" + random.nextInt(15)));
            }
            case INDIAN -> {
                // Indian nations get provinces in India
                provinces.addAll(List.of("india_" + random.nextInt(25)));
            }
            case EASTERN -> {
                // Eastern nations get provinces in Central Asia, China, etc.
                if (spawnInfo.name.contains("Chinese") || spawnInfo.name.contains("Han")) {
                    provinces.addAll(List.of("china_" + random.nextInt(30)));
                } else {
                    provinces.addAll(List.of("central_asia_" + random.nextInt(20)));
                }
            }
            case GREEK -> {
                // Assign Greek provinces (example)
                provinces.addAll(List.of("greece_" + random.nextInt(10)));
            }
            case ROMAN -> {
                // Assign Roman provinces (example)
                provinces.addAll(List.of("italy_" + random.nextInt(10)));
            }
        }
        
        return provinces;
    }
    
    public List<String> getSpawnedNations() {
        return new ArrayList<>(spawnedNations);
    }
    
    public Map<String, NationSpawnInfo> getHistoricalNations() {
        return new HashMap<>(historicalNations);
    }
    
    private static class NationSpawnInfo {
        String name;
        int spawnYear;
        NationType type;
        String government;
        String religion;
        String culture;
        
        NationSpawnInfo(String name, int spawnYear, NationType type, String government, String religion, String culture) {
            this.name = name;
            this.spawnYear = spawnYear;
            this.type = type;
            this.government = government;
            this.religion = religion;
            this.culture = culture;
        }
    }
} 