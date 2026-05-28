package com.guacamole.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages a singleton HikariCP connection pool.
 * Configuration is loaded from db.properties on the classpath.
 */
public class DbUtil {

    private static final HikariDataSource DATA_SOURCE;

    static {
        // Explicitly register the MySQL driver before HikariCP tries to find it
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath", e);
        }

        Properties props = loadProperties();

        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl(     get(props, "db.url",
                "jdbc:mysql://localhost:3306/guacamole_db" +
                "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"));
        cfg.setUsername(    get(props, "db.user",     "guacamole_user"));
        cfg.setPassword(    get(props, "db.password", "guacamole_password"));
        cfg.setMaximumPoolSize(  intGet(props, "db.pool.maxSize", 10));
        cfg.setMinimumIdle(      intGet(props, "db.pool.minIdle", 2));
        cfg.setConnectionTimeout(30_000);
        cfg.setIdleTimeout(     600_000);
        cfg.setMaxLifetime(   1_800_000);
        cfg.setPoolName("GuacAdminPool");
        cfg.setConnectionTestQuery("SELECT 1");

        DATA_SOURCE = new HikariDataSource(cfg);
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static void shutdown() {
        if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DbUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                System.out.println("[DbUtil] Loaded db.properties from classpath.");
            } else {
                System.out.println("[DbUtil] db.properties not found — using defaults.");
            }
        } catch (Exception e) {
            System.err.println("[DbUtil] Could not load db.properties: " + e.getMessage());
        }
        return props;
    }

    private static String get(Properties props, String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;
        return props.getProperty(key, defaultValue);
    }

    private static int intGet(Properties props, String key, int defaultValue) {
        try { return Integer.parseInt(get(props, key, String.valueOf(defaultValue))); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    private DbUtil() {}
}
