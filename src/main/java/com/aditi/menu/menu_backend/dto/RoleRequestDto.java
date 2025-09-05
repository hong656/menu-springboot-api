package com.aditi.menu.menu_backend.dto;

import lombok.Data;

@Data
public class RoleRequestDto {
    private String name;
    private String description;
    private int status;
}
