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
        ProvinceCreationSnippets.createAllProvinces(this);
        initializeSeaZones();
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

    private void createProvince(String id, String owner, int r, int g, int b) {
        // Use dummy coordinates and type since we're creating from color data
        Province province = new Province(id, owner, 0.0, 0.0, "Auto");
        provinces.put(id, province);

        // Create country if it doesn't exist
        if (!countries.containsKey(owner)) {
            Country country = new Country(owner);
            countries.put(owner, country);
            System.out.println("Created country: " + owner);
        }

        // Add province to its country
        Country country = countries.get(owner);
        country.addProvince(province);
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
        System.out.println("getAllCountries called, countries size: " + countries.size());
        return new ArrayList<>(countries.values());
    }
}