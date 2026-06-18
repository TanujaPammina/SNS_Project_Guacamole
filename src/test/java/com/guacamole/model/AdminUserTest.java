package com.guacamole.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AdminUser model — getters, setters, and helpers.
 */
@DisplayName("AdminUser Model Tests")
class AdminUserTest {

    private AdminUser user;

    @BeforeEach
    void setUp() {
        user = new AdminUser();
        user.setId(1);
        user.setUsername("testuser");
        user.setActive(true);
    }

    @Test
    @DisplayName("isSuperAdmin returns true only for SUPER_ADMIN role")
    void isSuperAdmin_onlyForSuperAdmin() {
        user.setRole(Role.SUPER_ADMIN);
        assertTrue(user.isSuperAdmin());

        user.setRole(Role.ADMIN);
        assertFalse(user.isSuperAdmin());

        user.setRole(Role.AUDITOR);
        assertFalse(user.isSuperAdmin());
    }

    @Test
    @DisplayName("isAdmin returns true for ADMIN and SUPER_ADMIN")
    void isAdmin_trueForAdminAndSuperAdmin() {
        user.setRole(Role.SUPER_ADMIN);
        assertTrue(user.isAdmin());

        user.setRole(Role.ADMIN);
        assertTrue(user.isAdmin());

        user.setRole(Role.AUDITOR);
        assertFalse(user.isAdmin());
    }

    @Test
    @DisplayName("isAuditor returns true only for AUDITOR role")
    void isAuditor_onlyForAuditor() {
        user.setRole(Role.AUDITOR);
        assertTrue(user.isAuditor());

        user.setRole(Role.ADMIN);
        assertFalse(user.isAuditor());
    }

    @Test
    @DisplayName("getRoleDisplayName returns correct display name")
    void getRoleDisplayName_returnsCorrectName() {
        user.setRole(Role.SUPER_ADMIN);
        assertEquals("Super Admin", user.getRoleDisplayName());

        user.setRole(Role.ADMIN);
        assertEquals("Admin", user.getRoleDisplayName());

        user.setRole(null);
        assertEquals("Unknown", user.getRoleDisplayName());
    }

    @Test
    @DisplayName("getters and setters work correctly")
    void gettersSetters_workCorrectly() {
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertTrue(user.isActive());
    }
}
