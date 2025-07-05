package com.romagame.map;

public class SeaZone {
    private String name;
    private String region;
    private double navalTraffic;
    
    public SeaZone(String name, String region) {
        this.name = name;
        this.region = region;
        this.navalTraffic = 0.0;
    }
    
    public void update() {
        // Update naval traffic
        navalTraffic += Math.random() * 0.1;
    }
    
    // Getters
    public String getName() { return name; }
    public String getRegion() { return region; }
    public double getNavalTraffic() { return navalTraffic; }
    
    public void setNavalTraffic(double traffic) {
        this.navalTraffic = traffic;
    }
} 