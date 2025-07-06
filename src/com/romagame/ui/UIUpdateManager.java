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
    
    public void scheduleUpdate() {
        updateScheduled.set(true);
    }
    
    private void updateUI() {
        // Update all UI components
        updateMainWindow();
        updateMapPanel();
        updateControlPanel();
        updateInfoPanels();
    }
    
    private void updateMainWindow() {
        // Update main window components (date, speed, etc.)
        SwingUtilities.invokeLater(() -> {
            // This will be called from the EDT
            // Update main window components here
        });
    }
    
    private void updateMapPanel() {
        // Update map panel (repaint if needed)
        SwingUtilities.invokeLater(() -> {
            // Update map panel components
        });
    }
    
    private void updateControlPanel() {
        // Update control panel components
        SwingUtilities.invokeLater(() -> {
            // Update control panel components
        });
    }
    
    private void updateInfoPanels() {
        // Update all info panels (economy, military, etc.)
        SwingUtilities.invokeLater(() -> {
            // Update info panels
        });
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