package com.guacamole.dao;

import com.guacamole.model.Role;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.*;

/**
 * DAO — reads and writes the role_report_permissions table.
 *
 * The table stores one row per (role, report_key) pair with an allowed flag.
 * SUPER_ADMIN is always fully permitted in code; this DAO is mainly used for
 * ADMIN and AUDITOR configuration.
 */
public class ReportPermissionDao {

    /**
     * Returns the set of report keys that are allowed for the given role.
     * If no rows exist for a role (e.g. fresh DB), returns an empty set
     * and callers should treat that as "no access".
     */
    public Set<String> getAllowedReports(Role role) throws SQLException {
        String sql = "SELECT report_key FROM role_report_permissions " +
                     "WHERE role = ? AND allowed = 1";

        Set<String> keys = new HashSet<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) keys.add(rs.getString("report_key"));
            }
        }
        return keys;
    }

    /**
     * Returns a map of reportKey → allowed(boolean) for ALL known reports
     * for the given role.  Rows missing from the DB default to false.
     */
    public Map<String, Boolean> getPermissionMap(Role role) throws SQLException {
        String sql = "SELECT report_key, allowed FROM role_report_permissions WHERE role = ?";

        Map<String, Boolean> map = new LinkedHashMap<>();
        // seed with all known reports defaulting to false
        for (String key : ALL_REPORT_KEYS) map.put(key, false);

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("report_key"), rs.getBoolean("allowed"));
                }
            }
        }
        return map;
    }

    /**
     * Saves the full permission set for a role in a single transaction.
     * Upserts every (role, reportKey) pair.
     */
    public void savePermissions(Role role, Set<String> allowedKeys) throws SQLException {
        String upsert = "INSERT INTO role_report_permissions (role, report_key, allowed) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE allowed = VALUES(allowed)";

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(upsert)) {
                for (String key : ALL_REPORT_KEYS) {
                    ps.setString(1, role.name());
                    ps.setString(2, key);
                    ps.setBoolean(3, allowedKeys.contains(key));
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Ordered list of all 10 report keys (9 operational + audit-log).
     * The order determines display order in the UI.
     */
    public static final List<String> ALL_REPORT_KEYS = List.of(
        "active-sessions",
        "historical-logs",
        "top-users",
        "top-connections",
        "session-duration",
        "failed-logins",
        "concurrent-sessions",
        "remote-hosts",
        "after-hours",
        "audit-log"
    );

    /** Human-readable label for each report key, used in the UI. */
    public static final Map<String, String> REPORT_LABELS = Map.of(
        "active-sessions",     "Active Sessions",
        "historical-logs",     "Historical Session Logs",
        "top-users",           "Top Users by Sessions",
        "top-connections",     "Top Connections",
        "session-duration",    "Session Duration by Connection",
        "failed-logins",       "Failed Login Attempts",
        "concurrent-sessions", "Concurrent Sessions",
        "remote-hosts",        "Remote Host Report",
        "after-hours",         "After-Hours Access",
        "audit-log",           "Audit Log"
    );
}
