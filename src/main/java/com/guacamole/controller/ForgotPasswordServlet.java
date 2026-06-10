package com.guacamole.controller;

import com.guacamole.service.PasswordResetService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /forgot-password  → show "enter your email" form
 * POST /forgot-password  → send reset email
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final PasswordResetService resetService = new PasswordResetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");

        // Build the base URL of this application for the reset link
        String scheme  = req.getScheme();
        String server  = req.getServerName();
        int    port    = req.getServerPort();
        String context = req.getContextPath();
        String baseUrl = scheme + "://" + server +
                         (port == 80 || port == 443 ? "" : ":" + port) + context;

        try {
            String error = resetService.requestReset(email, baseUrl);
            if (error != null) {
                req.setAttribute("errorMessage", error);
                req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
            } else {
                // Always show success — never reveal if email exists
                req.setAttribute("successMessage",
                        "If an account with that email exists, a reset link has been sent. " +
                        "Please check your inbox (and spam folder). " +
                        "The link expires in 30 minutes.");
                req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "A system error occurred. Please try again.");
            req.getRequestDispatcher("/jsp/forgot-password.jsp").forward(req, resp);
        }
    }
}
