Write-Host "Starting Roma Game..." -ForegroundColor Green
Write-Host ""

# Compile the game
Write-Host "Compiling..." -ForegroundColor Yellow
try {
    javac -cp "src" src/Main.java
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "Compilation error: $_" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host ""

# Run the game
Write-Host "Starting game..." -ForegroundColor Yellow
try {
    java -cp "src" Main
} catch {
    Write-Host "Game error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "Game ended." -ForegroundColor Green
Read-Host "Press Enter to exit" 