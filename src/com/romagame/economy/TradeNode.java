package com.romagame.economy;

import java.util.Map;
import java.util.HashMap;

public class TradeNode {
    private String name;
    private String region;
    private double tradePower;
    private double tradeValue;
    private Map<String, Double> goods;
    
    public TradeNode(String name, String region) {
        this.name = name;
        this.region = region;
        this.tradePower = 100.0;
        this.tradeValue = 0.0;
        this.goods = new HashMap<>();
        initializeGoods();
    }
    
    private void initializeGoods() {
        // Initialize trade goods based on region
        switch (region) {
            case "Western Europe" -> {
                goods.put("Wool", 10.0);
                goods.put("Wine", 8.0);
                goods.put("Cloth", 5.0);
            }
            case "Mediterranean" -> {
                goods.put("Wine", 12.0);
                goods.put("Olive Oil", 8.0);
                goods.put("Silk", 6.0);
            }
            case "East Asia" -> {
                goods.put("Tea", 15.0);
                goods.put("Silk", 10.0);
                goods.put("Porcelain", 8.0);
            }
            case "Southeast Asia" -> {
                goods.put("Spices", 20.0);
                goods.put("Tea", 8.0);
                goods.put("Rice", 12.0);
            }
            default -> {
                goods.put("Grain", 10.0);
                goods.put("Fish", 8.0);
            }
        }
    }
    
    public void update() {
        // Update trade node
        calculateTradeValue();
        updateTradePower();
    }
    
    private void calculateTradeValue() {
        tradeValue = goods.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    private void updateTradePower() {
        // Trade power can fluctuate based on various factors
        double fluctuation = (Math.random() - 0.5) * 0.05; // ±2.5% fluctuation
        tradePower *= (1 + fluctuation);
    }
    
    public void addGood(String good, double amount) {
        goods.merge(good, amount, Double::sum);
    }
    
    public void removeGood(String good, double amount) {
        if (goods.containsKey(good)) {
            double current = goods.get(good);
            goods.put(good, Math.max(0, current - amount));
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getRegion() { return region; }
    public double getTradePower() { return tradePower; }
    public double getTradeValue() { return tradeValue; }
    public Map<String, Double> getGoods() { return goods; }
    
    public void setTradePower(double power) {
        this.tradePower = power;
    }
} 