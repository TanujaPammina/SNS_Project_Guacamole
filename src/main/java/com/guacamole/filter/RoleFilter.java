package com.guacamole.filter;

import com.guacamole.dao.ReportPermissionDao;
import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.service.RoleService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * Filter — enforces role-based access control on protected routes.
 *
 * Report visibility is driven by the role_report_permissions table (configurable
 * by Super Admin) rather than hard-coded rules.
 *
 * SUPER_ADMIN always has full access regardless of the DB table.
 *
 * Runs AFTER AuthFilter (which guarantees a valid session exists).
 */
@WebFilter("/*")
public class RoleFilter implements Filter {

    /**
     * Maps URL path prefixes to the minimum Role required.
     * First match wins.
     */
    private static final Map<String, Role> ROUTE_ROLES = Map.of(
        "/admin/users",               Role.SUPER_ADMIN,
        "/admin/report-permissions",  Role.SUPER_ADMIN,
        "/reports",                   Role.AUDITOR,
        "/users",                     Role.AUDITOR,
        "/dashboard",                 Role.AUDITOR
    );

    private final ReportPermissionDao permDao = new ReportPermissionDao();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Public paths — skip role check (AuthFilter already handles these)
        if (path.startsWith("/login")  || path.startsWith("/css") ||
            path.startsWith("/js")     || path.startsWith("/logout") ||
            path.startsWith("/profile")|| path.startsWith("/forgot-password") ||
            path.startsWith("/reset-password")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) { chain.doFilter(request, response); return; }

        AdminUser current = (AdminUser) session.getAttribute("currentUser");
        if (current == null) { chain.doFilter(request, response); return; }

        // SUPER_ADMIN — unrestricted; ensure sidebar cache is cleared for super admin
        if (current.getRole() == Role.SUPER_ADMIN) {
            session.removeAttribute("allowedReports");
            chain.doFilter(request, response);
            return;
        }

        // ── Refresh allowed-reports cache in session (once per request) ────────
        // This Set is used by the sidebar JSP to show/hide report links.
        Set<String> allowed;
        try {
            allowed = permDao.getAllowedReports(current.getRole());
            session.setAttribute("allowedReports", allowed);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Permission check failed: " + e.getMessage());
            req.getRequestDispatcher("/jsp/access-denied.jsp").forward(req, resp);
            return;
        }

        // ── Route-level minimum role ──────────────────────────────────────────
        Role requiredRoute = resolveRequiredRouteRole(path);
        if (requiredRoute != null && !RoleService.hasRole(current, requiredRoute)) {
            denyAccess(req, resp, current);
            return;
        }

        // ── Per-report DB permission check ────────────────────────────────────
        if ("/reports".equals(path)) {
            String reportType = req.getParameter("type");
            if (reportType != null && !reportType.isBlank()) {
                if (!allowed.contains(reportType)) {
                    denyAccess(req, resp, current);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Role resolveRequiredRouteRole(String path) {
        for (Map.Entry<String, Role> entry : ROUTE_ROLES.entrySet()) {
            if (path.startsWith(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private void denyAccess(HttpServletRequest req, HttpServletResponse resp, AdminUser current)
            throws IOException, ServletException {
        req.setAttribute("errorMessage",
                "Access Denied — your role (" + current.getRoleDisplayName() +
                ") does not have permission to view this page.");
        req.getRequestDispatcher("/jsp/access-denied.jsp").forward(req, resp);
    }
}
