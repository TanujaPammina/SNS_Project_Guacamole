package com.guacamole.service;

import com.guacamole.dao.AdminUserDao;
import com.guacamole.dao.UserDao;
import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.model.User;
import com.guacamole.model.dto.AdminUserDto;
import com.guacamole.util.AuditLogger;
import com.guacamole.util.EmailNotifier;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;

/**
 * Service — authentication and admin-user management.
 *
 * Authentication is against the admin_users table (BCrypt).
 * Guacamole user listing is a separate read-only operation via UserDao.
 */
public class UserService {

    private final AdminUserDao adminUserDao = new AdminUserDao();
    private final UserDao      userDao      = new UserDao();

    // ── Authentication ────────────────────────────────────────────────────────

    /**
     * Authenticates an admin against the admin_users table.
     *
     * @return the authenticated AdminUser, or null on failure
     */
    public AdminUser authenticate(String username, String password, String remoteIp)
            throws SQLException {

        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            return null;
        }

        AdminUser admin = adminUserDao.findByUsername(username);

        if (admin == null || !BCrypt.checkpw(password, admin.getPasswordHash())) {
            AuditLogger.log(username, "LOGIN_FAILED", username,
                    "Invalid credentials from " + remoteIp, remoteIp);
            EmailNotifier.sendAlert(
                    "Failed Login Attempt",
                    "User '" + username + "' failed to log in from IP: " + remoteIp);
            return null;
        }

        if (!admin.isActive()) {
            AuditLogger.log(username, "LOGIN_BLOCKED", username,
                    "Account is inactive", remoteIp);
            return null;
        }

        adminUserDao.updateLastLogin(username);
        AuditLogger.log(username, "LOGIN", username,
                "Successful login [role=" + admin.getRole().name() + "]", remoteIp);
        return admin;
    }

    // ── Admin user CRUD (SUPER_ADMIN only) ────────────────────────────────────

    public List<AdminUser> getAllAdminUsers() throws SQLException {
        return adminUserDao.findAll();
    }

    public AdminUser getAdminUserById(int id) throws SQLException {
        return adminUserDao.findById(id);
    }

    /**
     * Creates a new admin user from a validated DTO.
     * Hashes the password before persisting.
     *
     * @param dto       validated form data
     * @param createdBy username of the SUPER_ADMIN performing the action
     * @return error message string, or null on success
     */
    public String createAdminUser(AdminUserDto dto, String createdBy) throws SQLException {
        String validationError = dto.validateForCreate();
        if (validationError != null) return validationError;

        if (adminUserDao.existsByUsername(dto.getUsername())) {
            return "Username '" + dto.getUsername() + "' is already taken.";
        }

        AdminUser user = new AdminUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(12)));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(Role.fromString(dto.getRole()));
        user.setActive(dto.isActive());
        user.setCreatedBy(createdBy);

        adminUserDao.insert(user);
        AuditLogger.log(createdBy, "CREATE_ADMIN_USER", dto.getUsername(),
                "Role: " + dto.getRole(), null);
        return null;
    }

    /**
     * Updates an existing admin user's profile and role.
     * If dto.getPassword() is non-blank, also updates the password.
     *
     * @return error message string, or null on success
     */
    public String updateAdminUser(int id, AdminUserDto dto, String updatedBy) throws SQLException {
        String validationError = dto.validateForEdit();
        if (validationError != null) return validationError;

        AdminUser existing = adminUserDao.findById(id);
        if (existing == null) return "Admin user not found.";

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setRole(Role.fromString(dto.getRole()));
        existing.setActive(dto.isActive());
        adminUserDao.update(existing);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            adminUserDao.updatePassword(id,
                    BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt(12)));
            AuditLogger.log(updatedBy, "CHANGE_PASSWORD", existing.getUsername(),
                    "Password changed by " + updatedBy, null);
        }

        AuditLogger.log(updatedBy, "EDIT_ADMIN_USER", existing.getUsername(),
                "Role=" + dto.getRole() + " Active=" + dto.isActive(), null);
        return null;
    }

    /**
     * Deletes an admin user. A SUPER_ADMIN cannot delete themselves.
     *
     * @return error message string, or null on success
     */
    public String deleteAdminUser(int id, String deletedBy) throws SQLException {
        AdminUser target = adminUserDao.findById(id);
        if (target == null) return "Admin user not found.";
        if (target.getUsername().equals(deletedBy)) return "You cannot delete your own account.";

        adminUserDao.delete(id);
        AuditLogger.log(deletedBy, "DELETE_ADMIN_USER", target.getUsername(),
                "Deleted by " + deletedBy, null);
        return null;
    }

    // ── Guacamole user list (read-only) ───────────────────────────────────────

    public List<User> getAllGuacamoleUsers() throws SQLException {
        return userDao.findAll();
    }

    public User getGuacamoleUserByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }
}
