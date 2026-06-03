package com.romagame.map;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProvinceDataLoader {
    
    public static Map<String, ProvinceData> loadProvinceData(String path) throws IOException {
        Map<String, ProvinceData> map = new HashMap<>();
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(path)));
            JSONObject root = new JSONObject(jsonText);
            JSONArray provincesArr = root.optJSONArray("provinces");
            if (provincesArr != null) {
                for (int i = 0; i < provincesArr.length(); i++) {
                    JSONObject province = provincesArr.getJSONObject(i);
                    String provinceId = province.getString("province_id");
                    
                    JSONArray maskColorArr = province.optJSONArray("mask_color");
                    int[] maskColor = null;
                    if (maskColorArr != null && maskColorArr.length() == 3) {
                        maskColor = new int[]{maskColorArr.getInt(0), maskColorArr.getInt(1), maskColorArr.getInt(2)};
                    }
                    
                    JSONArray ownerColorArr = province.optJSONArray("owner_color");
                    int[] ownerColor = null;
                    if (ownerColorArr != null && ownerColorArr.length() == 3) {
                        ownerColor = new int[]{ownerColorArr.getInt(0), ownerColorArr.getInt(1), ownerColorArr.getInt(2)};
                    }
                    
                    String nation = province.optString("owner", "Uncolonized");
                    
                    if (maskColor != null && ownerColor != null) {
                        map.put(provinceId, new ProvinceData(maskColor, ownerColor, nation));
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to load province data: " + e.getMessage(), e);
        }
        return map;
    }
    
    public static Map<String, String> buildMaskColorToProvinceId(Map<String, ProvinceData> provinceData) {
        Map<String, String> maskColorToProvinceId = new HashMap<>();
        for (Map.Entry<String, ProvinceData> entry : provinceData.entrySet()) {
            String provinceId = entry.getKey();
            int[] mask = entry.getValue().getMaskColor();
            if (mask != null) {
                String maskKey = mask[0] + "," + mask[1] + "," + mask[2];
                maskColorToProvinceId.put(maskKey, provinceId);
            }
        }
        return maskColorToProvinceId;
    }
    
    public static Map<String, String> buildOwnerColorToNation(Map<String, ProvinceData> provinceData) {
        Map<String, String> ownerColorToNation = new HashMap<>();
        for (ProvinceData data : provinceData.values()) {
            int[] owner = data.getOwnerColor();
            if (owner != null) {
                String ownerKey = owner[0] + "," + owner[1] + "," + owner[2];
                ownerColorToNation.put(ownerKey, data.getNation());
            }
        }
        return ownerColorToNation;
    }

    public static Map<String, String> loadNationColorToName() throws IOException {
        Map<String, String> colorToNation = new HashMap<>();
        File file = new File("src/resources/nation.txt");
        if (!file.exists()) return colorToNation;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Nation(") && line.endsWith(");")) {
                    String content = line.substring(7, line.length() - 2);
                    String[] parts = content.split(",");
                    if (parts.length == 4) {
                        int r = Integer.parseInt(parts[0].trim());
                        int g = Integer.parseInt(parts[1].trim());
                        int b = Integer.parseInt(parts[2].trim());
                        String name = parts[3].trim();
                        colorToNation.put(r + "," + g + "," + b, name);
                    }
                }
            }
        }
        return colorToNation;
    }
}