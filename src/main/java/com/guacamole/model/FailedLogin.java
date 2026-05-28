package com.guacamole.model;

import java.time.LocalDateTime;

/**
 * Represents a failed login attempt captured in the audit log.
 */
public class FailedLogin {

    private int           id;
    private String        username;
    private String        remoteIp;
    private LocalDateTime attemptTime;
    private int           failCount;   // used in summary view

    public FailedLogin() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getUsername()                     { return username; }
    public void setUsername(String username)        { this.username = username; }

    public String getRemoteIp()                     { return remoteIp; }
    public void setRemoteIp(String remoteIp)        { this.remoteIp = remoteIp; }

    public LocalDateTime getAttemptTime()                       { return attemptTime; }
    public void setAttemptTime(LocalDateTime attemptTime)       { this.attemptTime = attemptTime; }

    public int getFailCount()                       { return failCount; }
    public void setFailCount(int failCount)         { this.failCount = failCount; }
}
