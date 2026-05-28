package com.guacamole.dao;

import com.guacamole.model.AuditLog;
import com.guacamole.model.FailedLogin;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the admin_audit_log table and failed-login queries.
 */
public class AuditDao {

    // -----------------------------------------------------------------------
    // Report 2 — Historical / Audit Logs
    // -----------------------------------------------------------------------

    public List<AuditLog> findAuditLogs(String actor, String action,
                                        String fromDate, String toDate) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT id, actor_username, action, target_entity, details, remote_ip, action_time " +
                "FROM admin_audit_log WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (actor != null && !actor.isBlank()) {
            sql.append("AND actor_username = ? ");
            params.add(actor);
        }
        if (action != null && !action.isBlank()) {
            sql.append("AND action = ? ");
            params.add(action);
        }
        if (fromDate != null && !fromDate.isBlank()) {
            sql.append("AND DATE(action_time) >= ? ");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append("AND DATE(action_time) <= ? ");
            params.add(toDate);
        }

        sql.append("ORDER BY action_time DESC LIMIT 1000");

        List<AuditLog> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AuditLog log = new AuditLog();
                    log.setId(rs.getInt("id"));
                    log.setActorUsername(rs.getString("actor_username"));
                    log.setAction(rs.getString("action"));
                    log.setTargetEntity(rs.getString("target_entity"));
                    log.setDetails(rs.getString("details"));
                    log.setRemoteIp(rs.getString("remote_ip"));
                    Timestamp ts = rs.getTimestamp("action_time");
                    if (ts != null) log.setActionTime(ts.toLocalDateTime());
                    list.add(log);
                }
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 6 — Failed Login Attempts
    // -----------------------------------------------------------------------

    /** Returns individual failed login events, most recent first. */
    public List<FailedLogin> findFailedLogins(String username,
                                              String fromDate,
                                              String toDate) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT id, actor_username AS username, remote_ip, action_time " +
                "FROM admin_audit_log " +
                "WHERE action = 'LOGIN_FAILED' ");

        List<Object> params = new ArrayList<>();

        if (username != null && !username.isBlank()) {
            sql.append("AND actor_username = ? ");
            params.add(username);
        }
        if (fromDate != null && !fromDate.isBlank()) {
            sql.append("AND DATE(action_time) >= ? ");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append("AND DATE(action_time) <= ? ");
            params.add(toDate);
        }

        sql.append("ORDER BY action_time DESC LIMIT 500");

        List<FailedLogin> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FailedLogin fl = new FailedLogin();
                    fl.setId(rs.getInt("id"));
                    fl.setUsername(rs.getString("username"));
                    fl.setRemoteIp(rs.getString("remote_ip"));
                    Timestamp ts = rs.getTimestamp("action_time");
                    if (ts != null) fl.setAttemptTime(ts.toLocalDateTime());
                    list.add(fl);
                }
            }
        }
        return list;
    }

    /** Returns a summary: username → count of failed attempts in the last N days. */
    public List<FailedLogin> findFailedLoginSummary(int days) throws SQLException {
        String sql = "SELECT actor_username AS username, COUNT(*) AS fail_count " +
                     "FROM admin_audit_log " +
                     "WHERE action = 'LOGIN_FAILED' " +
                     "  AND action_time >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "GROUP BY actor_username " +
                     "ORDER BY fail_count DESC";

        List<FailedLogin> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FailedLogin fl = new FailedLogin();
                    fl.setUsername(rs.getString("username"));
                    fl.setFailCount(rs.getInt("fail_count"));
                    list.add(fl);
                }
            }
        }
        return list;
    }
}
