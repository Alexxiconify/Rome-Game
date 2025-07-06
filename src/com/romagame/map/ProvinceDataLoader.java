package com.romagame.map;

import java.io.*;
import java.util.*;

public class ProvinceDataLoader {
    
    public static Map<String, ProvinceData> loadProvinceData(String path) throws IOException {
        Map<String, ProvinceData> map = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            String json = content.toString();
            parseProvinces(json, map);
        }
        
        return map;
    }
    
    private static void parseProvinces(String json, Map<String, ProvinceData> map) {
        // Find the "provinces" object
        int provincesStart = json.indexOf("\"provinces\"");
        if (provincesStart == -1) return;
        
        int braceStart = json.indexOf("{", provincesStart);
        if (braceStart == -1) return;
        
        int braceCount = 0;
        int start = braceStart;
        
        for (int i = braceStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') braceCount++;
            else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    parseProvinceObject(json.substring(start, i + 1), map);
                    break;
                }
            }
        }
    }
    
    private static void parseProvinceObject(String json, Map<String, ProvinceData> map) {
        // Simple parsing for province objects
        String[] parts = json.split("\"province_");
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int colonIndex = part.indexOf("\":");
            if (colonIndex == -1) continue;
            
            String provinceId = "province_" + part.substring(0, colonIndex);
            
            // Extract mask_color, owner_color, and nation
            int[] maskColor = extractColorArray(part, "mask_color");
            int[] ownerColor = extractColorArray(part, "owner_color");
            String nation = extractString(part, "nation");
            
            if (maskColor != null && ownerColor != null && nation != null) {
                map.put(provinceId, new ProvinceData(maskColor, ownerColor, nation));
            }
        }
    }
    
    private static int[] extractColorArray(String json, String key) {
        int start = json.indexOf("\"" + key + "\":");
        if (start == -1) return null;
        
        start = json.indexOf("[", start);
        if (start == -1) return null;
        
        int end = json.indexOf("]", start);
        if (end == -1) return null;
        
        String colorStr = json.substring(start + 1, end);
        String[] parts = colorStr.split(",");
        if (parts.length != 3) return null;
        
        try {
            return new int[]{
                Integer.parseInt(parts[0].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[2].trim())
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private static String extractString(String json, String key) {
        int start = json.indexOf("\"" + key + "\":");
        if (start == -1) return null;
        
        start = json.indexOf("\"", start + key.length() + 3);
        if (start == -1) return null;
        
        int end = json.indexOf("\"", start + 1);
        if (end == -1) return null;
        
        return json.substring(start + 1, end);
    }
    
    public static Map<String, String> buildMaskColorToProvinceId(Map<String, ProvinceData> provinceData) {
        Map<String, String> maskColorToProvinceId = new HashMap<>();
        
        for (Map.Entry<String, ProvinceData> entry : provinceData.entrySet()) {
            String provinceId = entry.getKey();
            int[] mask = entry.getValue().mask_color;
            String maskKey = mask[0] + "," + mask[1] + "," + mask[2];
            maskColorToProvinceId.put(maskKey, provinceId);
        }
        
        return maskColorToProvinceId;
    }
    
    public static Map<String, String> buildOwnerColorToNation(Map<String, ProvinceData> provinceData) {
        Map<String, String> ownerColorToNation = new HashMap<>();
        
        for (ProvinceData data : provinceData.values()) {
            int[] owner = data.owner_color;
            String ownerKey = owner[0] + "," + owner[1] + "," + owner[2];
            ownerColorToNation.put(ownerKey, data.nation);
        }
        
        return ownerColorToNation;
    }
} 