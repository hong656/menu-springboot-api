package com.aditi.menu.menu_backend.dto;

import com.aditi.menu.menu_backend.entity.Role;

public class SimpleRoleDto {
    private Long id;
    private String name;
    private String description;

    public SimpleRoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
