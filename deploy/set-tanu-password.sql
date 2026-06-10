USE guacamole_db;
UPDATE admin_users SET password_hash = '' WHERE username = 'superadmin';
SELECT username, LEFT(password_hash,20) AS hash FROM admin_users;
