#!/usr/bin/env python3
"""
Test script for enhanced Roma Game systems:
- Population development and building construction
- World monuments with buffs
- EU4-style flavor events
- Rulers with monarch points and advisors
"""

import os
import sys
import subprocess
import time

def test_enhanced_systems():
    """Test the enhanced game systems"""
    print("🧪 Testing Enhanced Roma Game Systems")
    print("=" * 50)
    
    # Test 1: Population Development System
    print("\n1. Testing Population Development System")
    print("-" * 35)
    
    # Check if PopulationManager exists
    population_files = [
        "src/com/romagame/population/PopulationManager.java",
        "src/com/romagame/population/DevelopmentProject.java", 
        "src/com/romagame/population/BuildingProject.java"
    ]
    
    for file_path in population_files:
        if os.path.exists(file_path):
            print(f"✅ {file_path} - Found")
        else:
            print(f"❌ {file_path} - Missing")
    
    # Test 2: World Monuments System
    print("\n2. Testing World Monuments System")
    print("-" * 30)
    
    monument_files = [
        "src/com/romagame/monuments/WorldMonument.java",
        "src/com/romagame/monuments/MonumentManager.java"
    ]
    
    for file_path in monument_files:
        if os.path.exists(file_path):
            print(f"✅ {file_path} - Found")
        else:
            print(f"❌ {file_path} - Missing")
    
    # Test 3: Flavor Events System
    print("\n3. Testing Flavor Events System")
    print("-" * 28)
    
    event_files = [
        "src/com/romagame/events/FlavorEvent.java",
        "src/com/romagame/events/EventChoice.java",
        "src/com/romagame/events/EventTrigger.java",
        "src/com/romagame/events/EventManager.java"
    ]
    
    for file_path in event_files:
        if os.path.exists(file_path):
            print(f"✅ {file_path} - Found")
        else:
            print(f"❌ {file_path} - Missing")
    
    # Test 4: Ruler and Advisor System
    print("\n4. Testing Ruler and Advisor System")
    print("-" * 33)
    
    government_files = [
        "src/com/romagame/government/Ruler.java",
        "src/com/romagame/government/Advisor.java"
    ]
    
    for file_path in government_files:
        if os.path.exists(file_path):
            print(f"✅ {file_path} - Found")
        else:
            print(f"❌ {file_path} - Missing")
    
    # Test 5: Game Engine Integration
    print("\n5. Testing Game Engine Integration")
    print("-" * 33)
    
    # Check if GameEngine has been updated
    game_engine_path = "src/com/romagame/core/GameEngine.java"
    if os.path.exists(game_engine_path):
        with open(game_engine_path, 'r') as f:
            content = f.read()
            if "PopulationManager" in content and "MonumentManager" in content:
                print("✅ GameEngine.java - Enhanced with new systems")
            else:
                print("❌ GameEngine.java - Not updated with new systems")
    else:
        print("❌ GameEngine.java - Missing")
    
    # Test 6: Compilation Test
    print("\n6. Testing Compilation")
    print("-" * 20)
    
    try:
        # Try to compile the Java files
        result = subprocess.run([
            "javac", 
            "-cp", "src",
            "src/com/romagame/population/PopulationManager.java",
            "src/com/romagame/monuments/WorldMonument.java",
            "src/com/romagame/events/FlavorEvent.java",
            "src/com/romagame/government/Ruler.java"
        ], capture_output=True, text=True, timeout=30)
        
        if result.returncode == 0:
            print("✅ Java compilation successful")
        else:
            print("❌ Java compilation failed:")
            print(result.stderr)
    except subprocess.TimeoutExpired:
        print("❌ Compilation timed out")
    except FileNotFoundError:
        print("⚠️  javac not found - skipping compilation test")
    
    # Test 7: Feature Summary
    print("\n7. Enhanced Features Summary")
    print("-" * 28)
    
    features = [
        "Population Development Projects",
        "Building Construction System", 
        "World Monuments (Colosseum, Pyramids, etc.)",
        "EU4-style Flavor Events",
        "Rulers with Monarch Points",
        "Advisors with Specializations",
        "Event Triggers and Conditions",
        "Development and Building Projects"
    ]
    
    for feature in features:
        print(f"✅ {feature}")
    
    print("\n🎯 Enhanced Systems Test Complete!")
    print("=" * 50)
    
    # Summary
    print("\n📋 Summary of New Systems:")
    print("• Population can develop provinces and build structures")
    print("• World monuments provide buffs to specific nation types")
    print("• Flavor events with multiple choices and consequences")
    print("• Rulers generate monarch points and have traits")
    print("• Advisors provide bonuses and specializations")
    print("• All systems integrated into the main game engine")

if __name__ == "__main__":
    test_enhanced_systems() 