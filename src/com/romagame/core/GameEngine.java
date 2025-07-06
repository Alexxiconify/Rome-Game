package com.romagame.core;

import com.romagame.map.WorldMap;
import com.romagame.map.Country;
import com.romagame.country.CountryManager;
import com.romagame.economy.EconomyManager;
import com.romagame.military.MilitaryManager;
import com.romagame.diplomacy.DiplomacyManager;
import com.romagame.technology.TechnologyManager;
import com.romagame.colonization.ColonizationManager;
import com.romagame.population.PopulationManager;
import com.romagame.events.EventManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.function.Consumer;
import com.romagame.ui.UIUpdateManager;

public class GameEngine {
    private WorldMap worldMap;
    private CountryManager countryManager;
    private EconomyManager economyManager;
    private MilitaryManager militaryManager;
    private DiplomacyManager diplomacyManager;
    private TechnologyManager technologyManager;
    private ColonizationManager colonizationManager;
    private PopulationManager populationManager;
    private EventManager eventManager;
    
    private GameDate currentDate;
    private GameSpeed gameSpeed;
    private boolean isRunning;
    private GameThread gameThread;
    private UIUpdateManager uiUpdateManager;
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
        economyManager = new EconomyManager();
        militaryManager = new MilitaryManager();
        diplomacyManager = new DiplomacyManager();
        technologyManager = new TechnologyManager();
        colonizationManager = new ColonizationManager(worldMap);
        populationManager = new PopulationManager();
        eventManager = new EventManager();
        
        // Initialize country manager with AI support
        countryManager = new CountryManager(worldMap, diplomacyManager, militaryManager, economyManager);
        
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
            
            // Initialize UI update manager
            uiUpdateManager = new UIUpdateManager(this);
            
            // Create and start game thread
            gameThread = new GameThread(this);
            gameThread.start();
        }
    }
    
    public void pause() {
        if (gameThread != null) {
            gameThread.pause();
        }
        isRunning = false;
    }
    
    public void resume() {
        if (gameThread != null) {
            gameThread.resumeGame();
        }
        isRunning = true;
    }
    
    public void setGameSpeed(GameSpeed speed) {
        this.gameSpeed = speed;
        if (gameThread != null) {
            int threadSpeed = switch (speed) {
                case PAUSED -> 0;
                case SLOW -> 1;
                case NORMAL -> 1;
                case FAST -> 2;
                case VERY_FAST -> 3;
            };
            gameThread.setGameSpeed(threadSpeed);
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
    
    public void stop() {
        isRunning = false;
        if (gameThread != null) {
            gameThread.stopGame();
        }
        if (uiUpdateManager != null) {
            uiUpdateManager.stop();
        }
    }
    
    public GameThread getGameThread() {
        return gameThread;
    }
    
    public UIUpdateManager getUIUpdateManager() {
        return uiUpdateManager;
    }
    
    private void processEvents() {
        // Handle random events, decisions, etc.
        Country playerCountry = countryManager.getPlayerCountry();
        if (playerCountry != null) {
            eventManager.processRandomEvents(playerCountry);
        }
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
    public PopulationManager getPopulationManager() { return populationManager; }
    public EventManager getEventManager() { return eventManager; }
    public GameDate getCurrentDate() { return currentDate; }
    public GameSpeed getGameSpeed() { return gameSpeed; }
    public boolean isRunning() { return isRunning; }
    public Consumer<GameEngine> getUIUpdateCallback() { return uiUpdateCallback; }
    
    // Convenience methods for country access
    public List<Country> getAllCountries() {
        return worldMap.getAllCountries();
    }
    
    public Country getCountry(String name) {
        return worldMap.getCountry(name);
    }
} 