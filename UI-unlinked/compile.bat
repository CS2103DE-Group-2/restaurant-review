@echo off
:: ============================================================
::  compile.bat — Build & run the Restaurant Feedback Kiosk (Windows)
::
::  Requirements:
::    Java 11+  and  JavaFX 17+ SDK
::
::  JavaFX auto-detection order:
::    1. JAVAFX_HOME environment variable
::    2. Manual fallback — set JAVAFX_HOME below or in your system env.
::
::  To set JAVAFX_HOME for your session:
::    set JAVAFX_HOME=C:\path\to\javafx-sdk-21
:: ============================================================
setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set OUT=%SCRIPT_DIR%out
if not exist "%OUT%" mkdir "%OUT%"

:: ── Locate JavaFX ────────────────────────────────────────────────────────────
set JAVAFX_LIB=
if defined JAVAFX_HOME (
    if exist "%JAVAFX_HOME%\lib" (
        set JAVAFX_LIB=%JAVAFX_HOME%\lib
    )
)

:: ── Collect sources ──────────────────────────────────────────────────────────
set SOURCES=
for /r "%SCRIPT_DIR%" %%f in (*.java) do (
    echo %%f | findstr /i "\\out\\" >nul || set SOURCES=!SOURCES! "%%f"
)

:: ── Compile ──────────────────────────────────────────────────────────────────
echo ^>  Compiling...
if defined JAVAFX_LIB (
    javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls -d "%OUT%" %SOURCES%
) else (
    javac -d "%OUT%" %SOURCES%
)
if errorlevel 1 (
    echo [ERROR] Compilation failed. Make sure JAVAFX_HOME is set correctly.
    exit /b 1
)
echo [OK] Compilation succeeded.

:: ── Copy CSS resource ────────────────────────────────────────────────────────
if exist "%SCRIPT_DIR%styles" (
    xcopy /e /i /q "%SCRIPT_DIR%styles" "%OUT%\styles\" >nul
)

:: ── Run ──────────────────────────────────────────────────────────────────────
echo ^>  Launching...
if defined JAVAFX_LIB (
    java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls -cp "%OUT%" Main
) else (
    java -cp "%OUT%" Main
)
endlocal
