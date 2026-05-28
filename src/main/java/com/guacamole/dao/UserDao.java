package com.guacamole.dao;

import com.guacamole.model.User;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for Guacamole users.
 * Reads from guacamole_entity (name) joined with guacamole_user (password hash, flags).
 */
public class UserDao {

    // -----------------------------------------------------------------------
    // Authentication
    // -----------------------------------------------------------------------

    /**
     * Loads the stored BCrypt password hash for the given username.
     * Returns null if the user does not exist.
     */
    public String findPasswordHash(String username) throws SQLException {
        String sql = "SELECT gu.password_hash " +
                     "FROM guacamole_entity ge " +
                     "JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "WHERE ge.name = ? AND ge.type = 'USER'";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password_hash") : null;
            }
        }
    }

    // -----------------------------------------------------------------------
    // User list
    // -----------------------------------------------------------------------

    /** Returns all users with basic profile information. */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT ge.entity_id, ge.name AS username, " +
                     "       gu.disabled, gu.expired, " +
                     "       gu.full_name, gu.email_address, " +
                     "       gu.last_active " +
                     "FROM guacamole_entity ge " +
                     "JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "WHERE ge.type = 'USER' " +
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

    /** Finds a single user by username. */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT ge.entity_id, ge.name AS username, " +
                     "       gu.disabled, gu.expired, " +
                     "       gu.full_name, gu.email_address, " +
                     "       gu.last_active " +
                     "FROM guacamole_entity ge " +
                     "JOIN guacamole_user gu ON gu.entity_id = ge.entity_id " +
                     "WHERE ge.name = ? AND ge.type = 'USER'";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Mapping helper
    // -----------------------------------------------------------------------

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setEntityId(rs.getInt("entity_id"));
        u.setUsername(rs.getString("username"));
        u.setDisabled(rs.getBoolean("disabled"));
        u.setExpired(rs.getBoolean("expired"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email_address"));

        Timestamp ts = rs.getTimestamp("last_active");
        if (ts != null) {
            u.setLastActive(ts.toLocalDateTime());
        }
        return u;
    }
}
