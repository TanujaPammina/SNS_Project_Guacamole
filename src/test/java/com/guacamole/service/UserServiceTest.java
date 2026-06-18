package com.guacamole.service;

import com.guacamole.dao.AdminUserDao;
import com.guacamole.dao.UserDao;
import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.model.dto.AdminUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock private AdminUserDao adminUserDao;
    @Mock private UserDao      userDao;

    private UserService userService;
    private AdminUser   mockAdmin;

    @BeforeEach
    void setUp() throws Exception {
        // Inject mocks via reflection
        userService = new UserService();
        inject(userService, "adminUserDao", adminUserDao);
        inject(userService, "userDao",      userDao);

        mockAdmin = new AdminUser();
        mockAdmin.setId(1);
        mockAdmin.setUsername("superadmin");
        mockAdmin.setPasswordHash(BCrypt.hashpw("Admin@1234", BCrypt.gensalt(10)));
        mockAdmin.setRole(Role.SUPER_ADMIN);
        mockAdmin.setActive(true);
        mockAdmin.setEmail("admin@test.com");
    }

    // ── authenticate ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("authenticate succeeds with correct credentials")
    void authenticate_correctCredentials_returnsAdminUser() throws SQLException {
        when(adminUserDao.findByUsername("superadmin")).thenReturn(mockAdmin);
        doNothing().when(adminUserDao).updateLastLogin(anyString());

        AdminUser result = userService.authenticate("superadmin", "Admin@1234", "127.0.0.1");

        assertNotNull(result);
        assertEquals("superadmin", result.getUsername());
        verify(adminUserDao).updateLastLogin("superadmin");
    }

    @Test
    @DisplayName("authenticate fails with wrong password")
    void authenticate_wrongPassword_returnsNull() throws SQLException {
        when(adminUserDao.findByUsername("superadmin")).thenReturn(mockAdmin);

        AdminUser result = userService.authenticate("superadmin", "WrongPass", "127.0.0.1");

        assertNull(result);
    }

    @Test
    @DisplayName("authenticate fails when user does not exist")
    void authenticate_userNotFound_returnsNull() throws SQLException {
        when(adminUserDao.findByUsername(anyString())).thenReturn(null);

        AdminUser result = userService.authenticate("ghost", "Admin@1234", "127.0.0.1");

        assertNull(result);
    }

    @Test
    @DisplayName("authenticate returns null for blank username without DB call")
    void authenticate_blankUsername_returnsNull() throws SQLException {
        AdminUser result = userService.authenticate("", "Admin@1234", "127.0.0.1");
        assertNull(result);
        verify(adminUserDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("authenticate returns null for blank password without DB call")
    void authenticate_blankPassword_returnsNull() throws SQLException {
        AdminUser result = userService.authenticate("superadmin", "", "127.0.0.1");
        assertNull(result);
        verify(adminUserDao, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("authenticate fails when account is inactive")
    void authenticate_inactiveAccount_returnsNull() throws SQLException {
        mockAdmin.setActive(false);
        when(adminUserDao.findByUsername("superadmin")).thenReturn(mockAdmin);

        AdminUser result = userService.authenticate("superadmin", "Admin@1234", "127.0.0.1");
        assertNull(result);
    }

    // ── createAdminUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("createAdminUser succeeds with valid data")
    void createAdminUser_validData_returnsNull() throws SQLException {
        when(adminUserDao.existsByUsername("newuser")).thenReturn(false);
        doNothing().when(adminUserDao).insert(any(AdminUser.class));

        AdminUserDto dto = buildCreateDto("newuser", "Password@123", "ADMIN");
        String error = userService.createAdminUser(dto, "superadmin");

        assertNull(error);
        verify(adminUserDao).insert(any(AdminUser.class));
    }

    @Test
    @DisplayName("createAdminUser fails when username already taken")
    void createAdminUser_duplicateUsername_returnsError() throws SQLException {
        when(adminUserDao.existsByUsername("superadmin")).thenReturn(true);

        AdminUserDto dto = buildCreateDto("superadmin", "Password@123", "ADMIN");
        String error = userService.createAdminUser(dto, "superadmin");

        assertNotNull(error);
        assertTrue(error.contains("already taken"));
        verify(adminUserDao, never()).insert(any());
    }

    @Test
    @DisplayName("createAdminUser fails with invalid DTO (short username)")
    void createAdminUser_invalidDto_returnsValidationError() throws SQLException {
        AdminUserDto dto = new AdminUserDto();
        dto.setUsername("ab"); // too short

        String error = userService.createAdminUser(dto, "superadmin");

        assertNotNull(error);
        verify(adminUserDao, never()).existsByUsername(any());
    }

    @Test
    @DisplayName("createAdminUser fails when passwords do not match")
    void createAdminUser_passwordMismatch_returnsError() throws SQLException {
        AdminUserDto dto = new AdminUserDto();
        dto.setUsername("validuser");
        dto.setPassword("Pass@1234");
        dto.setConfirmPassword("Different@1234");
        dto.setRole("ADMIN");

        String error = userService.createAdminUser(dto, "superadmin");

        assertNotNull(error);
        assertTrue(error.contains("do not match"));
    }

    // ── updateAdminUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("updateAdminUser succeeds with valid data")
    void updateAdminUser_validData_returnsNull() throws SQLException {
        when(adminUserDao.findById(1)).thenReturn(mockAdmin);
        doNothing().when(adminUserDao).update(any(AdminUser.class));

        AdminUserDto dto = buildEditDto("Admin", "admin@test.com", "ADMIN");
        String error = userService.updateAdminUser(1, dto, "superadmin");

        assertNull(error);
        verify(adminUserDao).update(any(AdminUser.class));
    }

    @Test
    @DisplayName("updateAdminUser fails when user not found")
    void updateAdminUser_notFound_returnsError() throws SQLException {
        when(adminUserDao.findById(999)).thenReturn(null);

        AdminUserDto dto = buildEditDto("Name", "e@e.com", "ADMIN");
        String error = userService.updateAdminUser(999, dto, "superadmin");

        assertNotNull(error);
        assertTrue(error.contains("not found"));
    }

    // ── deleteAdminUser ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteAdminUser prevents self-deletion")
    void deleteAdminUser_selfDelete_returnsError() throws SQLException {
        when(adminUserDao.findById(1)).thenReturn(mockAdmin);

        String error = userService.deleteAdminUser(1, "superadmin");

        assertNotNull(error);
        assertTrue(error.contains("cannot delete your own account"));
        verify(adminUserDao, never()).delete(anyInt());
    }

    @Test
    @DisplayName("deleteAdminUser succeeds for another user")
    void deleteAdminUser_otherUser_succeeds() throws SQLException {
        AdminUser other = new AdminUser();
        other.setId(2);
        other.setUsername("admin1");
        when(adminUserDao.findById(2)).thenReturn(other);
        doNothing().when(adminUserDao).delete(2);

        String error = userService.deleteAdminUser(2, "superadmin");

        assertNull(error);
        verify(adminUserDao).delete(2);
    }

    @Test
    @DisplayName("deleteAdminUser fails when user not found")
    void deleteAdminUser_notFound_returnsError() throws SQLException {
        when(adminUserDao.findById(999)).thenReturn(null);

        String error = userService.deleteAdminUser(999, "superadmin");

        assertNotNull(error);
        assertTrue(error.contains("not found"));
    }

    // ── getAllAdminUsers ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllAdminUsers returns list from DAO")
    void getAllAdminUsers_returnsList() throws SQLException {
        when(adminUserDao.findAll()).thenReturn(Arrays.asList(mockAdmin));

        List<AdminUser> result = userService.getAllAdminUsers();

        assertEquals(1, result.size());
        assertEquals("superadmin", result.get(0).getUsername());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AdminUserDto buildCreateDto(String username, String password, String role) {
        AdminUserDto dto = new AdminUserDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setConfirmPassword(password);
        dto.setRole(role);
        dto.setActive(true);
        return dto;
    }

    private AdminUserDto buildEditDto(String fullName, String email, String role) {
        AdminUserDto dto = new AdminUserDto();
        dto.setFullName(fullName);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setActive(true);
        return dto;
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
