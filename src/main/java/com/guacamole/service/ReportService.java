package com.guacamole.service;

import com.guacamole.dao.AuditDao;
import com.guacamole.dao.SessionDao;
import com.guacamole.model.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic layer for all 9 reports.
 * Delegates to SessionDao and AuditDao; applies any cross-cutting rules
 * (e.g. after-hours threshold, top-N limits).
 */
public class ReportService {

    private static final int BUSINESS_HOUR_START = 8;   // 08:00
    private static final int BUSINESS_HOUR_END   = 18;  // 18:00
    private static final int TOP_N               = 20;

    private final SessionDao sessionDao = new SessionDao();
    private final AuditDao   auditDao   = new AuditDao();

    // Report 1 — Active Sessions
    public List<ActiveSession> getActiveSessions() throws SQLException {
        return sessionDao.findActiveSessions();
    }

    // Report 2 — Historical Session Logs
    public List<ActiveSession> getHistoricalSessions(String username,
                                                     String fromDate,
                                                     String toDate) throws SQLException {
        return sessionDao.findHistoricalSessions(username, fromDate, toDate);
    }

    // Report 4 — Top Users
    public List<UserStat> getTopUsers() throws SQLException {
        return sessionDao.findTopUsers(TOP_N);
    }

    // Report 4b — Top Connections
    public List<ConnectionStat> getTopConnections() throws SQLException {
        return sessionDao.findTopConnections(TOP_N);
    }

    // Report 5 — Session Duration per connection
    public List<ConnectionStat> getSessionDurations() throws SQLException {
        return sessionDao.findSessionDurationByConnection();
    }

    // Report 6 — Failed Login Attempts (detail)
    public List<FailedLogin> getFailedLogins(String username,
                                             String fromDate,
                                             String toDate) throws SQLException {
        return auditDao.findFailedLogins(username, fromDate, toDate);
    }

    // Report 6b — Failed Login Summary (last 30 days)
    public List<FailedLogin> getFailedLoginSummary() throws SQLException {
        return auditDao.findFailedLoginSummary(30);
    }

    // Report 7 — Concurrent Sessions
    public List<ConnectionStat> getConcurrentSessions() throws SQLException {
        return sessionDao.findConcurrentSessions();
    }

    // Report 8 — Remote Host Report
    public List<ActiveSession> getRemoteHostReport() throws SQLException {
        return sessionDao.findRemoteHostReport();
    }

    // Report 9 — After-Hours Access
    public List<ActiveSession> getAfterHoursSessions() throws SQLException {
        return sessionDao.findAfterHoursSessions(BUSINESS_HOUR_START, BUSINESS_HOUR_END);
    }

    // Audit Log (admin action history)
    public List<AuditLog> getAuditLogs(String actor, String action,
                                       String fromDate, String toDate) throws SQLException {
        return auditDao.findAuditLogs(actor, action, fromDate, toDate);
    }
}
