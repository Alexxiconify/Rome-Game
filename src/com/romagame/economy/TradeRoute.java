package com.romagame.economy;

public class TradeRoute {
    private String name;
    private String fromNode;
    private String toNode;
    private double efficiency;
    private double income;
    private boolean isActive;
    
    public TradeRoute(String name, String fromNode, String toNode) {
        this.name = name;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.efficiency = 0.8; // Base efficiency
        this.income = 0.0;
        this.isActive = true;
    }
    
    public void update() {
        if (!isActive) return;
        
        // Calculate income based on efficiency and connected nodes
        calculateIncome();
        
        // Efficiency can fluctuate
        updateEfficiency();
    }
    
    private void calculateIncome() {
        // Simplified income calculation
        income = efficiency * 10.0; // Base income
    }
    
    private void updateEfficiency() {
        // Efficiency can change due to various factors
        double fluctuation = (Math.random() - 0.5) * 0.02; // ±1% fluctuation
        efficiency = Math.max(0.1, Math.min(1.0, efficiency + fluctuation));
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public void setEfficiency(double efficiency) {
        this.efficiency = Math.max(0.0, Math.min(1.0, efficiency));
    }
    
    // Getters
    public String getName() { return name; }
    public String getFromNode() { return fromNode; }
    public String getToNode() { return toNode; }
    public double getEfficiency() { return efficiency; }
    public double getIncome() { return income; }
    public boolean isActive() { return isActive; }
} 