package com.guacamole.model;

/**
 * Application roles for the Guacamole Admin portal.
 *
 * SUPER_ADMIN — Full access: reports, audit log, manage admin users & configure permissions.
 * ADMIN       — "IT Admin" — report access configured by Super Admin via the DB.
 * AUDITOR     — "Auditor"  — report access configured by Super Admin via the DB.
 */
public enum Role {

    SUPER_ADMIN("Super Admin",  "Full system access including user management"),
    ADMIN      ("IT Admin",     "Access to reports as configured by Super Admin"),
    AUDITOR    ("Auditor",      "Access to reports as configured by Super Admin");

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
