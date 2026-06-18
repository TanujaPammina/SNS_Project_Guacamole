package com.guacamole.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminUserDto Validation Tests")
class AdminUserDtoTest {

    // ── validateForCreate ─────────────────────────────────────────────────────

    @Test
    @DisplayName("validateForCreate passes with valid data")
    void validateForCreate_validData_returnsNull() {
        AdminUserDto dto = validCreateDto();
        assertNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when username is blank")
    void validateForCreate_blankUsername_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setUsername("");
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when username is too short")
    void validateForCreate_shortUsername_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setUsername("ab"); // less than 3 chars
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when username has invalid characters")
    void validateForCreate_invalidCharsInUsername_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setUsername("user name!"); // space and !
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when password is too short")
    void validateForCreate_shortPassword_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setPassword("short");
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when passwords do not match")
    void validateForCreate_passwordMismatch_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setConfirmPassword("DifferentPass@1");
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when role is blank")
    void validateForCreate_blankRole_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setRole("");
        assertNotNull(dto.validateForCreate());
    }

    @Test
    @DisplayName("validateForCreate fails when role is invalid")
    void validateForCreate_invalidRole_returnsError() {
        AdminUserDto dto = validCreateDto();
        dto.setRole("MANAGER");
        assertNotNull(dto.validateForCreate());
    }

    // ── validateForEdit ───────────────────────────────────────────────────────

    @Test
    @DisplayName("validateForEdit passes with valid data")
    void validateForEdit_validData_returnsNull() {
        AdminUserDto dto = validEditDto();
        assertNull(dto.validateForEdit());
    }

    @Test
    @DisplayName("validateForEdit fails with invalid email")
    void validateForEdit_invalidEmail_returnsError() {
        AdminUserDto dto = validEditDto();
        dto.setEmail("not-an-email");
        assertNotNull(dto.validateForEdit());
    }

    @Test
    @DisplayName("validateForEdit fails when new password is too short")
    void validateForEdit_shortPassword_returnsError() {
        AdminUserDto dto = validEditDto();
        dto.setPassword("short");
        dto.setConfirmPassword("short");
        assertNotNull(dto.validateForEdit());
    }

    @Test
    @DisplayName("validateForEdit allows blank password (no change)")
    void validateForEdit_blankPassword_isAllowed() {
        AdminUserDto dto = validEditDto();
        dto.setPassword("");
        dto.setConfirmPassword("");
        assertNull(dto.validateForEdit());
    }

    @Test
    @DisplayName("validateForEdit fails when passwords do not match")
    void validateForEdit_passwordMismatch_returnsError() {
        AdminUserDto dto = validEditDto();
        dto.setPassword("NewPass@123");
        dto.setConfirmPassword("Different@123");
        assertNotNull(dto.validateForEdit());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AdminUserDto validCreateDto() {
        AdminUserDto dto = new AdminUserDto();
        dto.setUsername("newuser");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@123");
        dto.setEmail("user@example.com");
        dto.setRole("ADMIN");
        dto.setActive(true);
        return dto;
    }

    private AdminUserDto validEditDto() {
        AdminUserDto dto = new AdminUserDto();
        dto.setFullName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setRole("AUDITOR");
        dto.setActive(true);
        return dto;
    }
}
