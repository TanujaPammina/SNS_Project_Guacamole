package com.guacamole.dao;

import com.guacamole.model.User;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Guacamole users.
 * Reads from guacamole_entity + guacamole_user.
 * last_active is derived from guacamole_user_history (MAX start_date).
 */
public class UserDao {

    // ── Authentication ────────────────────────────────────────────────────────

    public String findPasswordHash(String username) throws SQLException {
        String sql = "SELECT gu.password_hash " +
                     "FROM guacamole_entity ge " +
                     "LEFT JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "WHERE ge.name = ? AND ge.type = 'USER'";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password_hash") : null;
            }
        }
    }

    // ── User list ─────────────────────────────────────────────────────────────

    public List<User> findAll() throws SQLException {
        // last_active comes from guacamole_user_history, not guacamole_user
        String sql = "SELECT ge.entity_id, ge.name AS username, " +
                     "       gu.disabled, gu.expired, " +
                     "       gu.full_name, gu.email_address, " +
                     "       MAX(uh.start_date) AS last_active " +
                     "FROM guacamole_entity ge " +
                     "JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "LEFT JOIN guacamole_user_history uh ON uh.user_id = gu.user_id " +
                     "WHERE ge.type = 'USER' " +
                     "GROUP BY ge.entity_id, ge.name, gu.disabled, gu.expired, " +
                     "         gu.full_name, gu.email_address " +
                     "ORDER BY ge.name";

        List<User> users = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT ge.entity_id, ge.name AS username, " +
                     "       gu.disabled, gu.expired, " +
                     "       gu.full_name, gu.email_address, " +
                     "       MAX(uh.start_date) AS last_active " +
                     "FROM guacamole_entity ge " +
                     "LEFT JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "LEFT JOIN guacamole_user_history uh ON uh.user_id = gu.user_id " +
                     "WHERE ge.name = ? AND ge.type = 'USER' " +
                     "GROUP BY ge.entity_id, ge.name, gu.disabled, gu.expired, " +
                     "         gu.full_name, gu.email_address";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setEntityId(rs.getInt("entity_id"));
        u.setUsername(rs.getString("username"));
        u.setDisabled(rs.getBoolean("disabled"));
        u.setExpired(rs.getBoolean("expired"));
        String fullName = rs.getString("full_name");
        String email = rs.getString("email_address");

        u.setFullName(
                fullName != null && !fullName.isBlank()
                ? fullName
                : "Not Provided"
        );

        u.setEmail(
                email != null && !email.isBlank()
                ? email
                : "Not Provided"
        );

        Timestamp ts = rs.getTimestamp("last_active");
        if (ts != null) u.setLastActive(ts.toLocalDateTime());
        return u;
    }
}
