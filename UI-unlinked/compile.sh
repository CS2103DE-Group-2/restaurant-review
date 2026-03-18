#!/usr/bin/env bash
# ============================================================
#  compile.sh — Build & run the Restaurant Feedback Kiosk
#
#  Requirements:
#    Java 11+  and  JavaFX 17+ (separate download if needed)
#
#  JavaFX auto-detection order:
#    1. $JAVAFX_HOME environment variable
#    2. Homebrew (macOS):  brew install openjfx
#    3. SDKMAN:            sdk install java 21.0.3-librca
#       (Liberica / BellSoft full JDK ships JavaFX bundled)
#    4. Manual path — edit JAVAFX_HOME below.
# ============================================================
set -euo pipefail

# ── Locate JavaFX ─────────────────────────────────────────────────────────────
if [ -n "${JAVAFX_HOME:-}" ]; then
    JAVAFX_LIB="$JAVAFX_HOME/lib"

# macOS — Homebrew
elif command -v brew &>/dev/null; then
    BREW_FX="$(brew --prefix openjfx 2>/dev/null || true)"
    if [ -d "${BREW_FX}/libexec/lib" ]; then
        JAVAFX_LIB="${BREW_FX}/libexec/lib"
    else
        JAVAFX_LIB=""
    fi

# Linux — common package-manager install paths
elif [ -d "/usr/share/openjfx/lib" ]; then
    JAVAFX_LIB="/usr/share/openjfx/lib"
elif [ -d "/usr/lib/jvm/javafx" ]; then
    JAVAFX_LIB="/usr/lib/jvm/javafx/lib"

# SDKMAN — Liberica full JDK ships JavaFX bundled inside JAVA_HOME
elif [ -n "${JAVA_HOME:-}" ] && [ -d "$JAVA_HOME/lib/javafx" ]; then
    JAVAFX_LIB="$JAVA_HOME/lib/javafx"

else
    JAVAFX_LIB=""
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$SCRIPT_DIR/out"
mkdir -p "$OUT"

# ── Collect sources ────────────────────────────────────────────────────────────
SOURCES=$(find "$SCRIPT_DIR" -name "*.java" \
    ! -path "*/out/*" \
    ! -path "*/.git/*")

# ── Compile ────────────────────────────────────────────────────────────────────
echo "▶  Compiling…"

if [ -n "$JAVAFX_LIB" ]; then
    javac \
        --module-path "$JAVAFX_LIB" \
        --add-modules javafx.controls \
        -d "$OUT" \
        $SOURCES
else
    # Attempt compilation without explicit module-path (works if JavaFX is
    # bundled in the JDK, e.g. Liberica full JDK).
    javac -d "$OUT" $SOURCES
fi

echo "✅  Compilation succeeded."

# ── Copy CSS resource ──────────────────────────────────────────────────────────
if [ -d "$SCRIPT_DIR/styles" ]; then
    cp -r "$SCRIPT_DIR/styles" "$OUT/"
fi

# ── Run ────────────────────────────────────────────────────────────────────────
echo "▶  Launching…"

if [ -n "$JAVAFX_LIB" ]; then
    java \
        --module-path "$JAVAFX_LIB" \
        --add-modules javafx.controls \
        -cp "$OUT" \
        Main
else
    java -cp "$OUT" Main
fi
