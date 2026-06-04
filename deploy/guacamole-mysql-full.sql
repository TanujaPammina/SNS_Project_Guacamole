-- ============================================================
-- Apache Guacamole 1.5.5 — Full MySQL Schema
-- Run this in guacamole_db BEFORE starting the Docker container
-- Source: https://github.com/apache/guacamole-client
-- ============================================================

USE guacamole_db;

-- Drop existing tables if re-running (safe order)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS guacamole_connection_history;
DROP TABLE IF EXISTS guacamole_connection_parameter;
DROP TABLE IF EXISTS guacamole_connection_attribute;
DROP TABLE IF EXISTS guacamole_connection_permission;
DROP TABLE IF EXISTS guacamole_connection;
DROP TABLE IF EXISTS guacamole_connection_group_permission;
DROP TABLE IF EXISTS guacamole_connection_group_attribute;
DROP TABLE IF EXISTS guacamole_connection_group;
DROP TABLE IF EXISTS guacamole_sharing_profile_parameter;
DROP TABLE IF EXISTS guacamole_sharing_profile_attribute;
DROP TABLE IF EXISTS guacamole_sharing_profile_permission;
DROP TABLE IF EXISTS guacamole_sharing_profile;
DROP TABLE IF EXISTS guacamole_user_password_history;
DROP TABLE IF EXISTS guacamole_user_attribute;
DROP TABLE IF EXISTS guacamole_user_permission;
DROP TABLE IF EXISTS guacamole_system_permission;
DROP TABLE IF EXISTS guacamole_user_group_member;
DROP TABLE IF EXISTS guacamole_user_group_attribute;
DROP TABLE IF EXISTS guacamole_user_group_permission;
DROP TABLE IF EXISTS guacamole_user_group;
DROP TABLE IF EXISTS guacamole_user;
DROP TABLE IF EXISTS guacamole_entity;
SET FOREIGN_KEY_CHECKS = 1;

-- Entity
CREATE TABLE guacamole_entity (
    entity_id   INT           NOT NULL AUTO_INCREMENT,
    name        VARCHAR(128)  NOT NULL,
    type        ENUM('USER','USER_GROUP') NOT NULL,
    PRIMARY KEY (entity_id),
    UNIQUE KEY uq_entity_type_name (type, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User group
CREATE TABLE guacamole_user_group (
    user_group_id INT NOT NULL AUTO_INCREMENT,
    entity_id     INT NOT NULL,
    disabled      TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (user_group_id),
    UNIQUE KEY uq_user_group_entity (entity_id),
    CONSTRAINT fk_user_group_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User
CREATE TABLE guacamole_user (
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

-- User group member
CREATE TABLE guacamole_user_group_member (
    user_group_id    INT NOT NULL,
    member_entity_id INT NOT NULL,
    PRIMARY KEY (user_group_id, member_entity_id),
    CONSTRAINT fk_member_entity FOREIGN KEY (member_entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_member_group FOREIGN KEY (user_group_id)
        REFERENCES guacamole_user_group (user_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection group
CREATE TABLE guacamole_connection_group (
    connection_group_id   INT           NOT NULL AUTO_INCREMENT,
    parent_id             INT,
    connection_group_name VARCHAR(128)  NOT NULL,
    type                  ENUM('ORGANIZATIONAL','BALANCING') NOT NULL DEFAULT 'ORGANIZATIONAL',
    max_connections       INT,
    max_connections_per_user INT,
    enable_session_affinity TINYINT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (connection_group_id),
    UNIQUE KEY uq_connection_group_name (connection_group_name, parent_id),
    CONSTRAINT fk_cg_parent FOREIGN KEY (parent_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection
CREATE TABLE guacamole_connection (
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
    UNIQUE KEY uq_connection_name (connection_name, parent_id),
    CONSTRAINT fk_conn_group FOREIGN KEY (parent_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection parameter
CREATE TABLE guacamole_connection_parameter (
    connection_id   INT           NOT NULL,
    parameter_name  VARCHAR(128)  NOT NULL,
    parameter_value VARCHAR(4096) NOT NULL,
    PRIMARY KEY (connection_id, parameter_name),
    CONSTRAINT fk_param_connection FOREIGN KEY (connection_id)
        REFERENCES guacamole_connection (connection_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sharing profile
CREATE TABLE guacamole_sharing_profile (
    sharing_profile_id   INT          NOT NULL AUTO_INCREMENT,
    sharing_profile_name VARCHAR(128) NOT NULL,
    primary_connection_id INT         NOT NULL,
    PRIMARY KEY (sharing_profile_id),
    UNIQUE KEY uq_sharing_profile_name (sharing_profile_name, primary_connection_id),
    CONSTRAINT fk_sp_connection FOREIGN KEY (primary_connection_id)
        REFERENCES guacamole_connection (connection_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sharing profile parameter
CREATE TABLE guacamole_sharing_profile_parameter (
    sharing_profile_id INT           NOT NULL,
    parameter_name     VARCHAR(128)  NOT NULL,
    parameter_value    VARCHAR(4096) NOT NULL,
    PRIMARY KEY (sharing_profile_id, parameter_name),
    CONSTRAINT fk_spp_profile FOREIGN KEY (sharing_profile_id)
        REFERENCES guacamole_sharing_profile (sharing_profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection history
CREATE TABLE guacamole_connection_history (
    history_id           INT           NOT NULL AUTO_INCREMENT,
    user_id              INT,
    username             VARCHAR(128)  NOT NULL,
    remote_host          VARCHAR(256),
    connection_id        INT,
    connection_name      VARCHAR(128)  NOT NULL,
    sharing_profile_id   INT,
    sharing_profile_name VARCHAR(128),
    start_date           DATETIME      NOT NULL,
    end_date             DATETIME,
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

-- User attribute
CREATE TABLE guacamole_user_attribute (
    user_id         INT           NOT NULL,
    attribute_name  VARCHAR(128)  NOT NULL,
    attribute_value VARCHAR(4096) NOT NULL,
    PRIMARY KEY (user_id, attribute_name),
    CONSTRAINT fk_ua_user FOREIGN KEY (user_id)
        REFERENCES guacamole_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection attribute
CREATE TABLE guacamole_connection_attribute (
    connection_id   INT           NOT NULL,
    attribute_name  VARCHAR(128)  NOT NULL,
    attribute_value VARCHAR(4096) NOT NULL,
    PRIMARY KEY (connection_id, attribute_name),
    CONSTRAINT fk_ca_connection FOREIGN KEY (connection_id)
        REFERENCES guacamole_connection (connection_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection group attribute
CREATE TABLE guacamole_connection_group_attribute (
    connection_group_id INT           NOT NULL,
    attribute_name      VARCHAR(128)  NOT NULL,
    attribute_value     VARCHAR(4096) NOT NULL,
    PRIMARY KEY (connection_group_id, attribute_name),
    CONSTRAINT fk_cga_group FOREIGN KEY (connection_group_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sharing profile attribute
CREATE TABLE guacamole_sharing_profile_attribute (
    sharing_profile_id INT           NOT NULL,
    attribute_name     VARCHAR(128)  NOT NULL,
    attribute_value    VARCHAR(4096) NOT NULL,
    PRIMARY KEY (sharing_profile_id, attribute_name),
    CONSTRAINT fk_spa_profile FOREIGN KEY (sharing_profile_id)
        REFERENCES guacamole_sharing_profile (sharing_profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User group attribute
CREATE TABLE guacamole_user_group_attribute (
    user_group_id   INT           NOT NULL,
    attribute_name  VARCHAR(128)  NOT NULL,
    attribute_value VARCHAR(4096) NOT NULL,
    PRIMARY KEY (user_group_id, attribute_name),
    CONSTRAINT fk_uga_group FOREIGN KEY (user_group_id)
        REFERENCES guacamole_user_group (user_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- System permission
CREATE TABLE guacamole_system_permission (
    entity_id  INT NOT NULL,
    permission ENUM('CREATE_CONNECTION','CREATE_CONNECTION_GROUP','CREATE_SHARING_PROFILE',
                    'CREATE_USER','CREATE_USER_GROUP','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, permission),
    CONSTRAINT fk_sp_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User permission
CREATE TABLE guacamole_user_permission (
    entity_id        INT NOT NULL,
    affected_user_id INT NOT NULL,
    permission       ENUM('READ','UPDATE','DELETE','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, affected_user_id, permission),
    CONSTRAINT fk_up_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_up_affected FOREIGN KEY (affected_user_id)
        REFERENCES guacamole_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User group permission
CREATE TABLE guacamole_user_group_permission (
    entity_id              INT NOT NULL,
    affected_user_group_id INT NOT NULL,
    permission             ENUM('READ','UPDATE','DELETE','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, affected_user_group_id, permission),
    CONSTRAINT fk_ugp_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_ugp_affected FOREIGN KEY (affected_user_group_id)
        REFERENCES guacamole_user_group (user_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection permission
CREATE TABLE guacamole_connection_permission (
    entity_id     INT NOT NULL,
    connection_id INT NOT NULL,
    permission    ENUM('READ','UPDATE','DELETE','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, connection_id, permission),
    CONSTRAINT fk_cp_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_connection FOREIGN KEY (connection_id)
        REFERENCES guacamole_connection (connection_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Connection group permission
CREATE TABLE guacamole_connection_group_permission (
    entity_id           INT NOT NULL,
    connection_group_id INT NOT NULL,
    permission          ENUM('READ','UPDATE','DELETE','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, connection_group_id, permission),
    CONSTRAINT fk_cgp_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_cgp_group FOREIGN KEY (connection_group_id)
        REFERENCES guacamole_connection_group (connection_group_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sharing profile permission
CREATE TABLE guacamole_sharing_profile_permission (
    entity_id          INT NOT NULL,
    sharing_profile_id INT NOT NULL,
    permission         ENUM('READ','UPDATE','DELETE','ADMINISTER') NOT NULL,
    PRIMARY KEY (entity_id, sharing_profile_id, permission),
    CONSTRAINT fk_spp2_entity FOREIGN KEY (entity_id)
        REFERENCES guacamole_entity (entity_id) ON DELETE CASCADE,
    CONSTRAINT fk_spp2_profile FOREIGN KEY (sharing_profile_id)
        REFERENCES guacamole_sharing_profile (sharing_profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User password history
CREATE TABLE guacamole_user_password_history (
    password_history_id INT        NOT NULL AUTO_INCREMENT,
    user_id             INT        NOT NULL,
    password_hash       BINARY(32) NOT NULL,
    password_salt       BINARY(32),
    password_date       DATETIME   NOT NULL,
    PRIMARY KEY (password_history_id),
    CONSTRAINT fk_ph_user FOREIGN KEY (user_id)
        REFERENCES guacamole_user (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Default admin user (guacadmin / guacadmin) ────────────────
INSERT INTO guacamole_entity (name, type) VALUES ('guacadmin', 'USER');

INSERT INTO guacamole_user (entity_id, password_hash, password_salt, password_date)
SELECT entity_id,
       x'CA458A7D494E3BE824F5E1E175A1556C0F8EEF2C2D7DF3633BEC4A29C4411960',
       x'FE24ADC5E11E2B25288D1704ABE67A79E342ECC26064CE69C5B3177795A82264',
       NOW()
FROM guacamole_entity WHERE name = 'guacadmin';

-- Give guacadmin full system permissions
INSERT INTO guacamole_system_permission (entity_id, permission)
SELECT entity_id, permission
FROM guacamole_entity,
(SELECT 'CREATE_CONNECTION'       AS permission UNION
 SELECT 'CREATE_CONNECTION_GROUP' UNION
 SELECT 'CREATE_SHARING_PROFILE'  UNION
 SELECT 'CREATE_USER'             UNION
 SELECT 'CREATE_USER_GROUP'       UNION
 SELECT 'ADMINISTER') perms
WHERE name = 'guacadmin';

-- guacadmin can manage itself
INSERT INTO guacamole_user_permission (entity_id, affected_user_id, permission)
SELECT e.entity_id, u.user_id, 'READ'
FROM guacamole_entity e
JOIN guacamole_user u ON u.entity_id = e.entity_id
WHERE e.name = 'guacadmin';

INSERT INTO guacamole_user_permission (entity_id, affected_user_id, permission)
SELECT e.entity_id, u.user_id, 'UPDATE'
FROM guacamole_entity e
JOIN guacamole_user u ON u.entity_id = e.entity_id
WHERE e.name = 'guacadmin';

INSERT INTO guacamole_user_permission (entity_id, affected_user_id, permission)
SELECT e.entity_id, u.user_id, 'ADMINISTER'
FROM guacamole_entity e
JOIN guacamole_user u ON u.entity_id = e.entity_id
WHERE e.name = 'guacadmin';

SELECT 'Guacamole schema created. Login: guacadmin / guacadmin' AS status;
