package com.romagame.core;

public enum GameSpeed {
    PAUSED("Paused"),
    SLOW("Slow"),
    NORMAL("Normal"),
    FAST("Fast"),
    VERY_FAST("Very Fast");
    
    private final String displayName;
    
    GameSpeed(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 