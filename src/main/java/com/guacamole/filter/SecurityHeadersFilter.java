package com.guacamole.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * Adds security-related HTTP response headers to every response.
 */
@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("X-Content-Type-Options",    "nosniff");
        resp.setHeader("X-Frame-Options",           "DENY");
        resp.setHeader("X-XSS-Protection",          "1; mode=block");
        resp.setHeader("Referrer-Policy",           "strict-origin-when-cross-origin");
        resp.setHeader("Cache-Control",             "no-store");
        resp.setHeader("Content-Security-Policy",
                "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'");

        chain.doFilter(request, response);
    }
}
