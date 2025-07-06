package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
import com.romagame.map.ProvinceData;
import com.romagame.map.ProvinceDataLoader;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MapPanel extends JPanel {
    private GameEngine engine;
    private double zoom = 4.0;
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
    private BufferedImage landShading = null;
    private float waterAnimPhase = 0f;
    private javax.swing.Timer waterAnimTimer;
    private Map<String, Color> countryColors = new HashMap<>();
    private javax.swing.Timer repaintTimer;
    private Map<String, Color> provinceIdToOwnerColor = new HashMap<>();
    private String selectedNation = null; // Add this field to track selected nation
    private BufferedImage cachedBorders = null;
    private BufferedImage cachedNationHighlight = null;
    private String cachedNationForHighlight = null;
    private BufferedImage cachedProvinceHighlight = null;
    private String cachedProvinceForHighlight = null;
    
    // Province data from JSON
    private Map<String, ProvinceData> provinceData = new HashMap<>();
    private Map<String, String> maskColorToProvinceId = new HashMap<>();
    private Map<String, String> ownerColorToNation = new HashMap<>();

    // Centralized transform fields
    private double currentScale;
    private int currentOffsetX, currentOffsetY;
    private int mapImgWidth, mapImgHeight;
    
    // Performance optimization fields
    private BufferedImage cachedOceanBackground = null;
    private int lastOceanBgWidth = -1;
    private int lastOceanBgHeight = -1;

    public MapPanel(GameEngine engine) {
        this.engine = engine;
        setupPanel();
        loadProvinceData();
        loadMapBackground();
        loadProvinceOwnerColors();
        loadProvinceMask();
        setupMouseListeners();
        setupWaterAnimation();
        setupRepaintTimer();
        // Center on Buyeo's centroid
        SwingUtilities.invokeLater(() -> centerOnNation("Buyeo"));
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(1600, 900));
        setBackground(new Color(40, 70, 120)); // Deep blue
        setFocusable(true);
        // Enable double buffering for smoother rendering
        setDoubleBuffered(true);
    }

    private void loadMapBackground() {
        try {
            File mapFile = new File("src/resources/map_background.png");
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
        GradientPaint gp = new GradientPaint(0, 0, new Color(24, 38, 66), 0, 700, new Color(10, 18, 36));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, 1200, 700);
        // Optional: add subtle noise/texture overlay for realism
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
        for (int y = 0; y < 700; y += 2) {
            for (int x = 0; x < 1200; x += 2) {
                int v = (int)(Math.random() * 16);
                g2d.setColor(new Color(Math.min(255, 24+v), Math.min(255, 38+v), Math.min(255, 66+v)));
                g2d.fillRect(x, y, 2, 2);
            }
        }
        g2d.dispose();
        mapLoaded = true;
        createLandShading();
    }

    private void createLandShading() {
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

    private void loadProvinceMask() {
        try {
            File maskFile = new File("src/resources/province_mask.png");
            if (maskFile.exists()) {
                provinceMask = ImageIO.read(maskFile);
                loadColorToProvinceId();
                updateProvinceColorMap();
                updateBorderOverlay();
                updateLandShading();
                createLandShading();
                repaint();
            }
        } catch (IOException e) {
            System.out.println("Could not load province mask image");
        }
    }

    private void loadColorToProvinceId() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/resources/province_color_map.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
                }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        long longValue = Long.parseLong(parts[0].trim());
                        int argb = (int) longValue;
                        String provinceId = parts[2].trim();
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

    private void loadProvinceOwnerColors() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/resources/province_ownership_report.csv"))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String provinceId = parts[0].trim();
                    int r = Math.min(255, Math.max(0, Integer.parseInt(parts[6].trim())));
                    int g = Math.min(255, Math.max(0, Integer.parseInt(parts[7].trim())));
                    int b = Math.min(255, Math.max(0, Integer.parseInt(parts[8].trim())));
                    provinceIdToOwnerColor.put(provinceId, new Color(r, g, b));
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load province owner color map: " + e.getMessage());
        }
    }

    private void updateProvinceColorMap() {
        if (provinceMask == null) return;
        int w = provinceMask.getWidth(), h = provinceMask.getHeight();
        provinceColorMap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = provinceMask.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                String maskKey = r + "," + g + "," + b;
                String provinceId = maskColorToProvinceId.get(maskKey);

                if (provinceId != null && provinceData.containsKey(provinceId)) {
                    int[] ownerColor = provinceData.get(provinceId).owner_color;
                    int ownerR = Math.min(255, Math.max(0, ownerColor[0]));
                    int ownerG = Math.min(255, Math.max(0, ownerColor[1]));
                    int ownerB = Math.min(255, Math.max(0, ownerColor[2]));
                    int ownerArgb = (0xFF << 24) | (ownerR << 16) | (ownerG << 8) | ownerB;
                    provinceColorMap.setRGB(x, y, ownerArgb);
                } else {
                    // Transparent for unassigned
                    provinceColorMap.setRGB(x, y, 0x00000000);
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

    private void updateLandShading() {
        if (provinceMask == null) return;
        int w = provinceMask.getWidth(), h = provinceMask.getHeight();
        landShading = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = landShading.createGraphics();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = provinceMask.getRGB(x, y);
                if ((argb & 0xFFFFFF) != 0) {
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
                if (provinceMask == null) return;
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
                    invalidateOverlayCache();
                    // Use repaint() instead of repaint() for smoother dragging
                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                if (provinceMask == null) return;
                Point oldHovered = hoveredProvinceId != null ? new Point(mouseMapPoint) : null;
                mouseMapPoint = screenToMap(e.getPoint());
                hoveredProvinceId = getProvinceIdAt(mouseMapPoint);
                
                // Only repaint if hover state changed
                if ((oldHovered == null) != (hoveredProvinceId == null) || 
                    (oldHovered != null && hoveredProvinceId != null && !oldHovered.equals(mouseMapPoint))) {
                    
                    // Update tooltip with nation name
                    if (hoveredProvinceId != null) {
                        ProvinceData provinceDataItem = provinceData.get(hoveredProvinceId);
                        if (provinceDataItem != null) {
                            setToolTipText("Province: " + hoveredProvinceId + " | Nation: " + provinceDataItem.getNation());
                        } else {
                            setToolTipText("Province: " + hoveredProvinceId);
                        }
                    } else {
                        setToolTipText(null);
                    }
                    
                    repaint();
                }
            }
        });
        addMouseWheelListener(e -> {
            double oldZoom = zoom;
            double zoomFactor = e.getWheelRotation() > 0 ? 0.9 : 1.1;
            zoom = Math.max(0.1, Math.min(40.0, zoom * zoomFactor));
            
            // Only invalidate cache and repaint if zoom actually changed
            if (Math.abs(zoom - oldZoom) > 0.01) {
                invalidateOverlayCache();
                repaint();
            }
        });
    }

    private void setupWaterAnimation() {
        waterAnimTimer = new javax.swing.Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                waterAnimPhase += 0.03f;
                repaint();
            }
        });
        waterAnimTimer.start();
    }

    private void setupRepaintTimer() {

    }

    private void updateCachedBorders(int imgW, int imgH, double scale, int x, int y) {
        if (cachedBorders != null && cachedBorders.getWidth() == getWidth() && cachedBorders.getHeight() == getHeight()) return;
        cachedBorders = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cachedBorders.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setStroke(new BasicStroke((float)Math.max(1.0, scale * 1.1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(100,100,100,200));
        for (int yy = 1; yy < imgH-1; yy++) {
            for (int xx = 1; xx < imgW-1; xx++) {
                int argb = provinceMask.getRGB(xx, yy);
                if (argb != 0xFF000000) {
                    boolean border = false;
                    int[] dx = {-1,1,0,0};
                    int[] dy = {0,0,-1,1};
                    for (int d = 0; d < 4; d++) {
                        int nx = xx+dx[d], ny = yy+dy[d];
                        if (nx < 0 || nx >= imgW || ny < 0 || ny >= imgH) continue;
                        if (provinceMask.getRGB(nx, ny) == 0xFF000000) border = true;
                    }
                    if (border) {
                        g2d.drawLine(x + (int)(xx * scale), y + (int)(yy * scale), x + (int)(xx * scale), y + (int)(yy * scale));
                    }
                }
            }
        }
        g2d.dispose();
    }

    private void updateCachedNationHighlight(String nation, int imgW, int imgH, double scale, int x, int y) {
        if (cachedNationHighlight != null && nation.equals(cachedNationForHighlight) && cachedNationHighlight.getWidth() == getWidth() && cachedNationHighlight.getHeight() == getHeight()) return;
        cachedNationHighlight = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        cachedNationForHighlight = nation;
        Graphics2D g2d = cachedNationHighlight.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
        g2d.setColor(Color.YELLOW);
        for (int yy = 0; yy < imgH; yy++) {
            for (int xx = 0; xx < imgW; xx++) {
                int argb = provinceMask.getRGB(xx, yy) | 0xFF000000;
                String pid = colorToProvinceId.get(argb);
                if (pid != null) {
                    Province p = engine.getWorldMap().getProvince(pid);
                    if (p != null && nation.equals(p.getOwner())) {
                        g2d.fillRect(x + (int)(xx * scale), y + (int)(yy * scale), (int)Math.ceil(scale), (int)Math.ceil(scale));
                    }
                }
            }
        }
        g2d.dispose();
    }

    private void updateCachedProvinceHighlight(String provinceId, int imgW, int imgH, double scale, int x, int y) {
        if (cachedProvinceHighlight != null && provinceId.equals(cachedProvinceForHighlight) && cachedProvinceHighlight.getWidth() == getWidth() && cachedProvinceHighlight.getHeight() == getHeight()) return;
        cachedProvinceHighlight = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        cachedProvinceForHighlight = provinceId;
        Province hovered = engine.getWorldMap().getProvince(provinceId);
        if (hovered == null) return;
        Color glow = getProvinceColor(hovered);
        Graphics2D g2d = cachedProvinceHighlight.createGraphics();
        for (int r = 8; r >= 2; r -= 2) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f * (10 - r)));
            for (int yy = 0; yy < imgH; yy++) {
                for (int xx = 0; xx < imgW; xx++) {
                    int argb = provinceMask.getRGB(xx, yy) | 0xFF000000;
                    if (colorToProvinceId.get(argb) != null && colorToProvinceId.get(argb).equals(provinceId)) {
                        g2d.setColor(glow);
                        g2d.fillOval(x + (int)(xx * scale) - r, y + (int)(yy * scale) - r, (int)Math.ceil(scale) + 2*r, (int)Math.ceil(scale) + 2*r);
                    }
                }
            }
        }
        g2d.dispose();
    }

    private void invalidateOverlayCache() {
        cachedBorders = null;
        cachedNationHighlight = null;
        cachedProvinceHighlight = null;
        cachedNationForHighlight = null;
        cachedProvinceForHighlight = null;
        // Don't invalidate ocean background cache - it's static
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateTransform();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 1. Draw enhanced animated ocean background (cached for performance)
        if (mapLoaded && mapBackground != null) {
            // Cache ocean background to avoid recreating every frame
            if (cachedOceanBackground == null || lastOceanBgWidth != getWidth() || lastOceanBgHeight != getHeight()) {
                cachedOceanBackground = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D gAnim = cachedOceanBackground.createGraphics();
                GradientPaint gp = new GradientPaint(0, 0, new Color(64, 128, 255), 0, getHeight(), new Color(32, 64, 128));
                gAnim.setPaint(gp);
                gAnim.fillRect(0, 0, getWidth(), getHeight());
                // Subtle noise/texture
                gAnim.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                for (int y = 0; y < getHeight(); y += 2) {
                    for (int x = 0; x < getWidth(); x += 2) {
                        int v = (int)(Math.random() * 12);
                        gAnim.setColor(new Color(Math.min(255, 64+v), Math.min(255, 128+v), Math.min(255, 255+v)));
                        gAnim.fillRect(x, y, 2, 2);
                    }
                }
                gAnim.dispose();
                lastOceanBgWidth = getWidth();
                lastOceanBgHeight = getHeight();
            }
            g2d.drawImage(cachedOceanBackground, 0, 0, null);
        }
        // 2. Province fill (from mask)
        if (provinceColorMap != null) {
            int drawW = (int)(mapImgWidth * currentScale);
            int drawH = (int)(mapImgHeight * currentScale);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.drawImage(provinceColorMap, currentOffsetX, currentOffsetY, drawW, drawH, null);
            // 3. Land shading overlay
            if (landShading != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2d.drawImage(landShading, currentOffsetX, currentOffsetY, drawW, drawH, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            // 4. Province borders (cached)
            updateCachedBorders(mapImgWidth, mapImgHeight, currentScale, currentOffsetX, currentOffsetY);
            g2d.drawImage(cachedBorders, 0, 0, null);
            // 5. Highlight all provinces of selected nation (cached)
            if (selectedNation != null) {
                updateCachedNationHighlight(selectedNation, mapImgWidth, mapImgHeight, currentScale, currentOffsetX, currentOffsetY);
                g2d.drawImage(cachedNationHighlight, 0, 0, null);
            }
            // 6. Fog of war: dim provinces not owned by player
            if (engine.getCountryManager().getPlayerCountry() != null) {
                String player = engine.getCountryManager().getPlayerCountry().getName();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
                g2d.setColor(new Color(30, 30, 30));
                for (int yy = 0; yy < mapImgHeight; yy++) {
                    for (int xx = 0; xx < mapImgWidth; xx++) {
                        int argb = provinceMask.getRGB(xx, yy) | 0xFF000000;
                        String pid = colorToProvinceId.get(argb);
                        if (pid != null) {
                            Province p = engine.getWorldMap().getProvince(pid);
                            if (p != null && !player.equals(p.getOwner())) {
                                g2d.fillRect(currentOffsetX + (int)(xx * currentScale), currentOffsetY + (int)(yy * currentScale), (int)Math.ceil(currentScale), (int)Math.ceil(currentScale));
                            }
                        }
                    }
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            // 7. Province highlight with colored glow (hovered, cached)
            if (hoveredProvinceId != null && provinceMask != null) {
                updateCachedProvinceHighlight(hoveredProvinceId, mapImgWidth, mapImgHeight, currentScale, currentOffsetX, currentOffsetY);
                g2d.drawImage(cachedProvinceHighlight, 0, 0, null);
                // Draw tooltip with drop shadow and rounded background
                Province hovered = engine.getWorldMap().getProvince(hoveredProvinceId);
                if (hovered != null) {
                    String tooltip = "Province: " + hovered.getName() + " | Owner: " + hovered.getOwner();
                    Font tooltipFont = new Font("Segoe UI", Font.BOLD, 16);
                    g2d.setFont(tooltipFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int tw = fm.stringWidth(tooltip) + 24;
                    int th = fm.getHeight() + 12;
                    int tx = 20, ty = getHeight() - 60;
                    g2d.setColor(new Color(30, 30, 30, 220));
                    g2d.fillRoundRect(tx, ty, tw, th, 14, 14);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(tx, ty, tw, th, 14, 14);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(tooltip, tx + 12, ty + th - 10);
                }
            }
            // 8. Nation labels (draw last, always on top)
            drawNationLabels(g2d, currentOffsetX, currentOffsetY, currentScale, mapImgWidth, mapImgHeight);
        }
        // 9. UI overlays
        drawUI(g2d);
    }

    private Color getProvinceColor(Province province) {
        String owner = province.getOwner();
        if (!countryColors.containsKey(owner)) {
            // Assign a unique, non-magenta color for each country
            int hash = Math.abs(owner.hashCode());
            float hue = (hash % 360) / 360.0f;
            Color uniqueColor = Color.getHSBColor(hue, 0.6f, 0.85f);
            countryColors.put(owner, uniqueColor);
        }
        return countryColors.get(owner);
    }

    private void drawNationLabels(Graphics2D g2d, int x0, int y0, double scale, int imgW, int imgH) {
        Map<String, double[]> centroids = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgW; x++) {
                int argb = provinceMask.getRGB(x, y);
                String pid = colorToProvinceId.get(argb);
                if (pid != null) {
                    Province p = engine.getWorldMap().getProvince(pid);
                    if (p != null) {
                        String owner = p.getOwner();
                        if (owner.equals("Ocean") || owner.equals("Uncolonized") || owner.startsWith("Unknown") || owner.startsWith("rgb_") || owner.equals("REMOVE_FROM_MAP") || owner.equals("BORDER"))
                            continue;
                        centroids.putIfAbsent(owner, new double[]{0, 0});
                        counts.put(owner, counts.getOrDefault(owner, 0) + 1);
                        centroids.get(owner)[0] += x;
                        centroids.get(owner)[1] += y;
                    }
                }
            }
        }
        for (String owner : centroids.keySet()) {
            int count = counts.get(owner);
            if (count < 200) continue; // Only label major nations
            double[] c = centroids.get(owner);
            int cx = (int)(c[0] / count);
            int cy = (int)(c[1] / count);
            int sx = x0 + (int)(cx * scale);
            int sy = y0 + (int)(cy * scale);

            int fontSize = Math.max(14, Math.min((int)(44 * scale), 48));
            Font labelFont = new Font("Segoe UI", Font.BOLD, fontSize);
            g2d.setFont(labelFont);
            FontMetrics fm = g2d.getFontMetrics();
            int lw = fm.stringWidth(owner) + 32;
            int lh = fm.getHeight() + 10;
            int lx = sx - lw / 2;
            int ly = sy - lh / 2;

            g2d.setColor(new Color(30,30,30,180));
            g2d.fillRoundRect(lx, ly, lw, lh, 18, 18);
            g2d.setColor(new Color(0,0,0,120));
            g2d.drawRoundRect(lx, ly, lw, lh, 18, 18);
            g2d.setColor(new Color(0,0,0,180));
            g2d.drawString(owner, sx+3, sy+3);
            Color col = countryColors.getOrDefault(owner, Color.WHITE);
            g2d.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 220));
            g2d.drawString(owner, sx, sy);
        }
    }

    private void drawUI(Graphics2D g2d) {
        // Reset transform for UI elements
        g2d.setTransform(new AffineTransform());
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(8, 8, 260, 110, 18, 18);
        g2d.setColor(new Color(0,0,0,60));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(8, 8, 260, 110, 18, 18);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(8, 8, 260, 110, 18, 18);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("Zoom: %.1fx", zoom), 20, 35);
        g2d.drawString("Date: " + engine.getCurrentDate().getFormattedDate(), 20, 60);
        g2d.drawString("Speed: " + engine.getGameSpeed().getDisplayName(), 20, 85);
        if (engine.getCountryManager().getPlayerCountry() != null) {
            g2d.drawString("Playing as: " + engine.getCountryManager().getPlayerCountry().getName(), 20, 110);
        }
    }

    private void centerOnNation(String nation) {
        if (provinceMask == null || colorToProvinceId.isEmpty()) return;
        int imgW = provinceMask.getWidth();
        int imgH = provinceMask.getHeight();
        double sumX = 0, sumY = 0; int count = 0;
        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgW; x++) {
                int argb = provinceMask.getRGB(x, y);
                String pid = colorToProvinceId.get(argb);
                if (pid != null) {
                    Province p = engine.getWorldMap().getProvince(pid);
                    if (p != null && nation.equals(p.getOwner())) {
                        sumX += x;
                        sumY += y;
                        count++;
                    }
                }
            }
        }
        if (count == 0) return;
        double cx = sumX / count, cy = sumY / count;
        int panelW = getWidth() > 0 ? getWidth() : 1600;
        int panelH = getHeight() > 0 ? getHeight() : 900;
        double scale = zoom * Math.min(panelW / (double)imgW, panelH / (double)imgH);
        int drawW = (int)(imgW * scale);
        int drawH = (int)(imgH * scale);
        int x0 = (panelW - drawW) / 2;
        int y0 = (panelH - drawH) / 2;
        int cxScreen = x0 + (int)(cx * scale);
        int cyScreen = y0 + (int)(cy * scale);
        offsetX = panelW/2 - cxScreen;
        offsetY = panelH/2 - cyScreen;
        invalidateOverlayCache();
        repaint();
    }

    private void loadProvinceData() {
        try {
            provinceData = ProvinceDataLoader.loadProvinceData("src/resources/province_data.json");
            maskColorToProvinceId = ProvinceDataLoader.buildMaskColorToProvinceId(provinceData);
            ownerColorToNation = ProvinceDataLoader.buildOwnerColorToNation(provinceData);
            System.out.println("Loaded " + provinceData.size() + " provinces from province_data.json");
        } catch (Exception e) {
            System.err.println("Warning: Could not load province_data.json: " + e.getMessage());
        }
    }

    // Call this in paintComponent before drawing anything
    private void updateTransform() {
        if (provinceMask == null) return;
        mapImgWidth = provinceMask.getWidth();
        mapImgHeight = provinceMask.getHeight();
        int panelW = getWidth();
        int panelH = getHeight();
        currentScale = Math.min(panelW / (double)mapImgWidth, panelH / (double)mapImgHeight) * zoom;
        int drawW = (int)(mapImgWidth * currentScale);
        int drawH = (int)(mapImgHeight * currentScale);
        currentOffsetX = (panelW - drawW) / 2 + offsetX;
        currentOffsetY = (panelH - drawH) / 2 + offsetY;
    }

    private Point mapToScreen(int mapX, int mapY) {
        int x = (int)(mapX * currentScale) + currentOffsetX;
        int y = (int)(mapY * currentScale) + currentOffsetY;
        return new Point(x, y);
    }

    private Point screenToMap(Point p) {
        if (provinceMask == null || p == null) return null;
        int mapX = (int)((p.x - currentOffsetX) / currentScale);
        int mapY = (int)((p.y - currentOffsetY) / currentScale);
        return new Point(mapX, mapY);
    }

    private String getProvinceIdAt(Point mapPoint) {
        if (provinceMask == null || mapPoint == null) return null;
        int x = mapPoint.x, y = mapPoint.y;
        if (x < 0 || y < 0 || x >= provinceMask.getWidth() || y >= provinceMask.getHeight()) return null;
        int argb = provinceMask.getRGB(x, y);
        
        // First try the old colorToProvinceId mapping
        String provinceId = colorToProvinceId.get(argb);
        if (provinceId != null) {
            return provinceId;
        }
        
        // If not found, try using the JSON data
        if (!provinceData.isEmpty()) {
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;
            String maskKey = r + "," + g + "," + b;
            return maskColorToProvinceId.get(maskKey);
        }
        
        return null;
    }

    private void handleProvinceClick(Point p) {
        if (provinceMask == null) return;
        Point mapPoint = screenToMap(p);
        if (mapPoint == null) return;
        String provinceId = getProvinceIdAt(mapPoint);
        if (provinceId != null) {
            Province clickedProvince = engine.getWorldMap().getProvince(provinceId);
            if (clickedProvince != null) {
                showProvinceInfo(clickedProvince);
            }
        }
    }

    private void showProvinceInfo(Province province) {
        // Get the nation name from JSON data if available
        String nationName = province.getOwner();
        ProvinceData provinceDataItem = provinceData.get(province.getId());
        if (provinceDataItem != null) {
            nationName = provinceDataItem.getNation();
        }
        
        String info = String.format("<html><b>Province:</b> %s<br><b>Nation:</b> %s<br><b>Owner:</b> %s<br><b>Population:</b> %d<br><b>Development:</b> %.1f<br><b>Terrain:</b> %s<br><b>Trade Goods:</b> %s</html>",
                province.getName(), nationName, province.getOwner(), province.getPopulation(),
                province.getDevelopment(), province.getTerrain(),
                String.join(", ", province.getTradeGoods()));
        JOptionPane.showMessageDialog(this, info, "Province Information", JOptionPane.INFORMATION_MESSAGE);
    }
} 