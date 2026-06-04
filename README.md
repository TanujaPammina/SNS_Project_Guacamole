# Guacamole Admin Portal

A production-ready Java web application for administering and monitoring an **Apache Guacamole** remote desktop gateway system.

Built with **Jakarta EE 10**, **Servlets 6.0**, **JSP**, **JDBC + HikariCP**, and **BCrypt** — no Spring, no Hibernate, no frameworks.

---

## What is this project?

Apache Guacamole lets users connect to remote desktops (RDP, SSH, VNC) through a browser. This admin portal sits on top of it and gives IT administrators a dashboard to:

- See who is connected right now
- Review past session history
- Track suspicious activity (failed logins, after-hours access)
- Manage admin accounts with role-based access control

---

## The 9 Reports

| # | Report | What it shows |
|---|--------|--------------|
| 1 | **Active Sessions** | Who is connected right now, from which IP, for how long |
| 2 | **Historical Logs** | All past sessions with start/end time and duration |
| 3 | **User Details** | All Guacamole users with profile and account status |
| 4 | **Top Users / Connections** | Most active users and most accessed machines |
| 5 | **Session Duration** | Average and total time spent per connection |
| 6 | **Failed Login Attempts** | Tracks failed logins with risk level classification |
| 7 | **Concurrent Sessions** | Peak simultaneous connections per remote machine |
| 8 | **Remote Host Report** | Which client IPs users are connecting from |
| 9 | **After-Hours Access** | Sessions outside business hours (before 8AM / after 6PM / weekends) |

---

## Role-Based Access Control

| Feature | Super Admin | Admin | Auditor |
|---------|:-----------:|:-----:|:-------:|
| All 9 reports | ✅ | ✅ | ✅ |
| Audit log | ✅ | ✅ | ❌ |
| Manage admin users | ✅ | ❌ | ❌ |
| Change own password | ✅ | ✅ | ✅ |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Web | Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0 |
| Architecture | MVC — Controller → Service → DAO → Model / DTO |
| Database | MySQL 8 / MariaDB |
| Connection Pool | HikariCP 5.1 |
| Security | BCrypt, AuthFilter, RoleFilter, Security Headers |
| Build | Maven 3, WAR packaging |
| Server | Apache Tomcat 10.1 |

---

## Project Structure

```
src/main/java/com/guacamole/
├── controller/      Servlets — LoginServlet, ReportServlet, AdminUserServlet, ProfileServlet
├── service/         Business logic — UserService, ReportService, RoleService
├── dao/             Database — UserDao, SessionDao, AuditDao, AdminUserDao
├── model/           POJOs — User, AdminUser, ActiveSession, ConnectionStat, etc.
│   └── dto/         Form data — AdminUserDto, LoginDto
├── filter/          AuthFilter (session check), RoleFilter (permissions), SecurityHeadersFilter
├── listener/        AppContextListener (pool init/shutdown)
└── util/            DbUtil (HikariCP), AuditLogger, EmailNotifier

src/main/webapp/
├── jsp/
│   ├── reports/     One JSP per report (9 reports)
│   ├── admin/       Admin user management pages
│   ├── layout/      sidebar.jsp, topbar.jsp (shared across all pages)
│   └── login.jsp, dashboard.jsp, profile.jsp
├── css/styles.css   Responsive CSS (desktop + mobile)
└── js/app.js        Hamburger menu, table search, date shortcuts

src/main/resources/
├── db.properties.template   Copy to db.properties and fill in your password
└── schema-audit.sql         STEP 2 — Creates admin_users + audit_log tables

deploy/
├── guacamole-official-schema.sql   STEP 1 — Official Guacamole 1.5.4 schema
├── schema-audit.sql                STEP 2 — Admin portal tables
├── reset-passwords.sql             Utility — reset passwords if locked out
├── fix-permissions.sql             Utility — fix Guacamole permissions
├── drop-guac-tables.sql            Utility — clean reinstall of Guacamole schema
├── docker-compose.yml              Run Guacamole locally with Docker
└── deploy.ps1                      One-click build and deploy script (Windows)
```

---

## Prerequisites

Install these before starting:

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | https://adoptium.net |
| Maven | 3.6+ | https://maven.apache.org/download.cgi |
| Apache Tomcat | 10.1+ | https://tomcat.apache.org/download-10.cgi |
| MySQL | 8.0+ | https://dev.mysql.com/downloads/mysql |
| MySQL Workbench | Any | https://dev.mysql.com/downloads/workbench (optional but recommended) |

---

## How to Run — Step by Step

### Step 1 — Clone the repository

```bash
git clone https://github.com/TanujaPammina/SNS_Gluacomole-Admin.git
cd SNS_Gluacomole-Admin
```

---

### Step 2 — Configure database credentials

Copy the template and fill in your password:

**Windows:**
```cmd
copy src\main\resources\db.properties.template src\main\resources\db.properties
```

**Mac / Linux:**
```bash
cp src/main/resources/db.properties.template src/main/resources/db.properties
```

Now open `src/main/resources/db.properties` and set your MySQL password:

```properties
db.url=jdbc:mysql://localhost:3306/guacamole_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=guacamole_user
db.password=YOUR_PASSWORD_HERE   ← change this
db.pool.maxSize=10
db.pool.minIdle=2
```

---

### Step 3 — Set up the database

Open **MySQL Workbench** (or any MySQL client) and run:

```sql
CREATE DATABASE IF NOT EXISTS guacamole_db CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'guacamole_user'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
GRANT ALL PRIVILEGES ON guacamole_db.* TO 'guacamole_user'@'localhost';
FLUSH PRIVILEGES;
```

Then run the two schema files **in order**:

**File → Open SQL Script → select file → Execute (⚡)**

```
STEP 1:  deploy/guacamole-official-schema.sql    ← Guacamole tables
STEP 2:  src/main/resources/schema-audit.sql     ← Admin portal tables
```

---

### Step 4 — Build the WAR

```bash
mvn clean package
```

Output: `target/guacamole-admin-1.0.war`

---

### Step 5 — Deploy to Tomcat

Copy the WAR to Tomcat's webapps folder:

**Windows:**
```cmd
copy target\guacamole-admin-1.0.war C:\path\to\tomcat\webapps\
```

**Mac / Linux:**
```bash
cp target/guacamole-admin-1.0.war /opt/tomcat/webapps/
```

---

### Step 6 — Start Tomcat

**Windows (CMD — keep window open):**
```cmd
set CATALINA_HOME=C:\path\to\tomcat
C:\path\to\tomcat\bin\catalina.bat run
```

**Mac / Linux:**
```bash
export CATALINA_HOME=/opt/tomcat
/opt/tomcat/bin/catalina.sh run
```

Wait for:
```
Server startup in [XXXX] milliseconds
```

---

### Step 7 — Open the app

```
http://localhost:8080/guacamole-admin-1.0/login
```

**Default login:**

| Username | Password |
|----------|----------|
| `superadmin` | `Admin@1234` |

> ⚠️ Change the superadmin password immediately after first login via **My Profile** or **Manage Admins → Edit**

---

## All Report URLs

| Report | URL |
|--------|-----|
| Dashboard | `http://localhost:8080/guacamole-admin-1.0/dashboard` |
| Active Sessions | `http://localhost:8080/guacamole-admin-1.0/reports?type=active-sessions` |
| Historical Logs | `http://localhost:8080/guacamole-admin-1.0/reports?type=historical-logs` |
| User Details | `http://localhost:8080/guacamole-admin-1.0/users` |
| Top Users | `http://localhost:8080/guacamole-admin-1.0/reports?type=top-users` |
| Top Connections | `http://localhost:8080/guacamole-admin-1.0/reports?type=top-connections` |
| Session Duration | `http://localhost:8080/guacamole-admin-1.0/reports?type=session-duration` |
| Failed Logins | `http://localhost:8080/guacamole-admin-1.0/reports?type=failed-logins` |
| Concurrent Sessions | `http://localhost:8080/guacamole-admin-1.0/reports?type=concurrent-sessions` |
| Remote Hosts | `http://localhost:8080/guacamole-admin-1.0/reports?type=remote-hosts` |
| After-Hours Access | `http://localhost:8080/guacamole-admin-1.0/reports?type=after-hours` |
| Audit Log | `http://localhost:8080/guacamole-admin-1.0/reports?type=audit-log` |
| Manage Admins | `http://localhost:8080/guacamole-admin-1.0/admin/users` |
| My Profile | `http://localhost:8080/guacamole-admin-1.0/profile` |

---

## Testing with Sample Data

If you don't have a real Apache Guacamole installation, you can insert sample data to see all reports working. Run this in MySQL Workbench:

```sql
USE guacamole_db;

INSERT INTO guacamole_connection_history
    (user_id, username, remote_host, connection_id, connection_name, start_date, end_date)
SELECT u.user_id, e.name,
    CONCAT('192.168.1.', FLOOR(10+RAND()*90)),
    c.connection_id, c.connection_name,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND()*29) DAY)
FROM guacamole_user u
JOIN guacamole_entity e ON e.entity_id = u.entity_id
CROSS JOIN guacamole_connection c
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) n
LIMIT 30;
```

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `404 Not Found` | Tomcat not running or WAR not deployed — check webapps folder |
| `Invalid username or password` | Run `deploy/reset-passwords.sql` in MySQL Workbench |
| `Table doesn't exist` error | Run both schema files in the correct order (Step 1 then Step 2) |
| DB connection failed | Check `db.properties` password matches your MySQL user password |
| Port 8080 in use | Edit `<TOMCAT_HOME>/conf/server.xml` and change port |
| Login works but reports show no data | Insert sample data using the SQL above |

---

## Windows One-Click Deploy

If you are on Windows with Tomcat at `D:\apache-tomcat-10.1.55`, run:

```powershell
.\deploy\deploy.ps1
```

This builds, deploys, and starts Tomcat automatically.

---

## Optional — Run with Docker (for testing Apache Guacamole)

If you want to test with a real Guacamole instance:

```bash
docker compose -f deploy/docker-compose.yml up -d
```

Then open `http://localhost:8081/guacamole` — login with `guacadmin` / `guacadmin`.
Sessions from Guacamole will automatically appear in the admin portal reports.
