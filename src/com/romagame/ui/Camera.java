package com.romagame.ui;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Modern camera system for handling viewport transformations, zooming, and panning.
 * Provides smooth camera movement with proper bounds checking and coordinate transformations.
 */
public class Camera {
    // Camera position (center of viewport in map coordinates)
    private double centerX = 0.0;
    private double centerY = 0.0;
    
    // Zoom level (1.0 = 100%, 2.0 = 200%, etc.)
    private double zoom = 1.0;
    
    // Zoom constraints
    private static final double MIN_ZOOM = 1.0;
    private static final double MAX_ZOOM = 2.0;
    
    // Map bounds (set when map is loaded)
    private int mapWidth = 0;
    private int mapHeight = 0;
    
    // Viewport dimensions
    private int viewportWidth = 1600;
    private int viewportHeight = 900;
    
    // Smooth movement settings
    private boolean smoothMovement = true;
    private double movementSpeed = 0.1; // For smooth transitions
    
    // Target position for smooth movement
    private double targetZoom = 1.0;
    
    /**
     * Initialize camera with map dimensions
     */
    public void setMapDimensions(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
        
        // Center on map initially
        centerOnMapCenter();
    }
    
    /**
     * Set viewport dimensions
     */
    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
    }
    
    /**
     * Get current camera center position
     */
    public Point getCenter() {
        return new Point((int)centerX, (int)centerY);
    }
    
    /**
     * Get current zoom level
     */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * Set zoom level with bounds checking
     */
    public void setZoom(double newZoom) {
        this.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));
        this.targetZoom = this.zoom;
        constrainToBounds();
    }
    
    /**
     * Zoom in/out by a factor
     */
    public void zoomBy(double factor) {
        setZoom(zoom * factor);
    }
    
    /**
     * Zoom to a specific point (mouse wheel zoom)
     */
    public void zoomToPoint(double mapX, double mapY, double zoomFactor) {
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * zoomFactor));
        
        // Calculate new center to keep the point under mouse
        double zoomRatio = newZoom / zoom;
        double newCenterX = mapX - (mapX - centerX) * zoomRatio;
        double newCenterY = mapY - (mapY - centerY) * zoomRatio;
        
        this.zoom = newZoom;
        this.targetZoom = newZoom;
        this.centerX = newCenterX;
        this.centerY = newCenterY;
        
        constrainToBounds();
    }
    
    /**
     * Center camera on map center
     */
    public void centerOnMapCenter() {
        if (mapWidth > 0 && mapHeight > 0) {
            centerX = mapWidth / 2.0;
            centerY = mapHeight / 2.0;
            constrainToBounds();
        }
    }
    
    /**
     * Center camera on specific coordinates
     */
    public void centerOn(double mapX, double mapY) {
        centerX = mapX;
        centerY = mapY;
        constrainToBounds();
    }
    
    /**
     * Move camera by delta amount
     */
    public void moveBy(double deltaX, double deltaY) {
        centerX += deltaX / zoom;
        centerY += deltaY / zoom;
        constrainToBounds();
    }
    
    /**
     * Smoothly move to target position
     */
    public void moveTo(double mapX, double mapY) {
        if (!smoothMovement) {
        }
        constrainToBounds();
    }
    
    /**
     * Update smooth movement (call in game loop)
     */
    public void update() {
        if (smoothMovement) {
            // Smooth movement towards target
            centerX +=  centerX * movementSpeed;
            centerY +=  centerY * movementSpeed;
            zoom += (targetZoom - zoom) * movementSpeed;
            
            // Stop if very close to target
            if (Math.abs(targetZoom - zoom) < 0.001) zoom = targetZoom;
        }
    }
    
    /**
     * Constrain camera position to map bounds
     */
    private void constrainToBounds() {
        if (mapWidth <= 0 || mapHeight <= 0) return;
        
        double halfViewportWidth = viewportWidth / (2.0 * zoom);
        double halfViewportHeight = viewportHeight / (2.0 * zoom);
        
        // Constrain X
        if (centerX < halfViewportWidth) {
            centerX = halfViewportWidth;
        } else if (centerX > mapWidth - halfViewportWidth) {
            centerX = mapWidth - halfViewportWidth;
        }
        
        // Constrain Y
        if (centerY < halfViewportHeight) {
            centerY = halfViewportHeight;
        } else if (centerY > mapHeight - halfViewportHeight) {
            centerY = mapHeight - halfViewportHeight;
        }
    }
    
    /**
     * Convert map coordinates to screen coordinates
     */
    public Point mapToScreen(double mapX, double mapY) {
        int screenX = (int)((mapX - centerX) * zoom + viewportWidth / 2.0);
        int screenY = (int)((mapY - centerY) * zoom + viewportHeight / 2.0);
        return new Point(screenX, screenY);
    }
    
    /**
     * Convert screen coordinates to map coordinates
     */
    public Point screenToMap(int screenX, int screenY) {
        double mapX = (screenX - viewportWidth / 2.0) / zoom + centerX;
        double mapY = (screenY - viewportHeight / 2.0) / zoom + centerY;
        return new Point((int)mapX, (int)mapY);
    }
    
    /**
     * Get the visible map rectangle
     */
    public Rectangle getVisibleMapRect() {
        Point topLeft = screenToMap(0, 0);
        Point bottomRight = screenToMap(viewportWidth, viewportHeight);
        
        return new Rectangle(
            topLeft.x, topLeft.y,
            bottomRight.x - topLeft.x,
            bottomRight.y - topLeft.y
        );
    }
    
    /**
     * Get camera transform for rendering
     */
    public java.awt.geom.AffineTransform getTransform() {
        java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
        // Translate to center the viewport
        transform.translate(viewportWidth / 2.0, viewportHeight / 2.0);
        // Scale by zoom
        transform.scale(zoom, zoom);
        // Translate by camera center (negative because we want to move the map, not the viewport)
        transform.translate(-centerX, -centerY);
        return transform;
    }
    
    /**
     * Check if a point is visible in the current viewport
     */
    public boolean isPointVisible(double mapX, double mapY) {
        Rectangle visible = getVisibleMapRect();
        return visible.contains(mapX, mapY);
    }
    
    /**
     * Get camera state for debugging
     */
    public String getDebugInfo() {
        return String.format("Camera: center=(%.1f, %.1f), zoom=%.2f, viewport=%dx%d", 
                           centerX, centerY, zoom, viewportWidth, viewportHeight);
    }
} 