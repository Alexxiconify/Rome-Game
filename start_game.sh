#!/bin/bash

echo "Starting Roma Game..."
echo

# Compile the game
echo "Compiling..."
if javac -cp "src" src/Main.java; then
    echo "Compilation successful!"
    echo
    
    # Run the game
    echo "Starting game..."
    java -cp "src" Main
else
    echo "Compilation failed!"
    exit 1
fi

echo
echo "Game ended." 