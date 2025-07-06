package com.romagame.core;

import com.romagame.map.WorldMap;
import com.romagame.map.Country;
import com.romagame.country.CountryManager;
import com.romagame.economy.EconomyManager;
import com.romagame.military.MilitaryManager;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.technology.TechnologyManager;
import com.romagame.colonization.ColonizationManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.function.Consumer;

public class GameEngine {
    private WorldMap worldMap;
    private CountryManager countryManager;
    private EconomyManager economyManager;
    private MilitaryManager militaryManager;
    private DiplomacyManager diplomacyManager;
    private TechnologyManager technologyManager;
    private ColonizationManager colonizationManager;
    
    private GameDate currentDate;
    private GameSpeed gameSpeed;
    private boolean isRunning;
    private ScheduledExecutorService gameLoop;
    private Consumer<GameEngine> uiUpdateCallback;
    
    public GameEngine() {
        initializeGame();
    }
    
    private void initializeGame() {
        currentDate = new GameDate(117, 1, 1); // Start in 117 AD
        gameSpeed = GameSpeed.NORMAL;
        isRunning = false;
        
        // Initialize all managers
        worldMap = new WorldMap();
        countryManager = new CountryManager(worldMap);
        economyManager = new EconomyManager();
        militaryManager = new MilitaryManager();
        diplomacyManager = new DiplomacyManager();
        technologyManager = new TechnologyManager();
        colonizationManager = new ColonizationManager(worldMap);
        
        // Setup initial game state
        setupInitialGameState();
    }
    
    private void setupInitialGameState() {
        // Create initial countries and setup world
        countryManager.initializeCountries();
        // worldMap.initializeProvinces();
        economyManager.initializeEconomies();
    }
    
    public void selectPlayerCountry(String countryName) {
        countryManager.setPlayerCountry(countryName);
    }
    
    public void setUIUpdateCallback(Consumer<GameEngine> callback) {
        this.uiUpdateCallback = callback;
    }
    
    public void start() {
        if (!isRunning) {
            isRunning = true;
            gameLoop = Executors.newScheduledThreadPool(1);
            gameLoop.scheduleAtFixedRate(this::update, 0, getUpdateInterval(), TimeUnit.MILLISECONDS);
        }
    }
    
    public void pause() {
        isRunning = false;
        if (gameLoop != null) {
            gameLoop.shutdown();
        }
    }
    
    public void setGameSpeed(GameSpeed speed) {
        this.gameSpeed = speed;
        // Restart the game loop with new speed if running
        if (isRunning && gameLoop != null) {
            gameLoop.shutdown();
            if (speed != GameSpeed.PAUSED) {
                gameLoop = Executors.newScheduledThreadPool(1);
                gameLoop.scheduleAtFixedRate(this::update, 0, getUpdateInterval(), TimeUnit.MILLISECONDS);
            }
        }
    }
    
    private long getUpdateInterval() {
        return switch (gameSpeed) {
            case PAUSED -> 1000; // 1 second but won't be used when paused
            case SLOW -> 1000; // 1 second
            case NORMAL -> 500; // 0.5 seconds
            case FAST -> 200; // 0.2 seconds
            case VERY_FAST -> 100; // 0.1 seconds
        };
    }
    
    private void update() {
        if (!isRunning) return;
        
        // Update all game systems
        currentDate.advance();
        countryManager.update();
        economyManager.update();
        militaryManager.update();
        diplomacyManager.update();
        technologyManager.update();
        colonizationManager.update();
        
        // Process events and AI decisions
        processEvents();
        processAI();
        
        // Update UI on EDT (Event Dispatch Thread)
        if (uiUpdateCallback != null) {
            javax.swing.SwingUtilities.invokeLater(() -> uiUpdateCallback.accept(this));
        }
    }
    
    private void processEvents() {
        // Handle random events, decisions, etc.
    }
    
    private void processAI() {
        // Process AI decisions for non-player countries
        countryManager.processAI();
    }
    
    // Getters for UI access
    public WorldMap getWorldMap() { return worldMap; }
    public CountryManager getCountryManager() { return countryManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public MilitaryManager getMilitaryManager() { return militaryManager; }
    public DiplomacyManager getDiplomacyManager() { return diplomacyManager; }
    public TechnologyManager getTechnologyManager() { return technologyManager; }
    public ColonizationManager getColonizationManager() { return colonizationManager; }
    public GameDate getCurrentDate() { return currentDate; }
    public GameSpeed getGameSpeed() { return gameSpeed; }
    public boolean isRunning() { return isRunning; }
    
    // Convenience methods for country access
    public List<Country> getAllCountries() {
        return worldMap.getAllCountries();
    }
    
    public Country getCountry(String name) {
        return worldMap.getCountry(name);
    }
} 