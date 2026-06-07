package com.guacamole.dao;

import com.guacamole.model.ActiveSession;
import com.guacamole.model.ConnectionStat;
import com.guacamole.model.UserStat;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for session-related reports.
 * All queries target guacamole_connection_history and guacamole_connection.
 */
public class SessionDao {

    // -----------------------------------------------------------------------
    // Report 1 — Active Sessions
    // -----------------------------------------------------------------------

    /**
     * Returns sessions that have no end_date (currently active tunnels).
     */
	/**
	 * Returns currently active Guacamole sessions.
	 * Calculates live duration and keeps records even if
	 * the connection was removed later.
	 */
	public List<ActiveSession> findActiveSessions() throws SQLException {

	    String sql =
	            "SELECT h.history_id, " +
	            "       ge.name AS username, " +
	            "       COALESCE(c.connection_name, 'Deleted Connection') AS connection_name, " +
	            "       h.remote_host, " +
	            "       h.start_date, " +
	            "       h.end_date, " +
	            "       TIMESTAMPDIFF(SECOND, h.start_date, NOW()) AS duration_seconds " +
	            "FROM guacamole_connection_history h " +
	            "JOIN guacamole_entity ge ON ge.entity_id = h.user_id " +
	            "LEFT JOIN guacamole_connection c ON c.connection_id = h.connection_id " +
	            "WHERE h.end_date IS NULL " +
	            "ORDER BY h.start_date DESC";

	    return querySessionList(sql);
	}

    // -----------------------------------------------------------------------
    // Report 2 — Historical Logs
    // -----------------------------------------------------------------------

    /**
     * Returns all completed sessions, most recent first.
     * Optionally filtered by username and/or date range.
     *
     * @param username  filter by username, or null for all
     * @param fromDate  ISO date string "YYYY-MM-DD", or null
     * @param toDate    ISO date string "YYYY-MM-DD", or null
     */
    public List<ActiveSession> findHistoricalSessions(String username,
                                                      String fromDate,
                                                      String toDate) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT h.history_id, ge.name AS username, " +
                "       c.connection_name, h.remote_host, " +
                "       h.start_date, h.end_date, " +
                "       TIMESTAMPDIFF(SECOND, h.start_date, h.end_date) AS duration_seconds " +
                "FROM guacamole_connection_history h " +
                "JOIN guacamole_entity ge ON ge.entity_id = h.user_id " +
                "JOIN guacamole_connection c  ON c.connection_id = h.connection_id " +
                "WHERE h.end_date IS NOT NULL ");

        List<Object> params = new ArrayList<>();

        if (username != null && !username.isBlank()) {
            sql.append("AND ge.name LIKE ? ");
            params.add("%" + username.trim() + "%");
        }
        if (fromDate != null && !fromDate.isBlank()) {
            sql.append("AND DATE(h.start_date) >= ? ");
            params.add(fromDate.trim());
        }
        if (toDate != null && !toDate.isBlank()) {
            sql.append("AND DATE(h.start_date) <= ? ");
            params.add(toDate.trim());
        }

        sql.append("ORDER BY h.start_date DESC LIMIT 500");

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            return mapSessionList(ps.executeQuery());
        }
    }

    // -----------------------------------------------------------------------
    // Report 4 — Top Users by session count
    // -----------------------------------------------------------------------

    public List<UserStat> findTopUsers(int limit) throws SQLException {
        String sql = "SELECT ge.name AS username, " +
                     "       COUNT(*) AS total_sessions, " +
                     "       COALESCE(SUM(TIMESTAMPDIFF(SECOND, h.start_date, h.end_date)), 0) AS total_secs, " +
                     "       MAX(h.start_date) AS last_seen " +
                     "FROM guacamole_connection_history h " +
                     "JOIN guacamole_entity ge ON ge.entity_id = h.user_id " +
                     "WHERE h.end_date IS NOT NULL " +
                     "GROUP BY ge.name " +
                     "ORDER BY total_sessions DESC " +
                     "LIMIT ?";

        List<UserStat> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserStat s = new UserStat();
                    s.setUsername(rs.getString("username"));
                    s.setTotalSessions(rs.getInt("total_sessions"));
                    s.setTotalDurationSeconds(rs.getLong("total_secs"));
                    Timestamp ts = rs.getTimestamp("last_seen");
                    s.setLastSeen(ts != null ? ts.toLocalDateTime().toString() : "—");
                    list.add(s);
                }
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 4b — Top Connections by session count
    // -----------------------------------------------------------------------

    public List<ConnectionStat> findTopConnections(int limit) throws SQLException {
        String sql = "SELECT c.connection_name, " +
                     "       COUNT(*) AS total_sessions, " +
                     "       COALESCE(SUM(TIMESTAMPDIFF(SECOND, h.start_date, h.end_date)), 0) AS total_secs, " +
                     "       COALESCE(AVG(TIMESTAMPDIFF(SECOND, h.start_date, h.end_date)), 0) AS avg_secs " +
                     "FROM guacamole_connection_history h " +
                     "JOIN guacamole_connection c ON c.connection_id = h.connection_id " +
                     "WHERE h.end_date IS NOT NULL " +
                     "GROUP BY c.connection_name " +
                     "ORDER BY total_sessions DESC " +
                     "LIMIT ?";

        List<ConnectionStat> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ConnectionStat s = new ConnectionStat();
                    s.setConnectionName(rs.getString("connection_name"));
                    s.setTotalSessions(rs.getInt("total_sessions"));
                    s.setTotalDurationSeconds(rs.getLong("total_secs"));
                    s.setAvgDurationSeconds(rs.getLong("avg_secs"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 5 — Session Duration per connection
    // -----------------------------------------------------------------------

    public List<ConnectionStat> findSessionDurationByConnection() throws SQLException {
        String sql = "SELECT c.connection_name, " +
                     "       COUNT(*) AS total_sessions, " +
                     "       COALESCE(SUM(TIMESTAMPDIFF(SECOND, h.start_date, h.end_date)), 0) AS total_secs, " +
                     "       COALESCE(AVG(TIMESTAMPDIFF(SECOND, h.start_date, h.end_date)), 0) AS avg_secs " +
                     "FROM guacamole_connection_history h " +
                     "JOIN guacamole_connection c ON c.connection_id = h.connection_id " +
                     "WHERE h.end_date IS NOT NULL " +
                     "GROUP BY c.connection_name " +
                     "ORDER BY total_secs DESC";

        List<ConnectionStat> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ConnectionStat s = new ConnectionStat();
                s.setConnectionName(rs.getString("connection_name"));
                s.setTotalSessions(rs.getInt("total_sessions"));
                s.setTotalDurationSeconds(rs.getLong("total_secs"));
                s.setAvgDurationSeconds(rs.getLong("avg_secs"));
                list.add(s);
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 7 — Concurrent Sessions (peak per connection)
    // -----------------------------------------------------------------------

    /**
     * Approximates max concurrent sessions per connection by counting
     * overlapping intervals using a self-join approach.
     */
    public List<ConnectionStat> findConcurrentSessions() throws SQLException {
        // For each session A, count how many other sessions B on the same
        // connection overlapped with it, then take the max per connection.
        String sql = "SELECT c.connection_name, " +
                     "       MAX(overlap_count) AS max_concurrent " +
                     "FROM ( " +
                     "    SELECT a.connection_id, " +
                     "           COUNT(*) AS overlap_count " +
                     "    FROM guacamole_connection_history a " +
                     "    JOIN guacamole_connection_history b " +
                     "         ON a.connection_id = b.connection_id " +
                     "        AND a.history_id <> b.history_id " +
                     "        AND a.start_date < COALESCE(b.end_date, NOW()) " +
                     "        AND COALESCE(a.end_date, NOW()) > b.start_date " +
                     "    GROUP BY a.connection_id, a.history_id " +
                     ") sub " +
                     "JOIN guacamole_connection c ON c.connection_id = sub.connection_id " +
                     "GROUP BY c.connection_name " +
                     "ORDER BY max_concurrent DESC";

        List<ConnectionStat> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ConnectionStat s = new ConnectionStat();
                s.setConnectionName(rs.getString("connection_name"));
                s.setMaxConcurrent(rs.getInt("max_concurrent"));
                list.add(s);
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 8 — Remote Host Report
    // -----------------------------------------------------------------------

    /**
     * Returns distinct client IPs with session counts and the users who
     * connected from each IP.
     */
    public List<ActiveSession> findRemoteHostReport() throws SQLException {
        String sql = "SELECT h.remote_host, " +
                     "       ge.name AS username, " +
                     "       COUNT(*) AS session_count, " +
                     "       MAX(h.start_date) AS last_seen " +
                     "FROM guacamole_connection_history h " +
                     "JOIN guacamole_entity ge ON ge.entity_id = h.user_id " +
                     "WHERE h.remote_host IS NOT NULL " +
                     "GROUP BY h.remote_host, ge.name " +
                     "ORDER BY h.remote_host, last_seen DESC";

        // Reuse ActiveSession as a lightweight DTO here
        List<ActiveSession> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActiveSession s = new ActiveSession();
                s.setRemoteHost(rs.getString("remote_host"));
                s.setUsername(rs.getString("username"));
                s.setDurationSeconds(rs.getLong("session_count")); // repurposed field
                Timestamp ts = rs.getTimestamp("last_seen");
                if (ts != null) s.setStartDate(ts.toLocalDateTime());
                list.add(s);
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // Report 9 — After-Hours Access
    // -----------------------------------------------------------------------

    /**
     * Returns sessions that started outside business hours (before 08:00 or after 18:00)
     * or on weekends (Saturday=7, Sunday=1 in MySQL DAYOFWEEK).
     */
    public List<ActiveSession> findAfterHoursSessions(int businessStart, int businessEnd) throws SQLException {
        String sql = "SELECT h.history_id, ge.name AS username, " +
                     "       c.connection_name, h.remote_host, " +
                     "       h.start_date, h.end_date, " +
                     "       TIMESTAMPDIFF(SECOND, h.start_date, COALESCE(h.end_date, NOW())) AS duration_seconds " +
                     "FROM guacamole_connection_history h " +
                     "JOIN guacamole_entity ge ON ge.entity_id = h.user_id " +
                     "JOIN guacamole_connection c  ON c.connection_id = h.connection_id " +
                     "WHERE HOUR(h.start_date) < ? " +
                     "   OR HOUR(h.start_date) >= ? " +
                     "   OR DAYOFWEEK(h.start_date) IN (1, 7) " +
                     "ORDER BY h.start_date DESC " +
                     "LIMIT 500";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, businessStart);
            ps.setInt(2, businessEnd);
            return mapSessionList(ps.executeQuery());
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private List<ActiveSession> querySessionList(String sql) throws SQLException {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapSessionList(rs);
        }
    }

    private List<ActiveSession> mapSessionList(ResultSet rs) throws SQLException {
        List<ActiveSession> list = new ArrayList<>();
        while (rs.next()) {
            ActiveSession s = new ActiveSession();
            s.setHistoryId(rs.getInt("history_id"));
            s.setUsername(rs.getString("username"));
            s.setConnectionName(rs.getString("connection_name"));
            s.setRemoteHost(rs.getString("remote_host"));

            Timestamp start = rs.getTimestamp("start_date");
            if (start != null) s.setStartDate(start.toLocalDateTime());

            Timestamp end = rs.getTimestamp("end_date");
            if (end != null) s.setEndDate(end.toLocalDateTime());

            // duration_seconds column may not exist in all queries
            try {
                s.setDurationSeconds(rs.getLong("duration_seconds"));
            } catch (SQLException ignored) {}

            list.add(s);
        }
        return list;
    }
}
