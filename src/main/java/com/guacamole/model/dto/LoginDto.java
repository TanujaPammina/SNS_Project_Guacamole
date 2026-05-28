package com.guacamole.model.dto;

/**
 * DTO — carries login form data before validation.
 */
public class LoginDto {

    private String username;
    private String password;

    public LoginDto() {}

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }

    public boolean isBlank() {
        return (username == null || username.isBlank()) ||
               (password == null || password.isBlank());
    }
}
