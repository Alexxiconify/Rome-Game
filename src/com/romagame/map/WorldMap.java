package com.romagame.map;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class WorldMap {
    private Map<String, Province> provinces;
    private Map<String, Country> countries;
    private List<SeaZone> seaZones;

    public WorldMap() {
        provinces = new HashMap<>();
        countries = new HashMap<>();
        seaZones = new ArrayList<>();
        initializeProvinces();
    }

    private void initializeProvinces() {
        // Try to load provinces from JSON first, fallback to hardcoded if needed
        if (!loadProvincesFromJson()) {
            System.out.println("Falling back to hardcoded province data...");
            ProvinceCreationSnippets.createAllProvinces(this);
        }
        initializeSeaZones();
    }
    
    private boolean loadProvincesFromJson() {
        try {
            java.nio.file.Path jsonPath = java.nio.file.Paths.get("src/resources/data/nations_and_provinces.json");
            if (!java.nio.file.Files.exists(jsonPath)) {
                System.out.println("JSON file not found: " + jsonPath);
                return false;
            }
            
            String jsonText = new String(java.nio.file.Files.readAllBytes(jsonPath));
            
            // Parse provinces from JSON
            String provincesBlock = jsonText.split("\"provinces\"\\s*:\\s*\\[", 2)[1].split("]")[0];
            String[] provinceEntries = provincesBlock.split("\\},\\s*\\{");
            
            int loadedCount = 0;
            for (String entry : provinceEntries) {
                try {
                    // Extract province_id
                    String provinceId = entry.split("\"province_id\"\\s*:\\s*\"")[1].split("\"")[0];
                    
                    // Extract owner (try both "owner" and "owner_name" fields)
                    String owner;
                    if (entry.contains("\"owner_name\"")) {
                        owner = entry.split("\"owner_name\"\\s*:\\s*\"")[1].split("\"")[0];
                    } else {
                        owner = entry.split("\"owner\"\\s*:\\s*\"")[1].split("\"")[0];
                    }
                    
                    // Extract color (try both "mask_color" and "owner_color" fields)
                    String colorStr;
                    if (entry.contains("\"mask_color\"")) {
                        colorStr = entry.split("\"mask_color\"\\s*:\\s*\\[")[1].split("]")[0].replaceAll("\\s", "");
                    } else {
                        colorStr = entry.split("\"owner_color\"\\s*:\\s*\\[")[1].split("]")[0].replaceAll("\\s", "");
                    }
                    
                    String[] rgb = colorStr.split(",");
                    int r = Integer.parseInt(rgb[0]);
                    int g = Integer.parseInt(rgb[1]);
                    int b = Integer.parseInt(rgb[2]);
                    
                    // Create province with actual coordinates if available
                    double lat = 0.0, lon = 0.0;
                    if (entry.contains("\"centroid_x\"") && entry.contains("\"centroid_y\"")) {
                        String centroidXStr = entry.split("\"centroid_x\"\\s*:\\s*")[1].split(",")[0];
                        String centroidYStr = entry.split("\"centroid_y\"\\s*:\\s*")[1].split(",")[0];
                        int centroidX = Integer.parseInt(centroidXStr);
                        int centroidY = Integer.parseInt(centroidYStr);
                        
                        // Convert pixel coordinates to lat/lon (approximate)
                        // This is a rough conversion - you might want to refine this
                        lat = (centroidY - 1000) / 1000.0 * 90.0; // Rough conversion
                        lon = (centroidX - 2000) / 2000.0 * 180.0; // Rough conversion
                    }
                    
                    createProvince(provinceId, owner, r, g, b);
                    loadedCount++;
                    
                } catch (Exception e) {
                    // Skip malformed province entries
                    System.err.println("Skipping malformed province entry: " + e.getMessage());
                }
            }
            
            System.out.println("Loaded " + loadedCount + " provinces from JSON");
            return loadedCount > 0;
            
        } catch (Exception e) {
            System.err.println("Failed to load provinces from JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void initializeSeaZones() {
        // Major sea zones based on Q-BAM map
        seaZones.add(new SeaZone("Mediterranean Sea", "Mediterranean"));
        seaZones.add(new SeaZone("Adriatic Sea", "Mediterranean"));
        seaZones.add(new SeaZone("Aegean Sea", "Mediterranean"));
        seaZones.add(new SeaZone("Black Sea", "Mediterranean"));
        seaZones.add(new SeaZone("Red Sea", "Indian Ocean"));
        seaZones.add(new SeaZone("Persian Gulf", "Indian Ocean"));
        seaZones.add(new SeaZone("Caspian Sea", "Central Asia"));
        seaZones.add(new SeaZone("North Sea", "Atlantic"));
        seaZones.add(new SeaZone("English Channel", "Atlantic"));
        seaZones.add(new SeaZone("Baltic Sea", "Atlantic"));
        seaZones.add(new SeaZone("Norwegian Sea", "Atlantic"));
        seaZones.add(new SeaZone("Barents Sea", "Arctic"));
        seaZones.add(new SeaZone("Arctic Ocean", "Arctic"));
        seaZones.add(new SeaZone("Atlantic Ocean", "Atlantic"));
        seaZones.add(new SeaZone("Indian Ocean", "Indian Ocean"));
        seaZones.add(new SeaZone("Pacific Ocean", "Pacific"));
        seaZones.add(new SeaZone("Caribbean Sea", "Atlantic"));
        seaZones.add(new SeaZone("Gulf of Mexico", "Atlantic"));
        seaZones.add(new SeaZone("Hudson Bay", "Arctic"));
        seaZones.add(new SeaZone("Labrador Sea", "Atlantic"));
        seaZones.add(new SeaZone("Greenland Sea", "Arctic"));
        seaZones.add(new SeaZone("Bering Sea", "Pacific"));
        seaZones.add(new SeaZone("Sea of Okhotsk", "Pacific"));
        seaZones.add(new SeaZone("Sea of Japan", "Pacific"));
        seaZones.add(new SeaZone("Yellow Sea", "Pacific"));
        seaZones.add(new SeaZone("East China Sea", "Pacific"));
        seaZones.add(new SeaZone("South China Sea", "Pacific"));
        seaZones.add(new SeaZone("Philippine Sea", "Pacific"));
        seaZones.add(new SeaZone("Coral Sea", "Pacific"));
        seaZones.add(new SeaZone("Tasman Sea", "Pacific"));
        seaZones.add(new SeaZone("Arabian Sea", "Indian Ocean"));
        seaZones.add(new SeaZone("Bay of Bengal", "Indian Ocean"));
        seaZones.add(new SeaZone("Andaman Sea", "Indian Ocean"));
        seaZones.add(new SeaZone("Java Sea", "Pacific"));
        seaZones.add(new SeaZone("Celebes Sea", "Pacific"));
        seaZones.add(new SeaZone("Banda Sea", "Pacific"));
        seaZones.add(new SeaZone("Arafura Sea", "Pacific"));
        seaZones.add(new SeaZone("Timor Sea", "Indian Ocean"));
    }

    public void createProvince(String id, String owner, int r, int g, int b) {
        // Use dummy coordinates and type since we're creating from color data
        Province province = new Province(id, owner, 0.0, 0.0, "Auto");
        provinces.put(id, province);

        // Create country if it doesn't exist (skip unknown/color provinces)
        if (!owner.startsWith("Unknown") && !owner.startsWith("Color_") && !owner.startsWith("rgb_")) {
            if (!countries.containsKey(owner)) {
                Country country = new Country(owner);
                countries.put(owner, country);
                System.out.println("Created country: " + owner);
            }

            // Add province to its country
            Country country = countries.get(owner);
            country.addProvince(province);
        }
    }

    public Province getProvince(String id) {
        return provinces.get(id);
    }

    public List<Province> getAllProvinces() {
        return new ArrayList<>(provinces.values());
    }

    public Country getCountry(String name) {
        return countries.get(name);
    }

    public List<Country> getAllCountries() {
        return new ArrayList<>(countries.values());
    }

    public void addCountry(Country country) {
        if (!countries.containsKey(country.getName())) {
            countries.put(country.getName(), country);
        }
    }
}