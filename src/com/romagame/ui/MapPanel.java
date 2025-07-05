package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {
    private GameEngine engine;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean isDragging = false;
    private Point lastMousePos;
    private BufferedImage mapBackground;
    private boolean mapLoaded = false;
    private BufferedImage provinceMask;
    private Map<Integer, String> colorToProvinceId = new HashMap<>(); // ARGB -> provinceId
    
    public MapPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        loadMapBackground();
        loadProvinceMask();
        setupMouseListeners();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(1000, 600));
        setBackground(new Color(50, 100, 150)); // Ocean blue
        setFocusable(true);
    }
    
    private void loadMapBackground() {
        try {
            // Try to load a map background image
            File mapFile = new File("resources/map_background.png");
            if (mapFile.exists()) {
                mapBackground = ImageIO.read(mapFile);
                mapLoaded = true;
            } else {
                // Create a simple map background if no image is available
                createSimpleMapBackground();
            }
        } catch (IOException e) {
            System.out.println("Could not load map background, using simple background");
            createSimpleMapBackground();
        }
    }
    
    private void createSimpleMapBackground() {
        // Create a simple world map background
        mapBackground = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = mapBackground.createGraphics();
        
        // Draw ocean
        g2d.setColor(new Color(50, 100, 150));
        g2d.fillRect(0, 0, 1000, 600);
        
        // Draw continents (simplified)
        g2d.setColor(new Color(100, 150, 100));
        
        // Europe
        g2d.fillRect(400, 150, 200, 150);
        
        // Africa
        g2d.fillRect(450, 300, 150, 200);
        
        // Asia
        g2d.fillRect(600, 100, 300, 250);
        
        // Middle East
        g2d.fillRect(550, 200, 100, 100);
        
        // Add some detail
        g2d.setColor(new Color(80, 120, 80));
        g2d.fillRect(420, 170, 160, 110); // More detailed Europe
        
        g2d.dispose();
        mapLoaded = true;
    }
    
    private void loadProvinceMask() {
        try {
            File maskFile = new File("resources/qbam_province_mask.png");
            if (maskFile.exists()) {
                provinceMask = ImageIO.read(maskFile);
                // TODO: Populate colorToProvinceId from a data file or hardcoded mapping
            }
        } catch (IOException e) {
            System.out.println("Could not load province mask image");
        }
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isDragging = true;
                    lastMousePos = e.getPoint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isDragging = false;
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                handleProvinceClick(e.getPoint());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && lastMousePos != null) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;
                    offsetX += dx;
                    offsetY += dy;
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });
        
        addMouseWheelListener(e -> {
            double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;
            zoom = Math.max(0.1, Math.min(5.0, zoom * zoomFactor));
            repaint();
        });
    }
    
    private void handleProvinceClick(Point p) {
        // Convert screen coordinates to map coordinates
        int mapX = (int)((p.x - offsetX) / zoom);
        int mapY = (int)((p.y - offsetY) / zoom);
        
        // Find province at this location (simplified)
        Province clickedProvince = findProvinceAt(mapX, mapY);
        if (clickedProvince != null) {
            showProvinceInfo(clickedProvince);
        }
    }
    
    private Province findProvinceAt(int x, int y) {
        // Use province mask if available
        if (provinceMask != null && x >= 0 && y >= 0 && x < provinceMask.getWidth() && y < provinceMask.getHeight()) {
            int argb = provinceMask.getRGB(x, y);
            String provinceId = colorToProvinceId.get(argb);
            if (provinceId != null) {
                return engine.getWorldMap().getProvince(provinceId);
            }
        }
        // Fallback: old method
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            int screenX = (int)((province.getLongitude() + 180) * 2.78);
            int screenY = (int)((90 - province.getLatitude()) * 3.33);
            if (Math.abs(screenX - x) < 20 && Math.abs(screenY - y) < 20) {
                return province;
            }
        }
        return null;
    }
    
    private void showProvinceInfo(Province province) {
        String info = String.format("Province: %s\nOwner: %s\nPopulation: %d\nDevelopment: %.1f\nTerrain: %s\nTrade Goods: %s",
                province.getName(), province.getOwner(), province.getPopulation(), 
                province.getDevelopment(), province.getTerrain(), 
                String.join(", ", province.getTradeGoods()));
        JOptionPane.showMessageDialog(this, info, "Province Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply zoom and offset
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoom, zoom);
        
        drawMap(g2d);
        drawProvinces(g2d);
        drawUI(g2d);
    }
    
    private void drawMap(Graphics2D g2d) {
        if (mapLoaded && mapBackground != null) {
            // Draw the map background
            g2d.drawImage(mapBackground, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback to simple background
            g2d.setColor(new Color(50, 100, 150)); // Ocean color
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw simple land masses
            g2d.setColor(new Color(100, 150, 100)); // Land color
            g2d.fillRect(400, 150, 200, 150); // Europe
            g2d.fillRect(450, 300, 150, 200); // Africa
            g2d.fillRect(600, 100, 300, 250); // Asia
            g2d.fillRect(550, 200, 100, 100); // Middle East
        }
    }
    
    private void drawProvinces(Graphics2D g2d) {
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            drawProvince(g2d, province);
        }
    }
    
    private void drawProvince(Graphics2D g2d, Province province) {
        // Convert lat/lon to screen coordinates
        int x = (int)((province.getLongitude() + 180) * 2.78);
        int y = (int)((90 - province.getLatitude()) * 3.33);
        
        // Draw province as a circle with better styling
        Color provinceColor = getProvinceColor(province);
        g2d.setColor(provinceColor);
        g2d.fillOval(x - 8, y - 8, 16, 16);
        
        // Draw border - thicker for better visibility
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - 8, y - 8, 16, 16);
        
        // Draw capital indicator
        if (province.isCapital()) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x - 3, y - 3, 6, 6);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - 3, y - 3, 6, 6);
        }
        
        // Draw province name for capitals and major provinces
        if (province.isCapital() || province.getDevelopment() > 5) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString(province.getName(), x + 10, y);
        }
        
        // Draw colonization indicator for uncolonized provinces
        if (province.getOwner().equals("Uninhabited")) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 8));
            g2d.drawString("?", x + 5, y + 5);
        }
        
        // Draw colonization progress for provinces being colonized
        var colonizationManager = engine.getColonizationManager();
        for (var mission : colonizationManager.getActiveMissions()) {
            if (mission.getProvinceId().equals(province.getId())) {
                // Draw progress ring
                g2d.setColor(new Color(255, 255, 0, 150)); // Semi-transparent yellow
                g2d.setStroke(new BasicStroke(3));
                int progressAngle = (int)(mission.getProgress() * 360);
                g2d.drawArc(x - 10, y - 10, 20, 20, 90, progressAngle);
                
                // Draw progress text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 8));
                g2d.drawString(String.format("%.0f%%", mission.getProgress() * 100), x - 5, y + 20);
                break;
            }
        }
    }
    
    private Color getProvinceColor(Province province) {
        // Assign colors based on country for 117 AD - matching Q-BAM style
        return switch (province.getOwner()) {
            case "Roman Empire" -> new Color(255, 100, 100); // Bright Red
            case "Parthia" -> new Color(100, 255, 100); // Bright Green
            case "Armenia" -> new Color(100, 100, 255); // Bright Blue
            case "Dacia" -> new Color(255, 255, 100); // Bright Yellow
            case "Sarmatia" -> new Color(255, 100, 255); // Bright Magenta
            case "Quadi", "Marcomanni", "Suebi", "Alemanni", "Chatti", "Cherusci", "Hermunduri", "Frisians" -> new Color(180, 180, 180); // Light Gray
            case "Britons", "Caledonians", "Hibernians", "Picts", "Scoti" -> new Color(120, 180, 120); // Forest Green
            case "Garamantes", "Nubia", "Axum" -> new Color(180, 120, 60); // Brown
            case "Himyar", "Saba", "Hadramaut", "Oman" -> new Color(255, 180, 100); // Orange
            case "Kushan", "Indo-Parthian" -> new Color(100, 255, 255); // Bright Cyan
            case "Iberia", "Albania", "Lazica", "Colchis" -> new Color(150, 150, 255); // Light Blue
            case "Uninhabited" -> new Color(80, 80, 80); // Dark Gray for uncolonized
            default -> new Color(140, 140, 140); // Medium Gray
        };
    }
    
    private void drawUI(Graphics2D g2d) {
        // Reset transform for UI elements
        g2d.setTransform(new AffineTransform());
        
        // Draw zoom level
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(String.format("Zoom: %.1fx", zoom), 10, 20);
        
        // Draw current date
        g2d.drawString("Date: " + engine.getCurrentDate().getFormattedDate(), 10, 40);
        
        // Draw game speed
        g2d.drawString("Speed: " + engine.getGameSpeed().getDisplayName(), 10, 60);
        
        // Draw selected country
        if (engine.getCountryManager().getPlayerCountry() != null) {
            g2d.drawString("Playing as: " + engine.getCountryManager().getPlayerCountry().getName(), 10, 80);
        }
    }
} 