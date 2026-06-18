package com.guacamole.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for Login / Logout / Forgot Password pages.
 */
@Tag("selenium")
@DisplayName("Login Page Selenium Tests")
class LoginSeleniumTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Login page loads with all required elements")
    void loginPage_hasRequiredElements() {
        driver.get(BASE_URL + "/login");
        assertTrue(elementExists(By.id("username")),   "Username field missing");
        assertTrue(elementExists(By.id("password")),   "Password field missing");
        assertTrue(elementExists(By.cssSelector("button[type='submit']")), "Submit button missing");
        assertTrue(pageContains("Guacamole Admin"),    "Title missing");
        assertTrue(pageContains("Forgot password?"),   "Forgot password link missing");
    }

    @Test
    @DisplayName("Login succeeds with valid superadmin credentials")
    void login_validSuperAdmin_redirectsToDashboard() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(isOnDashboard(), "Should redirect to dashboard");
        assertTrue(pageContains("superadmin"), "Username should appear in topbar");
        assertTrue(pageContains("Super Admin"), "Role badge should show");
    }

    @Test
    @DisplayName("Login succeeds with admin1 credentials")
    void login_validAdmin_redirectsToDashboard() {
        login(ADMIN_USER, ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(isOnDashboard());
        assertTrue(pageContains("Admin"));
    }

    @Test
    @DisplayName("Login succeeds with auditor1 credentials")
    void login_validAuditor_redirectsToDashboard() {
        login(AUDITOR_USER, AUDITOR_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertTrue(isOnDashboard());
        assertTrue(pageContains("Auditor"));
    }

    @Test
    @DisplayName("Login fails with wrong password and shows error")
    void login_wrongPassword_showsError() {
        login(SUPER_ADMIN_USER, "WrongPassword999");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-danger")));
        assertTrue(isOnLoginPage(), "Should stay on login page");
        assertTrue(pageContains("Invalid username or password"),
                "Error message should appear");
    }

    @Test
    @DisplayName("Login fails with non-existent username")
    void login_unknownUser_showsError() {
        login("nobody", "somepassword");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-danger")));
        assertTrue(isOnLoginPage());
    }

    @Test
    @DisplayName("Login fails with empty credentials")
    void login_emptyCredentials_staysOnLoginPage() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        assertTrue(isOnLoginPage());
    }

    @Test
    @DisplayName("SHOW button toggles password visibility")
    void showButton_togglesPasswordVisibility() {
        driver.get(BASE_URL + "/login");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("testpassword");

        assertEquals("password", passwordField.getAttribute("type"),
                "Should start as password type");

        WebElement showBtn = driver.findElement(
                By.cssSelector("button[onclick*='password']"));
        showBtn.click();

        assertEquals("text", passwordField.getAttribute("type"),
                "Should change to text after clicking SHOW");
    }

    @Test
    @DisplayName("Forgot password link navigates to forgot password page")
    void forgotPasswordLink_navigatesToForgotPage() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.linkText("Forgot password?")).click();
        wait.until(ExpectedConditions.urlContains("/forgot-password"));
        assertTrue(pageContains("Forgot Password"),  "Should show forgot password page");
        assertTrue(elementExists(By.id("email")),     "Email field should exist");
    }

    @Test
    @DisplayName("Logout redirects to login page")
    void logout_redirectsToLoginPage() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        logout();
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(isOnLoginPage());
    }

    @Test
    @DisplayName("Unauthenticated access to dashboard redirects to login")
    void unauthenticated_dashboardAccess_redirectsToLogin() {
        driver.get(BASE_URL + "/dashboard");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(isOnLoginPage());
    }
}
