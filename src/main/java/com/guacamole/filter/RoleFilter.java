package com.guacamole.filter;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.service.RoleService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

/**
 * Filter — enforces role-based access control on protected routes.
 *
 * Routes not listed in ROUTE_ROLES are accessible to any authenticated user.
 * Routes listed require the user to hold at least the specified role.
 *
 * Runs AFTER AuthFilter (which guarantees a valid session exists).
 */
@WebFilter("/*")
public class RoleFilter implements Filter {

    /**
     * Maps URL path prefixes to the minimum Role required.
     * Checked in order — first match wins.
     */
    private static final Map<String, Role> ROUTE_ROLES = Map.of(
        "/admin/users",   Role.SUPER_ADMIN,   // manage admin accounts
        "/reports",       Role.AUDITOR,        // all reports — any role
        "/users",         Role.AUDITOR,        // Guacamole user list
        "/dashboard",     Role.AUDITOR         // dashboard
    );

    /**
     * Routes that require ADMIN or above (audit log is not for AUDITOR).
     */
    private static final Map<String, Role> REPORT_TYPE_ROLES = Map.of(
        "audit-log", Role.ADMIN
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Public paths — skip role check (AuthFilter already handles these)
        if (path.startsWith("/login") || path.startsWith("/css") ||
            path.startsWith("/js")    || path.startsWith("/logout") ||
            path.startsWith("/profile") || path.startsWith("/forgot-password") ||
            path.startsWith("/reset-password")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        AdminUser current = (AdminUser) session.getAttribute("currentUser");
        if (current == null) {
            chain.doFilter(request, response);
            return;
        }

        // Check route-level role requirement
        Role required = resolveRequiredRole(path, req.getParameter("type"));

        if (required != null && !RoleService.hasRole(current, required)) {
            req.setAttribute("errorMessage",
                    "Access Denied — your role (" + current.getRoleDisplayName() +
                    ") does not have permission to view this page.");
            req.getRequestDispatcher("/jsp/access-denied.jsp").forward(req, resp);
            return;
        }

        chain.doFilter(request, response);
    }

    private Role resolveRequiredRole(String path, String reportType) {
        // Special case: audit-log report requires ADMIN
        if ("/reports".equals(path) && "audit-log".equals(reportType)) {
            return Role.ADMIN;
        }

        for (Map.Entry<String, Role> entry : ROUTE_ROLES.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null; // no restriction
    }
}
