package com.guacamole.controller;

import com.guacamole.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller — Report 3: Guacamole User Details / Users List.
 * Accessible to all roles (AUDITOR, ADMIN, SUPER_ADMIN).
 *
 * GET /users          → full Guacamole user list
 * GET /users?name=X   → single user detail
 */
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");

        try {
            if (name != null && !name.isBlank()) {
                req.setAttribute("user", userService.getGuacamoleUserByUsername(name));
                req.getRequestDispatcher("/jsp/reports/user-detail.jsp").forward(req, resp);
            } else {
                req.setAttribute("users", userService.getAllGuacamoleUsers());
                req.getRequestDispatcher("/jsp/reports/users.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
            req.getRequestDispatcher("/jsp/reports/users.jsp").forward(req, resp);
        }
    }
}
