package com.guacamole.model;

import java.time.LocalDateTime;

/**
 * Represents an entry in the admin_audit_log table — every administrative
 * action performed through this application.
 */
public class AuditLog {

    private int           id;
    private String        actorUsername;   // who performed the action
    private String        action;          // e.g. LOGIN, CREATE_USER, EDIT_CONNECTION
    private String        targetEntity;    // e.g. username or connection name affected
    private String        details;         // free-text detail / diff
    private String        remoteIp;
    private LocalDateTime actionTime;

    public AuditLog() {}

    public int getId()                                  { return id; }
    public void setId(int id)                           { this.id = id; }

    public String getActorUsername()                    { return actorUsername; }
    public void setActorUsername(String actorUsername)  { this.actorUsername = actorUsername; }

    public String getAction()                           { return action; }
    public void setAction(String action)                { this.action = action; }

    public String getTargetEntity()                     { return targetEntity; }
    public void setTargetEntity(String targetEntity)    { this.targetEntity = targetEntity; }

    public String getDetails()                          { return details; }
    public void setDetails(String details)              { this.details = details; }

    public String getRemoteIp()                         { return remoteIp; }
    public void setRemoteIp(String remoteIp)            { this.remoteIp = remoteIp; }

    public LocalDateTime getActionTime()                        { return actionTime; }
    public void setActionTime(LocalDateTime actionTime)         { this.actionTime = actionTime; }
}
