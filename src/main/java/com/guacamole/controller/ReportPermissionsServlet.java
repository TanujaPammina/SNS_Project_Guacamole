package com.guacamole.controller;

import com.guacamole.dao.ReportPermissionDao;
import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.service.RoleService;
import com.guacamole.util.AuditLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller — Super-Admin screen to configure which reports each role can see.
 *
 * GET  /admin/report-permissions?role=ADMIN   → show checkboxes for that role
 * POST /admin/report-permissions?role=ADMIN   → save new permission set
 */
@WebServlet("/admin/report-permissions")
public class ReportPermissionsServlet extends HttpServlet {

    private final ReportPermissionDao permDao = new ReportPermissionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isSuperAdmin(req, resp)) return;

        String roleName = req.getParameter("role");
        Role   selected = Role.fromString(roleName);

        // Default to AUDITOR when no role is specified
        if (selected == null) selected = Role.AUDITOR;

        try {
            Map<String, Boolean> permissions = permDao.getPermissionMap(selected);

            req.setAttribute("selectedRole",  selected);
            req.setAttribute("allRoles",      getNonSuperAdminRoles());
            req.setAttribute("permissions",   permissions);
            req.setAttribute("reportKeys",    ReportPermissionDao.ALL_REPORT_KEYS);
            req.setAttribute("reportLabels",  ReportPermissionDao.REPORT_LABELS);

            req.getRequestDispatcher("/jsp/admin/report-permissions.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/admin/report-permissions.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isSuperAdmin(req, resp)) return;

        String roleName = req.getParameter("role");
        Role   target   = Role.fromString(roleName);

        if (target == null || target == Role.SUPER_ADMIN) {
            req.getSession().setAttribute("flashError", "Invalid role.");
            resp.sendRedirect(req.getContextPath() + "/admin/report-permissions");
            return;
        }

        // Collect checked report keys from the form
        String[] checkedKeys = req.getParameterValues("reports");
        Set<String> allowed  = checkedKeys == null
                ? Collections.emptySet()
                : new HashSet<>(Arrays.asList(checkedKeys));

        // Only accept known keys (prevent tampering)
        allowed.retainAll(new HashSet<>(ReportPermissionDao.ALL_REPORT_KEYS));

        try {
            permDao.savePermissions(target, allowed);

            AdminUser current = (AdminUser) req.getSession().getAttribute("currentUser");
            AuditLogger.log(current.getUsername(), "EDIT_REPORT_PERMISSIONS",
                    target.name(), "Allowed: " + allowed, null);

            req.getSession().setAttribute("flashSuccess",
                    "Report permissions for " + target.getDisplayName() + " saved.");
            resp.sendRedirect(req.getContextPath() + "/admin/report-permissions?role=" + target.name());

        } catch (SQLException e) {
            req.getSession().setAttribute("flashError", "Database error: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/report-permissions?role=" + target.name());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isSuperAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        AdminUser current = (AdminUser) req.getSession(false).getAttribute("currentUser");
        if (!RoleService.canManageAdminUsers(current)) {
            req.setAttribute("errorMessage",
                    "Access Denied — only Super Admins can configure report permissions.");
            req.getRequestDispatcher("/jsp/access-denied.jsp").forward(req, resp);
            return false;
        }
        return true;
    }

    /** Returns all roles that can be configured (excludes SUPER_ADMIN — always full access). */
    private List<Role> getNonSuperAdminRoles() {
        return List.of(Role.ADMIN, Role.AUDITOR);
    }
}
