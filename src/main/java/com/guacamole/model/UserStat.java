package com.guacamole.model;

/**
 * Aggregated per-user statistics (Top Users report).
 */
public class UserStat {

    private String username;
    private int    totalSessions;
    private long   totalDurationSeconds;
    private String lastSeen;

    public UserStat() {}

    public String getUsername()                         { return username; }
    public void setUsername(String username)            { this.username = username; }

    public int getTotalSessions()                       { return totalSessions; }
    public void setTotalSessions(int totalSessions)     { this.totalSessions = totalSessions; }

    public long getTotalDurationSeconds()               { return totalDurationSeconds; }
    public void setTotalDurationSeconds(long s)         { this.totalDurationSeconds = s; }

    public String getLastSeen()                         { return lastSeen; }
    public void setLastSeen(String lastSeen)            { this.lastSeen = lastSeen; }

    public String getTotalDurationFormatted() {
        long h = totalDurationSeconds / 3600;
        long m = (totalDurationSeconds % 3600) / 60;
        long s = totalDurationSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
