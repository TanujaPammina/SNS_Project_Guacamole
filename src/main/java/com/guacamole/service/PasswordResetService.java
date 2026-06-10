package com.guacamole.service;

import com.guacamole.dao.AdminUserDao;
import com.guacamole.model.AdminUser;
import com.guacamole.util.AuditLogger;
import com.guacamole.util.EmailNotifier;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Handles the full forgot-password / reset-password flow:
 *
 * 1. requestReset(email)  — generates token, saves it, sends email
 * 2. validateToken(token) — checks token exists and is not expired
 * 3. resetPassword(token, newPassword) — updates password, clears token
 */
public class PasswordResetService {

    private static final int    TOKEN_EXPIRY_MINUTES = 30;
    private static final int    TOKEN_BYTES          = 32;

    private final AdminUserDao adminUserDao = new AdminUserDao();

    // ── Step 1: Request reset ─────────────────────────────────────────────────

    /**
     * Looks up the admin by email, generates a secure token,
     * saves it to the DB, and sends the reset email.
     *
     * Always returns a generic message (don't reveal if email exists).
     */
    public String requestReset(String email, String appBaseUrl) throws SQLException {

        if (email == null || email.isBlank()) {
            return "Please enter your email address.";
        }

        AdminUser user = adminUserDao.findByEmail(email.trim().toLowerCase());

        if (user != null && user.isActive()) {
            String token  = generateToken();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES);

            adminUserDao.saveResetToken(user.getId(), token, expiry);

            String resetLink = appBaseUrl + "/reset-password?token=" + token;

            String subject = "Reset Your Guacamole Admin Password";
            String body    = "Hello " + (user.getFullName() != null ? user.getFullName() : user.getUsername()) + ",\n\n" +
                             "You requested a password reset for your Guacamole Admin account.\n\n" +
                             "Click the link below to reset your password:\n\n" +
                             resetLink + "\n\n" +
                             "This link expires in " + TOKEN_EXPIRY_MINUTES + " minutes.\n\n" +
                             "If you did not request this, please ignore this email.\n\n" +
                             "— Guacamole Admin System";

            EmailNotifier.sendPasswordReset(email.trim().toLowerCase(), subject, body);

            AuditLogger.log(user.getUsername(), "PASSWORD_RESET_REQUESTED",
                    user.getUsername(), "Reset requested for email: " + email, null);
        }

        // Always return the same message — don't reveal if the email exists
        return null; // null = success, show "check your email" message
    }

    // ── Step 2: Validate token ────────────────────────────────────────────────

    /**
     * Returns the AdminUser if the token is valid and not expired.
     * Returns null if invalid or expired.
     */
    public AdminUser validateToken(String token) throws SQLException {
        if (token == null || token.isBlank()) return null;

        AdminUser user = adminUserDao.findByResetToken(token);
        if (user == null) return null;

        if (user.getResetTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            // Token expired — clear it
            adminUserDao.clearResetToken(user.getId());
            return null;
        }

        return user;
    }

    // ── Step 3: Reset password ────────────────────────────────────────────────

    /**
     * Validates the token, hashes the new password, updates the DB,
     * and clears the token.
     *
     * @return error message string, or null on success
     */
    public String resetPassword(String token, String newPassword, String confirmPassword)
            throws SQLException {

        if (newPassword == null || newPassword.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        AdminUser user = validateToken(token);
        if (user == null) {
            return "This reset link is invalid or has expired. Please request a new one.";
        }

        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        adminUserDao.updatePassword(user.getId(), newHash);
        adminUserDao.clearResetToken(user.getId());

        AuditLogger.log(user.getUsername(), "PASSWORD_RESET_COMPLETED",
                user.getUsername(), "Password reset via email link", null);

        return null; // success
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
