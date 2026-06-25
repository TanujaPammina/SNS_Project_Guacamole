package com.guacamole.controller;

import com.guacamole.service.ReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;


@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String type     = req.getParameter("type");
        String username = req.getParameter("username");
        String fromDate = req.getParameter("from");
        String toDate   = req.getParameter("to");

        if (type == null || type.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            switch (type) {

                case "active-sessions" -> {
                    req.setAttribute("sessions", reportService.getActiveSessions());
                    req.setAttribute("reportTitle", "Active Sessions");
                    forward(req, resp, "/jsp/reports/active-sessions.jsp");
                }

                case "historical-logs" -> {
                    req.setAttribute("sessions",
                            reportService.getHistoricalSessions(username, fromDate, toDate));
                    req.setAttribute("reportTitle", "Historical Session Logs");
                    forward(req, resp, "/jsp/reports/historical-logs.jsp");
                }

                case "top-users" -> {
                    req.setAttribute("userStats", reportService.getTopUsers());
                    req.setAttribute("reportTitle", "Top Users by Sessions");
                    forward(req, resp, "/jsp/reports/top-users.jsp");
                }

                case "top-connections" -> {
                    req.setAttribute("connStats", reportService.getTopConnections());
                    req.setAttribute("reportTitle", "Top Connections");
                    forward(req, resp, "/jsp/reports/top-connections.jsp");
                }

                case "session-duration" -> {
                    req.setAttribute("connStats", reportService.getSessionDurations());
                    req.setAttribute("reportTitle", "Session Duration by Connection");
                    forward(req, resp, "/jsp/reports/session-duration.jsp");
                }

                case "failed-logins" -> {
                    req.setAttribute("failedLogins",
                            reportService.getFailedLogins(username, fromDate, toDate));
                    req.setAttribute("summary", reportService.getFailedLoginSummary());
                    req.setAttribute("reportTitle", "Failed Login Attempts");
                    forward(req, resp, "/jsp/reports/failed-logins.jsp");
                }

                case "concurrent-sessions" -> {
                    req.setAttribute("connStats", reportService.getConcurrentSessions());
                    req.setAttribute("reportTitle", "Concurrent Sessions Report");
                    forward(req, resp, "/jsp/reports/concurrent-sessions.jsp");
                }

                case "remote-hosts" -> {
                    req.setAttribute("sessions", reportService.getRemoteHostReport());
                    req.setAttribute("reportTitle", "Remote Host Report");
                    forward(req, resp, "/jsp/reports/remote-hosts.jsp");
                }

                case "after-hours" -> {
                    req.setAttribute("sessions", reportService.getAfterHoursSessions());
                    req.setAttribute("reportTitle", "After-Hours Access Report");
                    forward(req, resp, "/jsp/reports/after-hours.jsp");
                }

                case "audit-log" -> {
                    String actor  = req.getParameter("actor");
                    String action = req.getParameter("action");
                    req.setAttribute("auditLogs",
                            reportService.getAuditLogs(actor, action, fromDate, toDate));
                    req.setAttribute("reportTitle", "Admin Audit Log");
                    forward(req, resp, "/jsp/reports/audit-log.jsp");
                }

                default -> resp.sendRedirect(req.getContextPath() + "/dashboard");
            }

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.setAttribute("reportTitle", "Error");
            forward(req, resp, "/jsp/error.jsp");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }
}
