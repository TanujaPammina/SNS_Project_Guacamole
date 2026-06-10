package com.guacamole.model;

import java.time.LocalDateTime;

/**
 * Model — represents an administrator account in the admin_users table.
 * This is completely separate from Guacamole's own user table.
 * Admins log into THIS application; they do not need a Guacamole account.
 */
public class AdminUser {

    private int           id;
    private String        username;
    private String        passwordHash;   // BCrypt hash — never expose in views
    private String        fullName;
    private String        email;
    private Role          role;
    private boolean       active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private String        createdBy;
    private String        resetToken;
    private LocalDateTime resetTokenExpiry;

    public AdminUser() {}

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getUsername()                     { return username; }
    public void setUsername(String username)        { this.username = username; }

    public String getPasswordHash()                 { return passwordHash; }
    public void setPasswordHash(String h)           { this.passwordHash = h; }

    public String getFullName()                     { return fullName; }
    public void setFullName(String fullName)        { this.fullName = fullName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public Role getRole()                           { return role; }
    public void setRole(Role role)                  { this.role = role; }

    public boolean isActive()                       { return active; }
    public void setActive(boolean active)           { this.active = active; }

    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }

    public LocalDateTime getLastLoginAt()                   { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt)   { this.lastLoginAt = lastLoginAt; }

    public String getCreatedBy()                    { return createdBy; }
    public void setCreatedBy(String createdBy)      { this.createdBy = createdBy; }

    public String getResetToken()                           { return resetToken; }
    public void setResetToken(String resetToken)            { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry()                      { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    // ── Convenience helpers ──────────────────────────────────────────────────

    public boolean isSuperAdmin() { return role == Role.SUPER_ADMIN; }
    public boolean isAdmin()      { return role == Role.ADMIN || role == Role.SUPER_ADMIN; }
    public boolean isAuditor()    { return role == Role.AUDITOR; }

    /** Returns the role display name safely. */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Unknown";
    }
}
