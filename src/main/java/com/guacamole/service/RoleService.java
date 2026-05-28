package com.guacamole.service;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;

/**
 * Service — centralised permission checks.
 *
 * Permission matrix:
 * ┌──────────────────────────────┬─────────────┬───────┬─────────┐
 * │ Permission                   │ SUPER_ADMIN │ ADMIN │ AUDITOR │
 * ├──────────────────────────────┼─────────────┼───────┼─────────┤
 * │ View all reports             │      ✓      │   ✓   │    ✓    │
 * │ View audit log               │      ✓      │   ✓   │    ✗    │
 * │ Manage admin users           │      ✓      │   ✗   │    ✗    │
 * │ Create / edit / delete admin │      ✓      │   ✗   │    ✗    │
 * └──────────────────────────────┴─────────────┴───────┴─────────┘
 */
public class RoleService {

    // ── Permission checks ─────────────────────────────────────────────────────

    /** Can the user view any report page? */
    public static boolean canViewReports(AdminUser user) {
        return user != null && user.isActive();
    }

    /** Can the user view the admin audit log? */
    public static boolean canViewAuditLog(AdminUser user) {
        return user != null && user.isActive() &&
               (user.getRole() == Role.SUPER_ADMIN || user.getRole() == Role.ADMIN);
    }

    /** Can the user manage (create/edit/delete) admin user accounts? */
    public static boolean canManageAdminUsers(AdminUser user) {
        return user != null && user.isActive() && user.getRole() == Role.SUPER_ADMIN;
    }

    /**
     * Checks whether the given user has at least the required role.
     * Role hierarchy: SUPER_ADMIN > ADMIN > AUDITOR
     */
    public static boolean hasRole(AdminUser user, Role required) {
        if (user == null || user.getRole() == null) return false;
        return switch (required) {
            case AUDITOR     -> true;                                    // everyone qualifies
            case ADMIN       -> user.getRole() == Role.ADMIN ||
                                user.getRole() == Role.SUPER_ADMIN;
            case SUPER_ADMIN -> user.getRole() == Role.SUPER_ADMIN;
        };
    }

    /**
     * Returns a human-readable "Access Denied" reason for a given route.
     */
    public static String denyReason(String route) {
        return "Your role does not have permission to access: " + route;
    }

    private RoleService() {}
}
