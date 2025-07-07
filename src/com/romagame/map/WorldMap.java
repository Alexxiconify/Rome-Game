package com.romagame.map;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class WorldMap {
    private Map<String, Province> provinces;
    private Map<String, Country> countries;

    public WorldMap() {
        provinces = new HashMap<>();
        countries = new HashMap<>();
        initializeProvinces();
    }

    private void initializeProvinces() {
        // Try to load provinces from JSON first, fallback to hardcoded if needed
        if (!loadProvincesFromJson()) {
            System.out.println("Falling back to hardcoded province data...");
            ProvinceCreationSnippets.createAllProvinces(this);
        }
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
                        
                        // Comment out or remove per-province debug
                        // System.out.println("DEBUG: Province[" + i + "] id=" + provinceId + " owner=" + owner +   " color=[" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "]" + (pixelCount != null ? " px=" + pixelCount : "") + (centroidX != null ? " centroid=" + centroidX + "," + centroidY : ""));
                    }
                    
                    createProvince(provinceId, owner, rgb[0], rgb[1], rgb[2]);
                    loadedCount++;
                } catch (Exception e) {
                    System.err.println("DEBUG: Exception parsing entry [" + i + "]: " + e.getMessage());
                }
            }
            System.out.println("[DEBUG] Loaded " + loadedCount + " provinces for " + countries.size() + " owners from JSON");
            // Print all created countries in a single line
            if (!countries.isEmpty()) {
                System.out.println("Created countries: " + String.join(", ", countries.keySet()));
            }
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
    

    public void createProvince(String id, String owner, int r, int g, int b) {
        // Use dummy coordinates and type since we're creating from color data
        Province province = new Province(id, owner, 0.0, 0.0, "Auto");
        provinces.put(id, province);

        // Create country if it doesn't exist (skip unknown/color provinces)
        if (!owner.startsWith("Unknown") && !owner.startsWith("Color_") && !owner.startsWith("rgb_")) {
            if (!countries.containsKey(owner)) {
                Country country = new Country(owner);
                countries.put(owner, country);
                // Don't print individual country creation - will be collected and printed later
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