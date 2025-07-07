package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
import com.romagame.map.Country;
import com.romagame.map.DistanceCalculator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.event.ActionEvent;
import com.romagame.military.Army;
import com.romagame.military.MilitaryManager;
import java.nio.file.Files;
import java.nio.file.Paths;
public class MapPanel extends JPanel {
    private GameEngine engine;
    private double zoom = 2.0;
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
    private Map<String, Color> countryColors = new HashMap<>();
    private Map<String, Color> provinceIdToOwnerColor = new HashMap<>();
    private String selectedNation = null; // Add this field to track selected nation
    private BufferedImage cachedBorders = null;
    private BufferedImage cachedNationHighlight = null;
    private String cachedNationForHighlight = null;
    private BufferedImage cachedProvinceHighlight = null;
    private String cachedProvinceForHighlight = null;
    
    // Province data from JSON
    private Map<String, String> provinceIdToOwner = new HashMap<>();
    private Map<String, String> nationToColor = new HashMap<>();
    private List<String> nationList = new ArrayList<>();

    // Centralized transform fields
    private double currentScale;
    private int currentOffsetX, currentOffsetY;
    private int mapImgWidth, mapImgHeight;
    
    // Performance optimization fields
    private BufferedImage cachedOceanBackground = null;
    private int lastOceanBgWidth = -1;
    private int lastOceanBgHeight = -1;

    private Army selectedArmy = null;

    private Map<String, String> colorKeyToProvinceId = new HashMap<>();

    public MapPanel(GameEngine engine) {
        System.out.println("MapPanel constructor called");
        this.engine = engine;
        setupPanel();
        System.out.println("Calling loadMapBackground()");
        loadMapBackground();
        System.out.println("Calling loadProvinceMask()");
        loadProvinceMask();
        setupMouseListeners();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(1600, 900));
        setBackground(new Color(40, 70, 120)); // Deep blue
        setFocusable(true);
        // Enable double buffering for smoother rendering
        setDoubleBuffered(true);
        
        // Add keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        // Escape key to clear selection
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSelection");
        getActionMap().put("clearSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearNationSelection();
            }
        });
        
        // Space key to center on selected nation
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "centerOnSelection");
        getActionMap().put("centerOnSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedNation != null) {
                    centerOnNation(selectedNation);
                }
            }
        });
    }

    private void loadMapBackground() {
        System.out.println("[DEBUG] Entering loadMapBackground()");
        try {
            File mapFile = new File("src/resources/img/start1.png");
            System.out.println("[DEBUG] Checking for map background at: " + mapFile.getAbsolutePath());
            if (!mapFile.exists()) {
                System.err.println("[ERROR] Map background not found at " + mapFile.getAbsolutePath());
                // Try absolute path fallback (edit this path if needed)
                mapFile = new File("C:/Users/taylo/Documents/projects/Roma Game/src/resources/img/start1.png");
                System.out.println("[DEBUG] Fallback absolute path: " + mapFile.getAbsolutePath());
            }
            if (mapFile.exists()) {
                mapBackground = ImageIO.read(mapFile);
                if (mapBackground != null) {
                    mapLoaded = true;
                    System.out.println("[DEBUG] Loaded map background from: " + mapFile.getPath());
                    System.out.println("[DEBUG] mapBackground loaded: " + mapBackground.getWidth() + "x" + mapBackground.getHeight());
                } else {
                    System.err.println("[ERROR] mapBackground is null after ImageIO.read!");
                    createGradientBackground();
                }
            } else {
                System.err.println("[ERROR] Map background not found at either relative or absolute path.");
                createGradientBackground();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load map background image: " + e.getMessage());
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
            File maskFile = new File("src/resources/img/province_mask.png");
            if (maskFile.exists()) {
                provinceMask = ImageIO.read(maskFile);
                updateProvinceColorMap();
                updateBorderOverlay();
                updateLandShading();
                createLandShading();
                repaint();
                System.out.println("Loaded province mask from: " + maskFile.getPath());
                if (provinceMask != null) {
                    System.out.println("Province mask loaded: " + provinceMask.getWidth() + "x" + provinceMask.getHeight());
                } else {
                    System.err.println("ERROR: Province mask is null after loading!");
                }
            } else {
                System.err.println("ERROR: Province mask not found at src/resources/img/province_mask.png");
            }
        } catch (IOException e) {
            System.err.println("Could not load province mask image: " + e.getMessage());
        }
    }

    public void loadColorToProvinceId() {
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

    public void loadProvinceOwnerColors() {
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
        // No-op: do not recolor provinces, just use the background image
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
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left-click: select army if clicked on icon
                    MilitaryManager mm = engine.getMilitaryManager();
                    Point clickPoint = e.getPoint();
                    boolean armyClicked = false;
                    for (Army army : mm.getArmies().values()) {
                        String loc = army.getLocation();
                        if (loc == null || loc.equals("Unknown")) continue;
                        Province province = engine.getWorldMap().getProvince(loc);
                        if (province == null) continue;
                        // Convert province coordinates to screen coordinates
                        Point2D.Double mapCoords = DistanceCalculator.latLonToMapCoords(
                            province.getLatitude(), province.getLongitude(), 
                            mapImgWidth, mapImgHeight
                        );
                        Point screenCoords = DistanceCalculator.mapToScreen(
                            mapCoords, currentScale, currentOffsetX, currentOffsetY
                        );
                        // Check if click is within army icon bounds
                        int iconSize = 20;
                        int x = screenCoords.x - iconSize / 2;
                        int y = screenCoords.y - iconSize / 2;
                        if (clickPoint.x >= x && clickPoint.x <= x + iconSize && 
                            clickPoint.y >= y && clickPoint.y <= y + iconSize) {
                            selectedArmy = army;
                            // Show context menu
                            JPopupMenu menu = new JPopupMenu();
                            JMenuItem move = new JMenuItem("Move Army");
                            JMenuItem split = new JMenuItem("Split Army");
                            JMenuItem merge = new JMenuItem("Merge Army");
                            JMenuItem disband = new JMenuItem("Disband Army");
                            JMenuItem details = new JMenuItem("Show Details");
                            move.addActionListener(evt -> showMoveArmyDialog(army));
                            split.addActionListener(evt -> showSplitArmyDialog(army));
                            merge.addActionListener(evt -> showMergeArmyDialog(army));
                            disband.addActionListener(evt -> showDisbandArmyDialog(army));
                            details.addActionListener(evt -> showArmyDetailsDialog(army));
                            menu.add(move);
                            menu.add(split);
                            menu.add(merge);
                            menu.add(disband);
                            menu.addSeparator();
                            menu.add(details);
                            menu.show(MapPanel.this, x, y + iconSize);
                            repaint();
                            armyClicked = true;
                            break;
                        }
                    }
                    if (!armyClicked) {
                        // Only select province if click is on a valid province pixel
                        Point mapPoint = screenToMap(e.getPoint());
                        if (getProvinceIdAt(mapPoint) != null) {
                handleProvinceClick(e.getPoint());
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // Right-click: move selected army to province
                    if (selectedArmy != null) {
                        String provinceId = getProvinceIdAt(e.getPoint());
                        if (provinceId != null) {
                            selectedArmy.setLocation(provinceId);
                            repaint();
                        }
                    }
                }
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
                        String owner = provinceIdToOwner.get(hoveredProvinceId);
                        String color = nationToColor.get(owner);
                        if (color != null) {
                            setToolTipText("Province: " + hoveredProvinceId + " | Nation: " + owner);
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
        
        // Enhanced highlighting with border and glow effect
        for (int yy = 0; yy < imgH; yy++) {
            for (int xx = 0; xx < imgW; xx++) {
                int argb = provinceMask.getRGB(xx, yy) | 0xFF000000;
                String pid = colorToProvinceId.get(argb);
                if (pid != null) {
                    Province p = engine.getWorldMap().getProvince(pid);
                    if (p != null && nation.equals(p.getOwner())) {
                        int screenX = x + (int)(xx * scale);
                        int screenY = y + (int)(yy * scale);
                        int pixelSize = (int)Math.ceil(scale);
                        
                        // Inner highlight (brighter)
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                        g2d.setColor(Color.YELLOW);
                        g2d.fillRect(screenX, screenY, pixelSize, pixelSize);
                        
                        // Border highlight
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                        g2d.setColor(Color.ORANGE);
                        g2d.setStroke(new BasicStroke(Math.max(2.0f, (float)scale * 0.5f)));
                        g2d.drawRect(screenX, screenY, pixelSize, pixelSize);
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
        System.out.println("paintComponent called");
        updateTransform();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 1. Draw enhanced animated ocean background (cached for performance)
        if (mapLoaded && mapBackground != null) {
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
        // 1b. Draw ocean mesh/tiles (rectangles) over ocean areas
        if (provinceMask != null && colorToProvinceId != null) {
            int tileSize = (int)Math.max(8, Math.round(currentScale * 8));
            for (int yy = 0; yy < mapImgHeight; yy += tileSize) {
                for (int xx = 0; xx < mapImgWidth; xx += tileSize) {
                    int argb = provinceMask.getRGB(xx, yy) | 0xFF000000;
                    String pid = colorToProvinceId.get(argb);
                    if (pid == null) continue;
                    com.romagame.map.Province p = engine.getWorldMap().getProvince(pid);
                    if (p == null) continue;
                    String owner = p.getOwner();
                    if (owner.equals("Ocean") || owner.equals("Uncolonized") || owner.startsWith("Unknown") || owner.startsWith("rgb_") || owner.equals("REMOVE_FROM_MAP") || owner.equals("BORDER")) {
                        int sx = currentOffsetX + (int)(xx * currentScale);
                        int sy = currentOffsetY + (int)(yy * currentScale);
                        g2d.setColor(new Color(70, 130, 200, 90)); // semi-transparent blue
                        g2d.fillRect(sx, sy, tileSize, tileSize);
                        g2d.setColor(new Color(40, 90, 160, 60));
                        g2d.drawRect(sx, sy, tileSize, tileSize);
                    }
                }
            }
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

        // Draw armies as icons
        MilitaryManager mm = engine.getMilitaryManager();
        for (Army army : mm.getArmies().values()) {
            String loc = army.getLocation();
            if (loc == null || loc.equals("Unknown")) continue;
            Province province = engine.getWorldMap().getProvince(loc);
            if (province == null) continue;
            
            // Convert province coordinates to map coordinates
            Point2D.Double mapCoords = DistanceCalculator.latLonToMapCoords(
                province.getLatitude(), province.getLongitude(), 
                mapImgWidth, mapImgHeight
            );
            
            // Convert map coordinates to screen coordinates
            Point screenCoords = DistanceCalculator.mapToScreen(
                mapCoords, currentScale, currentOffsetX, currentOffsetY
            );
            
            // Draw army icon
            int iconSize = 20;
            int x = screenCoords.x - iconSize / 2;
            int y = screenCoords.y - iconSize / 2;
            
            // Draw army icon with different colors based on ownership
            Country playerCountry = engine.getCountryManager().getPlayerCountry();
            if (playerCountry != null && army.getCountry().equals(playerCountry.getName())) {
                g2d.setColor(army == selectedArmy ? Color.YELLOW : Color.GREEN);
            } else {
                g2d.setColor(army == selectedArmy ? Color.YELLOW : Color.RED);
            }
            
            g2d.fillOval(x, y, iconSize, iconSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, iconSize, iconSize);
            
            // Draw army name
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(army.getName());
            g2d.setColor(Color.WHITE);
            g2d.drawString(army.getName(), x + (iconSize - textWidth) / 2, y + iconSize + 12);
        }
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
                String colorKey = String.format("%d,%d,%d", (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
                String provinceId = colorKeyToProvinceId.get(colorKey);
                if (provinceId != null) {
                    String owner = provinceIdToOwner.get(provinceId);
                    if (owner == null || owner.equals("Ocean") || owner.equals("Uncolonized") || owner.startsWith("Unknown") || owner.startsWith("rgb_") || owner.startsWith("Color_") || owner.equals("REMOVE_FROM_MAP") || owner.equals("BORDER") || owner.equals("Water") || owner.equals("Sea") || owner.equals("Lake") || owner.equals("River"))
                        continue;
                    centroids.putIfAbsent(owner, new double[]{0, 0});
                    counts.put(owner, counts.getOrDefault(owner, 0) + 1);
                    centroids.get(owner)[0] += x;
                    centroids.get(owner)[1] += y;
                }
            }
        }
        for (String owner : centroids.keySet()) {
            int count = counts.get(owner);
            if (count < 100) continue;
            double[] c = centroids.get(owner);
            int cx = (int)(c[0] / count);
            int cy = (int)(c[1] / count);
            int sx = x0 + (int)(cx * scale);
            int sy = y0 + (int)(cy * scale);
            boolean isSelected = owner.equals(selectedNation);
            int fontSize = Math.max(14, Math.min((int)(44 * scale), 48));
            if (isSelected) {
                fontSize = Math.max(18, Math.min((int)(52 * scale), 56));
            }
            Font labelFont = new Font("Segoe UI", Font.BOLD, fontSize);
            g2d.setFont(labelFont);
            FontMetrics fm = g2d.getFontMetrics();
            int lw = fm.stringWidth(owner) + 32;
            int lh = fm.getHeight() + 10;
            int lx = sx - lw / 2;
            int ly = sy - lh / 2;
            if (isSelected) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillRoundRect(lx - 4, ly - 4, lw + 8, lh + 8, 22, 22);
                g2d.setColor(new Color(255, 165, 0, 200));
                g2d.setStroke(new BasicStroke(3.0f));
                g2d.drawRoundRect(lx - 4, ly - 4, lw + 8, lh + 8, 22, 22);
            }
            g2d.setColor(new Color(30,30,30,180));
            g2d.fillRoundRect(lx, ly, lw, lh, 18, 18);
            g2d.setColor(new Color(0,0,0,120));
            g2d.drawRoundRect(lx, ly, lw, lh, 18, 18);
            g2d.setColor(new Color(0,0,0,180));
            g2d.drawString(owner, sx+3, sy+3);
            String colorStr = nationToColor.getOrDefault(owner, "255,255,255");
            String[] rgb = colorStr.split(",");
            Color col = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]), 220);
            g2d.setColor(col);
            g2d.drawString(owner, sx, sy);
        }
    }

    private void drawUI(Graphics2D g2d) {
        // Reset transform for UI elements
        g2d.setTransform(new AffineTransform());
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Main info panel
        int panelHeight = selectedNation != null ? 140 : 110;
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(8, 8, 260, panelHeight, 18, 18);
        g2d.setColor(new Color(0,0,0,60));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(8, 8, 260, panelHeight, 18, 18);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(8, 8, 260, panelHeight, 18, 18);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("Zoom: %.1fx", zoom), 20, 35);
        g2d.drawString("Date: " + engine.getCurrentDate().getFormattedDate(), 20, 60);
        g2d.drawString("Speed: " + engine.getGameSpeed().getDisplayName(), 20, 85);
        if (engine.getCountryManager().getPlayerCountry() != null) {
            g2d.drawString("Playing as: " + engine.getCountryManager().getPlayerCountry().getName(), 20, 110);
        }
        
        // Selected nation info
        if (selectedNation != null) {
            g2d.setColor(new Color(255, 255, 0, 200)); // Yellow highlight
            g2d.fillRoundRect(8, 8, 260, panelHeight, 18, 18);
            g2d.setColor(new Color(255, 165, 0, 200)); // Orange border
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRoundRect(8, 8, 260, panelHeight, 18, 18);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Selected: " + selectedNation, 20, 135);
        }
        
        // Controls hint
        g2d.setColor(new Color(100, 100, 100, 180));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("ESC: Clear selection | SPACE: Center on selection", 8, getHeight() - 10);
    }

    public void centerOnNation(String nation) {
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
    
    public void selectNation(String nation) {
        selectedNation = nation;
        invalidateOverlayCache();
        repaint();
    }
    
    public void clearNationSelection() {
        selectedNation = null;
        invalidateOverlayCache();
        repaint();
    }
    
    public String getSelectedNation() {
        return selectedNation;
    }
    
    // Army context menu dialog methods
    private void showMoveArmyDialog(Army army) {
        // Get all provinces for selection
        List<String> provinceIds = new ArrayList<>();
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            provinceIds.add(province.getId());
        }
        
        String selectedProvince = (String) JOptionPane.showInputDialog(
            this,
            "Select destination province for " + army.getName() + ":",
            "Move Army",
            JOptionPane.QUESTION_MESSAGE,
            null,
            provinceIds.toArray(new String[0]),
            army.getLocation()
        );
        
        if (selectedProvince != null && !selectedProvince.equals(army.getLocation())) {
            army.setLocation(selectedProvince);
            repaint();
            JOptionPane.showMessageDialog(this, 
                army.getName() + " moved to " + selectedProvince + "!",
                "Army Moved",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showSplitArmyDialog(Army army) {
        // For now, just show a message - full implementation would require army splitting logic
        JOptionPane.showMessageDialog(this,
            "Split army functionality to be implemented.\n" +
            "This would allow dividing " + army.getName() + " into smaller units.",
            "Split Army",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showMergeArmyDialog(Army army) {
        // For now, just show a message - full implementation would require army merging logic
        JOptionPane.showMessageDialog(this,
            "Merge army functionality to be implemented.\n" +
            "This would allow combining " + army.getName() + " with other armies.",
            "Merge Army",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showDisbandArmyDialog(Army army) {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to disband " + army.getName() + "?\n" +
            "This action cannot be undone.",
            "Disband Army",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            // Remove army from military manager
            engine.getMilitaryManager().getArmies().remove(army.getName());
            selectedArmy = null;
            repaint();
            JOptionPane.showMessageDialog(this,
                army.getName() + " has been disbanded.",
                "Army Disbanded",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showArmyDetailsDialog(Army army) {
        StringBuilder details = new StringBuilder();
        details.append("Army: ").append(army.getName()).append("\n");
        details.append("Country: ").append(army.getCountry()).append("\n");
        details.append("Location: ").append(army.getLocation()).append("\n");
        details.append("Total Strength: ").append(army.getTotalStrength()).append("\n");
        details.append("Combat Power: ").append(String.format("%.1f", army.getCombatPower())).append("\n");
        details.append("Morale: ").append(String.format("%.1f", army.getMorale())).append("\n");
        details.append("Organization: ").append(String.format("%.1f", army.getOrganization())).append("\n");
        details.append("Status: ").append(army.isEngaged() ? "Engaged" : "Ready").append("\n\n");
        
        details.append("Unit Composition:\n");
        for (Map.Entry<String, Integer> unit : army.getUnits().entrySet()) {
            details.append("- ").append(unit.getKey()).append(": ").append(unit.getValue()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this,
            details.toString(),
            "Army Details - " + army.getName(),
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadNationsAndProvinces() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get("src/resources/data/nations_and_provinces.json")));
            
            // Parse nations (if they exist in the JSON)
            if (jsonText.contains("\"nations\"")) {
                int nationsStart = jsonText.indexOf("\"nations\"");
                int nationsArrayStart = jsonText.indexOf("[", nationsStart);
                int nationsArrayEnd = findMatchingBracket(jsonText, nationsArrayStart);
                
                if (nationsArrayEnd != -1) {
                    String nationsBlock = jsonText.substring(nationsArrayStart + 1, nationsArrayEnd);
                    String[] nationEntries = nationsBlock.split("\\},\\s*\\{");
                    for (String entry : nationEntries) {
                        try {
                            String name = extractJsonValue(entry, "name");
                            String colorStr = extractColorArrayString(entry, "color");
                            if (name != null && colorStr != null) {
                                nationToColor.put(name, colorStr);
                                nationList.add(name);
                            }
                        } catch (Exception e) {
                            // Skip malformed nation entries
                        }
                    }
                }
            }

            // Parse provinces with improved error handling
            int provincesStart = jsonText.indexOf("\"provinces\"");
            if (provincesStart == -1) {
                System.out.println("Could not find provinces section in JSON");
                return;
            }
            
            int provincesArrayStart = jsonText.indexOf("[", provincesStart);
            int provincesArrayEnd = findMatchingBracket(jsonText, provincesArrayStart);
            
            if (provincesArrayEnd == -1) {
                System.out.println("Could not find end of provinces array in JSON");
                return;
            }
            
            String provincesBlock = jsonText.substring(provincesArrayStart + 1, provincesArrayEnd);
            String[] provinceEntries = provincesBlock.split("\\},\\s*\\{");
            
            int loadedCount = 0;
            for (int i = 0; i < provinceEntries.length; i++) {
                String entry = provinceEntries[i];
                
                // Clean up the entry
                if (i == 0) {
                    entry = entry.replaceFirst("^\\s*\\{\\s*", "");
                }
                if (i == provinceEntries.length - 1) {
                    entry = entry.replaceFirst("\\s*\\}\\s*$", "");
                }
                
                try {
                    // Extract province_id
                    String provinceId = extractJsonValue(entry, "province_id");
                    if (provinceId == null) continue;
                    
                    // Extract owner
                    String owner = extractJsonValue(entry, "owner");
                    if (owner == null) continue;
                    
                    // Extract color array
                    int[] rgb = extractColorArray(entry, "owner_color");
                    if (rgb == null) continue;
                    
                    String colorKey = String.format("%d,%d,%d", rgb[0], rgb[1], rgb[2]);
                    colorKeyToProvinceId.put(colorKey, provinceId);
                    provinceIdToOwner.put(provinceId, owner);
                    loadedCount++;
                    
                } catch (Exception e) {
                    // Skip malformed province entries
                    System.err.println("Skipping malformed province entry: " + e.getMessage());
                }
            }

            System.out.println("Loaded " + loadedCount + " provinces from nations_and_provinces.json");
            System.out.println("Loaded " + nationList.size() + " nations from nations_and_provinces.json");
        } catch (Exception e) {
            System.err.println("Failed to load nations_and_provinces.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private int findMatchingBracket(String text, int start) {
        int count = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[') count++;
            else if (c == ']') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private String extractColorArrayString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1) + "," + m.group(2) + "," + m.group(3);
        }
        return null;
    }
    
    private int[] extractColorArray(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\\[\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return new int[]{
                Integer.parseInt(m.group(1)),
                Integer.parseInt(m.group(2)),
                Integer.parseInt(m.group(3))
            };
        }
        return null;
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

    public Point mapToScreen(int mapX, int mapY) {
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
        
        // Extract RGB values from ARGB
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        
        // Skip black pixels (ocean)
        if (r == 0 && g == 0 && b == 0) {
            return null;
        }
        
        // Try the new colorKeyToProvinceId mapping first
        String colorKey = String.format("%d,%d,%d", r, g, b);
        String provinceId = colorKeyToProvinceId.get(colorKey);
        if (provinceId != null) {
            return provinceId;
        }
        
        // Fallback to old colorToProvinceId mapping
        provinceId = colorToProvinceId.get(argb);
        if (provinceId != null) {
            return provinceId;
        }
        
        return null;
    }

    public void handleProvinceClick(Point p) {
        if (provinceMask == null) return;
        Point mapPoint = screenToMap(p);
        if (mapPoint == null) return;
        String provinceId = getProvinceIdAt(mapPoint);
        if (provinceId != null) {
            Province clickedProvince = engine.getWorldMap().getProvince(provinceId);
            if (clickedProvince != null) {
                // Select the nation that owns this province
                String nationName = clickedProvince.getOwner();
                // Check for ocean, uncolonized, or transparent (black) pixel
                int x = mapPoint.x, y = mapPoint.y;
                int argb = provinceMask.getRGB(x, y);
                boolean isBlack = (argb & 0x00FFFFFF) == 0x000000;
                if (!nationName.equals("Ocean") && !nationName.equals("Uncolonized") && !isBlack) {
                    selectedNation = nationName;
                    invalidateOverlayCache();
                    repaint();
                    // Show province info dialog
                showProvinceInfo(clickedProvince);
                }
            }
        }
    }

    public void showProvinceInfo(Province province) {
        String nationName = province.getOwner();        
        String info = String.format("<html><b>Province:</b> %s<br><b>Nation:</b> %s<br><b>Owner:</b> %s<br><b>Population:</b> %d<br><b>Development:</b> %.1f<br><b>Terrain:</b> %s<br><b>Trade Goods:</b> %s</html>",
                province.getName(), nationName, province.getOwner(), province.getPopulation(),
                province.getDevelopment(), province.getTerrain(),
                String.join(", ", province.getTradeGoods()));
        JOptionPane.showMessageDialog(this, info, "Province Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void centerOnCoordinates(int x, int y) {
        Rectangle viewRect = getVisibleRect();
        int newX = x - viewRect.width / 2;
        int newY = y - viewRect.height / 2;
        scrollRectToVisible(new Rectangle(newX, newY, viewRect.width, viewRect.height));
    }
} 