package com.guacamole.service;

import com.guacamole.dao.AuditDao;
import com.guacamole.dao.SessionDao;
import com.guacamole.model.*;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ReportService Tests")
class ReportServiceTest {

    @Mock private SessionDao sessionDao;
    @Mock private AuditDao   auditDao;

    private ReportService  reportService;
    private ActiveSession  session1, session2;
    private UserStat       userStat;
    private ConnectionStat connStat;
    private FailedLogin    failedLogin;

    @BeforeEach
    void setUp() throws Exception {
        // Inject mocks via reflection
        reportService = new ReportService();
        java.lang.reflect.Field sf = ReportService.class.getDeclaredField("sessionDao");
        sf.setAccessible(true); sf.set(reportService, sessionDao);
        java.lang.reflect.Field af = ReportService.class.getDeclaredField("auditDao");
        af.setAccessible(true); af.set(reportService, auditDao);

        // Build test data
        session1 = new ActiveSession();
        session1.setHistoryId(1); session1.setUsername("alice");
        session1.setConnectionName("Web Server"); session1.setRemoteHost("192.168.1.10");
        session1.setStartDate(LocalDateTime.now().minusHours(1));

        session2 = new ActiveSession();
        session2.setHistoryId(2); session2.setUsername("bob");
        session2.setConnectionName("DB Server"); session2.setRemoteHost("192.168.1.11");
        session2.setStartDate(LocalDateTime.now().minusDays(1));
        session2.setEndDate(LocalDateTime.now().minusHours(23));
        session2.setDurationSeconds(3600);

        userStat = new UserStat();
        userStat.setUsername("alice"); userStat.setTotalSessions(5);
        userStat.setTotalDurationSeconds(18000);

        connStat = new ConnectionStat();
        connStat.setConnectionName("Web Server"); connStat.setTotalSessions(10);
        connStat.setTotalDurationSeconds(36000);  connStat.setAvgDurationSeconds(3600);

        failedLogin = new FailedLogin();
        failedLogin.setUsername("hacker");
        failedLogin.setRemoteIp("10.0.0.1");
        failedLogin.setFailCount(7);
    }

    // ── Report 1: Active Sessions ─────────────────────────────────────────────

    @Test
    @DisplayName("getActiveSessions returns list from DAO")
    void getActiveSessions_returnsList() throws SQLException {
        when(sessionDao.findActiveSessions()).thenReturn(Arrays.asList(session1));
        List<ActiveSession> result = reportService.getActiveSessions();
        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getUsername());
    }

    @Test
    @DisplayName("getActiveSessions returns empty list when none active")
    void getActiveSessions_empty() throws SQLException {
        when(sessionDao.findActiveSessions()).thenReturn(Collections.emptyList());
        assertTrue(reportService.getActiveSessions().isEmpty());
    }

    // ── Report 2: Historical Sessions ────────────────────────────────────────

    @Test
    @DisplayName("getHistoricalSessions passes filters to DAO")
    void getHistoricalSessions_withFilters() throws SQLException {
        when(sessionDao.findHistoricalSessions("alice", "2026-01-01", "2026-12-31"))
                .thenReturn(Arrays.asList(session2));
        List<ActiveSession> result = reportService.getHistoricalSessions(
                "alice", "2026-01-01", "2026-12-31");
        assertEquals(1, result.size());
        verify(sessionDao).findHistoricalSessions("alice", "2026-01-01", "2026-12-31");
    }

    @Test
    @DisplayName("getHistoricalSessions works with null filters")
    void getHistoricalSessions_nullFilters() throws SQLException {
        when(sessionDao.findHistoricalSessions(null, null, null))
                .thenReturn(Arrays.asList(session1, session2));
        assertEquals(2, reportService.getHistoricalSessions(null, null, null).size());
    }

    // ── Report 4a: Top Users ─────────────────────────────────────────────────

    @Test
    @DisplayName("getTopUsers returns list with correct data")
    void getTopUsers_returnsList() throws SQLException {
        when(sessionDao.findTopUsers(20)).thenReturn(Arrays.asList(userStat));
        List<UserStat> result = reportService.getTopUsers();
        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals(5, result.get(0).getTotalSessions());
    }

    // ── Report 4b: Top Connections ───────────────────────────────────────────

    @Test
    @DisplayName("getTopConnections returns list from DAO")
    void getTopConnections_returnsList() throws SQLException {
        when(sessionDao.findTopConnections(20)).thenReturn(Arrays.asList(connStat));
        List<ConnectionStat> result = reportService.getTopConnections();
        assertEquals(1, result.size());
        assertEquals("Web Server", result.get(0).getConnectionName());
    }

    // ── Report 5: Session Duration ───────────────────────────────────────────

    @Test
    @DisplayName("getSessionDurations returns list with formatted duration")
    void getSessionDurations_returnsList() throws SQLException {
        when(sessionDao.findSessionDurationByConnection()).thenReturn(Arrays.asList(connStat));
        List<ConnectionStat> result = reportService.getSessionDurations();
        assertEquals(1, result.size());
        assertEquals("01:00:00", result.get(0).getAvgDurationFormatted());
    }

    // ── Report 6: Failed Logins ───────────────────────────────────────────────

    @Test
    @DisplayName("getFailedLogins passes filters to AuditDao")
    void getFailedLogins_withFilters() throws SQLException {
        when(auditDao.findFailedLogins("hacker", null, null))
                .thenReturn(Arrays.asList(failedLogin));
        List<FailedLogin> result = reportService.getFailedLogins("hacker", null, null);
        assertEquals(1, result.size());
        assertEquals("hacker", result.get(0).getUsername());
        verify(auditDao).findFailedLogins("hacker", null, null);
    }

    @Test
    @DisplayName("getFailedLoginSummary requests last 30 days")
    void getFailedLoginSummary_uses30Days() throws SQLException {
        when(auditDao.findFailedLoginSummary(30)).thenReturn(Arrays.asList(failedLogin));
        List<FailedLogin> result = reportService.getFailedLoginSummary();
        assertEquals(1, result.size());
        assertEquals(7, result.get(0).getFailCount());
        verify(auditDao).findFailedLoginSummary(30);
    }

    // ── Report 7: Concurrent Sessions ────────────────────────────────────────

    @Test
    @DisplayName("getConcurrentSessions returns peak counts from DAO")
    void getConcurrentSessions_returnsList() throws SQLException {
        ConnectionStat concurrent = new ConnectionStat();
        concurrent.setConnectionName("Web Server");
        concurrent.setMaxConcurrent(8);
        when(sessionDao.findConcurrentSessions()).thenReturn(Arrays.asList(concurrent));
        List<ConnectionStat> result = reportService.getConcurrentSessions();
        assertEquals(1, result.size());
        assertEquals(8, result.get(0).getMaxConcurrent());
    }

    // ── Report 8: Remote Hosts ────────────────────────────────────────────────

    @Test
    @DisplayName("getRemoteHostReport returns list from DAO")
    void getRemoteHostReport_returnsList() throws SQLException {
        when(sessionDao.findRemoteHostReport()).thenReturn(Arrays.asList(session1));
        List<ActiveSession> result = reportService.getRemoteHostReport();
        assertEquals(1, result.size());
    }

    // ── Report 9: After-Hours ─────────────────────────────────────────────────

    @Test
    @DisplayName("getAfterHoursSessions uses business hours 08:00-18:00")
    void getAfterHoursSessions_usesCorrectHours() throws SQLException {
        when(sessionDao.findAfterHoursSessions(8, 18)).thenReturn(Arrays.asList(session1));
        List<ActiveSession> result = reportService.getAfterHoursSessions();
        assertEquals(1, result.size());
        verify(sessionDao).findAfterHoursSessions(8, 18);
    }

    @Test
    @DisplayName("getAfterHoursSessions returns empty list when none found")
    void getAfterHoursSessions_empty() throws SQLException {
        when(sessionDao.findAfterHoursSessions(8, 18)).thenReturn(Collections.emptyList());
        assertTrue(reportService.getAfterHoursSessions().isEmpty());
    }
}
