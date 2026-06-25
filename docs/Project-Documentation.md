# Guacamole Admin Portal — Complete Project Documentation

**Project Name:** Guacamole Admin
**Version:** 1.0
**Date:** June 2026

---

## PART 1 — WHAT IS THIS PROJECT?

### Simple Explanation

Apache Guacamole is a tool that lets employees connect to office computers remotely through a web browser (like RDP, SSH, VNC). This Admin Portal is a separate web application that IT administrators use to MONITOR and MANAGE all those connections.

Think of it like this:
- Guacamole = the door through which employees enter the office network remotely
- This Admin Portal = the security camera system watching who goes through that door

### Who Uses What

| Person | Where they login | What they do |
|--------|-----------------|-------------|
| Regular employee (Alice, Bob) | Apache Guacamole (port 8081) | Connect to office computers |
| IT Admin (Tanuja) | This Admin Portal (port 8080) | Monitor all connections, view reports |
| Security Auditor | This Admin Portal | View reports only |

---

## PART 2 — COMPLETE FEATURES LIST

### Feature 1: Secure Login System

**What it does:**
- Admin users log in with username and password
- Passwords are stored using BCrypt hashing (industry-standard security - passwords cannot be read even from the database)
- Wrong password attempts are logged automatically
- After 30 minutes of no activity, session expires automatically

**Forgot Password:**
- Admin clicks "Forgot password?" on login page
- Enters their email address
- System sends a reset link to their email (valid for 30 minutes only)
- Admin clicks link, sets new password
- Redirect back to login with success message

**Security features on login:**
- SHOW/HIDE button to toggle password visibility
- Email alert sent immediately when wrong password is entered
- Session fixation prevention (new session created on login)

---

### Feature 2: Role-Based Access Control (RBAC)

Three different roles with different permissions:

#### SUPER ADMIN
- Sees everything
- Can manage other admin accounts (create, edit, delete)
- Can view all 9 reports
- Can view audit log
- Full access to all features

#### ADMIN
- Can view all 9 reports
- Can view audit log
- CANNOT manage admin accounts
- CANNOT create or delete other users

#### AUDITOR
- Can ONLY view the 9 reports
- CANNOT view audit log
- CANNOT manage admin accounts
- Read-only access

**How it shows in the app:**
- Sidebar navigation changes based on your role
- Trying to access a page you are not allowed to shows "Access Denied" screen
- Role badge shown next to username (Super Admin / Admin / Auditor)

---

### Feature 3: Dashboard

The home page after login shows:

- **Active Sessions card** — how many users are connected right now
- **Failed Logins card** — how many failed login attempts in last 30 days
- **After-Hours Events card** — how many connections happened outside business hours
- **Quick links** to all 9 reports
- **Top Users table** — most active users at a glance

---

### Feature 4: Report 1 — Active Sessions List

**What it shows:**
Real-time list of every user currently connected to a remote machine.

| Column | What it means |
|--------|--------------|
| Username | Who is connected |
| Connection | Which remote machine they are on |
| Client IP | The IP address they are connecting from |
| Started | When the session started |
| Duration | How long they have been connected |
| Status | Always shows "Active" |

**Special feature:** Page auto-refreshes every 30 seconds so data is always current.

---

### Feature 5: Report 2 — Historical Logs

**What it shows:**
Complete history of all past connections that have ended.

**Filters available:**
- Filter by username (show only one person's history)
- Filter by date range (from date / to date)
- Quick buttons: Last 7 days, Last 30 days

**Columns:** Username, Connection, Client IP, Start time, End time, Duration

**Why it matters:** Useful for audits — "Who accessed the server last Tuesday at 3pm?"

---

### Feature 6: Report 3 — User Details / Users List

**What it shows:**
All users registered in the Apache Guacamole system.

**Columns:** Username, Full Name, Email, Status (Active/Disabled/Expired), Last Active

**Features:**
- Search bar to find users quickly
- Click any user to see their full profile
- From profile, jump directly to that user's session history

---

### Feature 7: Report 4 — Top Users & Top Connections

**Top Users:**
Ranks users by how many sessions they have started. Shows:
- Rank number
- Username
- Total number of sessions
- Total time spent connected
- Last seen date

**Top Connections:**
Ranks remote machines by how many times they were accessed. Shows:
- Rank number
- Connection name
- Total sessions
- Total time
- Average session length

---

### Feature 8: Report 5 — Session Duration

**What it shows:**
How long users spend connected to each machine — both average and total time.

| Column | Example | Meaning |
|--------|---------|---------|
| Connection Name | Web Server 01 | Name of remote machine |
| Total Sessions | 42 | How many times accessed |
| Total Duration | 84:30:00 | Total hours:minutes:seconds spent |
| Avg Duration | 02:00:00 | Average time per session |

**Why it matters:** Helps plan maintenance windows, identify unused servers.

---

### Feature 9: Report 6 — Failed Login Attempts Tracking

**Two views:**

**Summary view (30-day overview):**
- Username
- How many failed attempts
- Risk level: Low (under 5) / Medium (5-9) / High (10+)

**Detail view (individual events):**
- Exact username attempted
- IP address of attacker
- Date and time of attempt

**Automatic alert:** Email sent to admin every time a login fails.

**Why it matters:** Detects brute-force attacks, compromised accounts.

---

### Feature 10: Report 7 — Concurrent Sessions Report

**What it shows:**
The maximum number of users who were connected to the same machine at the same time (peak load).

| Column | Example | Meaning |
|--------|---------|---------|
| Connection Name | Web Server 01 | Machine name |
| Peak Concurrent | 8 | Max users at same time |
| Load Level | Medium | Low/Medium/High badge |

**Load levels:**
- Low = less than 5 users simultaneously
- Medium = 5 to 9 users simultaneously
- High = 10 or more users simultaneously

**Why it matters:** Identifies overloaded servers, licence compliance.

---

### Feature 11: Report 8 — Remote Host Report

**What it shows:**
Which client machines (IP addresses) users are connecting FROM.

| Column | Meaning |
|--------|---------|
| Client IP | IP address of connecting machine |
| Username | Who connected from that IP |
| Session Count | How many times from that IP |
| Last Seen | Most recent connection time |

**Why it matters:** Detects connections from unexpected locations (home networks, foreign countries, unknown IPs).

---

### Feature 12: Report 9 — After-Hours Access

**What it shows:**
Sessions that happened OUTSIDE business hours (before 8:00 AM or after 6:00 PM) or on weekends.

**Why it matters:** These are potentially suspicious — an employee should not be accessing company servers at 2 AM on a Sunday unless authorised.

**Automatic alert:** Email sent to admin whenever an after-hours session is detected.

**Columns:** Username, Connection, Client IP, Start Time, End Time, Duration, Status

---

### Feature 13: Audit Log

**What it shows:**
Every administrative action performed through this portal — a complete trail of "who did what and when."

**Logged actions:**
| Action Code | When it happens |
|-------------|----------------|
| LOGIN | Admin successfully logged in |
| LOGIN_FAILED | Wrong password entered |
| LOGIN_BLOCKED | Inactive account tried to login |
| LOGOUT | Admin logged out |
| CREATE_ADMIN_USER | New admin account created |
| EDIT_ADMIN_USER | Admin profile/role changed |
| DELETE_ADMIN_USER | Admin account deleted |
| CHANGE_PASSWORD | Password was changed |
| PASSWORD_RESET_REQUESTED | Forgot password email requested |
| PASSWORD_RESET_COMPLETED | Password reset via email link |

**Filters:** By actor, by action type, by date range.

**Who can see it:** Super Admin and Admin only (Auditors cannot).

---

### Feature 14: Manage Admin Users

**Available to:** Super Admin only

**What you can do:**
- View all admin accounts
- Create new admin account (set username, password, email, role)
- Edit existing account (change role, email, deactivate)
- Delete account (cannot delete your own account)
- See role permissions table

---

### Feature 15: My Profile (Change Password)

**Available to:** All roles

**What you can do:**
- View your account details (username, email, role, last login)
- Change your own password
- Must enter current password to confirm identity
- Password must be at least 8 characters
- New password cannot be same as current

---

### Feature 16: Email Notifications

**Two types of emails:**

**Security Alerts** (sent to admin email):
- Failed login attempt detected
- After-hours access detected

**Password Reset** (sent to user's own email):
- Contains a secure reset link
- Link expires in 30 minutes
- Token is single-use (cleared after use)

---

### Feature 17: Mobile Responsive Design

- Works on phones, tablets, laptops
- Hamburger menu (☰) appears on small screens to open/close sidebar
- Tables scroll horizontally on small screens
- All forms work on mobile
- Buttons and inputs are sized for touch

---

### Feature 18: Security Headers

Every page response includes these security headers automatically:

| Header | What it prevents |
|--------|-----------------|
| X-Content-Type-Options: nosniff | Browser sniffing attacks |
| X-Frame-Options: DENY | Clickjacking attacks |
| X-XSS-Protection: 1; mode=block | Cross-site scripting |
| Content-Security-Policy | Malicious script injection |
| Cache-Control: no-store | Sensitive data caching |
| Referrer-Policy | Information leakage |

---

## PART 3 — TESTING (BEGINNER FRIENDLY)

### What is Testing?

Testing is the process of checking that your application works correctly. Instead of manually opening the browser and clicking buttons every time you make a change, you write code that does the checking automatically.

There are different TYPES of tests — each one checks different things.

---

### Type 1: Unit Tests

**What is a Unit Test?**
A unit test checks ONE small piece of code in isolation. It does not need a database, browser, or server. It just runs Java code and checks the result.

**Example in our project:**
We test that the `ActiveSession` class formats duration correctly.

```
Input: 3661 seconds
Expected output: "01:01:01"
```

If the output matches — test PASSES ✅
If the output is wrong — test FAILS ❌

**Our Unit Tests (37 tests):**

| Test Class | What it tests |
|-----------|--------------|
| `RoleTest` | Role enum — parsing role names, display names |
| `AdminUserTest` | AdminUser model — isSuperAdmin(), isAdmin(), isAuditor() |
| `ActiveSessionTest` | Duration formatting — 3661 seconds → "01:01:01" |
| `ConnectionStatTest` | Duration formatting for connection statistics |
| `AdminUserDtoTest` | Form validation — blank username, short password, passwords not matching |
| `LoginDtoTest` | Login form validation — blank fields detection |

---

### Type 2: Integration Tests (Mockito)

**What is Mockito?**
Mockito is a tool that creates FAKE versions of database classes. Instead of actually connecting to MySQL, the test uses a fake database that you control. This lets you test business logic without needing a real database.

**Example:**
We test `UserService.authenticate()`. The service normally calls the database to get the password hash. In the test, we tell Mockito "pretend the database returned this hash" and then check if the service behaves correctly.

```
REAL flow:
UserService → AdminUserDao → MySQL database → returns hash

TEST flow with Mockito:
UserService → MockAdminUserDao → returns fake hash (no database needed)
```

**Our Mockito Tests (54 tests):**

| Test Class | What it tests | Number of tests |
|-----------|--------------|-----------------|
| `RoleServiceTest` | All permission checks — canViewReports, canViewAuditLog, canManageAdminUsers, hasRole | 13 |
| `UserServiceTest` | Login (correct password, wrong password, inactive account), create user, delete user, duplicate username | 16 |
| `PasswordResetServiceTest` | Request reset (valid email, unknown email, blank email), validate token (valid, expired, unknown), reset password (matching, mismatched, expired token) | 12 |
| `ReportServiceTest` | All 9 reports — checks that each report calls the correct DAO method with correct parameters | 13 |

---

### Type 3: Selenium Tests (Browser Automation)

**What is Selenium?**
Selenium is a tool that controls a real Chrome browser automatically. It opens pages, clicks buttons, fills forms, and checks what appears — exactly like a human tester would, but 100x faster.

**How it works:**
1. Selenium opens Chrome (headless — no visible window)
2. Navigates to the login page
3. Types the username and password
4. Clicks Sign In
5. Checks that the URL changed to /dashboard
6. If yes — PASS ✅ If no — FAIL ❌

**Our Selenium Tests (43 tests):**

---

#### LoginSeleniumTest (11 tests)

| Test Name | What it checks |
|-----------|---------------|
| `loginPage_hasRequiredElements` | Login page has username field, password field, submit button, forgot password link |
| `login_validSuperAdmin_redirectsToDashboard` | Correct login redirects to dashboard, shows "Super Admin" badge |
| `login_validAdmin_redirectsToDashboard` | admin1 login works |
| `login_validAuditor_redirectsToDashboard` | auditor1 login works |
| `login_wrongPassword_showsError` | Wrong password shows "Invalid username or password" message |
| `login_unknownUser_showsError` | Unknown username shows error |
| `login_emptyCredentials_staysOnLoginPage` | Empty form stays on login page |
| `showButton_togglesPasswordVisibility` | Clicking SHOW changes password field type to text |
| `forgotPasswordLink_navigatesToForgotPage` | Clicking "Forgot password?" goes to forgot password page |
| `logout_redirectsToLoginPage` | Logout goes back to login page |
| `unauthenticated_dashboardAccess_redirectsToLogin` | Opening /dashboard without login redirects to login |

---

#### RoleAccessSeleniumTest (11 tests)

| Test Name | What it checks |
|-----------|---------------|
| `superAdmin_seesFullSidebar` | Super Admin sees ALL sections including Administration |
| `superAdmin_canAccessManageAdmins` | Super Admin can open Manage Admins page |
| `superAdmin_canAccessAuditLog` | Super Admin can view Audit Log |
| `admin_seesSidebarWithoutAdministration` | Admin sees Audit but NOT Administration section |
| `admin_canAccessAuditLog` | Admin can view Audit Log |
| `admin_deniedManageAdmins` | Admin gets Access Denied on Manage Admins |
| `auditor_limitedSidebar` | Auditor does NOT see Audit Log or Administration links |
| `auditor_seesAllReports` | Auditor sees all 9 report links on dashboard |
| `auditor_deniedAuditLog` | Auditor gets Access Denied on Audit Log |
| `auditor_canAccessActiveSessions` | Auditor CAN access Active Sessions report |
| `auditor_deniedManageAdmins` | Auditor gets Access Denied on Manage Admins |

---

#### ReportSeleniumTest (14 tests)

| Test Name | What it checks |
|-----------|---------------|
| `activeSessions_pageLoads` | Active Sessions page loads with table, search box, refresh indicator |
| `activeSessions_correctColumns` | Table has Username, Connection, Client IP, Started, Duration columns |
| `historicalLogs_pageLoads` | Historical Logs page loads with filter section and table |
| `historicalLogs_dateShortcutButtons` | Last 7d and Last 30d buttons exist |
| `userDetails_pageLoads` | User Details page loads with All Users table |
| `topUsers_pageLoads` | Top Users page loads with Rank, Total Sessions, Total Duration columns |
| `topConnections_pageLoads` | Top Connections page loads with Connection Name, Avg Duration columns |
| `sessionDuration_pageLoads` | Session Duration page loads with Total Duration and Avg Duration |
| `failedLogins_pageLoads` | Failed Logins page loads with filter section |
| `failedLogins_showsRiskBadges` | Risk level or events section exists |
| `concurrentSessions_pageLoads` | Concurrent Sessions shows Peak Concurrent Sessions and Load Level columns |
| `remoteHosts_pageLoads` | Remote Hosts shows Client IP and Session Count columns |
| `afterHours_pageLoads` | After-Hours page shows warning about 08:00, 18:00, and weekends |
| `dashboard_showsStatCardsAndLinks` | Dashboard shows stat cards and report links |

---

#### AdminUserSeleniumTest (7 tests)

| Test Name | What it checks |
|-----------|---------------|
| `manageAdmins_showsAllUsers` | Manage Admins page shows superadmin, admin1, auditor1 |
| `manageAdmins_showsRoleMatrix` | Role permissions table is visible |
| `newAdminUser_formHasRequiredFields` | New user form has all required fields |
| `newAdminUser_roleDropdownHasAllRoles` | Role dropdown has exactly 3 options (SUPER_ADMIN, ADMIN, AUDITOR) |
| `createUser_blankUsername_showsValidation` | Cannot submit form with blank username |
| `manageAdmins_superAdminHasNoDeleteButton` | Superadmin row has no Delete button (cannot delete self) |
| `editAdmin_formPreFillsValues` | Edit form shows username as readonly |

---

### How to Run Tests

#### Run Unit + Mockito tests only (no browser, no server needed — fastest):

```bash
mvn test
```

Result: 91 tests run in about 10 seconds.

#### Run Selenium tests (Chrome browser + Tomcat must be running):

First start Tomcat:
```cmd
D:\apache-tomcat-10.1.55\bin\catalina.bat run
```

Then run:
```bash
mvn test -Dtest="LoginSeleniumTest,RoleAccessSeleniumTest,ReportSeleniumTest,AdminUserSeleniumTest"
```

Result: 43 tests run in about 2 minutes (each test opens a browser).

#### Run everything together:
```bash
mvn test -DfailIfNoTests=false
```

---

### Understanding Test Results

When you run `mvn test` you see output like this:

```
Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
```

| Word | Meaning |
|------|---------|
| Tests run | How many tests were executed |
| Failures | Tests that ran but got wrong answer (assertion failed) |
| Errors | Tests that crashed with an exception |
| Skipped | Tests that were skipped |

**Good result:** Failures: 0, Errors: 0
**Bad result:** Any number in Failures or Errors

---

### The CDP Warning (Ignore This)

When running Selenium tests you will see this warning:

```
WARNING: Unable to find version of CDP to use for 149.0.7827.114
```

**This is NOT an error.** It is just Chrome's DevTools Protocol version not exactly matching Selenium's version. Tests run perfectly fine despite this warning. You can ignore it completely.

---

## PART 4 — TECHNOLOGY STACK EXPLAINED

| Technology | What it is | Why we use it |
|-----------|-----------|---------------|
| Java 17 | Programming language | Main language for the backend |
| Jakarta Servlet 6.0 | Web framework | Handles HTTP requests and responses |
| JSP (JavaServer Pages) | Template engine | Creates the HTML pages you see |
| JSTL | JSP tag library | Loops and conditions in JSP files |
| MySQL 8 | Database | Stores all session data and admin users |
| HikariCP | Connection pool | Manages database connections efficiently |
| BCrypt | Password hashing | Securely stores passwords |
| Jakarta Mail (Angus) | Email library | Sends password reset and alert emails |
| Maven | Build tool | Compiles, tests, and packages the app |
| Tomcat 10.1 | Web server | Runs the Java web application |
| JUnit 5 | Test framework | Runs unit and integration tests |
| Mockito | Mocking library | Creates fake database objects for testing |
| Selenium 4 | Browser automation | Controls Chrome browser in tests |
| WebDriverManager | Driver manager | Automatically downloads ChromeDriver |

---

## PART 5 — HOW TO RUN THE PROJECT

### Every time you want to start the app:

**Step 1:** Make sure MySQL is running
- Search for "Services" in Windows → Find MySQL80 → Should be Running

**Step 2:** Start Tomcat (keep this window open)
```cmd
D:\apache-tomcat-10.1.55\bin\catalina.bat run
```

**Step 3:** Open browser
```
http://localhost:8080/guacamole-admin-1.0/login
```

**Step 4:** Login
- Username: superadmin
- Password: Admin@1234

### If login fails (password reset):
Open MySQL Workbench → run `deploy/reset-all-passwords.sql`

---

## PART 6 — PROJECT STRUCTURE

```
GuacamoleAdminProject/
│
├── src/main/java/com/guacamole/
│   ├── controller/     → Handles web requests (LoginServlet, ReportServlet, etc.)
│   ├── service/        → Business logic (UserService, ReportService, etc.)
│   ├── dao/            → Database queries (AdminUserDao, SessionDao, etc.)
│   ├── model/          → Data objects (AdminUser, ActiveSession, etc.)
│   │   └── dto/        → Form data objects (AdminUserDto, LoginDto)
│   ├── filter/         → Security filters (AuthFilter, RoleFilter)
│   ├── listener/       → App startup/shutdown
│   └── util/           → Utilities (DbUtil, AuditLogger, EmailNotifier)
│
├── src/main/webapp/
│   ├── jsp/            → HTML page templates
│   │   ├── reports/    → One page per report (9 reports)
│   │   ├── admin/      → Admin user management pages
│   │   └── layout/     → Shared sidebar and topbar
│   ├── css/styles.css  → All styling (responsive)
│   └── js/app.js       → JavaScript (hamburger menu, search, etc.)
│
├── src/main/resources/
│   ├── db.properties         → Database connection settings
│   ├── schema-audit.sql      → Creates admin_users + audit_log tables
│   └── db.properties.template → Template for new setups
│
├── src/test/java/com/guacamole/
│   ├── model/          → Unit tests for model classes
│   ├── service/        → Mockito tests for service classes
│   └── selenium/       → Browser automation tests
│
├── deploy/
│   ├── guacamole-official-schema.sql  → Guacamole database tables
│   ├── reset-all-passwords.sql        → Emergency password reset
│   ├── docker-compose.yml             → Run Guacamole with Docker
│   └── deploy.ps1                     → One-click Windows deploy script
│
├── docs/
│   ├── SRS.md                    → Software Requirements Specification
│   ├── README.md                 → Quick start guide
│   ├── HOW-TO-RUN.md            → Detailed run instructions
│   ├── Reports-Documentation.md  → All 9 reports explained
│   └── Project-Documentation.md  → This file
│
└── pom.xml    → Maven build configuration with all dependencies
```

---

## PART 7 — TOTAL TEST SUMMARY

| Category | Number of Tests | All Passing? |
|----------|-----------------|-------------|
| Unit Tests (Model) | 37 | YES |
| Integration Tests (Mockito) | 54 | YES |
| Selenium Tests (Browser) | 43 | YES |
| **TOTAL** | **134** | **YES** |
