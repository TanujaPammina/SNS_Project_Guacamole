package com.guacamole.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginDto Tests")
class LoginDtoTest {

    @Test
    @DisplayName("isBlank returns false when both fields filled")
    void isBlank_bothFilled_returnsFalse() {
        LoginDto dto = new LoginDto("admin", "password");
        assertFalse(dto.isBlank());
    }

    @Test
    @DisplayName("isBlank returns true when username is null")
    void isBlank_nullUsername_returnsTrue() {
        LoginDto dto = new LoginDto(null, "password");
        assertTrue(dto.isBlank());
    }

    @Test
    @DisplayName("isBlank returns true when password is blank")
    void isBlank_blankPassword_returnsTrue() {
        LoginDto dto = new LoginDto("admin", "  ");
        assertTrue(dto.isBlank());
    }

    @Test
    @DisplayName("isBlank returns true when both fields are empty")
    void isBlank_bothEmpty_returnsTrue() {
        LoginDto dto = new LoginDto("", "");
        assertTrue(dto.isBlank());
    }

    @Test
    @DisplayName("Getters return correct values")
    void getters_returnCorrectValues() {
        LoginDto dto = new LoginDto("superadmin", "Admin@1234");
        assertEquals("superadmin", dto.getUsername());
        assertEquals("Admin@1234", dto.getPassword());
    }
}
