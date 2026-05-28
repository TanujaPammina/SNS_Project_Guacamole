package com.guacamole.listener;

import com.guacamole.util.DbUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Lifecycle listener — initialises and shuts down the HikariCP pool.
 * A DB connection failure on startup logs a warning but does NOT
 * prevent the application from deploying.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[Guacamole Admin] Application starting...");
        try {
            DbUtil.getConnection().close();
            System.out.println("[Guacamole Admin] Database pool initialised successfully.");
        } catch (Exception e) {
            // Log warning but do NOT re-throw — let the app start anyway
            System.err.println("[Guacamole Admin] WARNING: DB pool init failed: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            DbUtil.shutdown();
            System.out.println("[Guacamole Admin] Database pool shut down.");
        } catch (Exception e) {
            System.err.println("[Guacamole Admin] Error shutting down pool: " + e.getMessage());
        }
    }
}
