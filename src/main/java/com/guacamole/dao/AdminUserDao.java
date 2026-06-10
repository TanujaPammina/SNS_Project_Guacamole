package com.guacamole.dao;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — all database operations for the admin_users table.
 * Strictly uses PreparedStatement; no string concatenation of user input.
 */
public class AdminUserDao {

    // ── Find ─────────────────────────────────────────────────────────────────

    /** Returns the AdminUser matching the given username, or null. */
    public AdminUser findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, email, " +
                     "       role, active, created_at, last_login_at, created_by " +
                     "FROM admin_users WHERE username = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Returns the AdminUser by primary key, or null. */
    public AdminUser findById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, email, " +
                     "       role, active, created_at, last_login_at, created_by " +
                     "FROM admin_users WHERE id = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Returns all admin users ordered by username. */
    public List<AdminUser> findAll() throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, email, " +
                     "       role, active, created_at, last_login_at, created_by " +
                     "FROM admin_users ORDER BY username";

        List<AdminUser> list = new ArrayList<>();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Returns true if a user with the given username already exists. */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM admin_users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new admin user. The passwordHash must already be BCrypt-hashed
     * by the service layer before calling this method.
     */
    public void insert(AdminUser user) throws SQLException {
        String sql = "INSERT INTO admin_users " +
                     "(username, password_hash, full_name, email, role, active, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            ps.setBoolean(6, user.isActive());
            ps.setString(7, user.getCreatedBy());
            ps.executeUpdate();
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /** Updates profile fields and role. Does NOT update the password. */
    public void update(AdminUser user) throws SQLException {
        String sql = "UPDATE admin_users " +
                     "SET full_name = ?, email = ?, role = ?, active = ? " +
                     "WHERE id = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
        }
    }

    /** Updates only the password hash for the given user id. */
    public void updatePassword(int id, String newPasswordHash) throws SQLException {
        String sql = "UPDATE admin_users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /** Records the last login timestamp for the given username. */
    public void updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE admin_users SET last_login_at = NOW() WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /** Deletes an admin user by id. */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM admin_users WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Password Reset ────────────────────────────────────────────────────────

    /** Finds a user by their email address. */
    public AdminUser findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, email, " +
                     "       role, active, created_at, last_login_at, created_by, " +
                     "       reset_token, reset_token_expiry " +
                     "FROM admin_users WHERE email = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Finds a user by their reset token. */
    public AdminUser findByResetToken(String token) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, email, " +
                     "       role, active, created_at, last_login_at, created_by, " +
                     "       reset_token, reset_token_expiry " +
                     "FROM admin_users WHERE reset_token = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** Saves a reset token and expiry for the given user id. */
    public void saveResetToken(int id, String token, java.time.LocalDateTime expiry)
            throws SQLException {
        String sql = "UPDATE admin_users SET reset_token = ?, reset_token_expiry = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setObject(2, expiry);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /** Clears the reset token after successful password reset. */
    public void clearResetToken(int id) throws SQLException {
        String sql = "UPDATE admin_users SET reset_token = NULL, reset_token_expiry = NULL WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private AdminUser map(ResultSet rs) throws SQLException {
        AdminUser u = new AdminUser();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setRole(Role.fromString(rs.getString("role")));
        u.setActive(rs.getBoolean("active"));
        u.setCreatedBy(rs.getString("created_by"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) u.setCreatedAt(created.toLocalDateTime());

        Timestamp lastLogin = rs.getTimestamp("last_login_at");
        if (lastLogin != null) u.setLastLoginAt(lastLogin.toLocalDateTime());

        // Reset token fields (may not exist in older schema — handle gracefully)
        try {
            u.setResetToken(rs.getString("reset_token"));
            Timestamp exp = rs.getTimestamp("reset_token_expiry");
            if (exp != null) u.setResetTokenExpiry(exp.toLocalDateTime());
        } catch (SQLException ignored) {}

        return u;
    }
}
