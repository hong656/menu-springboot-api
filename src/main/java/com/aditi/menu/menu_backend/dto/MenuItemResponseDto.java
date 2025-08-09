package com.aditi.menu.menu_backend.dto;

import lombok.Data;

@Data
public class MenuItemResponseDto {
    private Integer id;
    private String name;
    private String description;
    private Integer priceCents;
    private String imageUrl;
    private boolean isAvailable;
}
