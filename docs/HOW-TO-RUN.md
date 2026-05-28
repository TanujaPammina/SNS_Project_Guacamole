# How to Run — Guacamole Admin

## Your Environment (already confirmed)
| Component | Location / Status |
|-----------|------------------|
| Java 17   | Eclipse Adoptium — used by Maven |
| Maven 3.9 | `D:\maven` |
| Tomcat 10.1.55 | `D:\apache-tomcat-10.1.55` |
| MySQL 8.0 | Running as Windows service `MySQL80` |

---

## Step 1 — Set Up the Database

Open **MySQL Workbench** or a MySQL command prompt and run:

```sql
-- 1a. Create the database (skip if guacamole_db already exists)
CREATE DATABASE IF NOT EXISTS guacamole_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 1b. Create a dedicated user (skip if already exists)
CREATE USER IF NOT EXISTS 'guacamole_user'@'localhost' IDENTIFIED BY 'StrongPass123!';
GRANT ALL PRIVILEGES ON guacamole_db.* TO 'guacamole_user'@'localhost';
FLUSH PRIVILEGES;

-- 1c. Switch to the database
USE guacamole_db;
```

Then run the Guacamole schema (if not already done):
- Download from: https://github.com/apache/guacamole-client/blob/main/extensions/guacamole-auth-jdbc/modules/guacamole-auth-jdbc-mysql/schema/

Then run **our** supplementary schema:
```
mysql -u guacamole_user -p guacamole_db < src\main\resources\schema-audit.sql
```

This creates:
- `admin_users` table with a default **superadmin** account
- `admin_audit_log` table

---

## Step 2 — Build the WAR

```powershell
cd D:\GuacamoleAdminProject
mvn clean package
```

Output: `target\guacamole-admin-1.0.war`

---

## Step 3 — Configure Tomcat

### 3a. Set the JAVA_HOME for Tomcat

Edit `D:\apache-tomcat-10.1.55\bin\setenv.bat`
(create it if it doesn't exist):

```bat
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot
set JRE_HOME=%JAVA_HOME%

set CATALINA_OPTS=-Dguac.db.url=jdbc:mysql://localhost:3306/guacamole_db?useSSL=false^&serverTimezone=UTC^&allowPublicKeyRetrieval=true ^
 -Dguac.db.user=guacamole_user ^
 -Dguac.db.password=StrongPass123!
```

> Replace `StrongPass123!` with your actual MySQL password.

### 3b. Deploy the WAR

Copy the WAR to Tomcat's webapps folder:

```powershell
Copy-Item "D:\GuacamoleAdminProject\target\guacamole-admin-1.0.war" `
          "D:\apache-tomcat-10.1.55\webapps\"
```

---

## Step 4 — Start Tomcat

```powershell
D:\apache-tomcat-10.1.55\bin\startup.bat
```

Watch the logs:
```powershell
Get-Content "D:\apache-tomcat-10.1.55\logs\catalina.out" -Wait
```

You should see:
```
[Guacamole Admin] Database pool initialised successfully.
```

---

## Step 5 — Open the Application

```
http://localhost:8080/guacamole-admin-1.0/login
```

### Default Login
| Field | Value |
|-------|-------|
| Username | `superadmin` |
| Password | `Admin@1234` |

> **Change this password immediately** after first login via Manage Admins → Edit.

---

## Step 6 — Stop Tomcat

```powershell
D:\apache-tomcat-10.1.55\bin\shutdown.bat
```

---

## Quick Reference — All URLs

| Page | URL |
|------|-----|
| Login | `/guacamole-admin-1.0/login` |
| Dashboard | `/guacamole-admin-1.0/dashboard` |
| Active Sessions | `/guacamole-admin-1.0/reports?type=active-sessions` |
| Historical Logs | `/guacamole-admin-1.0/reports?type=historical-logs` |
| User Details | `/guacamole-admin-1.0/users` |
| Top Users | `/guacamole-admin-1.0/reports?type=top-users` |
| Top Connections | `/guacamole-admin-1.0/reports?type=top-connections` |
| Session Duration | `/guacamole-admin-1.0/reports?type=session-duration` |
| Failed Logins | `/guacamole-admin-1.0/reports?type=failed-logins` |
| Concurrent Sessions | `/guacamole-admin-1.0/reports?type=concurrent-sessions` |
| Remote Hosts | `/guacamole-admin-1.0/reports?type=remote-hosts` |
| After-Hours Access | `/guacamole-admin-1.0/reports?type=after-hours` |
| Audit Log | `/guacamole-admin-1.0/reports?type=audit-log` |
| Manage Admins | `/guacamole-admin-1.0/admin/users` |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `Database pool initialised` not shown | Check `setenv.bat` DB credentials; check MySQL is running |
| `404` on all pages | WAR not deployed — check `webapps` folder and Tomcat logs |
| `Access Denied` on login | Run `schema-audit.sql` to create `admin_users` table |
| Port 8080 in use | Edit `D:\apache-tomcat-10.1.55\conf\server.xml`, change `port="8080"` |
| Blank page / JSP error | Check `logs\catalina.out` for stack trace |
