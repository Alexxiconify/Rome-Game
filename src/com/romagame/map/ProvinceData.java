package com.romagame.map;

public class ProvinceData {
    public int[] mask_color;
    public int[] owner_color;
    public String nation;
    
    public ProvinceData() {
        // Default constructor for Gson
    }
    
    public ProvinceData(int[] maskColor, int[] ownerColor, String nation) {
        this.mask_color = maskColor;
        this.owner_color = ownerColor;
        this.nation = nation;
    }
    
    public int[] getMaskColor() { return mask_color; }
    public int[] getOwnerColor() { return owner_color; }
    public String getNation() { return nation; }
    
    public void setMaskColor(int[] maskColor) { this.mask_color = maskColor; }
    public void setOwnerColor(int[] ownerColor) { this.owner_color = ownerColor; }
    public void setNation(String nation) { this.nation = nation; }
} 