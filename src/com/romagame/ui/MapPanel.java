package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

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
    private BufferedImage provinceColorMap = null;
    private BufferedImage borderOverlay = null;
    private Map<Integer, String> colorToProvinceId = new HashMap<>(); // ARGB -> provinceId
    private Point mouseMapPoint = null;
    private String hoveredProvinceId = null;

    public MapPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        loadMapBackground();
        loadProvinceMask();
        setupMouseListeners();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(1200, 700));
        setBackground(new Color(40, 70, 120)); // Deep blue
        setFocusable(true);
    }

    private void loadMapBackground() {
        try {
            File mapFile = new File("resources/map_background.png");
            if (mapFile.exists()) {
                mapBackground = ImageIO.read(mapFile);
                mapLoaded = true;
            } else {
                createGradientBackground();
            }
        } catch (IOException e) {
            createGradientBackground();
        }
    }

    private void createGradientBackground() {
        mapBackground = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = mapBackground.createGraphics();
        // Deep blue gradient for ocean
        GradientPaint gp = new GradientPaint(0, 0, new Color(24, 38, 66), 0, 700, new Color(10, 18, 36));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 1200, 700);
        // Optional: add subtle noise/texture overlay for realism
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
        for (int y = 0; y < 700; y += 2) {
            for (int x = 0; x < 1200; x += 2) {
                int v = (int)(Math.random() * 16);
                g2d.setColor(new Color(24+v, 38+v, 66+v));
                g2d.fillRect(x, y, 2, 2);
            }
        }
        g2d.dispose();
        mapLoaded = true;
    }

    private void loadProvinceMask() {
        try {
            File maskFile = new File("province_mask.png");
            if (maskFile.exists()) {
                provinceMask = ImageIO.read(maskFile);
                loadColorToProvinceId();
                updateProvinceColorMap();
                updateBorderOverlay();
            }
        } catch (IOException e) {
            System.out.println("Could not load province mask image");
        }
    }

    private void loadColorToProvinceId() {
        try (BufferedReader br = new BufferedReader(new FileReader("resources/province_color_map.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
                }
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        // Parse as long first, then convert to int
                        long longValue = Long.parseLong(parts[0].trim());
                        int argb = (int) longValue;
                        String provinceId = parts[1].trim();
                        colorToProvinceId.put(argb, provinceId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ARGB value in mapping: " + parts[0]);
                    }
                }
            }
            System.out.println("Loaded " + colorToProvinceId.size() + " province color mappings");
        } catch (IOException e) {
            System.out.println("Could not load province color map: " + e.getMessage());
        }
    }

    private void updateProvinceColorMap() {
        if (provinceMask == null) return;
        provinceColorMap = new BufferedImage(
            provinceMask.getWidth(), provinceMask.getHeight(), BufferedImage.TYPE_INT_ARGB
        );
        Map<Integer, Color> maskColorToOwnerColor = new HashMap<>();
        for (int y = 0; y < provinceMask.getHeight(); y++) {
            for (int x = 0; x < provinceMask.getWidth(); x++) {
                int argb = provinceMask.getRGB(x, y);
                if (argb == 0xFF000000) continue; // skip black background
                if (!maskColorToOwnerColor.containsKey(argb)) {
                    String provinceId = colorToProvinceId.get(argb);
                    if (provinceId != null) {
                        Province province = engine.getWorldMap().getProvince(provinceId);
                        if (province != null) {
                            maskColorToOwnerColor.put(argb, getProvinceColor(province));
                        }
                    }
                }
                Color fill = maskColorToOwnerColor.get(argb);
                if (fill != null) {
                    provinceColorMap.setRGB(x, y, fill.getRGB());
                }
            }
        }
    }

    private void updateBorderOverlay() {
        if (provinceMask == null) return;
        // Use edge detection to highlight province borders (white, strong)
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
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMapPoint = screenToMap(e.getPoint());
                hoveredProvinceId = getProvinceIdAt(mouseMapPoint);
                repaint();
            }
        });
        addMouseWheelListener(e -> {
            double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;
            zoom = Math.max(0.1, Math.min(5.0, zoom * zoomFactor));
            repaint();
        });
    }

    private Point screenToMap(Point p) {
        int panelW = getWidth();
        int panelH = getHeight();
        int imgW = provinceMask.getWidth();
        int imgH = provinceMask.getHeight();
        double scale = Math.min(panelW / (double)imgW, panelH / (double)imgH) * zoom;
        int drawW = (int)(imgW * scale);
        int drawH = (int)(imgH * scale);
        int x0 = (panelW - drawW) / 2 + offsetX;
        int y0 = (panelH - drawH) / 2 + offsetY;
        int mapX = (int)((p.x - x0) / scale);
        int mapY = (int)((p.y - y0) / scale);
        return new Point(mapX, mapY);
    }

    private String getProvinceIdAt(Point mapPoint) {
        if (provinceMask == null || mapPoint == null) return null;
        int x = mapPoint.x, y = mapPoint.y;
        if (x < 0 || y < 0 || x >= provinceMask.getWidth() || y >= provinceMask.getHeight()) return null;
        int argb = provinceMask.getRGB(x, y);
        return colorToProvinceId.get(argb);
    }

    private void handleProvinceClick(Point p) {
        Point mapPoint = screenToMap(p);
        String provinceId = getProvinceIdAt(mapPoint);
        if (provinceId != null) {
            Province clickedProvince = engine.getWorldMap().getProvince(provinceId);
            if (clickedProvince != null) {
                showProvinceInfo(clickedProvince);
            }
        }
    }

    private void showProvinceInfo(Province province) {
        String info = String.format("<html><b>Province:</b> %s<br><b>Owner:</b> %s<br><b>Population:</b> %d<br><b>Development:</b> %.1f<br><b>Terrain:</b> %s<br><b>Trade Goods:</b> %s</html>",
                province.getName(), province.getOwner(), province.getPopulation(),
                province.getDevelopment(), province.getTerrain(),
                String.join(", ", province.getTradeGoods()));
        JOptionPane.showMessageDialog(this, info, "Province Information", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw background
        if (mapLoaded && mapBackground != null) {
            g2d.drawImage(mapBackground, 0, 0, getWidth(), getHeight(), null);
        }
        // Draw provinces (scaled)
        if (provinceColorMap != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = provinceColorMap.getWidth();
            int imgH = provinceColorMap.getHeight();
            double scale = Math.min(panelW / (double)imgW, panelH / (double)imgH) * zoom;
            int drawW = (int)(imgW * scale);
            int drawH = (int)(imgH * scale);
            int x = (panelW - drawW) / 2 + offsetX;
            int y = (panelH - drawH) / 2 + offsetY;
            g2d.drawImage(provinceColorMap, x, y, drawW, drawH, null);
        }
        // Draw borders overlay (white, strong)
        if (borderOverlay != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = borderOverlay.getWidth();
            int imgH = borderOverlay.getHeight();
            double scale = Math.min(panelW / (double)imgW, panelH / (double)imgH) * zoom;
            int drawW = (int)(imgW * scale);
            int drawH = (int)(imgH * scale);
            int x = (panelW - drawW) / 2 + offsetX;
            int y = (panelH - drawH) / 2 + offsetY;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
            g2d.drawImage(borderOverlay, x, y, drawW, drawH, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        // Add subtle coast glow (land/ocean boundary)
        if (provinceColorMap != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = provinceColorMap.getWidth();
            int imgH = provinceColorMap.getHeight();
            double scale = Math.min(panelW / (double)imgW, panelH / (double)imgH) * zoom;
            int drawW = (int)(imgW * scale);
            int drawH = (int)(imgH * scale);
            int x0 = (panelW - drawW) / 2 + offsetX;
            int y0 = (panelH - drawH) / 2 + offsetY;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
            g2d.setColor(new Color(180, 220, 255));
            for (int y = 1; y < imgH-1; y++) {
                for (int x = 1; x < imgW-1; x++) {
                    int argb = provinceColorMap.getRGB(x, y);
                    if (argb != 0 && (
                        provinceColorMap.getRGB(x-1, y) == 0 || provinceColorMap.getRGB(x+1, y) == 0 ||
                        provinceColorMap.getRGB(x, y-1) == 0 || provinceColorMap.getRGB(x, y+1) == 0)) {
                        g2d.fillRect(x0 + (int)(x * scale), y0 + (int)(y * scale), (int)Math.ceil(scale), (int)Math.ceil(scale));
                    }
                }
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        // Highlight hovered province
        if (hoveredProvinceId != null && provinceMask != null) {
            Province hovered = engine.getWorldMap().getProvince(hoveredProvinceId);
            if (hovered != null) {
                int panelW = getWidth();
                int panelH = getHeight();
                int imgW = provinceMask.getWidth();
                int imgH = provinceMask.getHeight();
                double scale = Math.min(panelW / (double)imgW, panelH / (double)imgH) * zoom;
                int drawW = (int)(imgW * scale);
                int drawH = (int)(imgH * scale);
                int x0 = (panelW - drawW) / 2 + offsetX;
                int y0 = (panelH - drawH) / 2 + offsetY;
                int highlightColor = 0x66FFFFFF;
                for (int y = 0; y < imgH; y++) {
                    for (int x = 0; x < imgW; x++) {
                        int argb = provinceMask.getRGB(x, y);
                        if (colorToProvinceId.get(argb) != null && colorToProvinceId.get(argb).equals(hoveredProvinceId)) {
                            g2d.setColor(new Color(highlightColor, true));
                            g2d.fillRect(x0 + (int)(x * scale), y0 + (int)(y * scale), (int)Math.ceil(scale), (int)Math.ceil(scale));
                        }
                    }
                }
                // Draw tooltip
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2d.setColor(new Color(30, 30, 30, 220));
                g2d.fillRoundRect(20, getHeight() - 60, 320, 40, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Province: " + hovered.getName() + " | Owner: " + hovered.getOwner(), 30, getHeight() - 35);
            }
        }
        drawUI(g2d);
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
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRoundRect(8, 8, 260, 110, 16, 16);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(8, 8, 260, 110, 16, 16);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("Zoom: %.1fx", zoom), 20, 35);
        g2d.drawString("Date: " + engine.getCurrentDate().getFormattedDate(), 20, 60);
        g2d.drawString("Speed: " + engine.getGameSpeed().getDisplayName(), 20, 85);
        if (engine.getCountryManager().getPlayerCountry() != null) {
            g2d.drawString("Playing as: " + engine.getCountryManager().getPlayerCountry().getName(), 20, 110);
        }
    }
} 