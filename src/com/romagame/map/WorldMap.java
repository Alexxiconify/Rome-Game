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
        // Initialize major regions based on 117 AD map
        initializeRomanEmpire();
        initializeParthia();
        initializeOtherStates();
        initializeSeaZones();
    }
    
    private void initializeRomanEmpire() {
        // Roman Empire provinces in 117 AD
        createProvince("ROME", "Roman Empire", 41.9028, 12.4964, "Capital");
        createProvince("CONSTANTINOPLE", "Roman Empire", 41.0082, 28.9784, "Capital");
        createProvince("ALEXANDRIA", "Roman Empire", 31.2001, 29.9187, "Capital");
        createProvince("CARTHAGE", "Roman Empire", 36.8065, 10.1815, "Capital");
        createProvince("ATHENS", "Roman Empire", 37.9838, 23.7275, "Capital");
        createProvince("ANTIOCH", "Roman Empire", 36.2021, 36.1613, "Capital");
        createProvince("EPHESUS", "Roman Empire", 37.9397, 27.3414, "Capital");
        createProvince("CORINTH", "Roman Empire", 37.9407, 22.9569, "Capital");
        createProvince("THESSALONICA", "Roman Empire", 40.6401, 22.9444, "Capital");
        createProvince("SALONA", "Roman Empire", 43.5081, 16.4402, "Capital");
        createProvince("AQUILEIA", "Roman Empire", 45.7689, 13.3711, "Capital");
        createProvince("MEDIOLANUM", "Roman Empire", 45.4642, 9.1900, "Capital");
        createProvince("LUGDUNUM", "Roman Empire", 45.7578, 4.8320, "Capital");
        createProvince("LONDINIUM", "Roman Empire", 51.5074, -0.1278, "Capital");
        createProvince("LUTETIA", "Roman Empire", 48.8566, 2.3522, "Capital");
        createProvince("COLONIA", "Roman Empire", 50.9375, 6.9603, "Capital");
        createProvince("TRIER", "Roman Empire", 49.7557, 6.6394, "Capital");
        createProvince("VIENNA", "Roman Empire", 48.2082, 16.3738, "Capital");
        createProvince("SIRMIUM", "Roman Empire", 44.9818, 19.6126, "Capital");
        createProvince("SINGIDUNUM", "Roman Empire", 44.8125, 20.4612, "Capital");
        createProvince("DUROSTORUM", "Roman Empire", 44.1167, 27.2667, "Capital");
        createProvince("TOMIS", "Roman Empire", 44.1733, 28.6383, "Capital");
        createProvince("APAMEIA", "Roman Empire", 35.4183, 36.3847, "Capital");
        createProvince("DAMASCUS", "Roman Empire", 33.5138, 36.2765, "Capital");
        createProvince("TYRE", "Roman Empire", 33.2700, 35.2033, "Capital");
        createProvince("SIDON", "Roman Empire", 33.5600, 35.3758, "Capital");
        createProvince("CAESAREA", "Roman Empire", 32.5019, 34.9056, "Capital");
        createProvince("JERUSALEM", "Roman Empire", 31.7683, 35.2137, "Capital");
        createProvince("PETRA", "Roman Empire", 30.3285, 35.4444, "Capital");
        createProvince("BOSTRA", "Roman Empire", 32.5183, 36.4833, "Capital");
        createProvince("PALMYRA", "Roman Empire", 34.5500, 38.2833, "Capital");
        createProvince("EDESSA", "Roman Empire", 37.1583, 38.7917, "Capital");
        createProvince("NISIBIS", "Roman Empire", 37.0667, 41.2167, "Capital");
        createProvince("SINGARA", "Roman Empire", 36.3333, 41.8500, "Capital");
        createProvince("CIRCESIUM", "Roman Empire", 35.1667, 40.4333, "Capital");
        createProvince("DURA", "Roman Empire", 34.7500, 40.7333, "Capital");
        createProvince("ZEUGMA", "Roman Empire", 37.0667, 37.8667, "Capital");
        createProvince("MELITENE", "Roman Empire", 38.3667, 38.3500, "Capital");
        createProvince("SEBASTE", "Roman Empire", 39.7500, 37.0167, "Capital");
        createProvince("AMASEIA", "Roman Empire", 40.6500, 35.8333, "Capital");
        createProvince("SINOPE", "Roman Empire", 42.0167, 35.1500, "Capital");
        createProvince("TRAPEZUS", "Roman Empire", 41.0000, 39.7167, "Capital");
        createProvince("NICAEA", "Roman Empire", 40.4333, 29.7167, "Capital");
        createProvince("PERGAMUM", "Roman Empire", 39.1167, 27.1833, "Capital");
        createProvince("SMYRNA", "Roman Empire", 38.4167, 27.1500, "Capital");
        createProvince("MILETUS", "Roman Empire", 37.5333, 27.2833, "Capital");
        createProvince("HALICARNASSUS", "Roman Empire", 37.0333, 27.4333, "Capital");
        createProvince("RHODES", "Roman Empire", 36.4500, 28.2333, "Capital");
        createProvince("CYPRUS", "Roman Empire", 35.1667, 33.3667, "Capital");
        createProvince("CRETE", "Roman Empire", 35.3333, 25.1333, "Capital");
        createProvince("SICILY", "Roman Empire", 37.5000, 14.0000, "Capital");
        createProvince("SARDINIA", "Roman Empire", 40.0000, 9.0000, "Capital");
        createProvince("CORSICA", "Roman Empire", 42.0000, 9.0000, "Capital");
        createProvince("BALEARIC", "Roman Empire", 39.5000, 3.0000, "Capital");
        createProvince("HISPANIA", "Roman Empire", 40.4168, -3.7038, "Capital");
        createProvince("GALLIA", "Roman Empire", 46.2276, 2.2137, "Capital");
        createProvince("GERMANIA", "Roman Empire", 51.1657, 10.4515, "Capital");
        createProvince("RAETIA", "Roman Empire", 47.3769, 8.5417, "Capital");
        createProvince("NORICUM", "Roman Empire", 47.0667, 15.4500, "Capital");
        createProvince("PANNONIA", "Roman Empire", 46.0667, 18.2333, "Capital");
        createProvince("DALMATIA", "Roman Empire", 43.5081, 16.4402, "Capital");
        createProvince("MOESIA", "Roman Empire", 44.8125, 20.4612, "Capital");
        createProvince("THRACE", "Roman Empire", 42.8833, 25.9000, "Capital");
        createProvince("MACEDONIA", "Roman Empire", 41.0000, 21.4333, "Capital");
        createProvince("ACHAEA", "Roman Empire", 37.9838, 23.7275, "Capital");
        createProvince("ASIA", "Roman Empire", 39.1167, 27.1833, "Capital");
        createProvince("BITHYNIA", "Roman Empire", 40.4333, 29.7167, "Capital");
        createProvince("PONTUS", "Roman Empire", 41.0000, 39.7167, "Capital");
        createProvince("CAPPADOCIA", "Roman Empire", 38.3667, 38.3500, "Capital");
        createProvince("GALATIA", "Roman Empire", 39.7500, 37.0167, "Capital");
        createProvince("LYCIA", "Roman Empire", 36.2333, 29.9833, "Capital");
        createProvince("PAMPHYLIA", "Roman Empire", 36.8833, 30.7000, "Capital");
        createProvince("CILICIA", "Roman Empire", 36.8167, 34.6333, "Capital");
        createProvince("SYRIA", "Roman Empire", 36.2021, 36.1613, "Capital");
        createProvince("JUDAEA", "Roman Empire", 31.7683, 35.2137, "Capital");
        createProvince("ARABIA", "Roman Empire", 30.3285, 35.4444, "Capital");
        createProvince("MESOPOTAMIA", "Roman Empire", 37.0667, 41.2167, "Capital");
        createProvince("OSRHOENE", "Roman Empire", 37.1583, 38.7917, "Capital");
        createProvince("EGYPT", "Roman Empire", 31.2001, 29.9187, "Capital");
        createProvince("CYRENAICA", "Roman Empire", 32.1167, 20.0833, "Capital");
        createProvince("AFRICA", "Roman Empire", 36.8065, 10.1815, "Capital");
        createProvince("MAURETANIA", "Roman Empire", 34.0181, -5.0078, "Capital");
        createProvince("BRITANNIA", "Roman Empire", 51.5074, -0.1278, "Capital");
        createProvince("BELGICA", "Roman Empire", 50.8503, 4.3517, "Capital");
        createProvince("LUGDUNENSIS", "Roman Empire", 45.7578, 4.8320, "Capital");
        createProvince("AQUITANIA", "Roman Empire", 44.8378, -0.5792, "Capital");
        createProvince("NARBONENSIS", "Roman Empire", 43.2965, 5.3698, "Capital");
        createProvince("TARRACONENSIS", "Roman Empire", 41.3851, 2.1734, "Capital");
        createProvince("LUSITANIA", "Roman Empire", 38.7223, -9.1393, "Capital");
        createProvince("BAETICA", "Roman Empire", 37.3891, -5.9845, "Capital");
    }
    
    private void initializeParthia() {
        // Parthian Empire provinces
        createProvince("CTESIPHON", "Parthia", 33.0936, 44.5808, "Capital");
        createProvince("ECBATANA", "Parthia", 34.7983, 48.5148, "Capital");
        createProvince("SUSA", "Parthia", 32.1892, 48.2536, "Capital");
        createProvince("PERSEPOLIS", "Parthia", 29.9358, 52.8916, "Capital");
        createProvince("PASARGADAE", "Parthia", 30.1939, 53.1678, "Capital");
        createProvince("HEPHATALITES", "Parthia", 36.7000, 67.1167, "Capital");
        createProvince("BACTRIA", "Parthia", 36.7500, 66.9000, "Capital");
        createProvince("SOGDIANA", "Parthia", 39.6500, 66.9667, "Capital");
        createProvince("PARTHIA", "Parthia", 35.5833, 51.4333, "Capital");
        createProvince("MEDIA", "Parthia", 34.7983, 48.5148, "Capital");
        createProvince("PERSIS", "Parthia", 29.9358, 52.8916, "Capital");
        createProvince("ELYMAIS", "Parthia", 32.1892, 48.2536, "Capital");
        createProvince("CHARACENE", "Parthia", 30.4333, 47.7000, "Capital");
        createProvince("MARGIANA", "Parthia", 37.6667, 61.6167, "Capital");
        createProvince("ARIA", "Parthia", 34.3667, 62.1833, "Capital");
        createProvince("DRANGIANA", "Parthia", 31.6167, 65.7167, "Capital");
        createProvince("ARACHOSIA", "Parthia", 31.6167, 64.3667, "Capital");
        createProvince("GEDROSIA", "Parthia", 25.3667, 60.3667, "Capital");
        createProvince("CARMANIA", "Parthia", 27.1833, 56.2667, "Capital");
        createProvince("PERSIS", "Parthia", 29.9358, 52.8916, "Capital");
    }
    
    private void initializeOtherStates() {
        // Other states in 117 AD
        createProvince("ARMENIA", "Armenia", 40.1833, 44.5167, "Capital");
        createProvince("IBERIA", "Iberia", 41.7167, 44.7833, "Capital");
        createProvince("ALBANIA", "Albania", 40.3667, 47.3667, "Capital");
        createProvince("LAZICA", "Lazica", 42.2667, 42.0333, "Capital");
        createProvince("COLCHIS", "Colchis", 42.2667, 42.0333, "Capital");
        createProvince("SARMATIA", "Sarmatia", 48.0167, 37.8000, "Capital");
        createProvince("DACIA", "Dacia", 44.4333, 26.1000, "Capital");
        createProvince("QUADI", "Quadi", 48.1500, 17.1167, "Capital");
        createProvince("MARCOMANNI", "Marcomanni", 49.2000, 16.6167, "Capital");
        createProvince("HERMUNDURI", "Hermunduri", 50.7333, 12.4833, "Capital");
        createProvince("CHATTI", "Chatti", 51.3167, 9.4667, "Capital");
        createProvince("CHERUSCI", "Cherusci", 52.2667, 8.0500, "Capital");
        createProvince("SUEBI", "Suebi", 49.4500, 11.0833, "Capital");
        createProvince("ALEMANNI", "Alemanni", 47.5500, 7.6000, "Capital");
        createProvince("FRISIANS", "Frisians", 53.2000, 5.7833, "Capital");
        createProvince("BRITONS", "Britons", 54.0000, -2.0000, "Capital");
        createProvince("CALEDONIANS", "Caledonians", 57.0000, -4.0000, "Capital");
        createProvince("HIBERNIANS", "Hibernians", 53.0000, -8.0000, "Capital");
        createProvince("PICTS", "Picts", 56.0000, -3.0000, "Capital");
        createProvince("SCOTI", "Scoti", 55.0000, -6.0000, "Capital");
        createProvince("GARAMANTES", "Garamantes", 26.5333, 12.8833, "Capital");
        createProvince("NUBIA", "Nubia", 19.6167, 30.2167, "Capital");
        createProvince("AXUM", "Axum", 14.1333, 38.7167, "Capital");
        createProvince("HIMYAR", "Himyar", 15.3500, 44.2000, "Capital");
        createProvince("SABA", "Saba", 15.3500, 44.2000, "Capital");
        createProvince("HADRAMAUT", "Hadramaut", 15.3500, 49.0167, "Capital");
        createProvince("OMAN", "Oman", 23.6167, 58.5833, "Capital");
        createProvince("GEDROSIA", "Gedrosia", 25.3667, 60.3667, "Capital");
        createProvince("KUSHAN", "Kushan", 34.0000, 62.0000, "Capital");
        createProvince("INDO-PARTHIAN", "Indo-Parthian", 30.1833, 67.0000, "Capital");
        createProvince("SATRAPS", "Satraps", 30.1833, 67.0000, "Capital");
    }
    
    private void initializeSeaZones() {
        // Major sea zones
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