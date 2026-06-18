package com.guacamole.service;

import com.guacamole.dao.AdminUserDao;
import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PasswordResetService Tests")
class PasswordResetServiceTest {

    @Mock
    private AdminUserDao adminUserDao;

    private PasswordResetService resetService;
    private AdminUser            mockUser;

    @BeforeEach
    void setUp() throws Exception {
        resetService = new PasswordResetService();
        java.lang.reflect.Field f = PasswordResetService.class.getDeclaredField("adminUserDao");
        f.setAccessible(true);
        f.set(resetService, adminUserDao);

        mockUser = new AdminUser();
        mockUser.setId(1);
        mockUser.setUsername("superadmin");
        mockUser.setEmail("admin@test.com");
        mockUser.setRole(Role.SUPER_ADMIN);
        mockUser.setActive(true);
        mockUser.setFullName("Super Admin");
    }

    // ── requestReset ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("requestReset returns null (success) for valid email")
    void requestReset_validEmail_returnsNull() throws SQLException {
        when(adminUserDao.findByEmail("admin@test.com")).thenReturn(mockUser);
        doNothing().when(adminUserDao).saveResetToken(anyInt(), anyString(), any());

        String result = resetService.requestReset("admin@test.com",
                "http://localhost:8080/guacamole-admin-1.0");

        assertNull(result);
        verify(adminUserDao).saveResetToken(eq(1), anyString(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("requestReset returns null even when email not found (security — no leak)")
    void requestReset_emailNotFound_stillReturnsNull() throws SQLException {
        when(adminUserDao.findByEmail("unknown@test.com")).thenReturn(null);

        String result = resetService.requestReset("unknown@test.com", "http://localhost:8080");

        assertNull(result);
        verify(adminUserDao, never()).saveResetToken(anyInt(), anyString(), any());
    }

    @Test
    @DisplayName("requestReset returns error for blank email")
    void requestReset_blankEmail_returnsError() throws SQLException {
        String result = resetService.requestReset("", "http://localhost:8080");

        assertNotNull(result);
        verify(adminUserDao, never()).findByEmail(any());
    }

    @Test
    @DisplayName("requestReset does not save token for inactive user")
    void requestReset_inactiveUser_doesNotSaveToken() throws SQLException {
        mockUser.setActive(false);
        when(adminUserDao.findByEmail("admin@test.com")).thenReturn(mockUser);

        String result = resetService.requestReset("admin@test.com", "http://localhost:8080");

        assertNull(result);
        verify(adminUserDao, never()).saveResetToken(anyInt(), anyString(), any());
    }

    // ── validateToken ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("validateToken returns user for valid non-expired token")
    void validateToken_validToken_returnsUser() throws SQLException {
        mockUser.setResetToken("valid-token-123");
        mockUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(20));
        when(adminUserDao.findByResetToken("valid-token-123")).thenReturn(mockUser);

        AdminUser result = resetService.validateToken("valid-token-123");

        assertNotNull(result);
        assertEquals("superadmin", result.getUsername());
    }

    @Test
    @DisplayName("validateToken returns null for expired token and clears it")
    void validateToken_expiredToken_returnsNull() throws SQLException {
        mockUser.setResetToken("expired-token");
        mockUser.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5));
        when(adminUserDao.findByResetToken("expired-token")).thenReturn(mockUser);
        doNothing().when(adminUserDao).clearResetToken(1);

        AdminUser result = resetService.validateToken("expired-token");

        assertNull(result);
        verify(adminUserDao).clearResetToken(1);
    }

    @Test
    @DisplayName("validateToken returns null for unknown token")
    void validateToken_unknownToken_returnsNull() throws SQLException {
        when(adminUserDao.findByResetToken("bad-token")).thenReturn(null);

        assertNull(resetService.validateToken("bad-token"));
    }

    @Test
    @DisplayName("validateToken returns null for blank token")
    void validateToken_blankToken_returnsNull() throws SQLException {
        assertNull(resetService.validateToken(""));
        verify(adminUserDao, never()).findByResetToken(any());
    }

    // ── resetPassword ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("resetPassword succeeds with valid token and matching passwords")
    void resetPassword_validRequest_succeeds() throws SQLException {
        mockUser.setResetToken("good-token");
        mockUser.setResetTokenExpiry(LocalDateTime.now().plusMinutes(25));
        when(adminUserDao.findByResetToken("good-token")).thenReturn(mockUser);
        doNothing().when(adminUserDao).updatePassword(anyInt(), anyString());
        doNothing().when(adminUserDao).clearResetToken(anyInt());

        String error = resetService.resetPassword("good-token", "NewPassword@1", "NewPassword@1");

        assertNull(error);
        verify(adminUserDao).updatePassword(eq(1), anyString());
        verify(adminUserDao).clearResetToken(1);
    }

    @Test
    @DisplayName("resetPassword fails when passwords do not match")
    void resetPassword_passwordMismatch_returnsError() throws SQLException {
        String error = resetService.resetPassword("token", "Password@1", "Password@2");

        assertNotNull(error);
        assertTrue(error.contains("do not match"));
        verify(adminUserDao, never()).updatePassword(anyInt(), anyString());
    }

    @Test
    @DisplayName("resetPassword fails when password is too short")
    void resetPassword_tooShort_returnsError() throws SQLException {
        String error = resetService.resetPassword("token", "short", "short");

        assertNotNull(error);
        assertTrue(error.contains("8 characters"));
    }

    @Test
    @DisplayName("resetPassword fails with expired token")
    void resetPassword_expiredToken_returnsError() throws SQLException {
        mockUser.setResetToken("exp-token");
        mockUser.setResetTokenExpiry(LocalDateTime.now().minusMinutes(1));
        when(adminUserDao.findByResetToken("exp-token")).thenReturn(mockUser);
        doNothing().when(adminUserDao).clearResetToken(anyInt());

        String error = resetService.resetPassword("exp-token", "NewPassword@1", "NewPassword@1");

        assertNotNull(error);
        assertTrue(error.contains("invalid or has expired"));
        verify(adminUserDao, never()).updatePassword(anyInt(), anyString());
    }
}
