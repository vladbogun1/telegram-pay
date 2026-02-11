Write-Host "SDDE NeuroDub Studio tool check"
$tools = @("ffmpeg", "WwiseConsole.exe", "wwiseutil", "SDDEUnpacker", "SDDE Text Tool", "FileRedirector")
foreach ($tool in $tools) {
  Write-Host "- $tool: configure path in UI"
}
if (Get-Command ffmpeg -ErrorAction SilentlyContinue) {
  $version = ffmpeg -version | Select-Object -First 1
  Write-Host "ffmpeg detected: $version"
} else {
  Write-Host "ffmpeg not found in PATH"
}
