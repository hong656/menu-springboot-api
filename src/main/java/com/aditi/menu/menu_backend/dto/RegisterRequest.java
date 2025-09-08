package com.aditi.menu.menu_backend.dto;

import java.util.Set;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Integer status;
    private Set<Long> roleIds;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, Set<Long> roleIds, Integer status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleIds = roleIds;
        this.status = status;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Long> getRoleIds() {
        return this.roleIds;
    }
    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
