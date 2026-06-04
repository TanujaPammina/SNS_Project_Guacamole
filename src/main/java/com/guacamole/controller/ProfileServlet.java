package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.service.UserService;
import com.guacamole.util.AuditLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller — lets any logged-in admin change their own password.
 *
 * GET  /profile  → show change-password form
 * POST /profile  → process password change
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        AdminUser current = (AdminUser) req.getSession().getAttribute("currentUser");

        String currentPassword = req.getParameter("currentPassword");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // ── Validation ────────────────────────────────────────────────────────
        if (isBlank(currentPassword) || isBlank(newPassword) || isBlank(confirmPassword)) {
            req.setAttribute("errorMessage", "All fields are required.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            return;
        }

        if (!BCrypt.checkpw(currentPassword, current.getPasswordHash())) {
            req.setAttribute("errorMessage", "Current password is incorrect.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            return;
        }

        if (newPassword.length() < 8) {
            req.setAttribute("errorMessage", "New password must be at least 8 characters.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "New passwords do not match.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            return;
        }

        if (newPassword.equals(currentPassword)) {
            req.setAttribute("errorMessage", "New password must be different from the current password.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            return;
        }

        // ── Update password ───────────────────────────────────────────────────
        try {
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
            userService.changeOwnPassword(current.getId(), newHash, current.getUsername());

            // Update the session object so the new hash is reflected immediately
            current.setPasswordHash(newHash);
            req.getSession().setAttribute("currentUser", current);

            AuditLogger.log(current.getUsername(), "CHANGE_PASSWORD",
                    current.getUsername(), "Self-service password change", req.getRemoteAddr());

            req.setAttribute("successMessage", "Password changed successfully.");
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
