package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.service.ReportService;
import com.guacamole.service.RoleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller — serves the main dashboard with summary stat cards.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AdminUser current = (AdminUser) req.getSession().getAttribute("currentUser");

        try {
            req.setAttribute("activeSessions", reportService.getActiveSessions().size());
            req.setAttribute("afterHoursCount", reportService.getAfterHoursSessions().size());

            // Failed login summary only visible to ADMIN+
            if (RoleService.canViewAuditLog(current)) {
                req.setAttribute("failedLogins30d",
                        reportService.getFailedLoginSummary()
                                .stream().mapToInt(f -> f.getFailCount()).sum());
            }

            req.setAttribute("topUsers", reportService.getTopUsers());

        } catch (SQLException e) {
            req.setAttribute("dbError", "Could not load dashboard data: " + e.getMessage());
        }

        req.getRequestDispatcher("/jsp/dashboard.jsp").forward(req, resp);
    }
}
