package com.guacamole.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for all 9 Reports.
 * Verifies each report page loads and displays expected elements.
 */
@Tag("selenium")
@DisplayName("Report Pages Selenium Tests")
class ReportSeleniumTest extends BaseSeleniumTest {

    @BeforeEach
    void loginAsSuperAdmin() {
        login(SUPER_ADMIN_USER, SUPER_ADMIN_PASS);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        // Confirm we are actually on the dashboard before each test
        assertTrue(isOnDashboard(), "Login failed in @BeforeEach");
    }

    // ── Report 1: Active Sessions ─────────────────────────────────────────────

    @Test
    @DisplayName("Active Sessions page loads with table and search")
    void activeSessions_pageLoads() {
        driver.get(BASE_URL + "/reports?type=active-sessions");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Active Sessions"),           "Page title missing");
        assertTrue(pageContains("Currently Active Sessions"), "Section header missing");
        assertTrue(elementExists(By.id("table-search")),      "Search field missing");
        assertTrue(elementExists(By.id("refresh-indicator")), "Auto-refresh missing");
    }

    @Test
    @DisplayName("Active Sessions table has correct column headers")
    void activeSessions_correctColumns() {
        driver.get(BASE_URL + "/reports?type=active-sessions");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        // Headers in JSP use lowercase text (CSS text-transform: uppercase visually)
        assertTrue(pageContains("Username")    || pageContains("USERNAME"),  "Username column missing");
        assertTrue(pageContains("Connection")  || pageContains("CONNECTION"),"Connection column missing");
        assertTrue(pageContains("Client IP")   || pageContains("CLIENT IP"), "Client IP column missing");
        assertTrue(pageContains("Started")     || pageContains("STARTED"),   "Started column missing");
        assertTrue(pageContains("Duration")    || pageContains("DURATION"),  "Duration column missing");
    }

    // ── Report 2: Historical Logs ────────────────────────────────────────────

    @Test
    @DisplayName("Historical Logs page loads with filters and table")
    void historicalLogs_pageLoads() {
        driver.get(BASE_URL + "/reports?type=historical-logs");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Historical Session Logs"),  "Page title missing");
        assertTrue(pageContains("Filters"),                  "Filters section missing");
        assertTrue(pageContains("Session History"),          "Table section missing");
        assertTrue(elementExists(By.name("username")),       "Username filter missing");
        assertTrue(elementExists(By.id("from")),             "Date from filter missing");
        assertTrue(elementExists(By.id("to")),               "Date to filter missing");
    }

    @Test
    @DisplayName("Historical Logs Last 7d shortcut button exists")
    void historicalLogs_dateShortcutButtons() {
        driver.get(BASE_URL + "/reports?type=historical-logs");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-days='7']")));
        assertTrue(elementExists(By.cssSelector("[data-days='7']")),  "Last 7d button missing");
        assertTrue(elementExists(By.cssSelector("[data-days='30']")), "Last 30d button missing");
    }

    // ── Report 3: User Details ────────────────────────────────────────────────

    @Test
    @DisplayName("User Details page loads with table")
    void userDetails_pageLoads() {
        driver.get(BASE_URL + "/users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("user details"),  "Page title missing");
        assertTrue(pageContains("all users"),     "Table section missing");
        assertTrue(pageContains("username"),      "Username column missing");
        assertTrue(pageContains("status"),        "Status column missing");
    }

    // ── Report 4a: Top Users ──────────────────────────────────────────────────

    @Test
    @DisplayName("Top Users page loads with ranked table")
    void topUsers_pageLoads() {
        driver.get(BASE_URL + "/reports?type=top-users");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Top Users"),           "Page title missing");
        assertTrue(pageContains("RANK"),                "Rank column missing");
        assertTrue(pageContains("TOTAL SESSIONS"),      "Sessions column missing");
        assertTrue(pageContains("TOTAL DURATION"),      "Duration column missing");
    }

    // ── Report 4b: Top Connections ────────────────────────────────────────────

    @Test
    @DisplayName("Top Connections page loads with ranked table")
    void topConnections_pageLoads() {
        driver.get(BASE_URL + "/reports?type=top-connections");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Top Connections"),   "Page title missing");
        assertTrue(pageContains("CONNECTION NAME"),   "Connection column missing");
        assertTrue(pageContains("AVG DURATION"),      "Avg duration column missing");
    }

    // ── Report 5: Session Duration ────────────────────────────────────────────

    @Test
    @DisplayName("Session Duration page loads with totals and averages")
    void sessionDuration_pageLoads() {
        driver.get(BASE_URL + "/reports?type=session-duration");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Session Duration"),      "Page title missing");
        assertTrue(pageContains("TOTAL DURATION"),        "Total duration column missing");
        assertTrue(pageContains("AVG DURATION"),          "Avg duration column missing");
    }

    // ── Report 6: Failed Logins ───────────────────────────────────────────────

    @Test
    @DisplayName("Failed Logins page loads with summary and detail sections")
    void failedLogins_pageLoads() {
        driver.get(BASE_URL + "/reports?type=failed-logins");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Failed Login"),    "Page title missing");
        assertTrue(pageContains("Filters"),         "Filters section missing");
    }

    @Test
    @DisplayName("Failed Logins shows risk level badges")
    void failedLogins_showsRiskBadges() {
        driver.get(BASE_URL + "/reports?type=failed-logins");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        // Risk column should exist in summary table if there are failed logins
        assertTrue(pageContains("RISK") || pageContains("Failed Login Events"),
                "Risk or events section should exist");
    }

    // ── Report 7: Concurrent Sessions ────────────────────────────────────────

    @Test
    @DisplayName("Concurrent Sessions page loads with load level badges")
    void concurrentSessions_pageLoads() {
        driver.get(BASE_URL + "/reports?type=concurrent-sessions");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Concurrent Sessions"),      "Page title missing");
        assertTrue(pageContains("PEAK CONCURRENT SESSIONS"), "Peak column missing");
        assertTrue(pageContains("LOAD LEVEL"),               "Load level column missing");
    }

    // ── Report 8: Remote Hosts ────────────────────────────────────────────────

    @Test
    @DisplayName("Remote Hosts page loads with IP and session data")
    void remoteHosts_pageLoads() {
        driver.get(BASE_URL + "/reports?type=remote-hosts");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("Remote Host Report"), "Page title missing");
        assertTrue(pageContains("CLIENT IP"),          "IP column missing");
        assertTrue(pageContains("SESSION COUNT"),      "Session count column missing");
    }

    // ── Report 9: After-Hours ─────────────────────────────────────────────────

    @Test
    @DisplayName("After-Hours Access page loads with warning alert")
    void afterHours_pageLoads() {
        driver.get(BASE_URL + "/reports?type=after-hours");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        assertTrue(pageContains("After-Hours Access Report"), "Page title missing");
        assertTrue(pageContains("08:00"),  "Business hours info missing");
        assertTrue(pageContains("18:00"),  "Business hours info missing");
        assertTrue(pageContains("weekends"), "Weekend mention missing");
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Dashboard shows all stat cards and report links")
    void dashboard_showsStatCardsAndLinks() {
        driver.get(BASE_URL + "/dashboard");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".stat-grid")));
        assertTrue(pageContains("active sessions"),    "Active sessions card missing");
        assertTrue(pageContains("after-hours"),        "After-hours card missing");
        assertTrue(pageContains("reports"),            "Reports section missing");
    }
}
