package com.romagame.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Optimized map renderer with caching, dirty rectangle management, and performance optimizations.
 * Separates rendering logic from UI logic for better maintainability and performance.
 */
public class MapRenderer {
    // Core rendering components
    private BufferedImage mapBackground;
    private BufferedImage borderlessOverlay;
    private BufferedImage provinceMask;
    private BufferedImage borderOverlay;
    private BufferedImage landShading;
    
    // Caching system
    private Map<String, BufferedImage> renderCache = new HashMap<>();
    private Map<String, Object> cacheKeys = new HashMap<>();
    private Rectangle lastVisibleRect = new Rectangle();
    private double lastZoom = 1.0;
    
    // Performance settings
    private static final int MAX_CACHE_SIZE = 50;
    private static final boolean ENABLE_CACHING = true;
    private static final boolean ENABLE_DIRTY_RECTANGLES = true;
    
    // Rendering quality settings
    private RenderingHints renderingHints;
    
    /**
     * Initialize renderer with rendering hints for quality
     */
    public MapRenderer() {
        setupRenderingHints();
    }
    
    /**
     * Setup high-quality rendering hints
     */
    private void setupRenderingHints() {
        renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    /**
     * Set map background image
     */
    public void setMapBackground(BufferedImage background) {
        this.mapBackground = background;
        clearCache();
    }
    
    /**
     * Set borderless overlay
     */
    public void setBorderlessOverlay(BufferedImage overlay) {
        this.borderlessOverlay = overlay;
        clearCache();
    }
    
    /**
     * Set province mask
     */
    public void setProvinceMask(BufferedImage mask) {
        this.provinceMask = mask;
        updateBorderOverlay();
        updateLandShading();
        clearCache();
    }
    
    /**
     * Update border overlay from province mask
     */
    private void updateBorderOverlay() {
        if (provinceMask == null) return;
        
        // Use edge detection to highlight province borders
        float[] kernel = {
            -1, -1, -1,
            -1,  8, -1,
            -1, -1, -1
        };
        ConvolveOp edgeOp = new ConvolveOp(new Kernel(3, 3, kernel));
        BufferedImage edge = edgeOp.filter(provinceMask, null);
        
        // Convert all non-black pixels to white for clear borders
        borderOverlay = new BufferedImage(edge.getWidth(), edge.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < edge.getHeight(); y++) {
            for (int x = 0; x < edge.getWidth(); x++) {
                int v = edge.getRGB(x, y) & 0xFFFFFF;
                if (v != 0) {
                    borderOverlay.setRGB(x, y, 0xFFFFFFFF); // white
                } else {
                    borderOverlay.setRGB(x, y, 0x00000000); // transparent
                }
            }
        }
    }
    
    /**
     * Update land shading overlay
     */
    private void updateLandShading() {
        if (provinceMask == null) return;
        
        int w = provinceMask.getWidth(), h = provinceMask.getHeight();
        landShading = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = landShading.createGraphics();
        
        // Simple radial gradient for land relief
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = provinceMask.getRGB(x, y);
                if (argb != 0xFF000000) {
                    double dx = (x - w/2) / (double)w;
                    double dy = (y - h/2) / (double)h;
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    int shade = (int)(60 * (1.0 - dist));
                    int c = Math.max(0, Math.min(255, 180 + shade));
                    landShading.setRGB(x, y, new Color(c, c, c, 40).getRGB());
                }
            }
        }
        g2.dispose();
    }
    
    /**
     * Render the map with camera transform
     */
    public void render(Graphics2D g2d, Camera camera, Rectangle clipBounds) {
        // Apply rendering hints
        g2d.setRenderingHints(renderingHints);
        
        // Get camera transform
        AffineTransform transform = camera.getTransform();
        g2d.setTransform(transform);
        
        // Get visible rectangle
        Rectangle visibleRect = camera.getVisibleMapRect();
        
        // Check if we need to redraw (dirty rectangle optimization)
        if (ENABLE_DIRTY_RECTANGLES && !hasVisibleRectChanged(visibleRect, camera.getZoom())) {
            // Use cached rendering if available
            BufferedImage cached = getCachedRender(visibleRect, camera.getZoom());
            if (cached != null) {
                g2d.drawImage(cached, 0, 0, null);
                return;
            }
        }
        
        // Perform full render
        renderMapLayers(g2d, visibleRect);
        
        // Cache the result
        if (ENABLE_CACHING) {
            cacheRender(visibleRect, camera.getZoom());
        }
        
        // Update last visible rectangle
        lastVisibleRect = visibleRect;
        lastZoom = camera.getZoom();
    }
    
    /**
     * Render all map layers
     */
    private void renderMapLayers(Graphics2D g2d, Rectangle visibleRect) {
        // 1. Background
        if (mapBackground != null) {
            g2d.drawImage(mapBackground, 0, 0, null);
        }
        
        // 2. Land shading
        if (landShading != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.drawImage(landShading, 0, 0, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // 3. Border overlay
        if (borderOverlay != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.drawImage(borderOverlay, 0, 0, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // 4. Borderless overlay (if different from background)
        if (borderlessOverlay != null) {
            g2d.drawImage(borderlessOverlay, 0, 0, null);
        }
    }
    
    /**
     * Check if visible rectangle has changed
     */
    private boolean hasVisibleRectChanged(Rectangle newRect, double newZoom) {
        return !lastVisibleRect.equals(newRect) || Math.abs(lastZoom - newZoom) > 0.01;
    }
    
    /**
     * Get cached render for current view
     */
    private BufferedImage getCachedRender(Rectangle visibleRect, double zoom) {
        String key = generateCacheKey(visibleRect, zoom);
        return renderCache.get(key);
    }
    
    /**
     * Cache current render
     */
    private void cacheRender(Rectangle visibleRect, double zoom) {
        String key = generateCacheKey(visibleRect, zoom);
        
        // Create cached image
        BufferedImage cached = new BufferedImage(
            visibleRect.width, visibleRect.height, 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = cached.createGraphics();
        g2d.setRenderingHints(renderingHints);
        
        // Render to cache
        renderMapLayers(g2d, visibleRect);
        g2d.dispose();
        
        // Store in cache
        renderCache.put(key, cached);
        cacheKeys.put(key, new Object());
        
        // Limit cache size
        if (renderCache.size() > MAX_CACHE_SIZE) {
            clearOldestCache();
        }
    }
    
    /**
     * Generate cache key for current view
     */
    private String generateCacheKey(Rectangle visibleRect, double zoom) {
        return String.format("%d,%d,%d,%d,%.2f", 
            visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height, zoom);
    }
    
    /**
     * Clear oldest cache entries
     */
    private void clearOldestCache() {
        if (renderCache.size() <= MAX_CACHE_SIZE / 2) return;
        
        // Simple LRU: remove oldest entries
        int toRemove = renderCache.size() - MAX_CACHE_SIZE / 2;
        List<String> keys = new ArrayList<>(renderCache.keySet());
        
        for (int i = 0; i < toRemove && i < keys.size(); i++) {
            String key = keys.get(i);
            renderCache.remove(key);
            cacheKeys.remove(key);
        }
    }
    
    /**
     * Clear all cache
     */
    public void clearCache() {
        renderCache.clear();
        cacheKeys.clear();
    }
    
    /**
     * Get province color at specific coordinates
     */
    public Color getProvinceColor(int x, int y) {
        if (provinceMask == null || x < 0 || y < 0 || 
            x >= provinceMask.getWidth() || y >= provinceMask.getHeight()) {
            return null;
        }
        
        int rgb = provinceMask.getRGB(x, y);
        if (rgb == 0xFF000000) { // Black = ocean
            return null;
        }
        
        return new Color(rgb);
    }
    
    /**
     * Check if coordinates are on land
     */
    public boolean isLand(int x, int y) {
        if (provinceMask == null || x < 0 || y < 0 || 
            x >= provinceMask.getWidth() || y >= provinceMask.getHeight()) {
            return false;
        }
        
        int rgb = provinceMask.getRGB(x, y);
        return rgb != 0xFF000000; // Not black = land
    }
    
    /**
     * Get map dimensions
     */
    public Dimension getMapDimensions() {
        if (mapBackground == null) {
            return new Dimension(0, 0);
        }
        return new Dimension(mapBackground.getWidth(), mapBackground.getHeight());
    }
    
    /**
     * Dispose resources
     */
    public void dispose() {
        clearCache();
        mapBackground = null;
        borderlessOverlay = null;
        provinceMask = null;
        borderOverlay = null;
        landShading = null;
    }
} 