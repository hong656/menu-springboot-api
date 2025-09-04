package com.aditi.menu.menu_backend.dto;

import java.util.Set;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Integer status;
    private Set<String> roles;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, Set<String> roles, Integer status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.status = status;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
        public Set<String> getRoles() {
        return this.roles;
    }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
