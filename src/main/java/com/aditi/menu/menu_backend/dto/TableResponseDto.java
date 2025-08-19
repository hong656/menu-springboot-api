package com.aditi.menu.menu_backend.dto;

import lombok.Data;

@Data
public class TableResponseDto {
    private Long id;
    private Integer number;
    private Integer status;
    private String qr_token;
}