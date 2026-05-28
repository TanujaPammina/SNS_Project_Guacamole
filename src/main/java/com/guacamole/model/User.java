package com.guacamole.model;

/**
 * Represents a Guacamole user from guacamole_entity + guacamole_user tables.
 */
public class User {

    private int    entityId;
    private String username;
    private String passwordHash;
    private boolean disabled;
    private boolean expired;
    private String  fullName;
    private String  email;
    private java.time.LocalDateTime lastActive;

    public User() {}

    public int getEntityId()                        { return entityId; }
    public void setEntityId(int entityId)           { this.entityId = entityId; }

    public String getUsername()                     { return username; }
    public void setUsername(String username)        { this.username = username; }

    public String getPasswordHash()                 { return passwordHash; }
    public void setPasswordHash(String h)           { this.passwordHash = h; }

    public boolean isDisabled()                     { return disabled; }
    public void setDisabled(boolean disabled)       { this.disabled = disabled; }

    public boolean isExpired()                      { return expired; }
    public void setExpired(boolean expired)         { this.expired = expired; }

    public String getFullName()                     { return fullName; }
    public void setFullName(String fullName)        { this.fullName = fullName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public java.time.LocalDateTime getLastActive()              { return lastActive; }
    public void setLastActive(java.time.LocalDateTime lastActive) { this.lastActive = lastActive; }
}
