package com.guacamole.model;

/**
 * Application roles for the Guacamole Admin portal.
 *
 * SUPER_ADMIN — Full access: reports, audit log, manage admin users & roles.
 * ADMIN       — Access to all reports and audit log. Cannot manage admin users.
 * AUDITOR     — Read-only access to reports only. No audit log, no user management.
 */
public enum Role {

    SUPER_ADMIN("Super Admin",  "Full system access including user management"),
    ADMIN      ("Admin",        "Access to all reports and audit log"),
    AUDITOR    ("Auditor",      "Read-only access to operational reports");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    /** Safe parse — returns null instead of throwing on unknown value. */
    public static Role fromString(String value) {
        if (value == null) return null;
        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
