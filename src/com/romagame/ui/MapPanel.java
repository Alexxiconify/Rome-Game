package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.map.Province;
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

/**
 * Optimized MapPanel with modern camera system and performance improvements.
 * Uses Camera and MapRenderer for better separation of concerns and performance.
 */
public class MapPanel extends JPanel {
    // Core components
    private GameEngine engine;
    private Camera camera;
    private MapRenderer renderer;
    
    // UI state
    private boolean isDragging = false;
    private boolean isHolding = false;
    private Point lastMousePos;
    private Point holdStartPos = null;
    private long holdStartTime = 0;
    private Point mouseMapPoint = null;
    private String hoveredProvinceId = null;
    private String selectedNation = null;
    private Army selectedArmy = null;
    private Province selectedProvince = null;
    
    // Hold-to-drag settings
    private static final long HOLD_THRESHOLD_MS = 200; // 200ms hold required
    private static final int HOLD_DISTANCE_THRESHOLD = 5; // 5 pixels max movement during hold
    
    // Performance tracking
    private long lastProvinceClickTime = 0;
    private long lastRepaintTime = 0;
    private static final long REPAINT_THROTTLE_MS = 16; // ~60 FPS
    
    // Data mappings
    private Map<Integer, String> colorToProvinceId = new HashMap<>();
    private Map<String, Color> countryColors = new HashMap<>();
    private Map<String, Color> provinceIdToOwnerColor = new HashMap<>();
    private Map<String, String> provinceIdToOwner = new HashMap<>();
    private Map<String, String> nationToColor = new HashMap<>();
    private List<String> nationList = new ArrayList<>();
    private Map<String, String> colorKeyToProvinceId = new HashMap<>();

    public MapPanel(GameEngine engine) {
        System.out.println("MapPanel constructor called");
        this.engine = engine;
        
        // Initialize camera and renderer
        this.camera = new Camera();
        this.renderer = new MapRenderer();
        
        setupPanel();
        System.out.println("Calling loadMapBackground()");
        loadMapBackground();
        System.out.println("Calling loadProvinceMask()");
        loadProvinceMask();
        System.out.println("Calling loadNationsAndProvinces()");
        loadNationsAndProvinces();
        setupMouseListeners();
        
        // Start at coordinates (0,0)
        camera.centerOn(0, 0);
    }

    private void setupPanel() {
        // Set preferred size to accommodate the full map image
        // This will be updated when the map background is loaded
        setPreferredSize(new Dimension(4974, 2516)); // Full map dimensions
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
                clearProvinceSelection();
            }
        });
        
        // Space key to center on map center
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "centerOnMap");
        getActionMap().put("centerOnMap", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerOnMapCenter();
            }
        });
        
        // R key to center on Rome
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "centerOnRome");
        getActionMap().put("centerOnRome", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerOnRome();
            }
        });
    }

    private void loadMapBackground() {
        System.out.println("[DEBUG] Entering loadMapBackground()");
        try {
            // Load start.png as the main background (with borders)
            File mapFile = new File("src/resources/img/start.png");
            System.out.println("[DEBUG] Checking for map background at: " + mapFile.getAbsolutePath());
            if (!mapFile.exists()) {
                System.err.println("[ERROR] Map background not found at " + mapFile.getAbsolutePath());
                // Try absolute path fallback (edit this path if needed)
                mapFile = new File("C:/Users/taylo/Documents/projects/Roma Game/src/resources/img/start.png");
                System.out.println("[DEBUG] Fallback absolute path: " + mapFile.getAbsolutePath());
            }
            if (mapFile.exists()) {
                BufferedImage mapBackground = ImageIO.read(mapFile);
                if (mapBackground != null) {
                    // Update preferred size to match the actual map dimensions
                    setPreferredSize(new Dimension(mapBackground.getWidth(), mapBackground.getHeight()));
                    revalidate(); // Notify layout manager of size change
                    System.out.println("Loaded map background from: " + mapFile.getPath() + " | Size: " + mapBackground.getWidth() + "x" + mapBackground.getHeight());
                    
                    // Set map dimensions in camera
                    camera.setMapDimensions(mapBackground.getWidth(), mapBackground.getHeight());
                    
                    // Set background in renderer
                    renderer.setMapBackground(mapBackground);
                } else {
                    System.err.println("[ERROR] mapBackground is null after ImageIO.read! | Path: " + mapFile.getPath());
                    createGradientBackground();
                }
            } else {
                System.err.println("[ERROR] Map background not found at either relative or absolute path.");
                createGradientBackground();
            }
            
            // Load start1.png as the borderless overlay
            File borderlessFile = new File("src/resources/img/start1.png");
            if (borderlessFile.exists()) {
                BufferedImage borderlessOverlay = ImageIO.read(borderlessFile);
                if (borderlessOverlay != null) {
                    System.out.println("Loaded borderless overlay from: " + borderlessFile.getPath() + " | Size: " + borderlessOverlay.getWidth() + "x" + borderlessOverlay.getHeight());
                    renderer.setBorderlessOverlay(borderlessOverlay);
                } else {
                    System.err.println("[ERROR] borderlessOverlay is null after ImageIO.read! | Path: " + borderlessFile.getPath());
                }
            } else {
                System.err.println("[ERROR] Borderless overlay not found at src/resources/img/start1.png");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Could not load map background image: " + e.getMessage());
            createGradientBackground();
        }
    }

    private void createGradientBackground() {
        BufferedImage mapBackground = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
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
        
        // Set in renderer and camera
        renderer.setMapBackground(mapBackground);
        camera.setMapDimensions(1200, 700);
    }

    private void loadProvinceMask() {
        try {
            // Load the original province_mask.png for province detection and tooltips
            File maskFile = new File("src/resources/img/province_mask.png");
            if (maskFile.exists()) {
                BufferedImage provinceMask = ImageIO.read(maskFile);
                updateProvinceColorMap();
                renderer.setProvinceMask(provinceMask);
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
                // Start hold detection for both left and right clicks
                isHolding = true;
                holdStartPos = e.getPoint();
                holdStartTime = System.currentTimeMillis();
                lastMousePos = e.getPoint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                // Check if this was a hold-and-drag or just a click
                long holdDuration = System.currentTimeMillis() - holdStartTime;
                int holdDistance = holdStartPos != null ? 
                    (int)Math.sqrt(Math.pow(e.getX() - holdStartPos.x, 2) + Math.pow(e.getY() - holdStartPos.y, 2)) : 0;
                
                if (holdDuration >= HOLD_THRESHOLD_MS && holdDistance <= HOLD_DISTANCE_THRESHOLD) {
                    // This was a hold - start dragging
                    isDragging = true;
                    System.out.println("Hold detected - dragging enabled");
                } else if (holdDuration < HOLD_THRESHOLD_MS && holdDistance <= HOLD_DISTANCE_THRESHOLD) {
                    // This was a quick click - handle as click
                    handleMouseClick(e);
                }
                
                // Reset hold state
                isHolding = false;
                isDragging = false;
                holdStartPos = null;
                holdStartTime = 0;
                lastMousePos = null;
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && lastMousePos != null) {
                    int dx = e.getX() - lastMousePos.x;
                    int dy = e.getY() - lastMousePos.y;
                    
                    // Check if this is a right-click drag (province interaction) or left-click drag (map panning)
                    if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK) {
                        // Right-click drag: province interaction
                        handleProvinceDrag(e.getPoint(), dx, dy);
                    } else {
                        // Left-click drag: map panning using camera
                        camera.moveBy(-dx, -dy);
                        
                        System.out.println("Map dragged: dx=" + dx + ", dy=" + dy);
                        repaint();
                    }
                    
                    lastMousePos = e.getPoint();
                } else if (isHolding && holdStartPos != null) {
                    // Check if hold is being maintained
                    int holdDistance = (int)Math.sqrt(Math.pow(e.getX() - holdStartPos.x, 2) + Math.pow(e.getY() - holdStartPos.y, 2));
                    long holdDuration = System.currentTimeMillis() - holdStartTime;
                    
                    if (holdDistance > HOLD_DISTANCE_THRESHOLD) {
                        // Hold broken by movement - cancel hold
                        isHolding = false;
                        holdStartPos = null;
                        holdStartTime = 0;
                    } else if (holdDuration >= HOLD_THRESHOLD_MS) {
                        // Hold threshold reached - start dragging
                        isDragging = true;
                        System.out.println("Hold threshold reached - dragging enabled");
                    }
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                if (provinceMask == null) return;
                String oldHoveredProvinceId = hoveredProvinceId;
                mouseMapPoint = screenToMap(e.getPoint());
                hoveredProvinceId = getProvinceIdAt(mouseMapPoint);
                
                // Only repaint if hover state changed
                if ((oldHoveredProvinceId == null) != (hoveredProvinceId == null) || 
                    (oldHoveredProvinceId != null && hoveredProvinceId != null && !oldHoveredProvinceId.equals(hoveredProvinceId))) {
                    
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
                    
                    // Invalidate hover cache and repaint
                    cachedHoverHighlight = null;
                    cachedHoverForHighlight = null;
                    repaint();
                }
            }
        });
        addMouseWheelListener(e -> {
            System.out.println("Mouse wheel event received: " + e.getWheelRotation());
            double oldZoom = camera.getZoom();
            double zoomFactor = e.getWheelRotation() > 0 ? 0.7 : 1.4; // Much faster zoom
            camera.setZoom(Math.max(1.0, Math.min(10.0, oldZoom * zoomFactor)));
            
            System.out.println("Zoom changed from " + oldZoom + " to " + camera.getZoom());
            
            // Only repaint if zoom actually changed
            if (Math.abs(camera.getZoom() - oldZoom) > 0.01) {
                repaint();
            }
        });
    }
    
    // Add the handleMouseClick method
    private void handleMouseClick(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Left-click: select army if clicked on icon, otherwise select province
            MilitaryManager mm = engine.getMilitaryManager();
            Point clickPoint = e.getPoint();
            boolean armyClicked = false;
            for (Army army : mm.getArmies().values()) {
                String loc = army.getLocation();
                if (loc == null || loc.equals("Unknown")) continue;
                Province province = engine.getWorldMap().getProvince(loc);
                if (province == null) continue;
                // Convert province coordinates to screen coordinates using camera
                Point mapPoint = camera.screenToMap(clickPoint.x, clickPoint.y);
                Point screenCoords = camera.mapToScreen(mapPoint.x, mapPoint.y);
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
                Point mapPoint = camera.screenToMap(e.getX(), e.getY());
                if (getProvinceIdAt(mapPoint) != null) {
                    handleProvinceClick(e.getPoint());
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            // Right-click: move selected army to province or show province context menu
            Point mapPoint = camera.screenToMap(e.getX(), e.getY());
            String provinceId = getProvinceIdAt(mapPoint);
            if (provinceId != null) {
                Province clickedProvince = engine.getWorldMap().getProvince(provinceId);
                if (clickedProvince != null) {
                    if (selectedArmy != null) {
                        // Move selected army to province
                        selectedArmy.setLocation(provinceId);
                        repaint();
                    } else {
                        // Show province context menu
                        showProvinceContextMenu(clickedProvince, e.getPoint());
                    }
                }
            }
        }
    }


    public void updateCachedBorders(int imgW, int imgH, double scale, int x, int y) {
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
        
        // Enhanced highlighting with border and glow effect for provinces belonging to the selected nation
        for (int yy = 0; yy < imgH; yy++) {
            for (int xx = 0; xx < imgW; xx++) {
                int argb = provinceMask.getRGB(xx, yy);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                
                // Skip black pixels (ocean/background)
                if (r == 0 && g == 0 && b == 0) {
                    continue;
                }
                
                // Check if this pixel belongs to a province owned by the selected nation
                String colorKey = String.format("%d,%d,%d", r, g, b);
                String provinceId = colorKeyToProvinceId.get(colorKey);
                if (provinceId != null) {
                    Province province = engine.getWorldMap().getProvince(provinceId);
                    if (province != null && nation.equals(province.getOwner())) {
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

    public void updateCachedProvinceHighlight(String provinceId, int imgW, int imgH, double scale, int x, int y) {
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
    
    private void updateCachedHoverHighlight(String provinceId, int imgW, int imgH, double scale, int x, int y) {
        if (cachedHoverHighlight != null && provinceId.equals(cachedHoverForHighlight) && cachedHoverHighlight.getWidth() == getWidth() && cachedHoverHighlight.getHeight() == getHeight()) return;
        cachedHoverHighlight = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        cachedHoverForHighlight = provinceId;
        Province hovered = engine.getWorldMap().getProvince(provinceId);
        if (hovered == null) return;
        
        Graphics2D g2d = cachedHoverHighlight.createGraphics();
        
        // Enhanced hover highlighting with bright border and glow effect
        for (int yy = 0; yy < imgH; yy++) {
            for (int xx = 0; xx < imgW; xx++) {
                int argb = provinceMask.getRGB(xx, yy);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                
                // Skip black pixels (ocean/background)
                if (r == 0 && g == 0 && b == 0) {
                    continue;
                }
                
                // Check if this pixel belongs to the hovered province
                String colorKey = String.format("%d,%d,%d", r, g, b);
                String pixelProvinceId = colorKeyToProvinceId.get(colorKey);
                if (pixelProvinceId != null && pixelProvinceId.equals(provinceId)) {
                    int screenX = x + (int)(xx * scale);
                    int screenY = y + (int)(yy * scale);
                    int pixelSize = (int)Math.ceil(scale);
                    
                    // Inner highlight (bright white glow)
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(screenX, screenY, pixelSize, pixelSize);
                    
                    // Border highlight (bright cyan border)
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                    g2d.setColor(Color.CYAN);
                    g2d.setStroke(new BasicStroke(Math.max(3.0f, (float)scale * 0.8f)));
                    g2d.drawRect(screenX, screenY, pixelSize, pixelSize);
                }
            }
        }
        g2d.dispose();
    }

    private void invalidateOverlayCache() {
        cachedBorders = null;
        cachedNationHighlight = null;
        cachedProvinceHighlight = null;
        cachedHoverHighlight = null;
        cachedNationForHighlight = null;
        cachedProvinceForHighlight = null;
        cachedHoverForHighlight = null;
        // Don't invalidate ocean background cache - it's static
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Use renderer to draw the map
        if (renderer != null) {
            renderer.render(g2d, camera);
        } else {
            // Fallback if renderer is not available
            g2d.setColor(Color.BLUE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw UI elements (labels, info panels, etc.)
        drawUI(g2d);
        
        // Draw viewing coordinates
        drawViewingCoordinates(g2d);
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
        
        // First pass: collect all nations and their pixel counts
        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgW; x++) {
                int argb = provinceMask.getRGB(x, y);
                String colorKey = String.format("%d,%d,%d", (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
                String provinceId = colorKeyToProvinceId.get(colorKey);
                if (provinceId != null) {
                    String owner = provinceIdToOwner.get(provinceId);
                    if (owner == null || owner.equals("Ocean") || owner.equals("Uncolonized") || 
                        owner.startsWith("Unknown") || owner.startsWith("rgb_") || owner.startsWith("Color_") || 
                        owner.equals("REMOVE_FROM_MAP") || owner.equals("BORDER") || owner.equals("Water") || 
                        owner.equals("Sea") || owner.equals("Lake") || owner.equals("River"))
                        continue;
                    
                    centroids.putIfAbsent(owner, new double[]{0, 0});
                    counts.put(owner, counts.getOrDefault(owner, 0) + 1);
                    centroids.get(owner)[0] += x;
                    centroids.get(owner)[1] += y;
                }
            }
        }
        
        // Sort nations by size (largest first) to prioritize important nations
        List<Map.Entry<String, Integer>> sortedNations = new ArrayList<>(counts.entrySet());
        sortedNations.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        for (Map.Entry<String, Integer> entry : sortedNations) {
            String owner = entry.getKey();
            int count = entry.getValue();
            
            // Skip uncivilized nations (make them transparent)
            if (owner.equals("Uncivilized")) {
                continue;
            }
            
            // Lower threshold to show more nations, but still filter out tiny ones
            if (count < 20) continue;
            
            double[] c = centroids.get(owner);
            int cx = (int)(c[0] / count);
            int cy = (int)(c[1] / count);
            int sx = x0 + (int)(cx * scale);
            int sy = y0 + (int)(cy * scale);
            
            boolean isSelected = owner.equals(selectedNation);
            
            // Scale font size based on country size and zoom
            int baseFontSize = Math.max(8, Math.min(16, (int)(Math.sqrt(count) * 0.5)));
            int fontSize = Math.max(10, Math.min((int)(baseFontSize * scale), 32));
            if (isSelected) {
                fontSize = Math.max(12, Math.min((int)(baseFontSize * scale * 1.2), 40));
            }
            
            Font labelFont = new Font("Segoe UI", Font.BOLD, fontSize);
            g2d.setFont(labelFont);
            FontMetrics fm = g2d.getFontMetrics();
            int lw = fm.stringWidth(owner) + 16;
            int lh = fm.getHeight() + 8;
            int lx = sx - lw / 2;
            int ly = sy - lh / 2;
            
            // Check if label is visible on screen
            if (lx + lw < 0 || lx > getWidth() || ly + lh < 0 || ly > getHeight()) {
                continue;
            }
            
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
        g2d.drawString(String.format("Zoom: %.1fx", camera.getZoom()), 20, 35);
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
        
        // Province info panel (integrated into main screen)
        if (selectedProvince != null) {
            drawProvinceInfoPanel(g2d);
        }
        
        // Controls hint
        g2d.setColor(new Color(100, 100, 100, 180));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("ESC: Clear selection | SPACE: Center map | Click: Select province", 8, getHeight() - 10);
        
        // Display current viewing coordinates
        drawViewingCoordinates(g2d);
    }

    public void centerOnMapCenter() {
        if (mapBackground == null) {
            System.out.println("Map background not loaded, cannot center");
            return;
        }
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        
        // Center of the map
        int mapCenterX = mapWidth / 2;
        int mapCenterY = mapHeight / 2;
        
        camera.centerOn(mapCenterX, mapCenterY);
        
        System.out.println("Centering on map center: mapW=" + mapWidth + ", mapH=" + mapHeight + 
                          ", mapCenterX=" + mapCenterX + ", mapCenterY=" + mapCenterY);
        
        repaint();
    }
    
    public void centerOnRome() {
        if (mapBackground == null) {
            System.out.println("Map background not loaded, cannot center on Rome");
            return;
        }
        
        // Rome coordinates: x=2210, y=300
        int romeX = 2210;
        int romeY = 300;
        
        camera.centerOn(romeX, romeY);
        
        System.out.println("Centering on Rome: romeX=" + romeX + ", romeY=" + romeY);
        
        repaint();
    }
    
    public void centerOnEurope() {
        if (mapBackground == null) {
            System.out.println("Map background not loaded, cannot center on Europe");
            return;
        }
        
        // Europe coordinates (approximate center of Europe)
        int europeX = 2400;
        int europeY = 300;
        
        camera.centerOn(europeX, europeY);
        
        System.out.println("Centering on Europe: europeX=" + europeX + ", europeY=" + europeY);
        
        repaint();
    }
    
    public void centerOnNation(String nationName) {
        if (mapBackground == null) {
            System.out.println("Map background not loaded, cannot center on nation");
            return;
        }
        
        // Try to find the nation's provinces and center on the first one
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            if (province.getOwner() != null && province.getOwner().equals(nationName)) {
                // Found a province owned by this nation, center on it
                centerOnProvince(province.getId());
                System.out.println("Centering on nation " + nationName + " via province " + province.getId());
                return;
            }
        }
        
        // If no provinces found for this nation, fallback to Europe
        System.out.println("No provinces found for nation " + nationName + ", centering on Europe instead");
        centerOnEurope();
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
    
    public void clearProvinceSelection() {
        selectedProvince = null;
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
        
        // In province_mask.png:
        // - Black (0,0,0): Ocean/background - not clickable
        // - Each province has a unique color (RGB values)
        
        // Skip black pixels (ocean/background)
        if (r == 0 && g == 0 && b == 0) {
            return null;
        }
        
        // Check if this pixel is on a border by looking at neighboring pixels
        if (isBorderPixel(x, y)) {
            return null;
        }
        
        // Try the colorKeyToProvinceId mapping first (from JSON data)
        String colorKey = String.format("%d,%d,%d", r, g, b);
        String provinceId = colorKeyToProvinceId.get(colorKey);
        if (provinceId != null) {
            // Check if this province is uncivilized - if so, return null (not clickable)
            String owner = provinceIdToOwner.get(provinceId);
            if (owner != null && owner.equals("Uncivilized")) {
                return null;
            }
            return provinceId;
        }
        
        // Fallback to old colorToProvinceId mapping
        provinceId = colorToProvinceId.get(argb);
        if (provinceId != null) {
            // Check if this province is uncivilized - if so, return null (not clickable)
            String owner = provinceIdToOwner.get(provinceId);
            if (owner != null && owner.equals("Uncivilized")) {
                return null;
            }
            return provinceId;
        }
        
        return null;
    }
    
    private boolean isBorderPixel(int x, int y) {
        if (provinceMask == null) return false;
        
        int centerColor = provinceMask.getRGB(x, y);
        
        // Check 8 neighboring pixels
        int[] dx = {-1, -1, -1,  0,  0,  1,  1,  1};
        int[] dy = {-1,  0,  1, -1,  1, -1,  0,  1};
        
        for (int i = 0; i < 8; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            
            if (nx >= 0 && nx < provinceMask.getWidth() && 
                ny >= 0 && ny < provinceMask.getHeight()) {
                int neighborColor = provinceMask.getRGB(nx, ny);
                
                // If neighbor is black (ocean) or different color, this might be a border
                if (neighborColor == 0xFF000000 || neighborColor != centerColor) {
                    return true;
                }
            }
        }
        
        return false;
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
                // Check for ocean, uncolonized, uncivilized, or transparent (black) pixel
                int x = mapPoint.x, y = mapPoint.y;
                int argb = provinceMask.getRGB(x, y);
                boolean isBlack = (argb & 0x00FFFFFF) == 0x000000;
                if (!nationName.equals("Ocean") && !nationName.equals("Uncolonized") && 
                    !nationName.equals("Uncivilized") && !isBlack) {
                    selectedNation = nationName;
                    selectedProvince = clickedProvince; // Set selected province for info display
                    lastProvinceClickTime = System.currentTimeMillis(); // Track click time for faster response
                    invalidateOverlayCache();
                    repaint();
                    // Show province info dialog (popup)
                    showProvinceInfo(clickedProvince);
                }
            }
        }
    }

    private void drawProvinceInfoPanel(Graphics2D g2d) {
        if (selectedProvince == null) return;
        
        // Position the panel on the right side of the screen
        int panelX = getWidth() - 320;
        int panelY = 8;
        int panelWidth = 300;
        int panelHeight = 200;
        
        // Draw background panel
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 18, 18);
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 18, 18);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 18, 18);
        
        // Draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2d.drawString("Province Information", panelX + 15, panelY + 25);
        
        // Draw province details
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        int yOffset = 45;
        int lineHeight = 20;
        
        g2d.drawString("Name: " + selectedProvince.getName(), panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        g2d.drawString("Owner: " + selectedProvince.getOwner(), panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        g2d.drawString("Population: " + selectedProvince.getPopulation(), panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        g2d.drawString("Development: " + String.format("%.1f", selectedProvince.getDevelopment()), panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        g2d.drawString("Terrain: " + selectedProvince.getTerrain(), panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        String tradeGoods = String.join(", ", selectedProvince.getTradeGoods());
        if (tradeGoods.isEmpty()) {
            tradeGoods = "None";
        }
        g2d.drawString("Trade Goods: " + tradeGoods, panelX + 15, panelY + yOffset);
        yOffset += lineHeight;
        
        // Add close button hint
        g2d.setColor(new Color(100, 100, 100, 180));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.drawString("ESC: Close panel", panelX + 15, panelY + panelHeight - 10);
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
    
    public void centerOnProvince(String provinceId) {
        if (provinceMask == null || provinceId == null) {
            centerOnMapCenter();
            return;
        }
        
        // Find the province coordinates in the mask
        int imgW = provinceMask.getWidth();
        int imgH = provinceMask.getHeight();
        double sumX = 0, sumY = 0;
        int count = 0;
        
        for (int y = 0; y < imgH; y++) {
            for (int x = 0; x < imgW; x++) {
                int argb = provinceMask.getRGB(x, y);
                String pid = colorToProvinceId.get(argb);
                if (pid != null && pid.equals(provinceId)) {
                    sumX += x;
                    sumY += y;
                    count++;
                }
            }
        }
        
        if (count == 0) {
            System.out.println("Province not found: " + provinceId + ", centering on map center");
            centerOnMapCenter();
            return;
        }
        
        double centerX = sumX / count;
        double centerY = sumY / count;
        
        int panelW = getWidth() > 0 ? getWidth() : 1600;
        int panelH = getHeight() > 0 ? getHeight() : 900;
        
        // Apply zoom scaling
        double scaledX = centerX * zoom;
        double scaledY = centerY * zoom;
        
        offsetX = panelW/2 - (int)scaledX;
        offsetY = panelH/2 - (int)scaledY;
        
        // Cap movement within bounds
        capMovementWithinBounds(offsetX, offsetY);
        
        System.out.println("Centering on province " + provinceId + ": centerX=" + centerX + 
                          ", centerY=" + centerY + ", offsetX=" + offsetX + ", offsetY=" + offsetY);
        
        invalidateOverlayCache();
        repaint();
    }
    
    private void capMovementWithinBounds(int newOffsetX, int newOffsetY) {
        if (mapBackground == null) {
            offsetX = newOffsetX;
            offsetY = newOffsetY;
            return;
        }
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        int panelW = getWidth() > 0 ? getWidth() : 1600;
        int panelH = getHeight() > 0 ? getHeight() : 900;
        
        // Calculate the scaled map dimensions
        double scaledMapWidth = mapWidth * zoom;
        double scaledMapHeight = mapHeight * zoom;
        
        // Calculate boundaries
        // The map should not be dragged beyond the point where the entire map is visible
        int minOffsetX = panelW - (int)scaledMapWidth;
        int maxOffsetX = 0;
        int minOffsetY = panelH - (int)scaledMapHeight;
        int maxOffsetY = 0;
        
        // If the scaled map is smaller than the panel, center it
        if (scaledMapWidth < panelW) {
            minOffsetX = maxOffsetX = (panelW - (int)scaledMapWidth) / 2;
        }
        if (scaledMapHeight < panelH) {
            minOffsetY = maxOffsetY = (panelH - (int)scaledMapHeight) / 2;
        }
        
        // Apply boundary constraints
        offsetX = Math.max(minOffsetX, Math.min(maxOffsetX, newOffsetX));
        offsetY = Math.max(minOffsetY, Math.min(maxOffsetY, newOffsetY));
        
        // Debug output for boundary checking
        if (newOffsetX != offsetX || newOffsetY != offsetY) {
            System.out.println("Movement capped: requested(" + newOffsetX + "," + newOffsetY + 
                              ") -> actual(" + offsetX + "," + offsetY + ")");
            // Could add visual feedback here (e.g., brief border flash)
        }
    }
    
    public void printMapBoundaries() {
        if (mapBackground == null) {
            System.out.println("Map background not loaded");
            return;
        }
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        int panelW = getWidth() > 0 ? getWidth() : 1600;
        int panelH = getHeight() > 0 ? getHeight() : 900;
        
        double scaledMapWidth = mapWidth * zoom;
        double scaledMapHeight = mapHeight * zoom;
        
        int minOffsetX = panelW - (int)scaledMapWidth;
        int maxOffsetX = 0;
        int minOffsetY = panelH - (int)scaledMapHeight;
        int maxOffsetY = 0;
        
        if (scaledMapWidth < panelW) {
            minOffsetX = maxOffsetX = (panelW - (int)scaledMapWidth) / 2;
        }
        if (scaledMapHeight < panelH) {
            minOffsetY = maxOffsetY = (panelH - (int)scaledMapHeight) / 2;
        }
        
        System.out.println("Map Boundaries:");
        System.out.println("  Map size: " + mapWidth + "x" + mapHeight);
        System.out.println("  Panel size: " + panelW + "x" + panelH);
        System.out.println("  Zoom: " + zoom);
        System.out.println("  Scaled map: " + (int)scaledMapWidth + "x" + (int)scaledMapHeight);
        System.out.println("  X bounds: " + minOffsetX + " to " + maxOffsetX);
        System.out.println("  Y bounds: " + minOffsetY + " to " + maxOffsetY);
        System.out.println("  Current offset: " + offsetX + ", " + offsetY);
    }
    
    private void drawViewingCoordinates(Graphics2D g2d) {
        if (mapBackground == null) return;
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        int panelW = getWidth();
        int panelH = getHeight();
        
        // Calculate the visible area of the map
        double scaledMapWidth = mapWidth * zoom;
        double scaledMapHeight = mapHeight * zoom;
        
        // Calculate the top-left corner of the visible map area
        int visibleX = -offsetX;
        int visibleY = -offsetY;
        
        // Convert screen coordinates to map coordinates
        Point topLeft = screenToMap(new Point(0, 0));
        Point bottomRight = screenToMap(new Point(panelW, panelH));
        Point center = screenToMap(new Point(panelW/2, panelH/2));
        
        if (topLeft != null && bottomRight != null && center != null) {
            // Draw coordinate display panel
            int panelX = 8;
            int panelY = getHeight() - 120;
            int panelWidth = 400;
            int panelHeight = 100;
            
            // Background
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 10, 10);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 10, 10);
            
            // Text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.drawString("Viewing Coordinates:", panelX + 10, panelY + 20);
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2d.drawString(String.format("Top Left: (%d, %d)", topLeft.x, topLeft.y), panelX + 10, panelY + 40);
            g2d.drawString(String.format("Bottom Right: (%d, %d)", bottomRight.x, bottomRight.y), panelX + 10, panelY + 60);
            g2d.drawString(String.format("Center: (%d, %d)", center.x, center.y), panelX + 10, panelY + 80);
            
            // Also display in console for debugging
            System.out.println(String.format("Viewing - Top Left: (%d, %d), Top Right: (%d, %d), Center: (%d, %d)", 
                topLeft.x, topLeft.y, bottomRight.x, bottomRight.y, center.x, center.y));
        }
    }
    
    private void showProvinceContextMenu(Province province, Point location) {
        JPopupMenu menu = new JPopupMenu();
        
        // Province information
        JMenuItem info = new JMenuItem("Province Information");
        info.addActionListener(e -> showProvinceInfo(province));
        menu.add(info);
        
        menu.addSeparator();
        
        // Military actions
        JMenuItem buildArmy = new JMenuItem("Build Army");
        buildArmy.addActionListener(e -> buildArmyInProvince(province));
        menu.add(buildArmy);
        
        JMenuItem buildNavy = new JMenuItem("Build Navy");
        buildNavy.addActionListener(e -> buildNavyInProvince(province));
        menu.add(buildNavy);
        
        menu.addSeparator();
        
        // Economic actions
        JMenuItem buildBuilding = new JMenuItem("Build Building");
        buildBuilding.addActionListener(e -> buildBuildingInProvince(province));
        menu.add(buildBuilding);
        
        JMenuItem developProvince = new JMenuItem("Develop Province");
        developProvince.addActionListener(e -> developProvince(province));
        menu.add(developProvince);
        
        menu.addSeparator();
        
        // Diplomatic actions
        JMenuItem declareWar = new JMenuItem("Declare War");
        declareWar.addActionListener(e -> declareWarOnProvince(province));
        menu.add(declareWar);
        
        JMenuItem offerPeace = new JMenuItem("Offer Peace");
        offerPeace.addActionListener(e -> offerPeaceToProvince(province));
        menu.add(offerPeace);
        
        menu.show(this, location.x, location.y);
    }
    
    private void handleProvinceDrag(Point currentPoint, int dx, int dy) {
        // Get the province at the current drag position
        Point mapPoint = screenToMap(currentPoint);
        String provinceId = getProvinceIdAt(mapPoint);
        
        if (provinceId != null) {
            Province draggedProvince = engine.getWorldMap().getProvince(provinceId);
            if (draggedProvince != null) {
                // Handle province drag interactions
                handleProvinceDragInteraction(draggedProvince, dx, dy);
            }
        }
    }
    
    private void handleProvinceDragInteraction(Province province, int dx, int dy) {
        // Implement province-specific drag interactions
        // For example: moving armies, adjusting province borders, etc.
        System.out.println("Province drag: " + province.getName() + " dx=" + dx + " dy=" + dy);
        
        // Example: Move armies in the province
        if (selectedArmy != null && selectedArmy.getLocation().equals(province.getName())) {
            // Move the army within the province based on drag
            System.out.println("Moving army within province: " + province.getName());
        }
        
        // Example: Adjust province development based on drag
        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
            // Significant drag - could trigger province development
            System.out.println("Significant province drag detected for: " + province.getName());
        }
    }
    
    private void buildArmyInProvince(Province province) {
        // Implementation for building army in province
        System.out.println("Building army in province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Building army in " + province.getName(), 
            "Build Army", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buildNavyInProvince(Province province) {
        // Implementation for building navy in province
        System.out.println("Building navy in province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Building navy in " + province.getName(), 
            "Build Navy", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buildBuildingInProvince(Province province) {
        // Implementation for building in province
        System.out.println("Building in province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Building in " + province.getName(), 
            "Build Building", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void developProvince(Province province) {
        // Implementation for developing province
        System.out.println("Developing province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Developing " + province.getName(), 
            "Develop Province", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void declareWarOnProvince(Province province) {
        // Implementation for declaring war
        System.out.println("Declaring war on province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Declaring war on " + province.getName(), 
            "Declare War", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void offerPeaceToProvince(Province province) {
        // Implementation for offering peace
        System.out.println("Offering peace to province: " + province.getName());
        JOptionPane.showMessageDialog(this, 
            "Offering peace to " + province.getName(), 
            "Offer Peace", 
            JOptionPane.INFORMATION_MESSAGE);
    }
} 