# Guacamole Admin Portal

A production-ready Java web application for administering and monitoring an **Apache Guacamole** remote desktop gateway system.

Built with **Jakarta EE 10**, **Servlets 6.0**, **JSP**, **JDBC + HikariCP**, and **BCrypt** — no Spring, no Hibernate, no frameworks.

---

## What is this project?

Apache Guacamole lets users connect to remote desktops (RDP, SSH, VNC) through a browser. This admin portal sits on top of it and gives IT administrators a dashboard to:

- See who is connected right now
- Review past session history
- Track suspicious activity (failed logins, after-hours access)
- Manage admin accounts with role-based access

---

## The 9 Reports

### 1. Active Sessions List
Shows every user who is **currently connected** to a remote machine in real time.
- Who is connected, to which machine, from which IP, and for how long
- Page auto-refreshes every 30 seconds
- Useful for: monitoring live access, spotting unauthorised connections

---

### 2. Historical Logs
A complete record of **all past sessions** — who connected, when, and for how long.
- Filter by username, date range (Last 7 days / Last 30 days / custom)
- Shows start time, end time, and duration for every session
- Useful for: audits, compliance reviews, investigating incidents

---

### 3. User Details / Users List
Lists **all Guacamole users** with their profile and account status.
- Shows full name, email, active/disabled/expired status, last active time
- Click any user to see their full detail page
- From the detail page you can jump straight to that user's session history
- Useful for: user management, checking who has access

---

### 4. Top Users / Top Connections
Two reports in one:
- **Top Users** — ranks users by how many sessions they have started (most active users first)
- **Top Connections** — ranks remote machines by how many times they have been accessed
- Both show total session count and total time spent
- Useful for: understanding usage patterns, capacity planning

---

### 5. Session Duration
Shows **how long users spend** on each connection on average and in total.
- Average duration per connection (e.g. "Web Server 01 — avg 45 min per session")
- Total cumulative time per connection
- All times shown in HH:MM:SS format
- Useful for: billing, resource planning, identifying heavily used servers

---

### 6. Failed Login Attempts
Tracks every **failed login** to the admin portal.
- 30-day summary showing how many times each username failed (with risk level: Low / Medium / High)
- Detailed log of every individual failed attempt with timestamp and client IP
- Filter by username and date range
- Email alert is sent automatically on every failed login
- Useful for: detecting brute-force attacks, compromised credentials

---

### 7. Concurrent Sessions Report
Shows the **peak number of simultaneous connections** per remote machine.
- Tells you the maximum number of users who were connected to the same machine at the same time
- Load level classification: Low (under 5), Medium (5–9), High (10+)
- Useful for: capacity planning, licence compliance, detecting overloaded servers

---

### 8. Remote Host Report
Shows **which client machines** (IP addresses) users are connecting from.
- Lists every unique IP address, which user connected from it, how many sessions, and when last seen
- Helps identify connections from unexpected locations
- Useful for: security monitoring, detecting access from unknown networks

---

### 9. After-Hours Access Report
Flags sessions that happened **outside business hours** (before 08:00 or after 18:00) or on **weekends**.
- Highlights potentially suspicious access
- Shows user, machine, client IP, start/end time, and duration
- Email alert sent automatically when after-hours access is detected
- Useful for: security compliance, insider threat detection

---

## Role-Based Access Control

Three roles control what each admin can see:

| Feature | Super Admin | Admin | Auditor |
|---------|:-----------:|:-----:|:-------:|
| All 9 reports | ✅ | ✅ | ✅ |
| Audit log | ✅ | ✅ | ❌ |
| Manage admin users | ✅ | ❌ | ❌ |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Web | Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0 |
| Architecture | MVC — Controller → Service → DAO → Model/DTO |
| Database | MySQL 8 / MariaDB (existing Guacamole schema) |
| Connection Pool | HikariCP 5.1 |
| Security | BCrypt (jbcrypt), AuthFilter, RoleFilter |
| Build | Maven 3, WAR packaging |
| Server | Apache Tomcat 10.1 |

---

## Project Structure

```
src/main/java/com/guacamole/
├── controller/      Servlets — handle HTTP requests
├── service/         Business logic
├── dao/             Database queries (JDBC PreparedStatement)
├── model/           POJOs — User, AdminUser, ActiveSession, etc.
│   └── dto/         Data Transfer Objects — form data
├── filter/          AuthFilter (login check), RoleFilter (permissions)
├── listener/        AppContextListener (startup/shutdown)
└── util/            DbUtil, AuditLogger, EmailNotifier

src/main/webapp/
├── jsp/
│   ├── reports/     One JSP per report
│   ├── admin/       Admin user management pages
│   ├── layout/      Shared sidebar
│   └── login.jsp, dashboard.jsp
├── css/styles.css
└── js/app.js

src/main/resources/
├── db.properties.template     Copy to db.properties and fill in your password
├── schema-audit.sql           STEP 2 — Creates admin_users + audit_log tables
```

---

## How to Run

### What you need
- Java 17
- Maven 3
- Apache Tomcat 10.1
- MySQL 8 or MariaDB

---

### Step 1 — Set up the database

Open MySQL Workbench and run:

```sql
CREATE DATABASE IF NOT EXISTS guacamole_db CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'guacamole_user'@'localhost' IDENTIFIED BY 'YourPassword';
GRANT ALL PRIVILEGES ON guacamole_db.* TO 'guacamole_user'@'localhost';
FLUSH PRIVILEGES;
USE guacamole_db;
```

Then run the two schema files in order:

```
Step 1: deploy/guacamole-official-schema.sql   ← Guacamole tables + default admin user
Step 2: src/main/resources/schema-audit.sql    ← Admin portal tables (admin_users, audit_log)
```

Open each file in MySQL Workbench (**File → Open SQL Script**), then click ⚡ Execute.

> After running schema-audit.sql, default login is: `superadmin` / `Admin@1234`

---

### Step 2 — Configure the database password

Open `src/main/resources/db.properties` and update:

```properties
db.url=jdbc:mysql://localhost:3306/guacamole_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=guacamole_user
db.password=YourPassword
```

---

### Step 3 — Build the WAR

```bash
mvn clean package
```

This creates `target/guacamole-admin-1.0.war`

---

### Step 4 — Deploy to Tomcat

Copy the WAR to Tomcat's webapps folder:

```
target/guacamole-admin-1.0.war  →  <TOMCAT_HOME>/webapps/
```

---

### Step 5 — Start Tomcat

Open a Command Prompt and run:

```cmd
set CATALINA_HOME=C:\path\to\tomcat
<TOMCAT_HOME>\bin\catalina.bat run
```

Wait for:
```
Server startup in [XXXX] milliseconds
```

---

### Step 6 — Open the app

```
http://localhost:8080/guacamole-admin-1.0/login
```

**Default login:**

| Username | Password |
|----------|----------|
| `superadmin` | `Admin@1234` |

> ⚠️ Change this password immediately after first login via **Manage Admins → Edit**

---

## All Report URLs

| Report | URL |
|--------|-----|
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
