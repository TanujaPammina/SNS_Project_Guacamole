package com.guacamole.model.dto;

/**
 * DTO — carries form data for creating or editing an AdminUser.
 * Keeps raw form strings separate from the validated domain model.
 * Password is only present when creating or explicitly changing it.
 */
public class AdminUserDto {

    private String username;
    private String password;        // plain-text, only set on create/password-change
    private String confirmPassword;
    private String fullName;
    private String email;
    private String role;            // raw string from form, validated in service
    private boolean active = true;

    public AdminUserDto() {}

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getUsername()                         { return username; }
    public void setUsername(String username)            { this.username = username; }

    public String getPassword()                         { return password; }
    public void setPassword(String password)            { this.password = password; }

    public String getConfirmPassword()                  { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getFullName()                         { return fullName; }
    public void setFullName(String fullName)            { this.fullName = fullName; }

    public String getEmail()                            { return email; }
    public void setEmail(String email)                  { this.email = email; }

    public String getRole()                             { return role; }
    public void setRole(String role)                    { this.role = role; }

    public boolean isActive()                           { return active; }
    public void setActive(boolean active)               { this.active = active; }

    // ── Validation ───────────────────────────────────────────────────────────

    /**
     * Returns a validation error message, or null if the DTO is valid for creation.
     */
    public String validateForCreate() {
        if (username == null || username.isBlank())
            return "Username is required.";
        if (username.length() < 3 || username.length() > 64)
            return "Username must be 3–64 characters.";
        if (!username.matches("[a-zA-Z0-9._-]+"))
            return "Username may only contain letters, digits, dots, hyphens, underscores.";
        if (password == null || password.length() < 8)
            return "Password must be at least 8 characters.";
        if (!password.equals(confirmPassword))
            return "Passwords do not match.";
        if (role == null || role.isBlank())
            return "Role is required.";
        if (com.guacamole.model.Role.fromString(role) == null)
            return "Invalid role selected.";
        return null;
    }

    /**
     * Returns a validation error message, or null if the DTO is valid for editing.
     * Password is optional on edit — only validated if provided.
     */
    public String validateForEdit() {
        if (fullName != null && fullName.length() > 128)
            return "Full name must be 128 characters or fewer.";
        if (email != null && !email.isBlank() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            return "Invalid email address.";
        if (password != null && !password.isBlank()) {
            if (password.length() < 8)
                return "Password must be at least 8 characters.";
            if (!password.equals(confirmPassword))
                return "Passwords do not match.";
        }
        if (role == null || role.isBlank())
            return "Role is required.";
        if (com.guacamole.model.Role.fromString(role) == null)
            return "Invalid role selected.";
        return null;
    }
}
