package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.model.dto.LoginDto;
import com.guacamole.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller — handles GET (show login page) and POST (process credentials).
 * On success, stores the AdminUser in the session and redirects to /dashboard.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        LoginDto dto = new LoginDto(
                req.getParameter("username"),
                req.getParameter("password"));

        if (dto.isBlank()) {
            req.setAttribute("errorMessage", "Username and password are required.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            AdminUser admin = userService.authenticate(
                    dto.getUsername(), dto.getPassword(), req.getRemoteAddr());

            if (admin != null) {
                // Prevent session fixation
                HttpSession old = req.getSession(false);
                if (old != null) old.invalidate();

                HttpSession session = req.getSession(true);
                session.setAttribute("currentUser", admin);
                session.setMaxInactiveInterval(30 * 60);

                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("errorMessage", "Invalid username or password.");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "A system error occurred. Please try again.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }
}
