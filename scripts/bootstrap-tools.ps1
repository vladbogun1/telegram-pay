param(
  [string]$ToolsDir = "./tools/bin"
)

if (-not (Test-Path $ToolsDir)) {
  New-Item -ItemType Directory -Path $ToolsDir | Out-Null
}

if (-not (Test-Path "$ToolsDir/wwiseutil")) {
  Write-Host "Building wwiseutil into $ToolsDir/wwiseutil"
  docker build -f docker/tools/Dockerfile -t sdde-tools-build .
  docker run --rm -v ${ToolsDir}:/opt/tools sdde-tools-build
} else {
  Write-Host "wwiseutil already present in $ToolsDir/wwiseutil"
}

if (-not (Test-Path "$ToolsDir/sound2wem")) {
  Write-Host "sound2wem not installed (optional; requires WwiseConsole on Windows)."
}
