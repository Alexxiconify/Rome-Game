package com.romagame.map;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

// Add json-20231013.jar to your classpath for org.json support:
// Example: javac -cp .;src/resources/json-20231013.jar ...

public class WorldMap {
    private Map<String, Province> provinces;
    private Map<String, Country> countries;

    private static class NationData {
        String name;
        int[] color;
        int[] startingViewpoint;
        NationData(String name, int[] color, int[] startingViewpoint) {
            this.name = name;
            this.color = color;
            this.startingViewpoint = startingViewpoint;
        }
    }

    private static class ProvinceData {
        String provinceId;
        int[] ownerColor;
        String owner;
        int pixelCount;
        int centroidX, centroidY;
        int regionId;
        ProvinceData(String provinceId, int[] ownerColor, String owner, int pixelCount, int centroidX, int centroidY, int regionId) {
            this.provinceId = provinceId;
            this.ownerColor = ownerColor;
            this.owner = owner;
            this.pixelCount = pixelCount;
            this.centroidX = centroidX;
            this.centroidY = centroidY;
            this.regionId = regionId;
        }
    }

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
        String path = "src/resources/data/nations_and_provinces.json";
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject root = new JSONObject(jsonText);
            // Parse nations
            Map<String, NationData> nationDataMap = new HashMap<>();
            JSONArray nations = root.optJSONArray("nations");
            if (nations != null) {
                for (int i = 0; i < nations.length(); i++) {
                    JSONObject nation = nations.getJSONObject(i);
                    String name = nation.getString("name");
                    JSONArray colorArr = nation.getJSONArray("color");
                    int[] color = { colorArr.getInt(0), colorArr.getInt(1), colorArr.getInt(2) };
                    int[] viewpoint = null;
                    if (nation.has("starting_viewpoint")) {
                        JSONArray vpArr = nation.getJSONArray("starting_viewpoint");
                        viewpoint = new int[] { vpArr.getInt(0), vpArr.getInt(1) };
                    }
                    nationDataMap.put(name, new NationData(name, color, viewpoint));
                }
            }
            // Parse provinces
            JSONArray provincesArr = root.getJSONArray("provinces");
            for (int i = 0; i < provincesArr.length(); i++) {
                JSONObject province = provincesArr.getJSONObject(i);
                String provinceId = province.getString("province_id");
                JSONArray colorArr = province.getJSONArray("owner_color");
                int[] ownerColor = { colorArr.getInt(0), colorArr.getInt(1), colorArr.getInt(2) };
                String owner = province.getString("owner");
                int pixelCount = province.optInt("pixel_count", 0);
                int centroidX = province.optInt("centroid_x", 0);
                int centroidY = province.optInt("centroid_y", 0);
                int regionId = province.optInt("region_id", 0);
                ProvinceData pdata = new ProvinceData(provinceId, ownerColor, owner, pixelCount, centroidX, centroidY, regionId);
                // Create Province and Country objects
                Province prov = new Province(provinceId, owner, centroidX, centroidY, "Auto");
                provinces.put(provinceId, prov);
                if (!countries.containsKey(owner)) {
                    Country country = new Country(owner);
                    countries.put(owner, country);
                }
                countries.get(owner).addProvince(prov);
            }
            System.out.println("[DEBUG] Loaded " + provinces.size() + " provinces, " + countries.size() + " countries from JSON");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to parse nations_and_provinces.json: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}