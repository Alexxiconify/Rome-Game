@echo off
echo Starting Roma Game...
echo.

REM Compile the game
echo Compiling...
javac -cp "src" src/Main.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Run the game
echo Starting game...
java -cp "src" Main

echo.
echo Game ended.
pause 