-- ============================================================
-- Guacamole Admin â€” Supplementary Schema  v2.0
-- ============================================================
-- STEP 2 of 2 â€” Run this AFTER guacamole-official-schema.sql
--
-- Run this ONCE against your guacamole_db database.
-- Creates the admin portal tables (does NOT touch Guacamole tables).
--
-- Tables created:
--   admin_users       â€” admin portal accounts with roles
--   admin_audit_log   â€” audit trail of all admin actions
--
-- Default login after running:
--   Username: superadmin
--   Password: Admin@1234  â† CHANGE THIS after first login
-- ============================================================

-- â”€â”€ 1. Admin Users â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
-- Stores accounts for this admin portal (separate from Guacamole users).
-- Roles: SUPER_ADMIN | ADMIN | AUDITOR

CREATE TABLE IF NOT EXISTS admin_users (
    id              INT           NOT NULL AUTO_INCREMENT,
    username        VARCHAR(64)   NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL  COMMENT 'BCrypt hash',
    full_name       VARCHAR(128)            COMMENT 'Display name',
    email           VARCHAR(255)            COMMENT 'Contact email',
    role            ENUM('SUPER_ADMIN','ADMIN','AUDITOR')
                                  NOT NULL  DEFAULT 'AUDITOR',
    active          TINYINT(1)    NOT NULL  DEFAULT 1,
    created_at      DATETIME      NOT NULL  DEFAULT CURRENT_TIMESTAMP,
    last_login_at   DATETIME                COMMENT 'Updated on each successful login',
    created_by      VARCHAR(64)             COMMENT 'Username of creator',

    PRIMARY KEY (id),
    UNIQUE KEY uq_username (username),
    INDEX idx_role   (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Admin portal user accounts with role-based access control';


-- â”€â”€ 2. Default Super Admin â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
-- Password: Admin@1234  (BCrypt hash â€” CHANGE THIS IMMEDIATELY after first login)
INSERT IGNORE INTO admin_users
    (username, password_hash, full_name, role, active, created_by)
VALUES (
    'superadmin',
    '$2a$12$lfqSEQG2rFyCRskWAbx9JuPr2zypvrL0ui0JBd2i//n8pKWYPm0DK',
    'Super Administrator',
    'SUPER_ADMIN',
    1,
    'system'
);


-- â”€â”€ 3. Audit Log â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
-- Records every administrative action performed via this portal.

CREATE TABLE IF NOT EXISTS admin_audit_log (
    id              INT           NOT NULL AUTO_INCREMENT,
    actor_username  VARCHAR(128)  NOT NULL  COMMENT 'Admin who performed the action',
    action          VARCHAR(64)   NOT NULL  COMMENT 'Action code: LOGIN, LOGIN_FAILED, etc.',
    target_entity   VARCHAR(256)            COMMENT 'Username or connection name affected',
    details         TEXT                    COMMENT 'Free-text detail or diff',
    remote_ip       VARCHAR(64)             COMMENT 'Client IP address',
    action_time     DATETIME      NOT NULL  DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX idx_actor  (actor_username),
    INDEX idx_action (action),
    INDEX idx_time   (action_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Audit trail for all administrative actions';


-- â”€â”€ Action Code Reference â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
-- LOGIN              Successful login
-- LOGIN_FAILED       Failed login attempt
-- LOGIN_BLOCKED      Login blocked (inactive account)
-- LOGOUT             User logged out
-- CREATE_ADMIN_USER  New admin user created
-- EDIT_ADMIN_USER    Admin user profile/role updated
-- DELETE_ADMIN_USER  Admin user deleted
-- CHANGE_PASSWORD    Password changed


-- ── 4. Role Report Permissions ────────────────────────────────────────────────
-- Configurable mapping of which report types each role can access.
-- SUPER_ADMIN always has access to everything (enforced in code).
-- This table governs ADMIN and AUDITOR visibility.

CREATE TABLE IF NOT EXISTS role_report_permissions (
    id          INT          NOT NULL AUTO_INCREMENT,
    role        ENUM('SUPER_ADMIN','ADMIN','AUDITOR') NOT NULL,
    report_key  VARCHAR(64)  NOT NULL  COMMENT 'Matches ?type= param in /reports servlet',
    allowed     TINYINT(1)   NOT NULL  DEFAULT 1,

    PRIMARY KEY (id),
    UNIQUE KEY uq_role_report (role, report_key),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Configurable per-role report visibility — managed by Super Admin';


-- ── 5. Default Permissions ────────────────────────────────────────────────────
-- SUPER_ADMIN: all reports (stored for completeness; code always grants full access)
-- ADMIN (IT Admin): all reports including audit-log
-- AUDITOR: only audit-log by default (configurable)

INSERT IGNORE INTO role_report_permissions (role, report_key, allowed) VALUES
  -- SUPER_ADMIN — all
  ('SUPER_ADMIN', 'active-sessions',     1),
  ('SUPER_ADMIN', 'historical-logs',     1),
  ('SUPER_ADMIN', 'top-users',           1),
  ('SUPER_ADMIN', 'top-connections',     1),
  ('SUPER_ADMIN', 'session-duration',    1),
  ('SUPER_ADMIN', 'failed-logins',       1),
  ('SUPER_ADMIN', 'concurrent-sessions', 1),
  ('SUPER_ADMIN', 'remote-hosts',        1),
  ('SUPER_ADMIN', 'after-hours',         1),
  ('SUPER_ADMIN', 'audit-log',           1),
  -- ADMIN (IT Admin) — all
  ('ADMIN', 'active-sessions',     1),
  ('ADMIN', 'historical-logs',     1),
  ('ADMIN', 'top-users',           1),
  ('ADMIN', 'top-connections',     1),
  ('ADMIN', 'session-duration',    1),
  ('ADMIN', 'failed-logins',       1),
  ('ADMIN', 'concurrent-sessions', 1),
  ('ADMIN', 'remote-hosts',        1),
  ('ADMIN', 'after-hours',         1),
  ('ADMIN', 'audit-log',           1),
  -- AUDITOR — only audit-log by default (Super Admin can reconfigure)
  ('AUDITOR', 'active-sessions',     0),
  ('AUDITOR', 'historical-logs',     0),
  ('AUDITOR', 'top-users',           0),
  ('AUDITOR', 'top-connections',     0),
  ('AUDITOR', 'session-duration',    0),
  ('AUDITOR', 'failed-logins',       0),
  ('AUDITOR', 'concurrent-sessions', 0),
  ('AUDITOR', 'remote-hosts',        0),
  ('AUDITOR', 'after-hours',         0),
  ('AUDITOR', 'audit-log',           1);


-- ── 6. Sample Users ───────────────────────────────────────────────────────────
-- IT Admin user  (username: itadmin / password: Admin@1234)
INSERT IGNORE INTO admin_users
    (username, password_hash, full_name, role, active, created_by)
VALUES (
    'itadmin',
    '$2a$12$lfqSEQG2rFyCRskWAbx9JuPr2zypvrL0ui0JBd2i//n8pKWYPm0DK',
    'IT Admin',
    'ADMIN',
    1,
    'system'
);

-- Auditor user  (username: auditor / password: Admin@1234)
INSERT IGNORE INTO admin_users
    (username, password_hash, full_name, role, active, created_by)
VALUES (
    'auditor',
    '$2a$12$lfqSEQG2rFyCRskWAbx9JuPr2zypvrL0ui0JBd2i//n8pKWYPm0DK',
    'Auditor',
    'AUDITOR',
    1,
    'system'
);
