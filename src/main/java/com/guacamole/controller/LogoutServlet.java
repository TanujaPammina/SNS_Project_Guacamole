package com.guacamole.controller;

import com.guacamole.model.AdminUser;
import com.guacamole.util.AuditLogger;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            AdminUser user = (AdminUser) session.getAttribute("currentUser");
            if (user != null) {
                AuditLogger.log(user.getUsername(), "LOGOUT", user.getUsername(),
                        "User logged out", req.getRemoteAddr());
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
