USE guacamole_db;

-- Give guacadmin full permissions on the Test SSH connection
INSERT IGNORE INTO guacamole_connection_permission (entity_id, connection_id, permission)
SELECT e.entity_id, c.connection_id, p.permission
FROM guacamole_entity e
CROSS JOIN guacamole_connection c
CROSS JOIN (
    SELECT 'READ'       AS permission UNION
    SELECT 'UPDATE'     UNION
    SELECT 'DELETE'     UNION
    SELECT 'ADMINISTER'
) p
WHERE e.name = 'guacadmin' AND e.type = 'USER';

-- Also ensure guacadmin has ADMINISTER on itself
INSERT IGNORE INTO guacamole_user_permission (entity_id, affected_user_id, permission)
SELECT e.entity_id, u.user_id, 'ADMINISTER'
FROM guacamole_entity e
JOIN guacamole_user u ON u.entity_id = e.entity_id
WHERE e.name = 'guacadmin';

SELECT 'Permissions fixed!' AS status;
