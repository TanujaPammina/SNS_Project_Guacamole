package com.guacamole.model;

/**
 * Aggregated statistics for a single connection (used in Top Connections,
 * Session Duration, and Concurrent Sessions reports).
 */
public class ConnectionStat {

    private String connectionName;
    private int    totalSessions;
    private long   totalDurationSeconds;
    private long   avgDurationSeconds;
    private int    maxConcurrent;

    public ConnectionStat() {}

    public String getConnectionName()                       { return connectionName; }
    public void setConnectionName(String connectionName)    { this.connectionName = connectionName; }

    public int getTotalSessions()                           { return totalSessions; }
    public void setTotalSessions(int totalSessions)         { this.totalSessions = totalSessions; }

    public long getTotalDurationSeconds()                   { return totalDurationSeconds; }
    public void setTotalDurationSeconds(long s)             { this.totalDurationSeconds = s; }

    public long getAvgDurationSeconds()                     { return avgDurationSeconds; }
    public void setAvgDurationSeconds(long s)               { this.avgDurationSeconds = s; }

    public int getMaxConcurrent()                           { return maxConcurrent; }
    public void setMaxConcurrent(int maxConcurrent)         { this.maxConcurrent = maxConcurrent; }

    public String getTotalDurationFormatted() {
        return formatSeconds(totalDurationSeconds);
    }

    public String getAvgDurationFormatted() {
        return formatSeconds(avgDurationSeconds);
    }

    private String formatSeconds(long secs) {
        long h = secs / 3600;
        long m = (secs % 3600) / 60;
        long s = secs % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
