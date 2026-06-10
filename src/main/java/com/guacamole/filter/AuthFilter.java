package com.guacamole.filter;

import com.guacamole.model.AdminUser;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Filter — ensures every request to a protected resource has an authenticated
 * AdminUser in the session. Unauthenticated requests are redirected to /login.
 *
 * The session attribute key is "currentUser" (AdminUser object).
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    /** Paths that bypass authentication entirely. */
    private static final String[] PUBLIC_PATHS = {
        "/login", "/css/", "/js/", "/favicon.ico",
        "/forgot-password", "/reset-password"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        for (String pub : PUBLIC_PATHS) {
            if (path.startsWith(pub)) {
                chain.doFilter(request, response);
                return;
            }
        }

        HttpSession session  = req.getSession(false);
        AdminUser   current  = (session != null)
                ? (AdminUser) session.getAttribute("currentUser")
                : null;

        if (current != null && current.isActive()) {
            chain.doFilter(request, response);
        } else {
            // Invalidate stale session if present
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
