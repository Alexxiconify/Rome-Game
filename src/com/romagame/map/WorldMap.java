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
        // Initialize provinces based on Q-BAM map white zones
        initializeEuropeProvinces();
        initializeAfricaProvinces();
        initializeAsiaProvinces();
        initializeAmericasProvinces();
        initializeSeaZones();
    }
    
    private void initializeEuropeProvinces() {
        // Western Europe
        createProvince("BRITAIN", "Britons", 54.0000, -2.0000, "Capital");
        createProvince("WALES", "Britons", 52.3000, -3.8000, "Province");
        createProvince("SCOTLAND", "Caledonians", 56.0000, -4.0000, "Capital");
        createProvince("IRELAND", "Hibernians", 53.0000, -8.0000, "Capital");
        createProvince("FRANCE", "Roman Empire", 46.2276, 2.2137, "Capital");
        createProvince("BRITTANY", "Roman Empire", 48.2020, -2.9326, "Province");
        createProvince("NORMANDY", "Roman Empire", 49.1800, -0.3700, "Province");
        createProvince("AQUITAINE", "Roman Empire", 44.8378, -0.5792, "Province");
        createProvince("BURGUNDY", "Roman Empire", 47.2167, 5.0167, "Province");
        createProvince("PROVENCE", "Roman Empire", 43.2965, 5.3698, "Province");
        createProvince("LANGUEDOC", "Roman Empire", 43.6100, 3.8760, "Province");
        createProvince("GASCONY", "Roman Empire", 44.8378, -0.5792, "Province");
        
        // Iberian Peninsula
        createProvince("CASTILE", "Roman Empire", 40.4168, -3.7038, "Capital");
        createProvince("ARAGON", "Roman Empire", 41.6500, -0.8833, "Province");
        createProvince("NAVARRE", "Roman Empire", 42.8167, -1.6500, "Province");
        createProvince("PORTUGAL", "Roman Empire", 38.7223, -9.1393, "Capital");
        createProvince("GALICIA", "Roman Empire", 42.8800, -8.5500, "Province");
        createProvince("ANDALUSIA", "Roman Empire", 37.3891, -5.9845, "Province");
        createProvince("CATALONIA", "Roman Empire", 41.3851, 2.1734, "Province");
        
        // Central Europe
        createProvince("GERMANY", "Roman Empire", 51.1657, 10.4515, "Capital");
        createProvince("BAVARIA", "Roman Empire", 48.1351, 11.5820, "Province");
        createProvince("SAXONY", "Roman Empire", 51.0500, 13.7500, "Province");
        createProvince("THURINGIA", "Roman Empire", 50.9833, 11.0333, "Province");
        createProvince("FRANCONIA", "Roman Empire", 49.4500, 10.9000, "Province");
        createProvince("SWABIA", "Roman Empire", 47.5500, 7.6000, "Province");
        createProvince("LORRAINE", "Roman Empire", 48.6833, 6.1833, "Province");
        createProvince("ALSACE", "Roman Empire", 48.5833, 7.7500, "Province");
        createProvince("SWITZERLAND", "Roman Empire", 46.9479, 7.4474, "Province");
        createProvince("AUSTRIA", "Roman Empire", 48.2082, 16.3738, "Capital");
        createProvince("BOHEMIA", "Roman Empire", 50.0755, 14.4378, "Province");
        createProvince("MORAVIA", "Roman Empire", 49.2000, 16.6167, "Province");
        createProvince("SILESIA", "Roman Empire", 51.1000, 17.0333, "Province");
        
        // Northern Europe
        createProvince("DENMARK", "Roman Empire", 55.6761, 12.5683, "Capital");
        createProvince("NORWAY", "Roman Empire", 59.9139, 10.7522, "Capital");
        createProvince("SWEDEN", "Roman Empire", 59.3293, 18.0686, "Capital");
        createProvince("FINLAND", "Roman Empire", 60.1699, 24.9384, "Capital");
        createProvince("ESTONIA", "Roman Empire", 59.4369, 24.7536, "Province");
        createProvince("LATVIA", "Roman Empire", 56.9496, 24.1052, "Province");
        createProvince("LITHUANIA", "Roman Empire", 54.6872, 25.2797, "Province");
        
        // Eastern Europe
        createProvince("POLAND", "Roman Empire", 52.2297, 21.0122, "Capital");
        createProvince("HUNGARY", "Roman Empire", 47.4979, 19.0402, "Capital");
        createProvince("CROATIA", "Roman Empire", 45.8150, 15.9819, "Province");
        createProvince("SLOVENIA", "Roman Empire", 46.0569, 14.5058, "Province");
        createProvince("SLOVAKIA", "Roman Empire", 48.1486, 17.1077, "Province");
        createProvince("CZECH_REPUBLIC", "Roman Empire", 50.0755, 14.4378, "Province");
        
        // Balkans
        createProvince("SERBIA", "Roman Empire", 44.0165, 21.0059, "Capital");
        createProvince("BOSNIA", "Roman Empire", 43.8564, 18.4131, "Province");
        createProvince("MONTENEGRO", "Roman Empire", 42.4304, 19.2594, "Province");
        createProvince("ALBANIA", "Roman Empire", 41.3275, 19.8187, "Capital");
        createProvince("MACEDONIA", "Roman Empire", 41.9981, 21.4254, "Province");
        createProvince("BULGARIA", "Roman Empire", 42.7339, 25.4858, "Capital");
        createProvince("ROMANIA", "Roman Empire", 44.4268, 26.1025, "Capital");
        createProvince("MOLDOVA", "Roman Empire", 47.0105, 28.8638, "Province");
        
        // Italy
        createProvince("ITALY", "Roman Empire", 41.9028, 12.4964, "Capital");
        createProvince("LOMBARDY", "Roman Empire", 45.4642, 9.1900, "Province");
        createProvince("PIEDMONT", "Roman Empire", 45.0703, 7.6869, "Province");
        createProvince("VENETO", "Roman Empire", 45.4371, 12.3326, "Province");
        createProvince("TUSCANY", "Roman Empire", 43.7711, 11.2486, "Province");
        createProvince("UMBRIA", "Roman Empire", 43.1107, 12.3908, "Province");
        createProvince("MARCHE", "Roman Empire", 43.6168, 13.5189, "Province");
        createProvince("LAZIO", "Roman Empire", 41.9028, 12.4964, "Province");
        createProvince("CAMPANIA", "Roman Empire", 40.8518, 14.2681, "Province");
        createProvince("PUGLIA", "Roman Empire", 41.1171, 16.8719, "Province");
        createProvince("CALABRIA", "Roman Empire", 38.1147, 15.6490, "Province");
        createProvince("SICILY", "Roman Empire", 37.5000, 14.0000, "Province");
        createProvince("SARDINIA", "Roman Empire", 40.0000, 9.0000, "Province");
        createProvince("CORSICA", "Roman Empire", 42.0000, 9.0000, "Province");
        
        // Greece
        createProvince("GREECE", "Roman Empire", 37.9838, 23.7275, "Capital");
        createProvince("MACEDONIA_GREECE", "Roman Empire", 41.0000, 21.4333, "Province");
        createProvince("THESSALY", "Roman Empire", 39.6401, 22.9444, "Province");
        createProvince("EPIRUS", "Roman Empire", 39.6667, 20.8500, "Province");
        createProvince("PELOPONNESE", "Roman Empire", 37.9407, 22.9569, "Province");
        createProvince("CRETE", "Roman Empire", 35.3333, 25.1333, "Province");
        createProvince("CYPRUS", "Roman Empire", 35.1667, 33.3667, "Province");
    }
    
    private void initializeAfricaProvinces() {
        // North Africa
        createProvince("MOROCCO", "Roman Empire", 31.7917, -7.0926, "Capital");
        createProvince("ALGERIA", "Roman Empire", 36.7538, 3.0588, "Capital");
        createProvince("TUNISIA", "Roman Empire", 36.8065, 10.1815, "Capital");
        createProvince("LIBYA", "Roman Empire", 32.8872, 13.1913, "Capital");
        createProvince("EGYPT", "Roman Empire", 31.2001, 29.9187, "Capital");
        createProvince("SUDAN", "Nubia", 12.8628, 30.2176, "Capital");
        createProvince("ETHIOPIA", "Axum", 9.1450, 40.4897, "Capital");
        createProvince("SOMALIA", "Axum", 5.1521, 46.1996, "Province");
        createProvince("KENYA", "Axum", -0.0236, 37.9062, "Province");
        createProvince("TANZANIA", "Axum", -6.3690, 34.8888, "Province");
        createProvince("UGANDA", "Axum", 1.3733, 32.2903, "Province");
        createProvince("DR_CONGO", "Axum", -4.0383, 21.7587, "Province");
        createProvince("ANGOLA", "Axum", -11.2027, 17.8739, "Province");
        createProvince("ZAMBIA", "Axum", -13.1339, 27.8493, "Province");
        createProvince("ZIMBABWE", "Axum", -19.0154, 29.1549, "Province");
        createProvince("SOUTH_AFRICA", "Axum", -30.5595, 22.9375, "Province");
        createProvince("NAMIBIA", "Axum", -22.9576, 18.4904, "Province");
        createProvince("BOTSWANA", "Axum", -22.3285, 24.6849, "Province");
        createProvince("MOZAMBIQUE", "Axum", -18.6657, 35.5296, "Province");
        createProvince("MADAGASCAR", "Axum", -18.7669, 46.8691, "Province");
        
        // West Africa
        createProvince("SENEGAL", "Garamantes", 14.4974, -14.4524, "Province");
        createProvince("GAMBIA", "Garamantes", 13.4432, -15.3101, "Province");
        createProvince("GUINEA_BISSAU", "Garamantes", 11.8037, -15.1804, "Province");
        createProvince("GUINEA", "Garamantes", 9.9456, -9.6966, "Province");
        createProvince("SIERRA_LEONE", "Garamantes", 8.4606, -11.7799, "Province");
        createProvince("LIBERIA", "Garamantes", 6.4281, -9.4295, "Province");
        createProvince("IVORY_COAST", "Garamantes", 7.5400, -5.5471, "Province");
        createProvince("GHANA", "Garamantes", 7.9465, -1.0232, "Province");
        createProvince("TOGO", "Garamantes", 8.6195, 0.8248, "Province");
        createProvince("BENIN", "Garamantes", 9.3077, 2.3158, "Province");
        createProvince("NIGERIA", "Garamantes", 9.0820, 8.6753, "Province");
        createProvince("NIGER", "Garamantes", 17.6078, 8.0817, "Province");
        createProvince("CHAD", "Garamantes", 15.4542, 18.7322, "Province");
        createProvince("CAMEROON", "Garamantes", 7.3697, 12.3547, "Province");
        createProvince("CENTRAL_AFRICAN_REPUBLIC", "Garamantes", 6.6111, 20.9394, "Province");
        createProvince("GABON", "Garamantes", -0.8037, 11.6094, "Province");
        createProvince("CONGO", "Garamantes", -0.2280, 15.8277, "Province");
        createProvince("EQUATORIAL_GUINEA", "Garamantes", 1.6508, 10.2679, "Province");
        createProvince("SAO_TOME", "Garamantes", 0.1864, 6.6131, "Province");
    }
    
    private void initializeAsiaProvinces() {
        // Middle East
        createProvince("TURKEY", "Roman Empire", 38.9637, 35.2433, "Capital");
        createProvince("SYRIA", "Roman Empire", 36.2021, 36.1613, "Capital");
        createProvince("LEBANON", "Roman Empire", 33.8547, 35.8623, "Province");
        createProvince("ISRAEL", "Roman Empire", 31.7683, 35.2137, "Capital");
        createProvince("PALESTINE", "Roman Empire", 31.9522, 35.2332, "Province");
        createProvince("JORDAN", "Roman Empire", 30.5852, 36.2384, "Province");
        createProvince("IRAQ", "Parthia", 33.2232, 43.6793, "Capital");
        createProvince("IRAN", "Parthia", 32.4279, 53.6880, "Capital");
        createProvince("AFGHANISTAN", "Parthia", 33.9391, 67.7100, "Capital");
        createProvince("PAKISTAN", "Indo-Parthian", 30.3753, 69.3451, "Capital");
        createProvince("INDIA", "Indo-Parthian", 20.5937, 78.9629, "Capital");
        createProvince("BANGLADESH", "Indo-Parthian", 23.6850, 90.3563, "Province");
        createProvince("SRI_LANKA", "Indo-Parthian", 7.8731, 80.7718, "Province");
        createProvince("NEPAL", "Indo-Parthian", 28.3949, 84.1240, "Province");
        createProvince("BHUTAN", "Indo-Parthian", 27.5142, 90.4336, "Province");
        createProvince("MYANMAR", "Indo-Parthian", 21.9162, 95.9560, "Province");
        createProvince("THAILAND", "Indo-Parthian", 15.8700, 100.9925, "Province");
        createProvince("LAOS", "Indo-Parthian", 19.8563, 102.4955, "Province");
        createProvince("CAMBODIA", "Indo-Parthian", 12.5657, 104.9910, "Province");
        createProvince("VIETNAM", "Indo-Parthian", 14.0583, 108.2772, "Province");
        createProvince("MALAYSIA", "Indo-Parthian", 4.2105, 108.9758, "Province");
        createProvince("SINGAPORE", "Indo-Parthian", 1.3521, 103.8198, "Province");
        createProvince("INDONESIA", "Indo-Parthian", -0.7893, 113.9213, "Province");
        createProvince("PHILIPPINES", "Indo-Parthian", 12.8797, 121.7740, "Province");
        createProvince("BRUNEI", "Indo-Parthian", 4.5353, 114.7277, "Province");
        createProvince("EAST_TIMOR", "Indo-Parthian", -8.8742, 125.7275, "Province");
        
        // Central Asia
        createProvince("KAZAKHSTAN", "Parthia", 48.0196, 66.9237, "Province");
        createProvince("UZBEKISTAN", "Parthia", 41.3775, 64.5853, "Province");
        createProvince("TURKMENISTAN", "Parthia", 38.9697, 59.5563, "Province");
        createProvince("KYRGYZSTAN", "Parthia", 41.2044, 74.7661, "Province");
        createProvince("TAJIKISTAN", "Parthia", 38.5358, 71.0965, "Province");
        createProvince("MONGOLIA", "Parthia", 46.8625, 103.8467, "Province");
        createProvince("CHINA", "Kushan", 35.8617, 104.1954, "Capital");
        createProvince("JAPAN", "Kushan", 36.2048, 138.2529, "Capital");
        createProvince("KOREA", "Kushan", 35.9078, 127.7669, "Capital");
        createProvince("TAIWAN", "Kushan", 23.5937, 121.0254, "Province");
        createProvince("HONG_KONG", "Kushan", 22.3193, 114.1694, "Province");
        createProvince("MACAU", "Kushan", 22.1987, 113.5439, "Province");
        
        // Arabian Peninsula
        createProvince("SAUDI_ARABIA", "Himyar", 23.8859, 45.0792, "Capital");
        createProvince("YEMEN", "Himyar", 15.5527, 48.5164, "Capital");
        createProvince("OMAN", "Oman", 21.4735, 55.9754, "Capital");
        createProvince("UAE", "Oman", 24.0000, 54.0000, "Province");
        createProvince("QATAR", "Oman", 25.3548, 51.1839, "Province");
        createProvince("BAHRAIN", "Oman", 26.0667, 50.5577, "Province");
        createProvince("KUWAIT", "Oman", 29.3117, 47.4818, "Province");
        
        // Caucasus
        createProvince("ARMENIA", "Armenia", 40.0691, 45.0382, "Capital");
        createProvince("GEORGIA", "Iberia", 42.3154, 43.3569, "Capital");
        createProvince("AZERBAIJAN", "Albania", 40.1431, 47.5769, "Capital");
        createProvince("RUSSIA", "Sarmatia", 61.5240, 105.3188, "Capital");
        createProvince("UKRAINE", "Sarmatia", 48.3794, 31.1656, "Province");
        createProvince("BELARUS", "Sarmatia", 53.7098, 27.9534, "Province");
        createProvince("MOLDOVA", "Sarmatia", 47.4116, 28.3699, "Province");
    }
    
    private void initializeAmericasProvinces() {
        // North America
        createProvince("CANADA", "Uninhabited", 56.1304, -106.3468, "Province");
        createProvince("USA", "Uninhabited", 37.0902, -95.7129, "Province");
        createProvince("MEXICO", "Uninhabited", 23.6345, -102.5528, "Province");
        createProvince("GUATEMALA", "Uninhabited", 15.7835, -90.2308, "Province");
        createProvince("BELIZE", "Uninhabited", 17.1899, -88.4976, "Province");
        createProvince("EL_SALVADOR", "Uninhabited", 13.7942, -88.8965, "Province");
        createProvince("HONDURAS", "Uninhabited", 15.1999, -86.2419, "Province");
        createProvince("NICARAGUA", "Uninhabited", 12.8654, -85.2072, "Province");
        createProvince("COSTA_RICA", "Uninhabited", 9.9281, -84.0907, "Province");
        createProvince("PANAMA", "Uninhabited", 8.5380, -80.7821, "Province");
        
        // South America
        createProvince("COLOMBIA", "Uninhabited", 4.5709, -74.2973, "Province");
        createProvince("VENEZUELA", "Uninhabited", 6.4238, -66.5897, "Province");
        createProvince("GUYANA", "Uninhabited", 4.8604, -58.9302, "Province");
        createProvince("SURINAME", "Uninhabited", 3.9193, -56.0278, "Province");
        createProvince("FRENCH_GUIANA", "Uninhabited", 3.9339, -53.1258, "Province");
        createProvince("BRAZIL", "Uninhabited", -14.2350, -51.9253, "Province");
        createProvince("ECUADOR", "Uninhabited", -1.8312, -78.1834, "Province");
        createProvince("PERU", "Uninhabited", -9.1900, -75.0152, "Province");
        createProvince("BOLIVIA", "Uninhabited", -16.2902, -63.5887, "Province");
        createProvince("CHILE", "Uninhabited", -35.6751, -71.5430, "Province");
        createProvince("ARGENTINA", "Uninhabited", -38.4161, -63.6167, "Province");
        createProvince("PARAGUAY", "Uninhabited", -23.4425, -58.4438, "Province");
        createProvince("URUGUAY", "Uninhabited", -32.5228, -55.7658, "Province");
        
        // Caribbean
        createProvince("CUBA", "Uninhabited", 21.5218, -77.7812, "Province");
        createProvince("JAMAICA", "Uninhabited", 18.1096, -77.2975, "Province");
        createProvince("HAITI", "Uninhabited", 18.9712, -72.2852, "Province");
        createProvince("DOMINICAN_REPUBLIC", "Uninhabited", 18.7357, -70.1627, "Province");
        createProvince("PUERTO_RICO", "Uninhabited", 18.2208, -66.5901, "Province");
        createProvince("TRINIDAD_TOBAGO", "Uninhabited", 10.6598, -61.5191, "Province");
        createProvince("BARBADOS", "Uninhabited", 13.1939, -59.5432, "Province");
        createProvince("GRENADA", "Uninhabited", 12.1165, -61.6790, "Province");
        createProvince("ST_VINCENT", "Uninhabited", 12.9843, -61.2872, "Province");
        createProvince("ST_LUCIA", "Uninhabited", 13.9094, -60.9789, "Province");
        createProvince("DOMINICA", "Uninhabited", 15.4150, -61.3710, "Province");
        createProvince("ANTIGUA_BARBUDA", "Uninhabited", 17.0608, -61.7964, "Province");
        createProvince("ST_KITTS_NEVIS", "Uninhabited", 17.3578, -62.7830, "Province");
        createProvince("MONTSERRAT", "Uninhabited", 16.7425, -62.1874, "Province");
        createProvince("ANGUILLA", "Uninhabited", 18.2206, -63.0686, "Province");
        createProvince("BRITISH_VIRGIN_ISLANDS", "Uninhabited", 18.4207, -64.6400, "Province");
        createProvince("US_VIRGIN_ISLANDS", "Uninhabited", 18.3358, -64.8963, "Province");
        createProvince("CAYMAN_ISLANDS", "Uninhabited", 19.3133, -81.2546, "Province");
        createProvince("TURKS_CAICOS", "Uninhabited", 21.6940, -71.7979, "Province");
        createProvince("BAHAMAS", "Uninhabited", 24.7736, -78.0000, "Province");
        createProvince("BERMUDA", "Uninhabited", 32.3078, -64.7505, "Province");
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