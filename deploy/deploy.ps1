# ============================================================
#  Guacamole Admin — One-click build & deploy script
#  Run from the project root:  .\deploy\deploy.ps1
# ============================================================

$ErrorActionPreference = "Stop"

$TOMCAT_HOME   = "D:\apache-tomcat-10.1.55"
$PROJECT_ROOT  = Split-Path -Parent $PSScriptRoot
$WAR_NAME      = "guacamole-admin-1.0.war"
$WAR_SOURCE    = "$PROJECT_ROOT\target\$WAR_NAME"
$WAR_DEST      = "$TOMCAT_HOME\webapps\$WAR_NAME"
$DB_PROPS      = "$PROJECT_ROOT\src\main\resources\db.properties"
$DB_TEMPLATE   = "$PROJECT_ROOT\src\main\resources\db.properties.template"

Write-Host ""
Write-Host "=== Guacamole Admin Deploy ===" -ForegroundColor Cyan

# ── Check db.properties exists ────────────────────────────────
if (-not (Test-Path $DB_PROPS)) {
    Write-Host "`n[!] db.properties not found — creating from template..." -ForegroundColor Yellow
    Copy-Item $DB_TEMPLATE $DB_PROPS
    Write-Host "    Created: $DB_PROPS" -ForegroundColor Green
    Write-Host "    IMPORTANT: Edit db.properties with your actual DB password before continuing!" -ForegroundColor Red
    Write-Host "    Press Enter after editing, or Ctrl+C to cancel..."
    Read-Host
}

# Verify password is not still the placeholder
$dbContent = Get-Content $DB_PROPS -Raw
if ($dbContent -match "YOUR_PASSWORD_HERE") {
    Write-Host "`n[ERROR] db.properties still has placeholder password!" -ForegroundColor Red
    Write-Host "        Edit $DB_PROPS and set your real DB password." -ForegroundColor Red
    exit 1
}

Write-Host "`n[OK] db.properties found with real credentials." -ForegroundColor Green

# ── Step 1: Build ─────────────────────────────────────────────
Write-Host "`n[1/4] Building WAR with Maven..." -ForegroundColor Yellow
Set-Location $PROJECT_ROOT
mvn clean package -q
if ($LASTEXITCODE -ne 0) { Write-Error "Maven build failed."; exit 1 }
Write-Host "      Build successful: $WAR_SOURCE" -ForegroundColor Green

# ── Step 2: Stop Tomcat if running ────────────────────────────
Write-Host "`n[2/4] Stopping Tomcat (if running)..." -ForegroundColor Yellow
Stop-Process -Name "java" -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3
Write-Host "      Done." -ForegroundColor Green

# ── Step 3: Deploy WAR ────────────────────────────────────────
Write-Host "`n[3/4] Deploying WAR..." -ForegroundColor Yellow
$explodedDir = "$TOMCAT_HOME\webapps\guacamole-admin-1.0"
if (Test-Path $explodedDir) {
    Remove-Item -Recurse -Force $explodedDir
    Write-Host "      Removed old deployment directory." -ForegroundColor Gray
}
if (Test-Path $WAR_DEST) {
    Remove-Item -Force $WAR_DEST
}
Copy-Item $WAR_SOURCE $WAR_DEST -Force
Write-Host "      WAR deployed: $WAR_DEST" -ForegroundColor Green

# ── Step 4: Start Tomcat ──────────────────────────────────────
Write-Host "`n[4/4] Starting Tomcat..." -ForegroundColor Yellow
Start-Process -FilePath "cmd.exe" `
    -ArgumentList "/c D:\apache-tomcat-10.1.55\bin\catalina.bat run" `
    -WorkingDirectory "D:\apache-tomcat-10.1.55\bin" `
    -WindowStyle Normal
Start-Sleep -Seconds 5

Write-Host ""
Write-Host "=== Done! ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "  App URL     : http://localhost:8080/guacamole-admin-1.0/login" -ForegroundColor White
Write-Host "  Login       : superadmin / Admin@1234" -ForegroundColor White
Write-Host "  Tomcat logs : $TOMCAT_HOME\logs" -ForegroundColor White
Write-Host ""
Write-Host "  TIP: Run reset-passwords.sql in MySQL Workbench if login fails." -ForegroundColor Yellow
Write-Host ""
