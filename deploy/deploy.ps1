# ============================================================
#  Guacamole Admin — One-click build & deploy script
#  Run from the project root:  .\deploy\deploy.ps1
# ============================================================

$ErrorActionPreference = "Stop"

$TOMCAT_HOME  = "D:\apache-tomcat-10.1.55"
$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot
$WAR_NAME     = "guacamole-admin-1.0.war"
$WAR_SOURCE   = "$PROJECT_ROOT\target\$WAR_NAME"
$WAR_DEST     = "$TOMCAT_HOME\webapps\$WAR_NAME"
$SETENV_SRC   = "$PROJECT_ROOT\deploy\setenv.bat"
$SETENV_DEST  = "$TOMCAT_HOME\bin\setenv.bat"

Write-Host ""
Write-Host "=== Guacamole Admin Deploy ===" -ForegroundColor Cyan

# ── Step 1: Build ─────────────────────────────────────────────
Write-Host "`n[1/4] Building WAR with Maven..." -ForegroundColor Yellow
Set-Location $PROJECT_ROOT
mvn clean package -q
if ($LASTEXITCODE -ne 0) { Write-Error "Maven build failed."; exit 1 }
Write-Host "      Build successful: $WAR_SOURCE" -ForegroundColor Green

# ── Step 2: Stop Tomcat if running ───────────────────────────
Write-Host "`n[2/4] Stopping Tomcat (if running)..." -ForegroundColor Yellow
$tomcatProc = Get-Process -Name "java" -ErrorAction SilentlyContinue |
              Where-Object { $_.Path -like "*jdk*" }
if ($tomcatProc) {
    & "$TOMCAT_HOME\bin\shutdown.bat" 2>$null
    Start-Sleep -Seconds 3
    Write-Host "      Tomcat stopped." -ForegroundColor Green
} else {
    Write-Host "      Tomcat was not running." -ForegroundColor Gray
}

# ── Step 3: Copy setenv.bat and WAR ──────────────────────────
Write-Host "`n[3/4] Deploying files..." -ForegroundColor Yellow

# Copy setenv.bat (only if not already customised)
if (-not (Test-Path $SETENV_DEST)) {
    Copy-Item $SETENV_SRC $SETENV_DEST
    Write-Host "      Copied setenv.bat — EDIT DB PASSWORD in: $SETENV_DEST" -ForegroundColor Magenta
} else {
    Write-Host "      setenv.bat already exists — skipping (edit manually if needed)." -ForegroundColor Gray
}

# Remove old exploded WAR directory if present
$explodedDir = "$TOMCAT_HOME\webapps\guacamole-admin-1.0"
if (Test-Path $explodedDir) {
    Remove-Item -Recurse -Force $explodedDir
    Write-Host "      Removed old deployment directory." -ForegroundColor Gray
}

# Copy WAR
Copy-Item $WAR_SOURCE $WAR_DEST -Force
Write-Host "      WAR deployed to: $WAR_DEST" -ForegroundColor Green

# ── Step 4: Start Tomcat ──────────────────────────────────────
Write-Host "`n[4/4] Starting Tomcat..." -ForegroundColor Yellow
Start-Process "$TOMCAT_HOME\bin\startup.bat" -NoNewWindow
Start-Sleep -Seconds 4

Write-Host ""
Write-Host "=== Done! ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "  Application URL : http://localhost:8080/guacamole-admin-1.0/login" -ForegroundColor White
Write-Host "  Default login   : superadmin / Admin@1234" -ForegroundColor White
Write-Host "  Tomcat logs     : $TOMCAT_HOME\logs\catalina.out" -ForegroundColor White
Write-Host ""
Write-Host "  IMPORTANT: Edit DB password in $SETENV_DEST before starting!" -ForegroundColor Red
Write-Host "  IMPORTANT: Change the superadmin password after first login!" -ForegroundColor Red
Write-Host ""
