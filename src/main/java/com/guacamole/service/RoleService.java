package com.guacamole.service;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;

/**
 * Service — centralised permission checks.
 *
 * Report visibility is now fully configurable via the role_report_permissions
 * table (managed through the Super Admin → Report Permissions screen).
 *
 * The checks below cover structural permissions (manage admin users, etc.)
 * which are NOT configurable and remain role-based.
 *
 * Permission matrix (structural — non-configurable):
 * ┌──────────────────────────────┬─────────────┬──────────┬─────────┐
 * │ Permission                   │ SUPER_ADMIN │ IT ADMIN │ AUDITOR │
 * ├──────────────────────────────┼─────────────┼──────────┼─────────┤
 * │ View reports (configurable)  │    always   │  per DB  │  per DB │
 * │ Configure report permissions │      ✓      │    ✗     │    ✗    │
 * │ Manage admin users           │      ✓      │    ✗     │    ✗    │
 * └──────────────────────────────┴─────────────┴──────────┴─────────┘
 */
public class RoleService {

    // ── Structural permission checks ──────────────────────────────────────────

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
            case AUDITOR     -> true;   // every authenticated role qualifies
            case ADMIN       -> user.getRole() == Role.ADMIN ||
                                user.getRole() == Role.SUPER_ADMIN;
            case SUPER_ADMIN -> user.getRole() == Role.SUPER_ADMIN;
        };
    }
}
