package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.model.Role;
import com.guacamole.model.dto.AdminUserDto;
import com.guacamole.service.RoleService;
import com.guacamole.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller — CRUD for admin user accounts.
 * All operations require SUPER_ADMIN role (enforced by RoleFilter + explicit check).
 *
 * GET  /admin/users              → list all admin users
 * GET  /admin/users?action=new   → show create form
 * GET  /admin/users?action=edit&id=X → show edit form
 * POST /admin/users?action=create    → create admin user
 * POST /admin/users?action=update&id=X → update admin user
 * POST /admin/users?action=delete&id=X → delete admin user
 */
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isSuperAdmin(req, resp)) return;

        String action = req.getParameter("action");

        try {
            if ("new".equals(action)) {
                req.setAttribute("roles", Role.values());
                req.getRequestDispatcher("/jsp/admin/admin-user-form.jsp").forward(req, resp);

            } else if ("edit".equals(action)) {
                int id = parseId(req.getParameter("id"));
                if (id < 0) { resp.sendRedirect(req.getContextPath() + "/admin/users"); return; }

                AdminUser target = userService.getAdminUserById(id);
                if (target == null) { resp.sendRedirect(req.getContextPath() + "/admin/users"); return; }

                req.setAttribute("editUser", target);
                req.setAttribute("roles", Role.values());
                req.getRequestDispatcher("/jsp/admin/admin-user-form.jsp").forward(req, resp);

            } else {
                req.setAttribute("adminUsers", userService.getAllAdminUsers());
                req.getRequestDispatcher("/jsp/admin/admin-users.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/admin/admin-users.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isSuperAdmin(req, resp)) return;

        String action = req.getParameter("action");
        AdminUser current = (AdminUser) req.getSession().getAttribute("currentUser");

        try {
            switch (action == null ? "" : action) {

                case "create" -> {
                    AdminUserDto dto = bindDto(req);
                    String error = userService.createAdminUser(dto, current.getUsername());
                    if (error != null) {
                        req.setAttribute("errorMessage", error);
                        req.setAttribute("dto", dto);
                        req.setAttribute("roles", Role.values());
                        req.getRequestDispatcher("/jsp/admin/admin-user-form.jsp").forward(req, resp);
                    } else {
                        req.getSession().setAttribute("flashSuccess",
                                "Admin user '" + dto.getUsername() + "' created successfully.");
                        resp.sendRedirect(req.getContextPath() + "/admin/users");
                    }
                }

                case "update" -> {
                    int id = parseId(req.getParameter("id"));
                    if (id < 0) { resp.sendRedirect(req.getContextPath() + "/admin/users"); return; }

                    AdminUserDto dto = bindDto(req);
                    String error = userService.updateAdminUser(id, dto, current.getUsername());
                    if (error != null) {
                        AdminUser target = userService.getAdminUserById(id);
                        req.setAttribute("errorMessage", error);
                        req.setAttribute("editUser", target);
                        req.setAttribute("dto", dto);
                        req.setAttribute("roles", Role.values());
                        req.getRequestDispatcher("/jsp/admin/admin-user-form.jsp").forward(req, resp);
                    } else {
                        req.getSession().setAttribute("flashSuccess", "Admin user updated successfully.");
                        resp.sendRedirect(req.getContextPath() + "/admin/users");
                    }
                }

                case "delete" -> {
                    int id = parseId(req.getParameter("id"));
                    if (id >= 0) {
                        String error = userService.deleteAdminUser(id, current.getUsername());
                        if (error != null) {
                            req.getSession().setAttribute("flashError", error);
                        } else {
                            req.getSession().setAttribute("flashSuccess", "Admin user deleted.");
                        }
                    }
                    resp.sendRedirect(req.getContextPath() + "/admin/users");
                }

                default -> resp.sendRedirect(req.getContextPath() + "/admin/users");
            }

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/admin/admin-users.jsp").forward(req, resp);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isSuperAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        AdminUser current = (AdminUser) req.getSession(false).getAttribute("currentUser");
        if (!RoleService.canManageAdminUsers(current)) {
            req.setAttribute("errorMessage",
                    "Access Denied — only Super Admins can manage admin user accounts.");
            req.getRequestDispatcher("/jsp/access-denied.jsp").forward(req, resp);
            return false;
        }
        return true;
    }

    private AdminUserDto bindDto(HttpServletRequest req) {
        AdminUserDto dto = new AdminUserDto();
        dto.setUsername(req.getParameter("username"));
        dto.setPassword(req.getParameter("password"));
        dto.setConfirmPassword(req.getParameter("confirmPassword"));
        dto.setFullName(req.getParameter("fullName"));
        dto.setEmail(req.getParameter("email"));
        dto.setRole(req.getParameter("role"));
        dto.setActive("on".equals(req.getParameter("active")) ||
                      "true".equals(req.getParameter("active")));
        return dto;
    }

    private int parseId(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return -1; }
    }
}
