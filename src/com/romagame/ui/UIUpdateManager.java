package com.romagame.ui;

import com.romagame.core.GameEngine;
import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class UIUpdateManager {
    public final GameEngine engine;
    private final AtomicBoolean updateScheduled;
    private final AtomicLong lastUpdateTime;
    private final Timer updateTimer;
    private GameWindow gameWindow;
    
    // UI update interval (in milliseconds)
    private static final long UI_UPDATE_INTERVAL = 50; // 20 FPS for UI updates
    
    public UIUpdateManager(GameEngine engine) {
        this.engine = engine;
        this.updateScheduled = new AtomicBoolean(false);
        this.lastUpdateTime = new AtomicLong(System.currentTimeMillis());
        
        // Create timer for UI updates
        this.updateTimer = new Timer((int)UI_UPDATE_INTERVAL, e -> {
            if (updateScheduled.get()) {
                updateUI();
                updateScheduled.set(false);
            }
        });
        updateTimer.start();
    }
    
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }
    
    public void scheduleUpdate() {
        updateScheduled.set(true);
    }
    
    private void updateUI() {
        if (gameWindow != null) {
            SwingUtilities.invokeLater(() -> {
                gameWindow.updateUI();
                lastUpdateTime.set(System.currentTimeMillis());
            });
        }
    }
    
    public void stop() {
        updateTimer.stop();
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime.get();
    }
    
    public boolean isUpdateScheduled() {
        return updateScheduled.get();
    }
}