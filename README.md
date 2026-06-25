# Guacamole Admin Portal

A production-ready Java web application for administering and monitoring an **Apache Guacamole** remote desktop gateway system.

Built with **Jakarta EE 10**, **Servlets 6.0**, **JSP**, **JDBC + HikariCP**, and **BCrypt** — no Spring, no Hibernate, no frameworks.

---

## What is this project?

Apache Guacamole lets users connect to remote desktops (RDP, SSH, VNC) through a browser. This admin portal sits on top of it and gives IT administrators a dashboard to:

- See who is connected right now
- Review past session history
- Track suspicious activity (failed logins, after-hours access)
- Manage admin accounts with **configurable** role-based access control

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

Plus: **Audit Log** — full trail of every admin action.

---

## Role-Based Access Control (Configurable)

Three roles. Report visibility is **fully configurable** by Super Admin — not hardcoded.

| Feature | Super Admin | IT Admin | Auditor |
|---------|:-----------:|:--------:|:-------:|
| All reports (default) | ✅ Always | ✅ Default | ❌ Default |
| Audit log (default) | ✅ | ✅ | ✅ Default |
| Configure report permissions | ✅ | ❌ | ❌ |
| Manage admin users | ✅ | ❌ | ❌ |
| Change own password | ✅ | ✅ | ✅ |
| Forgot password (email) | ✅ | ✅ | ✅ |

### Configurable Report Permissions
Super Admin can go to **Administration → Report Permissions** and assign/remove any of the 10 reports for IT Admin and Auditor roles using checkboxes — no code change required.

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
| Testing | JUnit 5, Mockito, Selenium 4 + WebDriverManager |

---

## Project Structure

```
src/main/java/com/guacamole/
├── controller/      LoginServlet, ReportServlet, AdminUserServlet,
│                    ReportPermissionsServlet, ProfileServlet,
│                    ForgotPasswordServlet, ResetPasswordServlet
├── service/         UserService, ReportService, RoleService,
│                    PasswordResetService
├── dao/             UserDao, SessionDao, AuditDao, AdminUserDao,
│                    ReportPermissionDao
├── model/           User, AdminUser, ActiveSession, ConnectionStat,
│                    UserStat, FailedLogin, AuditLog, Role (enum)
│   └── dto/         AdminUserDto, LoginDto
├── filter/          AuthFilter, RoleFilter, SecurityHeadersFilter
├── listener/        AppContextListener
└── util/            DbUtil, AuditLogger, EmailNotifier

src/main/webapp/
├── jsp/
│   ├── reports/     9 report JSPs + audit-log
│   ├── admin/       admin-users.jsp, admin-user-form.jsp,
│   │                report-permissions.jsp
│   ├── layout/      sidebar.jsp, topbar.jsp
│   └── login.jsp, dashboard.jsp, profile.jsp,
│       forgot-password.jsp, reset-password.jsp

src/main/resources/
├── db.properties.template   Copy to db.properties and fill credentials
├── schema-audit.sql         STEP 2 — Admin portal tables + role permissions
└── guacamole-schema.sql     Sample data for testing

deploy/
├── guacamole-official-schema.sql  STEP 1 — Official Guacamole 1.5.4 schema
├── reset-all-passwords.sql        Emergency password reset utility
├── docker-compose.yml             Run Guacamole locally for testing
└── deploy.ps1                     Windows one-click deploy script

src/test/
├── model/           Unit tests for all model classes
├── service/         Mockito tests for all service classes
└── selenium/        End-to-end browser tests (43 tests)
```

---

## Database Tables

| Table | Purpose |
|-------|---------|
| `admin_users` | Admin portal accounts (username, BCrypt password, role) |
| `admin_audit_log` | Every admin action logged |
| `role_report_permissions` | **Configurable** per-role report access |
| `guacamole_connection_history` | Session data (from Guacamole) |
| `guacamole_entity` + `guacamole_user` | Guacamole users |
| `guacamole_connection` | Remote connections |

---

## How to Run — Step by Step

### Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| Java JDK | 17+ | https://adoptium.net |
| Maven | 3.6+ | https://maven.apache.org/download.cgi |
| Apache Tomcat | 10.1+ | https://tomcat.apache.org/download-10.cgi |
| MySQL | 8.0+ | https://dev.mysql.com/downloads/mysql |

---

### Step 1 — Clone

```bash
git clone https://github.com/TanujaPammina/SNS_Project_Guacamole.git
cd SNS_Project_Guacamole
```

---

### Step 2 — Configure database credentials

```cmd
copy src\main\resources\db.properties.template src\main\resources\db.properties
```

Edit `db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/guacamole_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=guacamole_user
db.password=YOUR_PASSWORD_HERE
```

---

### Step 3 — Set up the database

In MySQL Workbench or CLI:
```sql
CREATE DATABASE IF NOT EXISTS guacamole_db CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'guacamole_user'@'localhost' IDENTIFIED BY 'YourPassword';
GRANT ALL PRIVILEGES ON guacamole_db.* TO 'guacamole_user'@'localhost';
FLUSH PRIVILEGES;
```

Run the two schema files **in order**:

**File → Open SQL Script → Execute ⚡**

```
STEP 1:  deploy/guacamole-official-schema.sql    ← Guacamole tables
STEP 2:  src/main/resources/schema-audit.sql     ← Admin portal tables + role permissions
```

---

### Step 4 — Add sample data (optional — for testing reports)

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
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3) n LIMIT 30;
```

---

### Step 5 — Build

```bash
mvn clean package
```

Output: `target/guacamole-admin-1.0.war`

---

### Step 6 — Deploy to Tomcat

Copy WAR:
```cmd
copy target\guacamole-admin-1.0.war C:\path\to\tomcat\webapps\
```

---

### Step 7 — Start Tomcat

```cmd
set CATALINA_HOME=C:\path\to\tomcat
C:\path\to\tomcat\bin\catalina.bat run
```

Wait for: `Server startup in [XXXX] milliseconds`

---

### Step 8 — Open the app

```
http://localhost:8080/guacamole-admin-1.0/login
```

**Default logins:**

| Username | Password | Role |
|----------|----------|------|
| `superadmin` | `Admin@1234` | Super Admin — full access |
| `itadmin` | `Admin@1234` | IT Admin — all reports |
| `auditor` | `Admin@1234` | Auditor — audit log only (configurable) |

> ⚠️ Change all passwords immediately after first login

---

## How to Configure Report Permissions

1. Login as `superadmin`
2. Click **Administration → Report Permissions** in sidebar
3. Select **IT Admin** or **Auditor** tab
4. Check/uncheck report checkboxes
5. Click **Save Permissions**

Changes take effect immediately on next login for that role.

---

## Running Tests

```bash
# Unit + Mockito tests (no browser needed)
mvn test

# Selenium tests (app must be running on :8080)
mvn test -Dtest="LoginSeleniumTest,RoleAccessSeleniumTest,ReportSeleniumTest,AdminUserSeleniumTest"
```

**Test coverage: 134 tests total**
- 91 unit/mock tests (model, service, DAO)
- 43 Selenium end-to-end tests (login, roles, all 9 reports, admin management)

---

## All URLs

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
| **Report Permissions** | `/guacamole-admin-1.0/admin/report-permissions` |
| My Profile | `/guacamole-admin-1.0/profile` |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `404 Not Found` | Tomcat not running — run `catalina.bat run` |
| `Invalid username or password` | Run `deploy/reset-all-passwords.sql` in MySQL Workbench |
| `Table doesn't exist` | Run both schema files in order (Step 1 then Step 2) |
| DB connection failed | Check `db.properties` password |
| Reports not showing for a role | Go to Report Permissions and configure access |
