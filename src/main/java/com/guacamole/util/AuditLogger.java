package com.guacamole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Writes every administrative action to the admin_audit_log table.
 * Call AuditLogger.log(...) from any Servlet after a state-changing operation.
 */
public class AuditLogger {

    private static final String INSERT_SQL =
            "INSERT INTO admin_audit_log " +
            "(actor_username, action, target_entity, details, remote_ip, action_time) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * @param actor        username of the admin performing the action
     * @param action       short action code, e.g. "LOGIN", "CREATE_USER"
     * @param targetEntity the object being acted upon (username, connection name, etc.)
     * @param details      optional free-text detail or diff
     * @param remoteIp     client IP from HttpServletRequest.getRemoteAddr()
     */
    public static void log(String actor,
                           String action,
                           String targetEntity,
                           String details,
                           String remoteIp) {

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, actor);
            ps.setString(2, action);
            ps.setString(3, targetEntity);
            ps.setString(4, details);
            ps.setString(5, remoteIp);
            ps.setObject(6, LocalDateTime.now());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Audit failures must never crash the application — log to stderr
            System.err.println("[AuditLogger] Failed to write audit entry: " + e.getMessage());
        }
    }

    private AuditLogger() {}
}
