# Guacamole Admin Portal — Reports Documentation

## Overview

The Guacamole Admin Portal provides **9 reports** that help IT administrators monitor,
audit, and secure remote desktop access through Apache Guacamole.

All reports pull data directly from the Guacamole MySQL database
(`guacamole_connection_history`, `guacamole_entity`, `guacamole_user`,
`guacamole_connection`) and the portal's own `admin_audit_log` table.

---

## Report 1 — Active Sessions List

### What is it?
A **live view** of every remote desktop session that is currently open and active
right now. A session is "active" when it has a start time but no end time in the
database (`end_date IS NULL`).

### What data does it show?
| Column | Description |
|--------|-------------|
| Username | The Guacamole user who is connected |
| Connection | The name of the remote machine they are connected to |
| Client IP | The IP address of the machine the user is connecting **from** |
| Started | The date and time the session began |
| Duration | How long the session has been running (HH:MM:SS) |
| Status | Always "Active" on this report |

### Why is it useful?
- See at a glance who is inside your network right now
- Spot any unexpected or unauthorised connections immediately
- The page auto-refreshes every 30 seconds so it stays current
- First line of defence for real-time security monitoring

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 2 — Historical Logs

### What is it?
A complete **record of all past sessions** that have ended. Every time a user
disconnects from a remote machine, a record is saved. This report lets you
search and review that full history.

### What data does it show?
| Column | Description |
|--------|-------------|
| Username | Who made the connection |
| Connection | Which remote machine was accessed |
| Client IP | Where the user connected from |
| Start | When the session started |
| End | When the session ended |
| Duration | Total time of the session (HH:MM:SS) |

### Filters available
- **Username** — show sessions for one specific user
- **From / To** — filter by date range
- **Last 7 days** / **Last 30 days** — quick shortcut buttons

### Why is it useful?
- Full audit trail of all remote access activity
- Required for compliance and security audits
- Investigate incidents — find out who accessed what and when
- Prove or disprove whether a specific user was connected at a specific time

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 3 — User Details / Users List

### What is it?
A directory of **all Guacamole users** registered in the system, with their
profile information and account status.

### What data does it show?

**Users List view:**
| Column | Description |
|--------|-------------|
| Username | The user's login name in Guacamole |
| Full Name | Display name (if set) |
| Email | Contact email address (if set) |
| Status | Active, Disabled, or Expired |
| Last Active | When the user last had a session |

**User Detail view** (click any user):
- All profile fields in full
- Direct link to view that user's session history
- Account flags (disabled, expired)

### Why is it useful?
- Quickly check who has access to the Guacamole system
- Identify disabled or expired accounts that should be cleaned up
- Jump from a user's profile straight to their session history
- Useful for onboarding/offboarding reviews

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 4 — Top Users / Top Connections

### What is it?
Two ranked reports showing the **most active users** and **most accessed
remote machines** based on session count.

### Report 4a — Top Users
Ranks users by how many sessions they have started.

| Column | Description |
|--------|-------------|
| Rank | Position (1 = most sessions) |
| Username | The user |
| Total Sessions | Number of completed sessions |
| Total Duration | Combined time of all their sessions |
| Last Seen | When they last had a session |

### Report 4b — Top Connections
Ranks remote machines by how many times they have been accessed.

| Column | Description |
|--------|-------------|
| Rank | Position (1 = most accessed) |
| Connection Name | The remote machine |
| Total Sessions | How many times it was accessed |
| Total Duration | Combined time of all sessions to this machine |
| Avg Duration | Average session length |

### Why is it useful?
- Identify your most active users — useful for licence management
- Find your most heavily used servers — useful for capacity planning
- Spot unusual spikes in activity for a particular user or machine
- Understand usage patterns across the organisation

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 5 — Session Duration

### What is it?
A detailed breakdown of **how long sessions last** for each remote connection —
both the average time per session and the total cumulative time.

### What data does it show?
| Column | Description |
|--------|-------------|
| Connection Name | The remote machine |
| Total Sessions | Number of completed sessions |
| Total Duration | Sum of all session times (HH:MM:SS) |
| Avg Duration | Average time per session (HH:MM:SS) |

### Example
```
Web Server 01   →  42 sessions  →  Total: 84:30:00  →  Avg: 02:00:00
DB Server 01    →  18 sessions  →  Total: 09:00:00  →  Avg: 00:30:00
```

### Why is it useful?
- Understand how long users typically spend on each machine
- Identify connections with unusually long sessions (possible forgotten sessions)
- Useful for billing if remote access time is charged
- Helps plan maintenance windows — know when machines are least used

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 6 — Tracking of Failed Login Attempts

### What is it?
A security report that records every **failed login attempt** to the admin
portal. It has two views — a summary and a detailed event log.

### Summary view (30-day overview)
| Column | Description |
|--------|-------------|
| Username | Which username was attempted |
| Failed Attempts (30d) | How many failures in the last 30 days |
| Risk Level | Low (under 5) / Medium (5–9) / High (10+) |

### Detail view (individual events)
| Column | Description |
|--------|-------------|
| Username | The username that was tried |
| Client IP | Where the attempt came from |
| Attempt Time | Exact date and time of the failed attempt |

### Filters available
- Username, date range (From / To), Last 7 days, Last 30 days

### Automatic alerts
Every failed login automatically sends an **email alert** to the configured
administrator email address.

### Why is it useful?
- Detect brute-force attacks — many failures from one IP in a short time
- Identify compromised or guessed usernames
- The risk classification (Low/Medium/High) highlights accounts under attack
- Required for security compliance and incident response

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 7 — Concurrent Sessions Report

### What is it?
Shows the **peak number of users simultaneously connected** to each remote
machine at the same time. This is calculated by finding overlapping session
time intervals.

### What data does it show?
| Column | Description |
|--------|-------------|
| Connection Name | The remote machine |
| Peak Concurrent Sessions | Maximum number of users connected at the same time |
| Load Level | Low (under 5) / Medium (5–9) / High (10+) |

### Example
```
Web Server 01  →  Peak: 8 simultaneous users  →  Load: Medium
DB Server 01   →  Peak: 2 simultaneous users  →  Load: Low
```

### Why is it useful?
- Know the maximum load each server has experienced
- Identify servers that are being overloaded
- Check if concurrent connection limits are being respected
- Essential for capacity planning and licence compliance
  (some remote desktop licences limit simultaneous connections)

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 8 — Remote Host Report

### What is it?
Shows **which client machines** (identified by IP address) users are connecting
from. This answers the question: "Where in the network are people accessing
remote desktops from?"

### What data does it show?
| Column | Description |
|--------|-------------|
| Client IP | The IP address of the machine the user connected from |
| Username | Which user connected from that IP |
| Session Count | How many sessions came from that IP |
| Last Seen | When the most recent session from that IP occurred |

### Example
```
192.168.1.55  →  john.doe    →  12 sessions  →  Last: 2026-05-28
10.0.0.22     →  jane.smith  →   3 sessions  →  Last: 2026-05-26
203.0.113.5   →  guacadmin   →   1 session   →  Last: 2026-05-20  ← suspicious (external IP)
```

### Why is it useful?
- Detect access from unexpected locations (e.g. external IPs, foreign countries)
- Verify that users are connecting from approved office networks
- Identify shared machines — multiple users connecting from the same IP
- Useful for investigating security incidents — trace a session back to a physical machine

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Report 9 — Security: After-Hours Access Report

### What is it?
Flags all sessions that started **outside normal business hours** or on
**weekends**. Business hours are defined as Monday–Friday, 08:00–18:00.

Any session starting before 08:00, at or after 18:00, or on Saturday/Sunday
appears in this report.

### What data does it show?
| Column | Description |
|--------|-------------|
| Username | Who accessed the system after hours |
| Connection | Which remote machine was accessed |
| Client IP | Where they connected from |
| Start Time | When the session started (shows the after-hours time) |
| End Time | When the session ended (or "Active" if still running) |
| Duration | How long the session lasted |
| Status | Active or Ended |

### Automatic alerts
When an after-hours session is detected, an **email alert** is automatically
sent to the configured administrator.

### Why is it useful?
- Detect insider threats — employees accessing systems outside working hours
- Identify compromised accounts being used at unusual times
- Required for security compliance in many industries
- Highlight sessions that need manager approval or investigation
- Weekend access is often a red flag for unauthorised activity

### Who can see it?
All roles — Super Admin, Admin, Auditor

---

## Summary Table

| # | Report | Data Source | Key Purpose |
|---|--------|-------------|-------------|
| 1 | Active Sessions | `connection_history` (no end_date) | Real-time monitoring |
| 2 | Historical Logs | `connection_history` (completed) | Audit trail |
| 3 | User Details | `entity` + `user` tables | User management |
| 4 | Top Users / Connections | `connection_history` (aggregated) | Usage analysis |
| 5 | Session Duration | `connection_history` (avg/sum) | Capacity planning |
| 6 | Failed Logins | `admin_audit_log` | Brute-force detection |
| 7 | Concurrent Sessions | `connection_history` (overlap) | Load monitoring |
| 8 | Remote Hosts | `connection_history` (by IP) | Location tracking |
| 9 | After-Hours Access | `connection_history` (by time) | Security compliance |
