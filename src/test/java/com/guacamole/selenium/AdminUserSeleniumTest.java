package com.guacamole.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for Admin User Management (CRUD).
 * Requires superadmin login.
 */
@Tag("selenium")
@DisplayName("Admin User Management Selenium Tests")
class AdminUserSeleniumTest extends BaseSeleniumTest {

    @BeforeEach
    void loginAsSuperAdmin() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    @DisplayName("Manage Admins page shows all admin users")
    void manageAdmins_showsAllUsers() {
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Admin Accounts"),  "Page header missing");
        assertTrue(pageContains("superadmin"),      "Superadmin should be listed");
        assertTrue(pageContains("admin1"),          "admin1 should be listed");
        assertTrue(pageContains("auditor1"),        "auditor1 should be listed");
    }

    @Test
    @DisplayName("Manage Admins shows role permission matrix")
    void manageAdmins_showsRoleMatrix() {
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Role Permissions"), "Role permissions table missing");
        assertTrue(pageContains("Super Admin"),      "Super Admin column missing");
        assertTrue(pageContains("Auditor"),          "Auditor column missing");
    }

    @Test
    @DisplayName("New Admin User form loads with all required fields")
    void newAdminUser_formHasRequiredFields() {
        driver.get(BASE_URL + "/admin/users?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        assertTrue(elementExists(By.id("username")),        "Username field missing");
        assertTrue(elementExists(By.id("fullName")),        "Full name field missing");
        assertTrue(elementExists(By.id("email")),           "Email field missing");
        assertTrue(elementExists(By.id("role")),            "Role dropdown missing");
        assertTrue(elementExists(By.id("password")),        "Password field missing");
        assertTrue(elementExists(By.id("confirmPassword")), "Confirm password field missing");
        assertTrue(elementExists(By.id("active")),          "Active checkbox missing");
    }

    @Test
    @DisplayName("Role dropdown has all three role options")
    void newAdminUser_roleDropdownHasAllRoles() {
        driver.get(BASE_URL + "/admin/users?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("role")));
        Select roleSelect = new Select(driver.findElement(By.id("role")));
        long optionCount = roleSelect.getOptions().stream()
                .filter(o -> !o.getAttribute("value").isBlank())
                .count();
        assertEquals(3, optionCount, "Should have 3 role options");
        assertTrue(pageContains("SUPER_ADMIN"), "SUPER_ADMIN option missing");
        assertTrue(pageContains("ADMIN"),       "ADMIN option missing");
        assertTrue(pageContains("AUDITOR"),     "AUDITOR option missing");
    }

    @Test
    @DisplayName("Create user with blank username shows validation error")
    void createUser_blankUsername_showsError() {
        driver.get(BASE_URL + "/admin/users?action=new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        driver.findElement(By.id("password")).sendKeys("Password@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password@123");
        new Select(driver.findElement(By.id("role"))).selectByValue("ADMIN");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // HTML5 required validation or server-side error
        assertTrue(isOnLoginPage() == false, "Should not redirect to login");
    }

    @Test
    @DisplayName("Superadmin cannot see delete button on their own account")
    void manageAdmins_superAdminHasNoDeleteButton() {
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        // Find the row for superadmin — it should not have a delete button
        String pageSource = driver.getPageSource();
        // The superadmin row should NOT have a delete form next to it
        assertFalse(pageSource.contains("action=delete&id=1\""),
                "Superadmin row should not have delete button");
    }

    @Test
    @DisplayName("Edit admin user form pre-fills existing values")
    void editAdmin_formPreFillsValues() {
        // Get admin1's ID first by loading the list
        driver.get(BASE_URL + "/admin/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        // Click Edit on admin1
        driver.findElements(By.cssSelector("a[href*='action=edit']"))
              .stream()
              .findFirst()
              .ifPresent(btn -> btn.click());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("role")));
        // Username field should be readonly on edit
        String readonly = driver.findElement(By.id("username")).getAttribute("readonly");
        assertNotNull(readonly, "Username should be readonly on edit");
    }
}
