USE guacamole_db;

-- Reset all admin portal passwords
-- superadmin  → Admin@1234
-- admin1      → Admin@1234
-- auditor1    → Audit@1234

UPDATE admin_users
SET password_hash = '$2a$10$9vxQAIqjedgn4o7reMaFVuZeQLqSO.JXQd0QjQ4JqSZI13EPYmbYq'
WHERE username = 'superadmin';

UPDATE admin_users
SET password_hash = '$2a$10$8GC05o3li0QSVDULzH/xb.ZJITKFURJJJidA9LgZ3AicE82.YJwqq'
WHERE username = 'admin1';

UPDATE admin_users
SET password_hash = '$2a$10$Haq22dABwRIvQ.AYp7qrxeC4fUwTKE2licuQiYQKL8YrNiqZKc9ku'
WHERE username = 'auditor1';

-- Verify
SELECT username, role, active FROM admin_users;
