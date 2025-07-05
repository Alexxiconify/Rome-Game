package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class MapPanel extends JPanel {
    private GameEngine engine;
    private double zoom = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean isDragging = false;
    private Point lastMousePos;
    
    public MapPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        setupMouseListeners();
    }
    
    private void setupPanel() {
        setPreferredSize(new Dimension(1000, 600));
        setBackground(new Color(50, 100, 150)); // Ocean blue
        setFocusable(true);
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
        // Simplified province finding - in a real game this would use proper map data
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            // Convert lat/lon to screen coordinates
            int screenX = (int)((province.getLongitude() + 180) * 2.78);
            int screenY = (int)((90 - province.getLatitude()) * 3.33);
            
            if (Math.abs(screenX - x) < 20 && Math.abs(screenY - y) < 20) {
                return province;
            }
        }
        return null;
    }
    
    private void showProvinceInfo(Province province) {
        String info = String.format("Province: %s\nOwner: %s\nPopulation: %d\nDevelopment: %.1f",
                province.getName(), province.getOwner(), province.getPopulation(), province.getDevelopment());
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
        // Draw basic world map outline
        g2d.setColor(new Color(100, 150, 100)); // Land color
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw ocean
        g2d.setColor(new Color(50, 100, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
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
        
        // Draw province as a circle
        Color provinceColor = getProvinceColor(province);
        g2d.setColor(provinceColor);
        g2d.fillOval(x - 10, y - 10, 20, 20);
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x - 10, y - 10, 20, 20);
        
        // Draw capital indicator
        if (province.isCapital()) {
            g2d.setColor(Color.YELLOW);
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
    }
    
    private Color getProvinceColor(Province province) {
        // Assign colors based on country
        return switch (province.getOwner()) {
            case "France" -> Color.BLUE;
            case "England" -> Color.RED;
            case "Castile" -> Color.YELLOW;
            case "Brandenburg" -> Color.GRAY;
            case "Ottomans" -> Color.GREEN;
            case "Ming" -> Color.ORANGE;
            case "Muscovy" -> Color.DARK_GRAY;
            default -> Color.LIGHT_GRAY;
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
    }
} 