-- ============================================================
-- Add password reset token support to admin_users
-- Run this ONCE in MySQL Workbench
-- ============================================================

USE guacamole_db;

ALTER TABLE admin_users
    ADD COLUMN reset_token       VARCHAR(64)  NULL COMMENT 'Password reset token',
    ADD COLUMN reset_token_expiry DATETIME    NULL COMMENT 'Token expiry time',
    ADD INDEX  idx_reset_token (reset_token);

SELECT 'Password reset columns added!' AS status;
