package com.romagame.ui;

import com.romagame.core.GameEngine;
import com.romagame.core.GameSpeed;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.romagame.map.Country;

public class GameWindow extends JFrame {
    private GameEngine engine;
    private MapPanel mapPanel;
    private InfoPanel infoPanel;
    private ControlPanel controlPanel;
    private JScrollPane mapScrollPane;
    private JTabbedPane mainTabbedPane;
    private SoldiersPanel soldiersPanel;
    private PopulationPanel populationPanel;
    private TradePanel tradePanel;
    private BuildingsPanel buildingsPanel;
    private LawsPanel lawsPanel;
    private ReformsPanel reformsPanel;
    private TechTreePanel techTreePanel;
    private NationalIdeasPanel nationalIdeasPanel;
    private FocusTreePanel focusTreePanel;
    private AIStatusPanel aiStatusPanel;
    private DiplomacyPanel diplomacyPanel;
    private JButton speedButton;
    
    public GameWindow(GameEngine engine) {
        this.engine = engine;
        setupWindow();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        setupGameEngineCallback();
        centerOnPlayerRegion();
    }
    
    private void setupWindow() {
        setTitle("Imperium Romanum - Grand Strategy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set ancient Rome theme colors
        UIManager.put("Panel.background", new Color(139, 69, 19)); // Saddle Brown
        UIManager.put("Button.background", new Color(205, 133, 63)); // Peru
        UIManager.put("Button.foreground", new Color(25, 25, 112)); // Midnight Blue
        UIManager.put("Label.foreground", new Color(255, 215, 0)); // Gold
        UIManager.put("TabbedPane.background", new Color(160, 82, 45)); // Sienna
        UIManager.put("TabbedPane.selected", new Color(210, 105, 30)); // Chocolate
    }
    
    private void createComponents() {
        mapPanel = new MapPanel(engine);
        infoPanel = new InfoPanel(engine);
        controlPanel = new ControlPanel(engine);
        
        // Create scrollable map panel
        mapScrollPane = new JScrollPane(mapPanel);
        // Always show scroll bars to allow scrolling across the entire map
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mapScrollPane.getViewport().setBackground(new Color(139, 69, 19));
        
        // Set viewport size to be smaller than the map to ensure scrolling is possible
        mapScrollPane.getViewport().setPreferredSize(new Dimension(1200, 800));
        
        // Create tabbed panels for different game aspects
        soldiersPanel = new SoldiersPanel(engine);
        populationPanel = new PopulationPanel(engine);
        tradePanel = new TradePanel(engine);
        buildingsPanel = new BuildingsPanel(engine);
        lawsPanel = new LawsPanel(engine);
        reformsPanel = new ReformsPanel(engine);
        techTreePanel = new TechTreePanel(engine);
        nationalIdeasPanel = new NationalIdeasPanel(engine);
        focusTreePanel = new FocusTreePanel(engine);
        aiStatusPanel = new AIStatusPanel(engine);
        diplomacyPanel = new DiplomacyPanel(engine);
        
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Times New Roman", Font.BOLD, 14));
        
        speedButton = new JButton("Speed: Normal");
        speedButton.setFont(new Font("Arial", Font.BOLD, 13));
        speedButton.setForeground(Color.YELLOW);
        speedButton.setBackground(new Color(80, 40, 0));
        speedButton.setFocusPainted(false);
        speedButton.setToolTipText("Click to change speed. Press Space to pause/unpause.");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Create main content area with tabs
        mainTabbedPane.addTab("🗺 Map", new ImageIcon(), mapScrollPane, "Main map view");
        mainTabbedPane.addTab("⚔ Soldiers", new ImageIcon(), soldiersPanel, "Military management");
        mainTabbedPane.addTab("👥 Population", new ImageIcon(), populationPanel, "Population management");
        mainTabbedPane.addTab("💰 Trade", new ImageIcon(), tradePanel, "Trade and economy");
        mainTabbedPane.addTab("🏗 Buildings", new ImageIcon(), buildingsPanel, "Building management");
        mainTabbedPane.addTab("⚖ Laws", new ImageIcon(), lawsPanel, "Legal system");
        mainTabbedPane.addTab("🔄 Reforms", new ImageIcon(), reformsPanel, "Government reforms");
        mainTabbedPane.addTab("🔬 Technology", new ImageIcon(), techTreePanel, "Technology tree");
        mainTabbedPane.addTab("💡 Ideas", new ImageIcon(), nationalIdeasPanel, "National ideas");
        mainTabbedPane.addTab("🎯 Focus", new ImageIcon(), focusTreePanel, "Focus tree");
        mainTabbedPane.addTab("🤖 AI Status", new ImageIcon(), aiStatusPanel, "AI nations status");
        mainTabbedPane.addTab("🤝 Diplomacy", new ImageIcon(), diplomacyPanel, "Diplomacy and war");
        mainTabbedPane.addTab("⏩ Speed", new ImageIcon(), new JPanel(), "Game speed");
        int speedTabIndex = mainTabbedPane.getTabCount() - 1;
        mainTabbedPane.setTabComponentAt(speedTabIndex, speedButton);
        
        // Main content area
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(mainTabbedPane, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);
        
        // Right panel for country info
        add(infoPanel, BorderLayout.EAST);
    }
    
    
    private void setupEventHandlers() {
        // Add tab change listener
        mainTabbedPane.addChangeListener(e -> {
            if (mainTabbedPane.getSelectedIndex() == 0) {
                // Map tab selected - refresh map
                mapPanel.repaint();
            }
        });
        
        speedButton.addActionListener(e -> cycleGameSpeed());
        getRootPane().registerKeyboardAction(
            e -> togglePause(),
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void setupGameEngineCallback() {
        // Set up the UI update callback for the game engine
        engine.setUIUpdateCallback(engineInstance -> updateUI());
    }
    
    public void updateUI() {
        mapPanel.repaint();
        infoPanel.updateInfo();
        controlPanel.updateControls();
        soldiersPanel.updatePanel();
        populationPanel.updatePanel();
        tradePanel.updatePanel();
        buildingsPanel.updatePanel();
        lawsPanel.updatePanel();
        reformsPanel.updatePanel();
        techTreePanel.updatePanel();
        nationalIdeasPanel.updatePanel();
        focusTreePanel.updatePanel();
        aiStatusPanel.updatePanel();
        diplomacyPanel.updateDiplomacy();
        updateSpeedLabel();
    }
    
    private void cycleGameSpeed() {
        GameSpeed current = engine.getGameSpeed();
        GameSpeed next = switch (current) {
            case PAUSED -> GameSpeed.NORMAL;
            case NORMAL -> GameSpeed.FAST;
            case FAST -> GameSpeed.VERY_FAST;
            case VERY_FAST -> GameSpeed.PAUSED;
            case SLOW -> GameSpeed.NORMAL;
        };
        engine.setGameSpeed(next);
        updateSpeedLabel();
    }
    
    private void togglePause() {
        if (engine.getGameSpeed() == GameSpeed.PAUSED) {
            engine.setGameSpeed(GameSpeed.NORMAL);
        } else {
            engine.setGameSpeed(GameSpeed.PAUSED);
        }
        updateSpeedLabel();
    }
    
    private void updateSpeedLabel() {
        String text = switch (engine.getGameSpeed()) {
            case PAUSED -> "Speed: Paused";
            case NORMAL -> "Speed: Normal";
            case FAST -> "Speed: Fast";
            case VERY_FAST -> "Speed: Very Fast";
            case SLOW -> "Speed: Slow";
        };
        speedButton.setText(text);
    }
    
    public void centerOnPlayerRegion() {
        Country player = engine.getCountryManager().getPlayerCountry();
        if (player == null) {
            // No player country selected, center on Europe
            mapPanel.centerOnEurope();
            return;
        }
        
        String name = player.getName();
        System.out.println("Centering on player country: " + name);
        
        // Use the MapPanel's centerOnNation method which has proper fallback to Europe
        mapPanel.centerOnNation(name);
    }
} 