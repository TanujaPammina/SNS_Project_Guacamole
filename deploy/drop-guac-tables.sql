USE guacamole_db;

-- Drop all old Guacamole tables in safe order
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
DROP TABLE IF EXISTS guacamole_user_history;
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

SELECT 'All old tables dropped. Now run guacamole-official-schema.sql' AS status;
