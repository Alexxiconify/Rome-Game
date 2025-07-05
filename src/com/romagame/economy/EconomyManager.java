package com.romagame.economy;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class EconomyManager {
    private Map<String, Double> globalPrices;
    private Map<String, TradeRoute> tradeRoutes;
    private List<TradeNode> tradeNodes;
    
    public EconomyManager() {
        globalPrices = new HashMap<>();
        tradeRoutes = new HashMap<>();
        tradeNodes = new ArrayList<>();
        initializeEconomy();
    }
    
    private void initializeEconomy() {
        // Initialize global prices for trade goods
        globalPrices.put("Grain", 2.0);
        globalPrices.put("Wool", 3.0);
        globalPrices.put("Wine", 5.0);
        globalPrices.put("Cloth", 8.0);
        globalPrices.put("Spices", 15.0);
        globalPrices.put("Tea", 12.0);
        globalPrices.put("Furs", 10.0);
        globalPrices.put("Fish", 4.0);
        globalPrices.put("Slaves", 20.0);
        globalPrices.put("Ivory", 25.0);
        
        // Initialize major trade nodes
        initializeTradeNodes();
    }
    
    private void initializeTradeNodes() {
        tradeNodes.add(new TradeNode("English Channel", "Western Europe"));
        tradeNodes.add(new TradeNode("Genoa", "Mediterranean"));
        tradeNodes.add(new TradeNode("Venice", "Mediterranean"));
        tradeNodes.add(new TradeNode("Constantinople", "Eastern Mediterranean"));
        tradeNodes.add(new TradeNode("Alexandria", "Red Sea"));
        tradeNodes.add(new TradeNode("Malacca", "Southeast Asia"));
        tradeNodes.add(new TradeNode("Hangzhou", "East Asia"));
        tradeNodes.add(new TradeNode("Zanzibar", "East Africa"));
    }
    
    public void update() {
        // Update global economy
        updatePrices();
        updateTradeRoutes();
        updateTradeNodes();
    }
    
    private void updatePrices() {
        // Simulate price fluctuations
        for (String good : globalPrices.keySet()) {
            double currentPrice = globalPrices.get(good);
            double fluctuation = (Math.random() - 0.5) * 0.1; // ±5% fluctuation
            globalPrices.put(good, currentPrice * (1 + fluctuation));
        }
    }
    
    private void updateTradeRoutes() {
        // Update trade route efficiency and income
        for (TradeRoute route : tradeRoutes.values()) {
            route.update();
        }
    }
    
    private void updateTradeNodes() {
        // Update trade node power and income
        for (TradeNode node : tradeNodes) {
            node.update();
        }
    }
    
    public double getPrice(String good) {
        return globalPrices.getOrDefault(good, 1.0);
    }
    
    public void setPrice(String good, double price) {
        globalPrices.put(good, price);
    }
    
    public TradeNode getTradeNode(String name) {
        return tradeNodes.stream()
                .filter(node -> node.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    public List<TradeNode> getAllTradeNodes() {
        return new ArrayList<>(tradeNodes);
    }
    
    public void addTradeRoute(String name, String from, String to) {
        TradeRoute route = new TradeRoute(name, from, to);
        tradeRoutes.put(name, route);
    }
    
    public void initializeEconomies() {
        // Initialize country-specific economies
    }
} 