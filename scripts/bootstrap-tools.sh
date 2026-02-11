#!/usr/bin/env bash
set -euo pipefail

TOOLS_DIR=${1:-./tools/bin}
mkdir -p "$TOOLS_DIR"

if [ ! -f "$TOOLS_DIR/wwiseutil" ]; then
  echo "Building wwiseutil into $TOOLS_DIR/wwiseutil"
  docker build -f docker/tools/Dockerfile -t sdde-tools-build .
  docker run --rm -v "$TOOLS_DIR:/opt/tools" sdde-tools-build
else
  echo "wwiseutil already present in $TOOLS_DIR/wwiseutil"
fi

if [ ! -f "$TOOLS_DIR/sound2wem" ]; then
  echo "sound2wem not installed (optional; requires WwiseConsole on Windows)."
fi
