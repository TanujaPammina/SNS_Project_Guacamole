USE guacamole_db;
UPDATE admin_users SET password_hash = '$2a$10$ceRYWQ3BmECZAACsZA1vk.s630rxZseoDn75EHYvpNUU/K7XvWj6y' WHERE username = 'superadmin';
UPDATE admin_users SET password_hash = '$2a$10$IUZ9nmpiF6/MDtoPkg9hkOhHD3CjS2tOBkoyqMUlluJM3br16oKDW' WHERE username = 'admin1';
UPDATE admin_users SET password_hash = '$2a$10$3N5iQ2UF331/ByKHGE5WMOruvdKQUZwNiioDjT8sSwjlNGZNSMmtO' WHERE username = 'auditor1';
SELECT username, role FROM admin_users;
