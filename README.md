# Roma Game - Grand Strategy Game

A Java-based grand strategy game inspired by Hearts of Iron 4, Europa Universalis 4/5, and Victoria 2/3, using the Q-BAM province map as reference.

## Features

### Core Game Systems

- **Time Management**: Real-time game progression with multiple speed settings
- **Province-Based Map**: Using Q-BAM reference for accurate historical geography
- **Country Management**: Nations with governments, religions, cultures, and ideas
- **Economy System**: Trade, resources, income, and expenses (Victoria-style)
- **Military System**: Units, warfare, and technology progression
- **Diplomacy**: Relations, alliances, and trade agreements
- **Technology**: Research and development system

### Game Mechanics

- **EU4-style**: Country management, stability, legitimacy, prestige
- **Victoria-style**: Economic simulation, population, resources
- **HOI4-style**: Military units, warfare, technology trees
- **Modern UI**: Clean, intuitive interface with keyboard shortcuts

### Starting Countries

- **Europe**: France, England, Castile, Brandenburg, Austria, Sweden, Denmark, Muscovy
- **Asia**: Ming, Japan, Delhi, Ottomans, Persia
- **Africa**: Mamluks, Tunisia, Morocco, Mali
- **Americas**: Aztec, Inca, Cherokee, Iroquois
- **Oceania**: Australia, New Zealand

## Controls

### Keyboard Shortcuts

- **Space**: Pause/Resume game
- **1**: Normal speed
- **2**: Fast speed  
- **3**: Very fast speed

### Mouse Controls

- **Left Click**: Select provinces
- **Left Drag**: Pan map
- **Mouse Wheel**: Zoom in/out

### UI Buttons

- **Speed Controls**: Pause, Slow, Normal, Fast, Very Fast
- **Actions**: Recruit units, Build buildings, Diplomacy

## Game Systems

### Economy

- **Resources**: Gold, Iron, Grain, Wool, Wine, Spices, etc.
- **Trade Nodes**: Major trading centers with regional goods
- **Income/Expenses**: Dynamic economic simulation
- **Treasury**: Country wealth management

### Military

- **Unit Types**: Infantry, Cavalry, Artillery, Ships
- **Army Management**: Morale, organization, location
- **War System**: War score, peace terms, alliances
- **Technology**: Military research and development

### Diplomacy

- **Relations**: Bilateral diplomatic relations (-100 to +100)
- **Alliances**: Military and political alliances
- **Trade Agreements**: Economic cooperation
- **War/Peace**: Conflict resolution system

### Technology

- **Categories**: Military, Diplomatic, Administrative, Trade
- **Research**: Progress-based technology development
- **Effects**: Bonuses to various game systems

## Installation & Running

### Prerequisites

- Java 17 or higher
- Maven (for building)

### Build and Run

```bash
# Compile the project
javac -d bin src/**/*.java

# Run the game
java -cp bin com.romagame.Main
```

### Development

```bash
# Run with debug information
java -cp bin -Ddebug=true com.romagame.Main
```

## Game Architecture

### Core Components

- **GameEngine**: Main game loop and system coordination
- **WorldMap**: Province and country management
- **CountryManager**: Nation state handling
- **EconomyManager**: Trade and resource systems
- **MilitaryManager**: Warfare and unit management
- **DiplomacyManager**: International relations
- **TechnologyManager**: Research and development

### UI Components

- **GameWindow**: Main application window
- **MapPanel**: Interactive world map display
- **InfoPanel**: Country and game information
- **ControlPanel**: Game controls and actions

## Game Balance

### Starting Conditions (1444)

- **Major Powers**: Start with 1-3 provinces and basic military
- **Resources**: Balanced starting resources based on region
- **Technology**: All countries start at similar tech levels
- **Diplomacy**: Neutral relations between most countries

### Progression

- **Time Period**: 1444-1821 (EU4-style timeline)
- **Technology**: Gradual advancement through research
- **Expansion**: Conquest, colonization, and diplomacy
- **Victory**: No specific victory condition - sandbox gameplay

## Future Development

### Planned Features

- **Save/Load System**: Game state persistence
- **Multiplayer**: Network-based multiplayer
- **Modding Support**: Custom scenarios and content
- **Advanced AI**: Improved computer opponent behavior
- **Historical Events**: Dynamic historical events system
- **Colonization**: New World exploration and settlement

### Technical Improvements

- **Performance**: Optimized rendering and game loop
- **Graphics**: Enhanced map visualization
- **Audio**: Sound effects and music
- **Networking**: Real-time multiplayer support

## Contributing

This is a learning project demonstrating grand strategy game development concepts. Feel free to:

1. **Fork** the repository
2. **Create** a feature branch
3. **Implement** improvements
4. **Submit** a pull request

## License

This project is for educational purposes. The Q-BAM map reference is used with permission from the original creator.

## Acknowledgments

- **Q-BAM Map**: Created by DinoSpain on DeviantArt
- **Paradox Games**: Inspiration from EU4, HOI4, and Victoria series
- **Java Community**: Open source libraries and tools

---

*Roma Game - A grand strategy adventure awaits!* 