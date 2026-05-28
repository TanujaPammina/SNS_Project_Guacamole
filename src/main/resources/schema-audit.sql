-- ============================================================
-- Guacamole Admin — Supplementary Schema  v2.0
-- Run this ONCE against your guacamole_db database.
-- Does NOT modify any existing Apache Guacamole tables.
-- ============================================================

-- ── 1. Admin Users ────────────────────────────────────────────
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


-- ── 2. Default Super Admin ────────────────────────────────────
-- Password: Admin@1234  (BCrypt hash — CHANGE THIS IMMEDIATELY after first login)
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


-- ── 3. Audit Log ──────────────────────────────────────────────
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


-- ── Action Code Reference ─────────────────────────────────────
-- LOGIN              Successful login
-- LOGIN_FAILED       Failed login attempt
-- LOGIN_BLOCKED      Login blocked (inactive account)
-- LOGOUT             User logged out
-- CREATE_ADMIN_USER  New admin user created
-- EDIT_ADMIN_USER    Admin user profile/role updated
-- DELETE_ADMIN_USER  Admin user deleted
-- CHANGE_PASSWORD    Password changed
