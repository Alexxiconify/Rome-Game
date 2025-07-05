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
        // Initialize major regions based on Q-BAM map
        initializeEurope();
        initializeAsia();
        initializeAfrica();
        initializeAmericas();
        initializeOceania();
    }
    
    private void initializeEurope() {
        // Major European countries and provinces
        createProvince("PARIS", "France", 48.8566, 2.3522, "Capital");
        createProvince("LONDON", "England", 51.5074, -0.1278, "Capital");
        createProvince("BERLIN", "Brandenburg", 52.5200, 13.4050, "Capital");
        createProvince("ROME", "Papal States", 41.9028, 12.4964, "Capital");
        createProvince("MADRID", "Castile", 40.4168, -3.7038, "Capital");
        createProvince("MOSCOW", "Muscovy", 55.7558, 37.6176, "Capital");
        createProvince("VIENNA", "Austria", 48.2082, 16.3738, "Capital");
        createProvince("STOCKHOLM", "Sweden", 59.3293, 18.0686, "Capital");
        createProvince("COPENHAGEN", "Denmark", 55.6761, 12.5683, "Capital");
        createProvince("LISBON", "Portugal", 38.7223, -9.1393, "Capital");
    }
    
    private void initializeAsia() {
        // Major Asian countries and provinces
        createProvince("BEIJING", "Ming", 39.9042, 116.4074, "Capital");
        createProvince("TOKYO", "Japan", 35.6762, 139.6503, "Capital");
        createProvince("DELHI", "Delhi", 28.7041, 77.1025, "Capital");
        createProvince("ISTANBUL", "Ottomans", 41.0082, 28.9784, "Capital");
        createProvince("TEHRAN", "Persia", 35.6892, 51.3890, "Capital");
        createProvince("BANGKOK", "Ayutthaya", 13.7563, 100.5018, "Capital");
    }
    
    private void initializeAfrica() {
        // Major African regions
        createProvince("CAIRO", "Mamluks", 30.0444, 31.2357, "Capital");
        createProvince("TUNIS", "Tunisia", 36.8065, 10.1815, "Capital");
        createProvince("FEZ", "Morocco", 34.0181, -5.0078, "Capital");
        createProvince("TIMBUKTU", "Mali", 16.7666, -3.0026, "Capital");
    }
    
    private void initializeAmericas() {
        // Native American and colonial regions
        createProvince("TENOCHTITLAN", "Aztec", 19.4326, -99.1332, "Capital");
        createProvince("CUZCO", "Inca", -13.5167, -71.9789, "Capital");
        createProvince("CHEROKEE", "Cherokee", 35.7596, -79.0193, "Tribal");
        createProvince("IROQUOIS", "Iroquois", 43.0962, -79.0377, "Tribal");
    }
    
    private void initializeOceania() {
        // Pacific and Australian regions
        createProvince("SYDNEY", "Australia", -33.8688, 151.2093, "Colonial");
        createProvince("AUCKLAND", "New Zealand", -36.8485, 174.7633, "Colonial");
    }
    
    private void createProvince(String id, String owner, double lat, double lon, String type) {
        Province province = new Province(id, owner, lat, lon, type);
        provinces.put(id, province);
        
        if (!countries.containsKey(owner)) {
            countries.put(owner, new Country(owner));
        }
        countries.get(owner).addProvince(province);
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
} 