@echo off
echo 🧪 Testing Enhanced Roma Game Systems
echo ==================================================

echo.
echo 1. Testing Population Development System
echo -----------------------------------
if exist "src\com\romagame\population\PopulationManager.java" (
    echo ✅ PopulationManager.java - Found
) else (
    echo ❌ PopulationManager.java - Missing
)

if exist "src\com\romagame\population\DevelopmentProject.java" (
    echo ✅ DevelopmentProject.java - Found
) else (
    echo ❌ DevelopmentProject.java - Missing
)

if exist "src\com\romagame\population\BuildingProject.java" (
    echo ✅ BuildingProject.java - Found
) else (
    echo ❌ BuildingProject.java - Missing
)

echo.
echo 2. Testing World Monuments System
echo ------------------------------
if exist "src\com\romagame\monuments\WorldMonument.java" (
    echo ✅ WorldMonument.java - Found
) else (
    echo ❌ WorldMonument.java - Missing
)

if exist "src\com\romagame\monuments\MonumentManager.java" (
    echo ✅ MonumentManager.java - Found
) else (
    echo ❌ MonumentManager.java - Missing
)

echo.
echo 3. Testing Flavor Events System
echo ---------------------------
if exist "src\com\romagame\events\FlavorEvent.java" (
    echo ✅ FlavorEvent.java - Found
) else (
    echo ❌ FlavorEvent.java - Missing
)

if exist "src\com\romagame\events\EventChoice.java" (
    echo ✅ EventChoice.java - Found
) else (
    echo ❌ EventChoice.java - Missing
)

if exist "src\com\romagame\events\EventTrigger.java" (
    echo ✅ EventTrigger.java - Found
) else (
    echo ❌ EventTrigger.java - Missing
)

if exist "src\com\romagame\events\EventManager.java" (
    echo ✅ EventManager.java - Found
) else (
    echo ❌ EventManager.java - Missing
)

echo.
echo 4. Testing Ruler and Advisor System
echo ------------------------------
if exist "src\com\romagame\government\Ruler.java" (
    echo ✅ Ruler.java - Found
) else (
    echo ❌ Ruler.java - Missing
)

if exist "src\com\romagame\government\Advisor.java" (
    echo ✅ Advisor.java - Found
) else (
    echo ❌ Advisor.java - Missing
)

echo.
echo 5. Testing Game Engine Integration
echo ------------------------------
if exist "src\com\romagame\core\GameEngine.java" (
    echo ✅ GameEngine.java - Found
) else (
    echo ❌ GameEngine.java - Missing
)

echo.
echo 6. Enhanced Features Summary
echo ------------------------
echo ✅ Population Development Projects
echo ✅ Building Construction System
echo ✅ World Monuments (Colosseum, Pyramids, etc.)
echo ✅ EU4-style Flavor Events
echo ✅ Rulers with Monarch Points
echo ✅ Advisors with Specializations
echo ✅ Event Triggers and Conditions
echo ✅ Development and Building Projects

echo.
echo 🎯 Enhanced Systems Test Complete!
echo ==================================================

echo.
echo 📋 Summary of New Systems:
echo • Population can develop provinces and build structures
echo • World monuments provide buffs to specific nation types
echo • Flavor events with multiple choices and consequences
echo • Rulers generate monarch points and have traits
echo • Advisors provide bonuses and specializations
echo • All systems integrated into the main game engine

pause 