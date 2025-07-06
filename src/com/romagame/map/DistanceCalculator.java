package com.romagame.map;

import java.awt.Point;
import java.awt.geom.Point2D;

public class DistanceCalculator {
    
    // Earth's radius in kilometers
    private static final double EARTH_RADIUS = 6371.0;
    
    /**
     * Calculate the great circle distance between two points using the Haversine formula
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * Calculate distance between two provinces
     * @param province1 First province
     * @param province2 Second province
     * @return Distance in kilometers
     */
    public static double calculateProvinceDistance(Province province1, Province province2) {
        return calculateDistance(
            province1.getLatitude(), province1.getLongitude(),
            province2.getLatitude(), province2.getLongitude()
        );
    }
    
    /**
     * Convert map coordinates to screen coordinates
     * @param mapPoint Point in map coordinates
     * @param scale Current zoom scale
     * @param offsetX X offset
     * @param offsetY Y offset
     * @return Point in screen coordinates
     */
    public static Point mapToScreen(Point2D.Double mapPoint, double scale, int offsetX, int offsetY) {
        int screenX = (int)(mapPoint.x * scale) + offsetX;
        int screenY = (int)(mapPoint.y * scale) + offsetY;
        return new Point(screenX, screenY);
    }
    
    /**
     * Convert screen coordinates to map coordinates
     * @param screenPoint Point in screen coordinates
     * @param scale Current zoom scale
     * @param offsetX X offset
     * @param offsetY Y offset
     * @return Point in map coordinates
     */
    public static Point2D.Double screenToMap(Point screenPoint, double scale, int offsetX, int offsetY) {
        double mapX = (screenPoint.x - offsetX) / scale;
        double mapY = (screenPoint.y - offsetY) / scale;
        return new Point2D.Double(mapX, mapY);
    }
    
    /**
     * Convert latitude/longitude to map coordinates (simplified projection)
     * @param lat Latitude in degrees
     * @param lon Longitude in degrees
     * @param mapWidth Width of the map image
     * @param mapHeight Height of the map image
     * @return Point in map coordinates
     */
    public static Point2D.Double latLonToMapCoords(double lat, double lon, int mapWidth, int mapHeight) {
        // Simple equirectangular projection
        // Map longitude -180 to 180 to x coordinates 0 to mapWidth
        double x = (lon + 180.0) / 360.0 * mapWidth;
        
        // Map latitude 90 to -90 to y coordinates 0 to mapHeight
        double y = (90.0 - lat) / 180.0 * mapHeight;
        
        return new Point2D.Double(x, y);
    }
    
    /**
     * Convert map coordinates to latitude/longitude (simplified projection)
     * @param mapX X coordinate in map space
     * @param mapY Y coordinate in map space
     * @param mapWidth Width of the map image
     * @param mapHeight Height of the map image
     * @return Point with lat/lon values
     */
    public static Point2D.Double mapCoordsToLatLon(double mapX, double mapY, int mapWidth, int mapHeight) {
        // Reverse of latLonToMapCoords
        double lon = (mapX / mapWidth * 360.0) - 180.0;
        double lat = 90.0 - (mapY / mapHeight * 180.0);
        
        return new Point2D.Double(lat, lon);
    }
    
    /**
     * Calculate fog of war distance between two countries
     * @param country1 First country
     * @param country2 Second country
     * @param worldMap World map containing provinces
     * @return Distance in kilometers, or -1 if cannot calculate
     */
    public static double calculateFogOfWarDistance(Country country1, Country country2, WorldMap worldMap) {
        // Find the closest provinces between the two countries
        double minDistance = Double.MAX_VALUE;
        
        for (Province province1 : country1.getProvinces()) {
            for (Province province2 : country2.getProvinces()) {
                double distance = calculateProvinceDistance(province1, province2);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        
        return minDistance == Double.MAX_VALUE ? -1 : minDistance;
    }
    
    /**
     * Check if a country is visible to another country based on fog of war
     * @param viewerCountry Country doing the viewing
     * @param targetCountry Country being viewed
     * @param worldMap World map containing provinces
     * @param fogDistance Maximum visibility distance in kilometers
     * @return true if target country is visible
     */
    public static boolean isCountryVisible(Country viewerCountry, Country targetCountry, WorldMap worldMap, double fogDistance) {
        if (viewerCountry.equals(targetCountry)) {
            return true; // Always visible to self
        }
        
        double distance = calculateFogOfWarDistance(viewerCountry, targetCountry, worldMap);
        return distance >= 0 && distance <= fogDistance;
    }
} 