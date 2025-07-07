Write-Host "Starting Roma Game..." -ForegroundColor Green
Write-Host ""

# Compile all Java files to tmp directory
if (!(Test-Path tmp)) { New-Item -ItemType Directory -Path tmp }
javac -cp "src/resources/json-20231013.jar" -d tmp src/Main.java src/com/romagame/map/*.java src/com/romagame/diplomacy/*.java src/com/romagame/ui/*.java src/com/romagame/core/*.java src/com/romagame/country/*.java src/com/romagame/economy/*.java src/com/romagame/military/*.java src/com/romagame/technology/*.java src/com/romagame/colonization/*.java src/com/romagame/events/*.java src/com/romagame/government/*.java src/com/romagame/monuments/*.java src/com/romagame/population/*.java

# Run the game from tmp directory
java -cp "tmp;src/resources/json-20231013.jar" Main

Write-Host ""
Write-Host "Game ended." -ForegroundColor Green
Read-Host "Press Enter to exit" 