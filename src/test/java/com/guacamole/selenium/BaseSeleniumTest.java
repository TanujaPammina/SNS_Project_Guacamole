package com.guacamole.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for all Selenium tests.
 * Handles driver setup/teardown and provides common helper methods.
 *
 * Prerequisites before running:
 *   1. Tomcat running: catalina.bat run
 *   2. App accessible at http://localhost:8080/guacamole-admin-1.0/login
 *   3. DB has test users: superadmin/Admin@1234, admin1/Admin@1234, auditor1/Audit@1234
 */
public abstract class BaseSeleniumTest {

    protected static final String BASE_URL =
            System.getProperty("app.url", "http://localhost:8080/guacamole-admin-1.0");

    protected static final String SUPER_ADMIN_USER = "superadmin";
    protected static final String SUPER_ADMIN_PASS = "Admin@1234";
    protected static final String ADMIN_USER       = "admin1";
    protected static final String ADMIN_PASS        = "Admin@1234";
    protected static final String AUDITOR_USER      = "auditor1";
    protected static final String AUDITOR_PASS      = "Audit@1234";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(20)); // increased to 20s
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ── Helper methods ────────────────────────────────────────────────────────

    protected void login(String username, String password) {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    protected void logout() {
        driver.get(BASE_URL + "/logout");
    }

    protected boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    protected boolean isOnDashboard() {
        return driver.getCurrentUrl().contains("/dashboard");
    }

    protected boolean pageContains(String text) {
        // Case-insensitive check against rendered page source
        return driver.getPageSource().toLowerCase().contains(text.toLowerCase());
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected boolean elementExists(By locator) {
        return !driver.findElements(locator).isEmpty();
    }
}
