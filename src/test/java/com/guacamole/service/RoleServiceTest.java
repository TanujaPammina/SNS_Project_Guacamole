package com.guacamole.service;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoleService.
 *
 * Note: canViewReports and canViewAuditLog were removed when report visibility
 * became fully configurable via the role_report_permissions table.
 * Report-level access is now enforced in RoleFilter using the DB.
 */
@DisplayName("RoleService Permission Tests")
class RoleServiceTest {

    // ── canManageAdminUsers ───────────────────────────────────────────────────

    @Test
    @DisplayName("canManageAdminUsers returns true only for SUPER_ADMIN")
    void canManageAdminUsers_superAdmin_returnsTrue() {
        assertTrue(RoleService.canManageAdminUsers(activeUser(Role.SUPER_ADMIN)));
    }

    @Test
    @DisplayName("canManageAdminUsers returns false for IT Admin and Auditor")
    void canManageAdminUsers_lowerRoles_returnsFalse() {
        assertFalse(RoleService.canManageAdminUsers(activeUser(Role.ADMIN)));
        assertFalse(RoleService.canManageAdminUsers(activeUser(Role.AUDITOR)));
    }

    @Test
    @DisplayName("canManageAdminUsers returns false for null")
    void canManageAdminUsers_null_returnsFalse() {
        assertFalse(RoleService.canManageAdminUsers(null));
    }

    // ── hasRole ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("hasRole: SUPER_ADMIN satisfies all role requirements")
    void hasRole_superAdmin_satisfiesAll() {
        AdminUser u = activeUser(Role.SUPER_ADMIN);
        assertTrue(RoleService.hasRole(u, Role.SUPER_ADMIN));
        assertTrue(RoleService.hasRole(u, Role.ADMIN));
        assertTrue(RoleService.hasRole(u, Role.AUDITOR));
    }

    @Test
    @DisplayName("hasRole: ADMIN (IT Admin) satisfies ADMIN and AUDITOR but not SUPER_ADMIN")
    void hasRole_admin_satisfiesAdminAndAuditor() {
        AdminUser u = activeUser(Role.ADMIN);
        assertFalse(RoleService.hasRole(u, Role.SUPER_ADMIN));
        assertTrue(RoleService.hasRole(u, Role.ADMIN));
        assertTrue(RoleService.hasRole(u, Role.AUDITOR));
    }

    @Test
    @DisplayName("hasRole: AUDITOR satisfies only AUDITOR")
    void hasRole_auditor_satisfiesOnlyAuditor() {
        AdminUser u = activeUser(Role.AUDITOR);
        assertFalse(RoleService.hasRole(u, Role.SUPER_ADMIN));
        assertFalse(RoleService.hasRole(u, Role.ADMIN));
        assertTrue(RoleService.hasRole(u, Role.AUDITOR));
    }

    @Test
    @DisplayName("hasRole returns false for null user")
    void hasRole_nullUser_returnsFalse() {
        assertFalse(RoleService.hasRole(null, Role.AUDITOR));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private AdminUser activeUser(Role role) {
        AdminUser u = new AdminUser();
        u.setUsername("testuser");
        u.setRole(role);
        u.setActive(true);
        return u;
    }
}
