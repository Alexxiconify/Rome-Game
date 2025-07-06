package com.romagame.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameThread extends Thread {
    private final GameEngine engine;
    private final AtomicBoolean running;
    private final AtomicBoolean paused;
    private final AtomicInteger gameSpeed; // 1 = normal, 2 = fast, 3 = very fast
    private final long[] frameTimes;
    private int frameTimeIndex;
    private long lastUpdateTime;
    
    // Game update intervals (in milliseconds)
    private static final long NORMAL_SPEED_INTERVAL = 1000; // 1 second
    private static final long FAST_SPEED_INTERVAL = 500;    // 0.5 seconds
    private static final long VERY_FAST_INTERVAL = 250;     // 0.25 seconds
    
    public GameThread(GameEngine engine) {
        this.engine = engine;
        this.running = new AtomicBoolean(true);
        this.paused = new AtomicBoolean(false);
        this.gameSpeed = new AtomicInteger(1);
        this.frameTimes = new long[60]; // Track last 60 frames for FPS calculation
        this.frameTimeIndex = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        
        setName("GameLogicThread");
        setDaemon(true); // Don't prevent JVM shutdown
    }
    
    public void run() {
        while (running.get()) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;
            
            // Calculate update interval based on game speed
            long updateInterval = getUpdateInterval();
            
            if (deltaTime >= updateInterval && !paused.get()) {
                try {
                    // Update game logic
                    updateGameLogic(deltaTime);
                    
                    // Update frame timing
                    frameTimes[frameTimeIndex] = deltaTime;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                    lastUpdateTime = currentTime;
                    
                } catch (Exception e) {
                    System.err.println("Error in game thread: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Sleep to prevent excessive CPU usage
            try {
                Thread.sleep(Math.max(1, updateInterval / 4)); // Check 4 times per update interval
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void updateGameLogic(long deltaTime) {
        // Update all game systems
        engine.getCountryManager().update();
        engine.getDiplomacyManager().update();
        engine.getEconomyManager().update();
        engine.getMilitaryManager().update();
        engine.getTechnologyManager().update();
        engine.getColonizationManager().update();
        engine.getPopulationManager().update();
        
        // Update historical nation spawning
        engine.updateHistoricalNations();
        
        // Process AI decisions
        engine.getCountryManager().processAI();
        
        // Update game date
        engine.getCurrentDate().advance();
        
        // Trigger UI update events (will be handled on EDT)
        if (engine.getUIUpdateCallback() != null) {
            javax.swing.SwingUtilities.invokeLater(() -> 
                engine.getUIUpdateCallback().accept(engine));
        }
    }
    
    private long getUpdateInterval() {
        return switch (gameSpeed.get()) {
            case 1 -> NORMAL_SPEED_INTERVAL;
            case 2 -> FAST_SPEED_INTERVAL;
            case 3 -> VERY_FAST_INTERVAL;
            default -> NORMAL_SPEED_INTERVAL;
        };
    }
    
    public void pause() {
        paused.set(true);
    }
    
    public void resumeGame() {
        paused.set(false);
    }
    
    public void setGameSpeed(int speed) {
        gameSpeed.set(Math.max(1, Math.min(3, speed)));
    }
    
    public int getGameSpeed() {
        return gameSpeed.get();
    }
    
    public boolean isPaused() {
        return paused.get();
    }
    
    public void stopGame() {
        running.set(false);
        interrupt();
    }
    
    public double getAverageFPS() {
        if (frameTimeIndex == 0) return 0.0;
        
        long totalTime = 0;
        int count = 0;
        for (int i = 0; i < frameTimeIndex; i++) {
            if (frameTimes[i] > 0) {
                totalTime += frameTimes[i];
                count++;
            }
        }
        
        if (count == 0) return 0.0;
        return 1000.0 / (totalTime / (double) count);
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
} 