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
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.event.ActionEvent;
import com.romagame.military.Army;
import com.romagame.military.MilitaryManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.romagame.ui.Camera;
import com.romagame.ui.MapRenderer;
import javax.swing.Timer;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

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
    private static final long HOLD_THRESHOLD_MS = 50; // 200ms hold required
    private static final int HOLD_DISTANCE_THRESHOLD = 5; // 5 pixels max movement during hold
    
    // Data mappings
    private Map<Integer, String> colorToProvinceId = new HashMap<>();
    private Map<String, Color> countryColors = new HashMap<>();
    private Map<String, Color> provinceIdToOwnerColor = new HashMap<>();
    private Map<String, String> provinceIdToOwner = new HashMap<>();
    private Map<String, String> nationToColor = new HashMap<>();
    private List<String> nationList = new ArrayList<>();
    private Map<String, String> colorKeyToProvinceId = new HashMap<>();
    
    private BufferedImage provinceMask;
    private BufferedImage mapBackground;

    // HOI4-style edge scrolling
    private Timer edgeScrollTimer;
    private static final int EDGE_SCROLL_ZONE = 60; // px from edge (restored buffer)
    private static final int EDGE_SCROLL_SPEED = 30; // px per timer tick

    private Map<String, Point> nationToViewpoint = new HashMap<>();
    private Map<String, Point> provinceIdToCentroid = new HashMap<>();

    private static int lastTopLeftX = Integer.MIN_VALUE;
    private static int lastTopLeftY = Integer.MIN_VALUE;
    private static int lastBottomRightX = Integer.MIN_VALUE;
    private static int lastBottomRightY = Integer.MIN_VALUE;
    private static int lastCenterX = Integer.MIN_VALUE;
    private static int lastCenterY = Integer.MIN_VALUE;

    public MapPanel(GameEngine engine) {
        this.engine = engine;
        this.camera = new Camera();
        this.renderer = new MapRenderer();
        setupPanel();
        loadMapBackground();
        loadProvinceMask();
        loadNationsAndProvinces();
        System.out.println("[DEBUG] Loaded " + colorToProvinceId.size() + " province mappings, " + nationList.size() + " nations");
        setupMouseListeners();
        startEdgeScrollTimer();
        centerOnPlayerNation();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(4974, 2516)); // Full map dimensions
        setBackground(new Color(40, 70, 120)); // Deep blue
        setFocusable(true);
        setDoubleBuffered(true);
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
        
        // + key to zoom in
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0), "zoomIn");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "zoomIn"); // Numpad +
        getActionMap().put("zoomIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.zoomBy(1.1); // Zoom in by 10%
                repaint();
            }
        });
        
        // - key to zoom out
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "zoomOut"); // Numpad -
        getActionMap().put("zoomOut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.zoomBy(0.9); // Zoom out by 10%
                repaint();
            }
        });
        
        // Arrow keys and WASD for panning
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "panLeft");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('A'), "panLeft");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "panRight");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('D'), "panRight");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "panUp");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('W'), "panUp");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "panDown");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('S'), "panDown");
        getActionMap().put("panLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.moveBy(-50, 0); // Pan left
                repaint();
            }
        });
        getActionMap().put("panRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.moveBy(50, 0); // Pan right
                repaint();
            }
        });
        getActionMap().put("panUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.moveBy(0, -50); // Pan up
                repaint();
            }
        });
        getActionMap().put("panDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.moveBy(0, 50); // Pan down
                repaint();
            }
        });
        // F5 for hot reload of map assets
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "hotReload");
        getActionMap().put("hotReload", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMapBackground();
                loadProvinceMask();
                camera.centerOn(2481, 560);
                repaint();
            }
        });
    }

    private void loadMapBackground() {
        try {
            // Load start.png as the main background (with borders)
            File mapFile = new File("src/resources/img/start.png");
            if (!mapFile.exists()) {
                // Try absolute path fallback (edit this path if needed)
                mapFile = new File("C:/Users/taylo/Documents/projects/Roma Game/src/resources/img/start.png");
            }
            if (mapFile.exists()) {
                BufferedImage loadedMapBackground = ImageIO.read(mapFile);
                if (loadedMapBackground != null) {
                    // Update preferred size to match the actual map dimensions
                    setPreferredSize(new Dimension(loadedMapBackground.getWidth(), loadedMapBackground.getHeight()));
                    revalidate(); // Notify layout manager of size change
                    
                    // Set map dimensions in camera
                    camera.setMapDimensions(loadedMapBackground.getWidth(), loadedMapBackground.getHeight());
                    // Center camera on Rome (2481, 704) at start
                    camera.centerOn(2481, 704);
                    
                    // Set background in renderer
                    renderer.setMapBackground(loadedMapBackground);
                    this.mapBackground = loadedMapBackground;
                } else {
                    createGradientBackground();
                }
            } else {
                createGradientBackground();
            }
            
            // Load start1.png as the borderless overlay
            File borderlessFile = new File("src/resources/img/start1.png");
            if (borderlessFile.exists()) {
                BufferedImage borderlessOverlay = ImageIO.read(borderlessFile);
                if (borderlessOverlay != null) {
                    renderer.setBorderlessOverlay(borderlessOverlay);
                }
            }
        } catch (IOException e) {
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
                this.provinceMask = provinceMask;
                repaint();
                if (provinceMask != null) {
                } else {
                }
            } else {
            }
        } catch (IOException e) {
        }
    }

    private void updateProvinceColorMap() {
        // No-op: do not recolor provinces, just use the background image
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
                    }
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                String oldHoveredProvinceId = hoveredProvinceId;
                mouseMapPoint = camera.screenToMap(e.getX(), e.getY());
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
                    repaint();
                }
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
                // Always use the color at the clicked location in provinceMask to map to provinceId
                Point mapPoint = camera.screenToMap(e.getX(), e.getY());
                if (provinceMask != null && mapPoint != null) {
                    int x = mapPoint.x, y = mapPoint.y;
                    if (x >= 0 && y >= 0 && x < provinceMask.getWidth() && y < provinceMask.getHeight()) {
                        int argb = provinceMask.getRGB(x, y);
                        int r = (argb >> 16) & 0xFF;
                        int g = (argb >> 8) & 0xFF;
                        int b = argb & 0xFF;
                        String colorKey = String.format("%d,%d,%d", r, g, b);
                        String provinceId = colorKeyToProvinceId.get(colorKey);
                        if (provinceId == null) {
                            provinceId = colorToProvinceId.get(argb);
                        }
                        if (provinceId != null) {
                            handleProvinceClick(e.getPoint());
                        } else {
                            // No province found at click location
                        }
                    }
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
                        showProvinceContextMenu(clickedProvince, e.getPoint());
                    }
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        camera.update();
        camera.setViewportSize(getWidth(), getHeight());
        renderer.render(g2d, camera, getVisibleRect());
        // UI overlays and province highlights remain here
        // Highlight hovered province with a soft white transparent glow
        if (hoveredProvinceId != null && provinceMask != null) {
            // Find the color of the hovered province in the mask
            String owner = provinceIdToOwner.get(hoveredProvinceId);
            int highlightAlpha = 70; // Soft glow
            Color glow = new Color(255, 255, 255, highlightAlpha);
            // Find the colorKey for this province
            String provinceColorKey = null;
            for (Map.Entry<String, String> entry : colorKeyToProvinceId.entrySet()) {
                if (entry.getValue().equals(hoveredProvinceId)) {
                    provinceColorKey = entry.getKey();
                    break;
                }
            }
            if (provinceColorKey != null) {
                String[] rgb = provinceColorKey.split(",");
                int r = Integer.parseInt(rgb[0]);
                int gCol = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                // Scan the visible area only for performance
                Rectangle visible = getVisibleRect();
                for (int y = visible.y; y < visible.y + visible.height; y++) {
                    for (int x = visible.x; x < visible.x + visible.width; x++) {
                        int argb = provinceMask.getRGB(x, y);
                        int pr = (argb >> 16) & 0xFF;
                        int pg = (argb >> 8) & 0xFF;
                        int pb = argb & 0xFF;
                        if (pr == r && pg == gCol && pb == b) {
                            // Convert map pixel to screen coordinates
                            Point screenPt = camera.mapToScreen(x, y);
                            // Draw a soft white oval (glow)
                            g2d.setColor(glow);
                            g2d.fillOval(screenPt.x - 2, screenPt.y - 2, 5, 5);
                        }
                    }
                }
            }
        }
        // Overlay province IDs at their centroids for visual identification
        if (provinceMask != null && mapBackground != null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.setColor(new Color(255,255,255,180));
            java.util.List<Province> provinces = engine.getWorldMap().getAllProvinces();
            for (Province province : provinces) {
                Point centroid = provinceIdToCentroid.get(province.getId());
                if (centroid != null) {
                    Point screenPt = camera.mapToScreen(centroid.x, centroid.y);
                    g2d.drawString(province.getId(), screenPt.x + 4, screenPt.y - 4);
                }
            }
        }
        drawUI(g2d);
        drawViewingCoordinates(g2d);
    }

    public Color getProvinceColor(Province province) {
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

    public void drawNationLabels(Graphics2D g2d, int x0, int y0, double scale, int imgW, int imgH) {
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
            return;
        }
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        
        // Center of the map
        int mapCenterX = mapWidth / 2;
        int mapCenterY = mapHeight / 2;
        
        camera.centerOn(mapCenterX, mapCenterY);
        
        repaint();
    }
    
    public void centerOnRome() {
        if (mapBackground == null) {
            return;
        }
        
        // Rome coordinates: x=2210, y=300
        int romeX = 2210;
        int romeY = 300;
        
        camera.centerOn(romeX, romeY);
        
        repaint();
    }
    
    public void centerOnEurope() {
        if (mapBackground == null) {
            return;
        }
        
        // Europe coordinates (approximate center of Europe)
        int europeX = 2400;
        int europeY = 300;
        
        camera.centerOn(europeX, europeY);
        
        repaint();
    }
    
    public void centerOnNation(String nationName) {
        if (mapBackground == null) {
            return;
        }
        
        if (nationToViewpoint.containsKey(nationName)) {
            Point vp = nationToViewpoint.get(nationName);
            centerOnCoordinates(vp.x, vp.y);
            return;
        }
        
        // Try to find the nation's provinces and center on the first one
        for (Province province : engine.getWorldMap().getAllProvinces()) {
            if (province.getOwner() != null && province.getOwner().equals(nationName)) {
                // Found a province owned by this nation, center on it
                centerOnProvince(province.getId());
                return;
            }
        }
        
        // If no provinces found for this nation, fallback to Europe
        centerOnEurope();
    }
    
    public void selectNation(String nation) {
        selectedNation = nation;
        repaint();
    }
    
    public void clearNationSelection() {
        selectedNation = null;
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

    public void loadNationsAndProvinces() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get("src/resources/data/nations_and_provinces.json")));
            JSONObject root = new JSONObject(jsonText);
            // Parse nations
            nationList.clear();
            nationToColor.clear();
            nationToViewpoint.clear();
            JSONArray nations = root.optJSONArray("nations");
            if (nations != null) {
                for (int i = 0; i < nations.length(); i++) {
                    JSONObject nation = nations.getJSONObject(i);
                    String name = nation.getString("name");
                    JSONArray colorArr = nation.getJSONArray("color");
                    String colorStr = colorArr.getInt(0) + "," + colorArr.getInt(1) + "," + colorArr.getInt(2);
                    nationToColor.put(name, colorStr);
                    nationList.add(name);
                    if (nation.has("starting_viewpoint")) {
                        JSONArray vpArr = nation.getJSONArray("starting_viewpoint");
                        nationToViewpoint.put(name, new Point(vpArr.getInt(0), vpArr.getInt(1)));
                    }
                }
            }
            // Parse provinces
            colorKeyToProvinceId.clear();
            provinceIdToOwner.clear();
            provinceIdToCentroid.clear();
            JSONArray provincesArr = root.optJSONArray("provinces");
            if (provincesArr != null) {
                for (int i = 0; i < provincesArr.length(); i++) {
                    JSONObject province = provincesArr.getJSONObject(i);
                    String provinceId = province.getString("province_id");
                    JSONArray colorArr = province.getJSONArray("owner_color");
                    String colorKey = colorArr.getInt(0) + "," + colorArr.getInt(1) + "," + colorArr.getInt(2);
                    String owner = province.getString("owner");
                    colorKeyToProvinceId.put(colorKey, provinceId);
                    provinceIdToOwner.put(provinceId, owner);
                    if (province.has("centroid_x") && province.has("centroid_y")) {
                        provinceIdToCentroid.put(provinceId, new Point(province.getInt("centroid_x"), province.getInt("centroid_y")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Point mapToScreen(int mapX, int mapY) {
        return camera.mapToScreen(mapX, mapY);
    }

    private Point screenToMap(Point p) {
        if (provinceMask == null || p == null) return null;
        return camera.screenToMap(p.x, p.y);
    }

    private String getProvinceIdAt(Point mapPoint) {
        if (provinceMask == null || mapPoint == null) {
            return null;
        }
        int x = mapPoint.x, y = mapPoint.y;
        if (x < 0 || y < 0 || x >= provinceMask.getWidth() || y >= provinceMask.getHeight()) {
            return null;
        }
        int argb = provinceMask.getRGB(x, y);
        // Extract RGB values from ARGB
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
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
                    repaint();
                    // Show province info dialog (popup)
                    showProvinceInfo(clickedProvince);
                }
            }
        } else {
            // Only print debug info on click if no province found
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
            centerOnMapCenter();
            return;
        }
        
        double centerX = sumX / count;
        double centerY = sumY / count;
        
        // Center camera on the province coordinates
        camera.centerOn(centerX, centerY);
        
        repaint();
    }
    
    // Camera handles bounds checking internally, so this method is no longer needed
    
    public void printMapBoundaries() {
        if (mapBackground == null) {
            return;
        }
        
        int mapWidth = mapBackground.getWidth();
        int mapHeight = mapBackground.getHeight();
        int panelW = getWidth() > 0 ? getWidth() : 1600;
    }
    
    private void drawViewingCoordinates(Graphics2D g2d) {
        if (mapBackground == null) return;
        
        int panelW = getWidth();
        int panelH = getHeight();
        
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
            boolean shouldLog = false;
            if (Math.abs(topLeft.x - lastTopLeftX) >= 100 || Math.abs(topLeft.y - lastTopLeftY) >= 100 ||
                Math.abs(bottomRight.x - lastBottomRightX) >= 100 || Math.abs(bottomRight.y - lastBottomRightY) >= 100 ||
                Math.abs(center.x - lastCenterX) >= 100 || Math.abs(center.y - lastCenterY) >= 100) {
                shouldLog = true;
            }
            if (shouldLog) {
                lastTopLeftX = topLeft.x;
                lastTopLeftY = topLeft.y;
                lastBottomRightX = bottomRight.x;
                lastBottomRightY = bottomRight.y;
                lastCenterX = center.x;
                lastCenterY = center.y;
            }
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
        
        // Example: Move armies in the province
        if (selectedArmy != null && selectedArmy.getLocation().equals(province.getName())) {
            // Move the army within the province based on drag
        }
        
        // Example: Adjust province development based on drag
        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
            // Significant drag - could trigger province development
        }
    }
    
    private void buildArmyInProvince(Province province) {
        // Implementation for building army in province
        JOptionPane.showMessageDialog(this, 
            "Building army in " + province.getName(), 
            "Build Army", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buildNavyInProvince(Province province) {
        // Implementation for building navy in province
        JOptionPane.showMessageDialog(this, 
            "Building navy in " + province.getName(), 
            "Build Navy", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buildBuildingInProvince(Province province) {
        // Implementation for building in province
        JOptionPane.showMessageDialog(this, 
            "Building in " + province.getName(), 
            "Build Building", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void developProvince(Province province) {
        // Implementation for developing province
        JOptionPane.showMessageDialog(this, 
            "Developing " + province.getName(), 
            "Develop Province", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void declareWarOnProvince(Province province) {
        // Implementation for declaring war
        JOptionPane.showMessageDialog(this, 
            "Declaring war on " + province.getName(), 
            "Declare War", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void offerPeaceToProvince(Province province) {
        // Implementation for offering peace
        JOptionPane.showMessageDialog(this, 
            "Offering peace to " + province.getName(), 
            "Offer Peace", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // HOI4-style edge scrolling
    private void startEdgeScrollTimer() {
        edgeScrollTimer = new Timer(20, e -> {
            PointerInfo pi = MouseInfo.getPointerInfo();
            if (pi == null) return;
            Point mouseLoc = pi.getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, this);
            int w = getWidth();
            int h = getHeight();
            boolean moved = false;
            if (mouseLoc.x >= 0 && mouseLoc.x < w && mouseLoc.y >= 0 && mouseLoc.y < h) {
                if (mouseLoc.x < EDGE_SCROLL_ZONE) {
                    camera.moveBy(-EDGE_SCROLL_SPEED, 0);
                    moved = true;
                } else if (mouseLoc.x > w - EDGE_SCROLL_ZONE) {
                    camera.moveBy(EDGE_SCROLL_SPEED, 0);
                    moved = true;
                }
                if (mouseLoc.y < EDGE_SCROLL_ZONE) {
                    camera.moveBy(0, -EDGE_SCROLL_SPEED);
                    moved = true;
                } else if (mouseLoc.y > h - EDGE_SCROLL_ZONE) {
                    camera.moveBy(0, EDGE_SCROLL_SPEED);
                    moved = true;
                }
            }
            if (moved) repaint();
        });
        edgeScrollTimer.start();
    }

    public void centerOnPlayerNation() {
        String playerNation = null;
        if (engine != null && engine.getCountryManager() != null && engine.getCountryManager().getPlayerCountry() != null) {
            playerNation = engine.getCountryManager().getPlayerCountry().getName();
        }
        if (playerNation != null && nationToViewpoint.containsKey(playerNation)) {
            Point vp = nationToViewpoint.get(playerNation);
            camera.centerOn(vp.x, vp.y);
        } else {
            centerOnEurope(); // fallback
        }
        repaint();
    }
} 