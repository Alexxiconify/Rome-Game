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
            
            // Parse provinces from JSON with more robust parsing
            int provincesStart = jsonText.indexOf("\"provinces\"");
            if (provincesStart == -1) {
                System.out.println("Could not find provinces section in JSON");
                return false;
            }
            
            int arrayStart = jsonText.indexOf("[", provincesStart);
            if (arrayStart == -1) {
                System.out.println("Could not find provinces array in JSON");
                return false;
            }
            
            int arrayEnd = findMatchingBracket(jsonText, arrayStart);
            if (arrayEnd == -1) {
                System.out.println("Could not find end of provinces array in JSON");
                return false;
            }
            
            String provincesBlock = jsonText.substring(arrayStart + 1, arrayEnd);
            
            // Split by province objects more carefully
            String[] provinceEntries = provincesBlock.split("\\},\\s*\\{");
            
            int loadedCount = 0;
            for (int i = 0; i < provinceEntries.length; i++) {
                String entry = provinceEntries[i];
                
                // Clean up the entry
                if (i == 0) {
                    entry = entry.replaceFirst("^\\s*\\{\\s*", "");
                }
                if (i == provinceEntries.length - 1) {
                    entry = entry.replaceFirst("\\s*\\}\\s*$", "");
                }
                
                try {
                    // Extract province_id
                    String provinceId = extractJsonValue(entry, "province_id");
                    if (provinceId == null) {
                        System.out.println("DEBUG: Skipping entry [" + i + "]: missing province_id");
                        continue;
                    }
                    // Extract owner
                    String owner = extractJsonValue(entry, "owner");
                    if (owner == null) {
                        System.out.println("DEBUG: Skipping entry [" + i + "]: missing owner");
                        continue;
                    }
                    // Extract color array
                    int[] rgb = extractColorArray(entry, "owner_color");
                    if (rgb == null) {
                        System.out.println("DEBUG: Skipping entry [" + i + "]: missing owner_color");
                        continue;
                    }
                    
                    // Single-line debug output for first 5 entries
                    if (i < 5) {
                        String pixelCount = extractJsonValue(entry, "pixel_count");
                        String centroidX = extractJsonValue(entry, "centroid_x");
                        String centroidY = extractJsonValue(entry, "centroid_y");
                        
                        System.out.println("DEBUG: Province[" + i + "] id=" + provinceId + " owner=" + owner + 
                                         " color=[" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "]" +
                                         (pixelCount != null ? " px=" + pixelCount : "") +
                                         (centroidX != null ? " cx=" + centroidX : "") +
                                         (centroidY != null ? " cy=" + centroidY : ""));
                    }
                    
                    createProvince(provinceId, owner, rgb[0], rgb[1], rgb[2]);
                    loadedCount++;
                } catch (Exception e) {
                    System.err.println("DEBUG: Exception parsing entry [" + i + "]: " + e.getMessage());
                }
            }
            System.out.println("Loaded " + loadedCount + " provinces from JSON");
            // Print loaded countries for verification
            System.out.println("Countries loaded from JSON: " + countries.keySet());
            return loadedCount > 0;
        } catch (Exception e) {
            System.err.println("Failed to load provinces from JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private int findMatchingBracket(String text, int start) {
        int count = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[') count++;
            else if (c == ']') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private int[] extractColorArray(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return new int[]{
                Integer.parseInt(m.group(1)),
                Integer.parseInt(m.group(2)),
                Integer.parseInt(m.group(3))
            };
        }
        return null;
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