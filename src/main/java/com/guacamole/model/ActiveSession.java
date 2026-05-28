package com.guacamole.model;

import java.time.LocalDateTime;

/**
 * Represents an active or historical Guacamole tunnel session
 * sourced from guacamole_connection_history.
 */
public class ActiveSession {

    private int           historyId;
    private String        username;
    private String        connectionName;
    private String        remoteHost;       // client IP
    private LocalDateTime startDate;
    private LocalDateTime endDate;          // null = still active
    private long          durationSeconds;  // computed

    public ActiveSession() {}

    public int getHistoryId()                           { return historyId; }
    public void setHistoryId(int historyId)             { this.historyId = historyId; }

    public String getUsername()                         { return username; }
    public void setUsername(String username)            { this.username = username; }

    public String getConnectionName()                   { return connectionName; }
    public void setConnectionName(String connectionName){ this.connectionName = connectionName; }

    public String getRemoteHost()                       { return remoteHost; }
    public void setRemoteHost(String remoteHost)        { this.remoteHost = remoteHost; }

    public LocalDateTime getStartDate()                 { return startDate; }
    public void setStartDate(LocalDateTime startDate)   { this.startDate = startDate; }

    public LocalDateTime getEndDate()                   { return endDate; }
    public void setEndDate(LocalDateTime endDate)       { this.endDate = endDate; }

    public long getDurationSeconds()                    { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds){ this.durationSeconds = durationSeconds; }

    /** Convenience: formats duration as HH:MM:SS */
    public String getDurationFormatted() {
        if (durationSeconds < 0) return "Active";
        long h = durationSeconds / 3600;
        long m = (durationSeconds % 3600) / 60;
        long s = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
