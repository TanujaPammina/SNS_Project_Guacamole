-- ============================================================
-- Apache Guacamole — MySQL Schema (minimal subset)
-- Creates only the tables needed by the Admin portal reports.
-- Run this in guacamole_db ONCE.
-- ============================================================

USE guacamole_db;

-- ── Entity (base for users and groups) ───────────────────────
CREATE TABLE IF NOT EXISTS guacamole_entity (
    entity_id   INT           NOT NULL AUTO_INCREMENT,
    name        VARCHAR(128)  NOT NULL,
    type        ENUM('USER','USER_GROUP') NOT NULL,
    PRIMARY KEY (entity_id),
    UNIQUE KEY uq_entity (type, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Users ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS guacamole_user (
    user_id         INT           NOT NULL AUTO_INCREMENT,
    entity_id       INT           NOT NULL,
    password_hash   BINARY(32)    NOT NULL,
    password_salt   BINARY(32),
    password_date   DATETIME      NOT NULL,
    disabled        TINYINT(1)    NOT NULL DEFAULT 0,
    expired         TINYINT(1)    NOT NULL DEFAULT 0,
    access_window_start    TIME,
    access_window_end      TIME,
    valid_from      DATE,
    valid_until     DATE,
    timezone        VARCHAR(64),
    full_name       VARCHAR(256),
    email_address   VARCHAR(256),
    organization    VARCHAR(256),
    organizational_role VARCHAR(256),
    last_active     DATETIME,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_user_entity (entity_id),
    CONSTRAINT fk_user_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Connection Groups ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS guacamole_connection_group (
    connection_group_id   INT           NOT NULL AUTO_INCREMENT,
    parent_id             INT,
    connection_group_name VARCHAR(128)  NOT NULL,
    type                  ENUM('ORGANIZATIONAL','BALANCING') NOT NULL DEFAULT 'ORGANIZATIONAL',
    max_connections       INT,
    max_connections_per_user INT,
    enable_session_affinity TINYINT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (connection_group_id),
    CONSTRAINT fk_cg_parent FOREIGN KEY (parent_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Connections ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS guacamole_connection (
    connection_id       INT           NOT NULL AUTO_INCREMENT,
    connection_name     VARCHAR(128)  NOT NULL,
    parent_id           INT,
    protocol            VARCHAR(32)   NOT NULL,
    proxy_port          INT,
    proxy_hostname      VARCHAR(512),
    proxy_encryption_method ENUM('NONE','SSL'),
    max_connections     INT,
    max_connections_per_user INT,
    connection_weight   INT,
    failover_only       TINYINT(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (connection_id),
    CONSTRAINT fk_conn_group FOREIGN KEY (parent_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Connection History ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS guacamole_connection_history (
    history_id          INT           NOT NULL AUTO_INCREMENT,
    user_id             INT,
    username            VARCHAR(128)  NOT NULL,
    remote_host         VARCHAR(256),
    connection_id       INT,
    connection_name     VARCHAR(128)  NOT NULL,
    sharing_profile_id  INT,
    sharing_profile_name VARCHAR(128),
    start_date          DATETIME      NOT NULL,
    end_date            DATETIME,
    PRIMARY KEY (history_id),
    INDEX idx_history_user       (user_id),
    INDEX idx_history_connection (connection_id),
    INDEX idx_history_start      (start_date),
    INDEX idx_history_end        (end_date),
    CONSTRAINT fk_history_user FOREIGN KEY (user_id)
        REFERENCES guacamole_user (user_id) ON DELETE SET NULL,
    CONSTRAINT fk_history_connection FOREIGN KEY (connection_id)
        REFERENCES guacamole_connection (connection_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Sample Data (so reports show something) ───────────────────

-- Sample users
INSERT IGNORE INTO guacamole_entity (name, type) VALUES
    ('guacadmin', 'USER'),
    ('john.doe',  'USER'),
    ('jane.smith','USER');

INSERT IGNORE INTO guacamole_user
    (entity_id, password_hash, password_salt, password_date, full_name, email_address, last_active)
SELECT entity_id,
       UNHEX(SHA2('password', 256)),
       UNHEX(SHA2(RAND(), 256)),
       NOW(),
       CASE name
           WHEN 'guacadmin'  THEN 'Guacamole Admin'
           WHEN 'john.doe'   THEN 'John Doe'
           WHEN 'jane.smith' THEN 'Jane Smith'
       END,
       CONCAT(name, '@example.com'),
       NOW()
FROM guacamole_entity
WHERE type = 'USER'
  AND name IN ('guacadmin','john.doe','jane.smith');

-- Sample connection group
INSERT IGNORE INTO guacamole_connection_group
    (connection_group_name, type)
VALUES ('Production Servers', 'ORGANIZATIONAL');

-- Sample connections
INSERT IGNORE INTO guacamole_connection
    (connection_name, parent_id, protocol)
VALUES
    ('Web Server 01',  1, 'rdp'),
    ('DB Server 01',   1, 'ssh'),
    ('Dev Machine',    1, 'vnc');

-- Sample connection history (last 30 days)
INSERT INTO guacamole_connection_history
    (user_id, username, remote_host, connection_id, connection_name, start_date, end_date)
SELECT
    u.user_id,
    e.name,
    CONCAT('192.168.1.', FLOOR(10 + RAND() * 90)),
    c.connection_id,
    c.connection_name,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 29) DAY)
FROM guacamole_user u
JOIN guacamole_entity e ON e.entity_id = u.entity_id
CROSS JOIN guacamole_connection c
LIMIT 30;

-- A few active sessions (no end_date)
INSERT INTO guacamole_connection_history
    (user_id, username, remote_host, connection_id, connection_name, start_date, end_date)
SELECT
    u.user_id, e.name,
    '192.168.1.55',
    1, 'Web Server 01',
    DATE_SUB(NOW(), INTERVAL 45 MINUTE),
    NULL
FROM guacamole_user u
JOIN guacamole_entity e ON e.entity_id = u.entity_id
WHERE e.name = 'john.doe';

-- Some after-hours sessions (2 AM)
INSERT INTO guacamole_connection_history
    (user_id, username, remote_host, connection_id, connection_name, start_date, end_date)
SELECT
    u.user_id, e.name,
    '10.0.0.22',
    2, 'DB Server 01',
    DATE_SUB(NOW(), INTERVAL 2 DAY) - INTERVAL (HOUR(NOW()) - 2) HOUR,
    DATE_SUB(NOW(), INTERVAL 2 DAY) - INTERVAL (HOUR(NOW()) - 3) HOUR
FROM guacamole_user u
JOIN guacamole_entity e ON e.entity_id = u.entity_id
WHERE e.name = 'jane.smith';

SELECT 'Schema and sample data created successfully!' AS status;
