package com.romagame.map;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldMap {
    private Map<String, Province> provinces;
    private Map<String, Country> countries;

    public WorldMap() {
        provinces = new HashMap<>();
        countries = new HashMap<>();
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

    private boolean loadProvincesFromJson() {
        java.nio.file.Path jsonPath = java.nio.file.Paths.get("src/resources/data/nations_and_provinces.json");
        if (!java.nio.file.Files.exists(jsonPath)) {
            System.out.println("JSON file not found: " + jsonPath);
            return false;
        }
        String jsonText;
        try {
            jsonText = new String(java.nio.file.Files.readAllBytes(jsonPath));
        } catch (Exception e) {
            System.out.println("Failed to read JSON file: " + e.getMessage());
            return false;
        }
        // ... parse provinces and nations ...
        // After loading:
        System.out.println("[DEBUG] Loaded " + provinces.size() + " provinces, " + countries.size() + " countries from JSON");
        return true;
    }
}