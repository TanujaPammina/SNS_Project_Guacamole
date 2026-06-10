package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.service.PasswordResetService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /reset-password?token=XXX → show "enter new password" form
 * POST /reset-password            → update password
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private final PasswordResetService resetService = new PasswordResetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token = req.getParameter("token");

        try {
            AdminUser user = resetService.validateToken(token);
            if (user == null) {
                req.setAttribute("errorMessage",
                        "This reset link is invalid or has expired. " +
                        "Please request a new one.");
                req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
                return;
            }
            // Token valid — show reset form
            req.setAttribute("token", token);
            req.setAttribute("username", user.getUsername());
            req.getRequestDispatcher("/jsp/reset-password.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "A system error occurred. Please try again.");
            req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String token           = req.getParameter("token");
        String newPassword     = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        try {
            String error = resetService.resetPassword(token, newPassword, confirmPassword);

            if (error != null) {
                req.setAttribute("errorMessage", error);
                req.setAttribute("token", token);
                req.getRequestDispatcher("/jsp/reset-password.jsp").forward(req, resp);
            } else {
                // Success — redirect to login with success message
                req.getSession(true).setAttribute("flashSuccess",
                        "Password reset successfully. Please log in with your new password.");
                resp.sendRedirect(req.getContextPath() + "/login");
            }

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "A system error occurred. Please try again.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/jsp/reset-password.jsp").forward(req, resp);
        }
    }
}
