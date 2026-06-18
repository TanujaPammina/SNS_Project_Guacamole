package com.guacamole.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for Role-Based Access Control.
 * Verifies that each role sees only what it should.
 */
@Tag("selenium")
@DisplayName("Role-Based Access Control Selenium Tests")
class RoleAccessSeleniumTest extends BaseSeleniumTest {

    // ── SUPER_ADMIN tests ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Super Admin sees all sidebar sections including Administration")
    void superAdmin_seesFullSidebar() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        assertTrue(pageContains("Session Reports"),  "Session Reports section missing");
        assertTrue(pageContains("User Reports"),      "User Reports section missing");
        assertTrue(pageContains("Security Reports"),  "Security Reports section missing");
        assertTrue(pageContains("Audit"),             "Audit section missing");
        assertTrue(pageContains("Administration"),    "Administration section missing");
        assertTrue(pageContains("Manage Admins"),     "Manage Admins link missing");
    }

    @Test
    @DisplayName("Super Admin can access Manage Admins page")
    void superAdmin_canAccessManageAdmins() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        driver.get(BASE_URL + "/admin/users");
        waitForElement(By.tagName("table"));
        assertTrue(pageContains("Admin Accounts"), "Manage Admins page should load");
        assertTrue(pageContains("superadmin"),      "Should see superadmin in list");
    }

    @Test
    @DisplayName("Super Admin can access Audit Log")
    void superAdmin_canAccessAuditLog() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        driver.get(BASE_URL + "/reports?type=audit-log");
        assertTrue(pageContains("Audit"),           "Audit log page should load");
        assertFalse(pageContains("Access Denied"),  "Should not get access denied");
    }

    // ── ADMIN tests ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Admin sees Audit section but NOT Administration section")
    void admin_seesSidebarWithoutAdministration() {
        logout();
        login(ADMIN_USER, ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(pageContains("audit"),              "Admin should see Audit section");
        assertFalse(pageContains("administration"),    "Admin should NOT see Administration");
        assertFalse(pageContains("manage admins"),     "Admin should NOT see Manage Admins");
    }

    @Test
    @DisplayName("Admin can access Audit Log")
    void admin_canAccessAuditLog() {
        logout();
        login(ADMIN_USER, ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/reports?type=audit-log");
        assertFalse(pageContains("access denied"), "Admin should access audit log");
    }

    @Test
    @DisplayName("Admin is denied access to Manage Admins")
    void admin_deniedManageAdmins() {
        logout();
        login(ADMIN_USER, ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".content")));
        assertTrue(pageContains("access denied") || pageContains("permission"),
                "Admin should be denied access to Manage Admins");
    }

    // ── AUDITOR tests ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Auditor does NOT see Audit or Administration sections")
    void auditor_limitedSidebar() {
        logout();
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertFalse(pageContains("audit log"),      "Auditor should NOT see Audit Log");
        assertFalse(pageContains("administration"), "Auditor should NOT see Administration");
        assertFalse(pageContains("manage admins"),  "Auditor should NOT see Manage Admins");
    }

    @Test
    @DisplayName("Auditor sees all 9 report links on dashboard")
    void auditor_seesAllReports() {
        logout();
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        assertTrue(pageContains("Active Sessions"),    "Should see Active Sessions");
        assertTrue(pageContains("Historical Logs"),    "Should see Historical Logs");
        assertTrue(pageContains("User Details"),       "Should see User Details");
        assertTrue(pageContains("Top Users"),          "Should see Top Users");
        assertTrue(pageContains("Session Duration"),   "Should see Session Duration");
        assertTrue(pageContains("Failed Logins"),      "Should see Failed Logins");
        assertTrue(pageContains("Concurrent Sessions"),"Should see Concurrent Sessions");
        assertTrue(pageContains("Remote Hosts"),       "Should see Remote Hosts");
        assertTrue(pageContains("After-Hours Access"), "Should see After-Hours Access");
    }

    @Test
    @DisplayName("Auditor is denied access to Audit Log")
    void auditor_deniedAuditLog() {
        logout();
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/reports?type=audit-log");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".content")));
        assertTrue(pageContains("access denied") || pageContains("permission"),
                "Auditor should be denied audit log access");
    }

    @Test
    @DisplayName("Auditor can access Active Sessions report")
    void auditor_canAccessActiveSessions() {
        logout();
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/reports?type=active-sessions");
        assertFalse(pageContains("access denied"), "Auditor should access active sessions");
        assertTrue(pageContains("active sessions"),  "Active Sessions page should load");
    }

    @Test
    @DisplayName("Auditor is denied access to Manage Admins")
    void auditor_deniedManageAdmins() {
        logout();
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".content")));
        assertTrue(pageContains("access denied") || pageContains("permission"),
                "Auditor should be denied admin management");
    }
}
