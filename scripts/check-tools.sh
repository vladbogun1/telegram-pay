#!/usr/bin/env bash
set -euo pipefail

TOOLS=("ffmpeg" "WwiseConsole.exe" "wwiseutil" "SDDEUnpacker" "SDDE Text Tool" "FileRedirector")

echo "SDDE NeuroDub Studio tool check"
for tool in "${TOOLS[@]}"; do
  echo "- ${tool}: configure path in UI"
done

if command -v ffmpeg >/dev/null 2>&1; then
  echo "ffmpeg detected: $(ffmpeg -version | head -n 1)"
else
  echo "ffmpeg not found in PATH"
fi
