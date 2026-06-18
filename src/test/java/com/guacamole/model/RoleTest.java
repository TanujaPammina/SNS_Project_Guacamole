package com.guacamole.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Role enum.
 */
@DisplayName("Role Enum Tests")
class RoleTest {

    @Test
    @DisplayName("fromString returns correct role for valid value")
    void fromString_validValue_returnsRole() {
        assertEquals(Role.SUPER_ADMIN, Role.fromString("SUPER_ADMIN"));
        assertEquals(Role.ADMIN,       Role.fromString("ADMIN"));
        assertEquals(Role.AUDITOR,     Role.fromString("AUDITOR"));
    }

    @Test
    @DisplayName("fromString is case-insensitive")
    void fromString_caseInsensitive() {
        assertEquals(Role.ADMIN,   Role.fromString("admin"));
        assertEquals(Role.AUDITOR, Role.fromString("auditor"));
    }

    @Test
    @DisplayName("fromString returns null for unknown value")
    void fromString_unknownValue_returnsNull() {
        assertNull(Role.fromString("UNKNOWN"));
        assertNull(Role.fromString(""));
        assertNull(Role.fromString(null));
    }

    @Test
    @DisplayName("Role display names are correct")
    void displayNames_areCorrect() {
        assertEquals("Super Admin", Role.SUPER_ADMIN.getDisplayName());
        assertEquals("Admin",       Role.ADMIN.getDisplayName());
        assertEquals("Auditor",     Role.AUDITOR.getDisplayName());
    }

    @Test
    @DisplayName("All roles have non-null descriptions")
    void descriptions_areNotNull() {
        for (Role r : Role.values()) {
            assertNotNull(r.getDescription());
            assertFalse(r.getDescription().isBlank());
        }
    }
}
