# Software Requirements Specification (SRS)
## Guacamole Admin — Web Application for Apache Guacamole Audit, Compliance Reporting & VAPT Certification

**Version:** 1.0  
**Date:** May 2026  
**Status:** Draft  

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Overall Description](#2-overall-description)
3. [Stakeholders](#3-stakeholders)
4. [Functional Requirements](#4-functional-requirements)
5. [Non-Functional Requirements](#5-non-functional-requirements)
6. [System Architecture](#6-system-architecture)
7. [Database Design](#7-database-design)
8. [Report Specifications](#8-report-specifications)
9. [Security Requirements (VAPT)](#9-security-requirements-vapt)
10. [Constraints & Assumptions](#10-constraints--assumptions)
11. [Glossary](#11-glossary)

---

## 1. Introduction

### 1.1 Purpose
This document specifies the software requirements for the **Guacamole Admin** web application — a production-ready administration portal for Apache Guacamole that provides audit trails, compliance reporting, and security monitoring.

### 1.2 Scope
The system provides:
- Secure administrator login with BCrypt-verified credentials
- Nine operational and security reports drawn from the live Guacamole database
- A complete audit trail of every administrative action
- Email alerts for security events (failed logins, after-hours access, suspicious activity)

### 1.3 Definitions
| Term | Definition |
|------|-----------|
| Guacamole | Apache Guacamole — an open-source clientless remote desktop gateway |
| Session | A single tunnel connection recorded in `guacamole_connection_history` |
| Admin | A user of this web application with administrative privileges |
| VAPT | Vulnerability Assessment and Penetration Testing |
| BCrypt | Adaptive password hashing algorithm used by Apache Guacamole |

---

## 2. Overall Description

### 2.1 Product Perspective
The application sits alongside an existing Apache Guacamole deployment. It connects **read-only** to the Guacamole MySQL/MariaDB database for reporting, and writes only to its own `admin_audit_log` table.

```
Browser ──HTTPS──► Guacamole Admin (Tomcat/WAR)
                        │
                        ├── READ ──► guacamole_db (existing Guacamole schema)
                        └── WRITE ─► admin_audit_log (new table, same DB)
```

### 2.2 Product Functions (Summary)
- Administrator authentication
- 9 operational/security reports
- Audit logging of all admin actions
- Email notifications for security events

### 2.3 User Classes
| Class | Description |
|-------|-------------|
| Administrator | Full access to all reports and audit logs |
| Read-Only Auditor | *(future)* View reports only, no configuration |

---

## 3. Stakeholders

| Stakeholder | Interest |
|-------------|---------|
| IT Operations | Monitor active sessions, connection health |
| Security Team | Failed logins, after-hours access, VAPT compliance |
| Compliance Officer | Audit trails, access reports |
| System Administrator | User management, connection configuration |

---

## 4. Functional Requirements

### FR-01 — Authentication
| ID | Requirement |
|----|-------------|
| FR-01.1 | The system SHALL authenticate administrators using username and BCrypt-hashed password stored in `guacamole_user`. |
| FR-01.2 | The system SHALL invalidate the existing session before creating a new one on login (session fixation prevention). |
| FR-01.3 | The system SHALL record every login attempt (success and failure) in `admin_audit_log`. |
| FR-01.4 | The system SHALL send an email alert on every failed login attempt. |
| FR-01.5 | The system SHALL redirect unauthenticated requests to the login page. |
| FR-01.6 | Sessions SHALL expire after 30 minutes of inactivity. |

### FR-02 — Report: Active Sessions
| ID | Requirement |
|----|-------------|
| FR-02.1 | The system SHALL display all sessions where `end_date IS NULL` in `guacamole_connection_history`. |
| FR-02.2 | Each row SHALL show: username, connection name, client IP, start time, elapsed duration. |
| FR-02.3 | The page SHALL auto-refresh every 30 seconds. |

### FR-03 — Report: Historical Session Logs
| ID | Requirement |
|----|-------------|
| FR-03.1 | The system SHALL display all completed sessions (where `end_date IS NOT NULL`). |
| FR-03.2 | The report SHALL be filterable by username, date-from, and date-to. |
| FR-03.3 | Results SHALL be limited to 1,000 rows per query. |

### FR-04 — Report: User Details / Users List
| ID | Requirement |
|----|-------------|
| FR-04.1 | The system SHALL list all users from `guacamole_entity` joined with `guacamole_user`. |
| FR-04.2 | Each row SHALL show: username, full name, email, account status (active/disabled/expired), last active timestamp. |
| FR-04.3 | Clicking a user SHALL show a detail page with all profile fields. |

### FR-05 — Report: Top Users / Top Connections
| ID | Requirement |
|----|-------------|
| FR-05.1 | The system SHALL rank the top 20 users by total session count. |
| FR-05.2 | The system SHALL rank the top 20 connections by total session count. |
| FR-05.3 | Both reports SHALL show total duration and average duration. |

### FR-06 — Report: Session Duration
| ID | Requirement |
|----|-------------|
| FR-06.1 | The system SHALL display total and average session duration per connection. |
| FR-06.2 | Durations SHALL be formatted as HH:MM:SS. |

### FR-07 — Report: Failed Login Attempts
| ID | Requirement |
|----|-------------|
| FR-07.1 | The system SHALL display a 30-day summary of failed login counts per username with risk classification (Low/Medium/High). |
| FR-07.2 | The system SHALL display individual failed login events filterable by username and date range. |
| FR-07.3 | Risk levels: Low < 5, Medium 5–9, High ≥ 10 attempts in 30 days. |

### FR-08 — Report: Concurrent Sessions
| ID | Requirement |
|----|-------------|
| FR-08.1 | The system SHALL calculate the peak number of simultaneous sessions per connection. |
| FR-08.2 | Load levels SHALL be classified: Low < 5, Medium 5–9, High ≥ 10. |

### FR-09 — Report: Remote Host Report
| ID | Requirement |
|----|-------------|
| FR-09.1 | The system SHALL display which client IP addresses each user has connected from. |
| FR-09.2 | Each row SHALL show: client IP, username, session count, last seen timestamp. |

### FR-10 — Report: After-Hours Access
| ID | Requirement |
|----|-------------|
| FR-10.1 | The system SHALL flag sessions that started before 08:00 or after 18:00 local server time. |
| FR-10.2 | The system SHALL flag sessions that started on Saturday or Sunday. |
| FR-10.3 | The system SHALL send an email alert when an after-hours session is detected. |

### FR-11 — Audit & Logging
| ID | Requirement |
|----|-------------|
| FR-11.1 | Every administrative action SHALL be written to `admin_audit_log` with: actor, action code, target entity, details, client IP, timestamp. |
| FR-11.2 | The audit log SHALL be viewable and filterable by actor, action type, and date range. |
| FR-11.3 | Audit log writes SHALL NOT cause application errors if they fail (fail-safe logging). |

### FR-12 — Email Notifications
| ID | Requirement |
|----|-------------|
| FR-12.1 | The system SHALL send email alerts for: failed login attempts, after-hours access, suspicious activity. |
| FR-12.2 | SMTP configuration SHALL be supplied via system properties (not hardcoded). |
| FR-12.3 | Email failures SHALL be logged to stderr and SHALL NOT crash the application. |

---

## 5. Non-Functional Requirements

### 5.1 Performance
| ID | Requirement |
|----|-------------|
| NFR-01 | Report pages SHALL load within 3 seconds under normal load (< 100 concurrent users). |
| NFR-02 | The HikariCP connection pool SHALL maintain a minimum of 2 idle connections. |
| NFR-03 | All report queries SHALL use indexed columns; full table scans on large tables are not acceptable. |

### 5.2 Security
| ID | Requirement |
|----|-------------|
| NFR-04 | All database queries SHALL use parameterized statements (PreparedStatement). No string concatenation of user input into SQL. |
| NFR-05 | Passwords SHALL be verified using BCrypt; plaintext passwords SHALL never be stored or logged. |
| NFR-06 | All HTTP responses SHALL include security headers: X-Content-Type-Options, X-Frame-Options, X-XSS-Protection, Content-Security-Policy. |
| NFR-07 | Session cookies SHALL be HttpOnly. In production, Secure flag SHALL be enabled. |
| NFR-08 | The application SHALL prevent session fixation by invalidating the old session on login. |

### 5.3 Reliability
| ID | Requirement |
|----|-------------|
| NFR-09 | The HikariCP pool SHALL be shut down cleanly on application undeploy via `ServletContextListener`. |
| NFR-10 | Database connection failures SHALL produce user-friendly error messages, not stack traces. |

### 5.4 Maintainability
| ID | Requirement |
|----|-------------|
| NFR-11 | The codebase SHALL strictly follow MVC: Controller (Servlet) → Service → DAO → Model. |
| NFR-12 | No Spring, Hibernate, Struts, or JSF frameworks SHALL be used. |
| NFR-13 | Database connection parameters SHALL be configurable via system properties without code changes. |

### 5.5 Compatibility
| ID | Requirement |
|----|-------------|
| NFR-14 | The application SHALL run on any Jakarta EE 10 compliant container (e.g., Tomcat 11, WildFly 30+). |
| NFR-15 | The application SHALL target Java 17+. |
| NFR-16 | The application SHALL connect to MySQL 8.x or MariaDB 10.6+. |

---

## 6. System Architecture

### 6.1 Technology Stack
| Layer | Technology |
|-------|-----------|
| Presentation | JSP 3.1, JSTL 3.0, HTML5, CSS3, Vanilla JS |
| Controller | Jakarta Servlet 6.0 (`@WebServlet`) |
| Business Logic | Plain Java service classes |
| Data Access | JDBC with `PreparedStatement` |
| Connection Pool | HikariCP 5.1 |
| Security | BCrypt (jbcrypt 0.4), `@WebFilter` |
| Build | Maven 3, WAR packaging |
| Runtime | Tomcat 11 / Jakarta EE 10 container |
| Database | MySQL 8 / MariaDB 10.6 (existing Guacamole schema) |

### 6.2 Package Structure
```
com.guacamole
├── controller/          Servlets (HTTP entry points)
│   ├── LoginServlet
│   ├── LogoutServlet
│   ├── DashboardServlet
│   ├── UserServlet
│   └── ReportServlet
├── service/             Business logic
│   ├── UserService
│   └── ReportService
├── dao/                 Database access
│   ├── UserDao
│   ├── SessionDao
│   └── AuditDao
├── model/               POJOs / DTOs
│   ├── User
│   ├── ActiveSession
│   ├── ConnectionStat
│   ├── UserStat
│   ├── FailedLogin
│   └── AuditLog
├── filter/              Servlet filters
│   ├── AuthFilter
│   └── SecurityHeadersFilter
├── listener/            Lifecycle
│   └── AppContextListener
└── util/                Shared utilities
    ├── DbUtil
    ├── AuditLogger
    └── EmailNotifier
```

### 6.3 Request Flow
```
Browser
  │
  ▼
AuthFilter (session check)
  │
  ▼
Servlet (Controller)
  │
  ▼
Service (business logic, audit logging)
  │
  ▼
DAO (parameterized SQL via HikariCP)
  │
  ▼
MySQL/MariaDB (Guacamole DB)
  │
  ▼ (results)
Service → Servlet → JSP (JSTL rendering)
  │
  ▼
Browser (HTML response)
```

---

## 7. Database Design

### 7.1 Existing Guacamole Tables Used (Read-Only)
| Table | Usage |
|-------|-------|
| `guacamole_entity` | Username lookup |
| `guacamole_user` | Password hash, disabled/expired flags, profile |
| `guacamole_connection` | Connection names |
| `guacamole_connection_history` | All session data (start, end, remote_host) |

### 7.2 New Table: admin_audit_log
```sql
CREATE TABLE admin_audit_log (
    id             INT          NOT NULL AUTO_INCREMENT,
    actor_username VARCHAR(128) NOT NULL,
    action         VARCHAR(64)  NOT NULL,  -- LOGIN, LOGIN_FAILED, LOGOUT, etc.
    target_entity  VARCHAR(256),
    details        TEXT,
    remote_ip      VARCHAR(64),
    action_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_actor  (actor_username),
    INDEX idx_action (action),
    INDEX idx_time   (action_time)
);
```

### 7.3 Action Codes
| Code | Trigger |
|------|---------|
| `LOGIN` | Successful administrator login |
| `LOGIN_FAILED` | Failed login attempt |
| `LOGIN_BLOCKED` | Login blocked (disabled/expired account) |
| `LOGOUT` | Administrator logout |

---

## 8. Report Specifications

| # | Report Name | Source Table(s) | Key Columns | Filter Options |
|---|-------------|-----------------|-------------|----------------|
| 1 | Active Sessions | `connection_history` | `end_date IS NULL` | None (live data) |
| 2 | Historical Logs | `connection_history` | All completed sessions | Username, date range |
| 3 | User Details | `entity`, `user` | Profile, status, last active | Search by name |
| 4a | Top Users | `connection_history` | COUNT(*) per user | Top 20 |
| 4b | Top Connections | `connection_history` | COUNT(*) per connection | Top 20 |
| 5 | Session Duration | `connection_history` | SUM/AVG TIMESTAMPDIFF | All connections |
| 6 | Failed Logins | `admin_audit_log` | `action = 'LOGIN_FAILED'` | Username, date range |
| 7 | Concurrent Sessions | `connection_history` | Overlap self-join | All connections |
| 8 | Remote Hosts | `connection_history` | `remote_host` | None |
| 9 | After-Hours Access | `connection_history` | HOUR < 8 OR HOUR >= 18 OR weekend | None |

---

## 9. Security Requirements (VAPT)

### 9.1 Authentication & Session Management
- BCrypt password verification (cost factor from Guacamole default)
- Session fixation prevention (invalidate + recreate on login)
- HttpOnly session cookies
- 30-minute session timeout
- No credentials in URLs or logs

### 9.2 Injection Prevention
- All SQL via `PreparedStatement` with bound parameters
- No dynamic SQL string concatenation with user input
- Output escaped via JSTL `<c:out>` / EL auto-escaping

### 9.3 HTTP Security Headers
| Header | Value |
|--------|-------|
| `X-Content-Type-Options` | `nosniff` |
| `X-Frame-Options` | `DENY` |
| `X-XSS-Protection` | `1; mode=block` |
| `Content-Security-Policy` | `default-src 'self'` |
| `Cache-Control` | `no-store` |
| `Referrer-Policy` | `strict-origin-when-cross-origin` |

### 9.4 VAPT Checklist
| Control | Status |
|---------|--------|
| SQL Injection | ✅ Parameterized queries throughout |
| XSS | ✅ JSTL EL auto-escaping + CSP header |
| CSRF | ⚠️ Recommended: add synchronizer token to state-changing POSTs |
| Session Fixation | ✅ Session invalidated on login |
| Clickjacking | ✅ X-Frame-Options: DENY |
| Sensitive Data Exposure | ✅ Passwords never logged; HTTPS required in production |
| Broken Access Control | ✅ AuthFilter on all routes |
| Security Misconfiguration | ✅ Security headers on all responses |
| Audit Logging | ✅ All admin actions logged |

---

## 10. Constraints & Assumptions

| # | Constraint / Assumption |
|---|------------------------|
| C-01 | The application connects to an **existing** Apache Guacamole MySQL/MariaDB database. The Guacamole schema is not modified. |
| C-02 | No Spring, Hibernate, Struts, or JSF frameworks are used. |
| C-03 | The runtime container must support Jakarta EE 10 (Servlet 6.0). Tomcat 11+ is recommended. |
| C-04 | Java 17 or higher is required. |
| C-05 | SMTP server details must be provided via system properties for email alerts to function. |
| C-06 | Business hours are defined as 08:00–18:00 server local time, Monday–Friday. This is configurable in `ReportService`. |
| C-07 | The `admin_audit_log` table must be created by running `schema-audit.sql` before first deployment. |
| C-08 | The application is intended for internal network use. HTTPS termination should be handled by a reverse proxy (nginx, Apache httpd) in production. |

---

## 11. Glossary

| Term | Definition |
|------|-----------|
| DAO | Data Access Object — class responsible for all database interaction |
| DTO | Data Transfer Object — plain Java object carrying data between layers |
| HikariCP | High-performance JDBC connection pool library |
| JSTL | Jakarta Standard Tag Library — used in JSP for iteration and conditionals |
| MVC | Model-View-Controller architectural pattern |
| POJO | Plain Old Java Object |
| SRS | Software Requirements Specification |
| VAPT | Vulnerability Assessment and Penetration Testing |
| WAR | Web Application Archive — deployable unit for Jakarta EE containers |
