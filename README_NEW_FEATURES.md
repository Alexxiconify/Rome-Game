# Roma Game - New Features Documentation

## Overview

This document describes the new features added to the Roma Game, including population development systems, building mechanics, world monuments, and EU4-style events with rulers and advisors.

## 🏗️ Population Development System

### Population Types

The game now features seven distinct population types, each with unique development preferences:

1. **Nobles** - Focus on administrative and cultural development
   - 30% chance to develop per turn
   - Prefer building schools and temples
   - High development efficiency

2. **City Folk** - Focus on trade and infrastructure
   - 40% chance to develop per turn
   - Prefer expanding markets and improving roads
   - Good trade development

3. **Craftsmen** - Focus on infrastructure and trade
   - 35% chance to develop per turn
   - Prefer improving roads and expanding markets
   - Balanced development

4. **Peasants** - Focus on basic infrastructure
   - 25% chance to develop per turn
   - Prefer improving roads
   - Basic development capabilities

5. **Slaves** - Limited development capabilities
   - 10% chance to develop per turn
   - Reduced development bonuses (50% efficiency)
   - Only basic infrastructure

6. **Serfs** - Focus on basic infrastructure
   - 20% chance to develop per turn
   - Reduced development bonuses (70% efficiency)
   - Basic infrastructure focus

7. **Soldiers** - Focus on military infrastructure
   - 30% chance to develop per turn
   - Prefer improving defenses
   - Military building construction

### Development Actions

Pops can perform various development actions:

- **Improve Roads** (Infrastructure) - Cost: 50, Bonus: 0.5 development
- **Expand Markets** (Trade) - Cost: 75, Bonus: 0.8 development
- **Build Schools** (Administrative) - Cost: 100, Bonus: 1.2 development
- **Construct Temples** (Culture) - Cost: 80, Bonus: 1.0 development
- **Improve Defenses** (Military) - Cost: 90, Bonus: 1.1 development
- **Establish Trade Routes** (Diplomatic) - Cost: 120, Bonus: 1.5 development

### Building System

Provinces can construct various buildings that provide ongoing bonuses:

#### Trade Buildings
- **Market** - Cost: 50, Maintenance: 0.3, Effect: +10% trade income
- **Harbor** - Cost: 120, Maintenance: 0.7, Effect: +20% naval capacity

#### Cultural Buildings
- **Temple** - Cost: 60, Maintenance: 0.4, Effect: +5% population happiness

#### Military Buildings
- **Barracks** - Cost: 80, Maintenance: 0.5, Effect: +25% recruitment speed
- **Walls** - Cost: 150, Maintenance: 0.8, Effect: +20% defense bonus

#### Infrastructure Buildings
- **Aqueduct** - Cost: 100, Maintenance: 0.6, Effect: +15% population growth

#### Administrative Buildings
- **Library** - Cost: 90, Maintenance: 0.5, Effect: +10% administrative efficiency
- **University** - Cost: 200, Maintenance: 1.0, Effect: +10% technology research

## 🏛️ World Monuments System

### Monument Types

Monuments are categorized into different types:

- **WONDER** - Greatest monuments of the ancient world
- **TEMPLE** - Religious and cultural centers
- **FORTRESS** - Defensive structures
- **PALACE** - Royal and administrative centers
- **THEATER** - Entertainment and cultural venues

### Historical Monuments

#### Roman Monuments
- **Colosseum** - Prestige +20%, Stability +10%, Population Happiness +15%
- **Pantheon** - Religious Unity +25%, Missionary Strength +10%, Clergy Influence +20%
- **Hadrian's Wall** - Defensiveness +30%, Fort Level +2, Separatism -20%

#### Eastern Monuments
- **Persepolis** - Diplomatic Relations +2, Prestige +15%, Trade Efficiency +10%
- **Hanging Gardens** - Population Growth +20%, Development Cost -10%, Agriculture Efficiency +15%
- **Great Wall** - Defensiveness +40%, Fort Level +3, Separatism -30%

#### African Monuments
- **Pyramids** - Prestige +30%, Stability +15%, Religious Unity +20%

#### Celtic Monuments
- **Stonehenge** - Religious Unity +15%, Stability +10%, Culture Conversion +10%

#### Greek Monuments
- **Parthenon** - Culture Conversion +20%, Prestige +15%, Technology Cost -10%
- **Temple of Artemis** - Trade Efficiency +15%, Merchant Slots +1, Diplomatic Relations +1
- **Lighthouse of Alexandria** - Naval Force Limit +30%, Trade Efficiency +20%, Ship Cost -10%

#### Arabian Monuments
- **Temple of Solomon** - Religious Unity +30%, Missionary Strength +15%, Clergy Influence +25%
- **Petra** - Trade Efficiency +20%, Merchant Slots +1, Diplomatic Relations +1

#### Indian Monuments
- **Mohenjo-daro** - Development Cost -15%, Infrastructure Efficiency +20%, Population Growth +10%
- **Angkor Wat** - Religious Unity +25%, Culture Conversion +15%, Stability +10%

### Construction System

Monuments require significant investment to construct:

1. **Start Construction** - 10% of total cost
2. **Advance Construction** - 5% of total cost per advancement
3. **Completion** - Provides permanent bonuses to buffed nations

## ⚡ EU4-Style Events System

### Event Types

Events are categorized into different types:

- **ADMINISTRATIVE** - Government and administrative events
- **DIPLOMATIC** - Foreign relations and diplomacy
- **MILITARY** - Warfare and military events
- **ECONOMIC** - Trade and economic events
- **RELIGIOUS** - Religious and cultural events
- **POLITICAL** - Internal politics and governance
- **DISASTER** - Natural and man-made disasters
- **POSITIVE** - Beneficial events and opportunities

### Historical Events

#### Disasters
- **The Great Fire of Rome** - Devastating fire affecting the capital
- **Plague Outbreak** - Deadly disease spreading through cities
- **Natural Disaster** - Earthquakes and other natural calamities

#### Military Events
- **Barbarian Invasion** - Foreign tribes threatening borders
- **Noble Rebellion** - Internal military conflicts

#### Economic Events
- **Merchant Guild Petition** - Trade guilds requesting privileges
- **Golden Age** - Periods of unprecedented prosperity

#### Religious Events
- **Religious Schism** - Religious tensions dividing population

### Event Choices

Each event typically offers three choices:

1. **Accept** - Usually positive but costly
2. **Decline** - Usually negative but cheap
3. **Compromise** - Balanced approach

### Effects

Event choices affect multiple game statistics:

- **Stability** - Internal political stability
- **Prestige** - International reputation
- **Treasury** - Financial resources
- **Religious Unity** - Religious cohesion
- **Trade Efficiency** - Economic performance
- **Military Tradition** - Military effectiveness

## 👑 Rulers & Advisors System

### Monarch Points

Rulers have three types of monarch points:

- **Administrative Points** - Used for administrative actions
- **Diplomatic Points** - Used for diplomatic actions
- **Military Points** - Used for military actions

### Historical Rulers

#### Roman Emperors
- **Trajan** (Military/Conqueror) - 6/5/4 points
- **Hadrian** (Administrative/Builder) - 4/6/5 points
- **Marcus Aurelius** (Diplomatic/Philosopher) - 5/6/6 points
- **Augustus** (Administrative/Founder) - 6/6/5 points
- **Constantine** (Diplomatic/Reformer) - 5/4/6 points
- **Julian** (Military/Restorer) - 4/5/4 points
- **Vespasian** (Administrative/Stabilizer) - 5/5/4 points
- **Titus** (Diplomatic/Peacemaker) - 4/4/5 points

### Historical Advisors

#### Military Advisors
- **Marcus Agrippa** - Naval Commander, Naval Force Limit +20%

#### Diplomatic Advisors
- **Maecenas** - Diplomat, Diplomatic Relations +1
- **Ovid** - Poet, Cultural Conversion +10%
- **Seneca** - Philosopher, Stability +0.1

#### Administrative Advisors
- **Vitruvius** - Architect, Development Cost -10%
- **Livy** - Historian, Prestige +0.1
- **Pliny the Elder** - Scholar, Technology Cost -5%
- **Tacitus** - Historian, Legitimacy +0.1

## 🎮 UI Integration

### New Panels

1. **EventsPanel** - Displays active events and choices
2. **RulersPanel** - Shows rulers, advisors, and monarch points
3. **MonumentsPanel** - Lists world monuments and construction progress
4. **Enhanced PopulationPanel** - Population development and building management

### Features

- **Real-time Updates** - All panels update automatically
- **Interactive Choices** - Click-based event resolution
- **Progress Tracking** - Visual progress bars for construction
- **Detailed Information** - Comprehensive tooltips and descriptions
- **Historical Context** - Educational information about monuments and events

## 🔧 Technical Implementation

### Core Classes

- **PopulationManager** - Handles population development logic
- **WorldMonument** - Individual monument implementation
- **EventManager** - Manages events, rulers, and advisors
- **EventsPanel** - UI for events system
- **RulersPanel** - UI for rulers and advisors
- **MonumentsPanel** - UI for monuments system

### Integration Points

- **GameEngine** - Central game loop integration
- **CountryManager** - Country-specific effects
- **Province** - Building and development tracking
- **Country** - Monument and event effects

### Data Flow

1. **Population Development** - Pops → Development Actions → Buildings → Effects
2. **Monument Construction** - Investment → Progress → Completion → Buffs
3. **Event Processing** - Random Events → Choices → Effects → Statistics
4. **Ruler Management** - Monarch Points → Actions → Costs → Benefits

## 🎯 Gameplay Impact

### Strategic Depth

- **Population Management** - Different pop types require different strategies
- **Monument Investment** - Long-term strategic decisions
- **Event Choices** - Risk vs. reward decision making
- **Ruler Selection** - Monarch point optimization

### Historical Accuracy

- **Authentic Monuments** - Based on real historical structures
- **Historical Events** - Inspired by actual historical events
- **Realistic Rulers** - Based on actual historical figures
- **Period-Appropriate Advisors** - Historical advisors with accurate specialties

### Balance Considerations

- **Development Costs** - Balanced to prevent rapid expansion
- **Monument Investment** - Significant but achievable costs
- **Event Frequency** - Random but not overwhelming
- **Monarch Point Economy** - Limited but renewable resource

## 🚀 Future Enhancements

### Planned Features

1. **More Monument Types** - Additional historical structures
2. **Dynamic Events** - Events that change based on game state
3. **Ruler Succession** - Dynamic ruler changes over time
4. **Advisor Recruitment** - Active advisor hiring system
5. **Monument Tourism** - Economic benefits from completed monuments
6. **Cultural Influence** - Monuments affecting cultural spread

### Technical Improvements

1. **Performance Optimization** - Efficient event processing
2. **Save/Load System** - Monument and event state persistence
3. **Modding Support** - Easy addition of custom events and monuments
4. **Multiplayer Support** - Monument competition between players

## 📚 Educational Value

The new features provide educational opportunities:

- **Historical Learning** - Real monuments and events teach history
- **Strategic Thinking** - Resource management and decision making
- **Cultural Understanding** - Different nation types and their characteristics
- **Economic Concepts** - Investment, development, and trade mechanics

This comprehensive system adds significant depth and historical authenticity to the Roma Game while maintaining engaging gameplay mechanics. 